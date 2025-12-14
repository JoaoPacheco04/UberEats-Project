import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { X, User, Mail, Lock, AlertCircle, Check, Loader2, Save, Hash, Shield } from 'lucide-react';
import { getUserById, updateUserProfile } from '../services/api';
import './EditProfileModal.css';

// Modal component for editing user profile
const EditProfileModal = ({ isOpen, onClose, currentUser, onUpdateSuccess }) => {
    // State for form fields that can be edited
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        confirmPassword: ''
    });

    // State for read-only profile information (username, role, student number)
    const [profileInfo, setProfileInfo] = useState({
        username: '',
        role: '',
        studentNumber: ''
    });

    // UI state variables
    const [loading, setLoading] = useState(false); // For submit loading state
    const [fetching, setFetching] = useState(true); // For initial data fetch loading state
    const [error, setError] = useState(null); // For form validation/API errors
    const [success, setSuccess] = useState(false); // For success state after update

    // Fetch full profile data when modal opens
    useEffect(() => {
        if (isOpen && currentUser?.id) {
            fetchProfileData();
        }
    }, [isOpen, currentUser?.id]);

    // Fetch complete user data from API for editing
    const fetchProfileData = async () => {
        try {
            setFetching(true);
            const response = await getUserById(currentUser.id);
            const userData = response.data;

            // Set editable form fields
            setFormData({
                firstName: userData.firstName || '',
                lastName: userData.lastName || '',
                email: userData.email || '',
                password: '', // Password fields start empty for security
                confirmPassword: ''
            });

            // Set read-only profile information
            setProfileInfo({
                username: userData.username || '',
                role: userData.role || '',
                studentNumber: userData.studentNumber || ''
            });
        } catch (err) {
            console.error('Failed to fetch profile:', err);
            // Fallback to currentUser data if API fails
            setFormData({
                firstName: currentUser?.firstName || '',
                lastName: currentUser?.lastName || '',
                email: currentUser?.email || '',
                password: '',
                confirmPassword: ''
            });
        } finally {
            setFetching(false);
        }
    };

    // Handle input changes for form fields
    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
        if (error) setError(null); // Clear error when user starts typing
    };

    // Validate form data before submission
    const validateForm = () => {
        // Check required fields
        if (!formData.firstName.trim() || !formData.lastName.trim()) {
            setError('First name and last name are required');
            return false;
        }
        // Validate email format
        if (!formData.email.trim().includes('@')) {
            setError('Please enter a valid email address');
            return false;
        }
        // Validate password if provided (password change is optional)
        if (formData.password) {
            if (formData.password.length < 6) {
                setError('Password must be at least 6 characters long');
                return false;
            }
            if (formData.password !== formData.confirmPassword) {
                setError('Passwords do not match');
                return false;
            }
        }
        return true;
    };

    // Handle form submission
    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validateForm()) return;

        setLoading(true);
        setError(null);

        try {
            // Prepare update payload - only include password if user provided one
            const updatePayload = {
                firstName: formData.firstName,
                lastName: formData.lastName,
                email: formData.email,
                ...(formData.password ? { password: formData.password } : {}) // Conditional password inclusion
            };

            // Send update request to API
            const response = await updateUserProfile(currentUser.id, updatePayload);

            // Update user data in localStorage to keep UI in sync
            const storedUser = JSON.parse(localStorage.getItem('user') || '{}');
            const updatedUser = {
                ...storedUser,
                firstName: response.data.firstName,
                lastName: response.data.lastName,
                email: response.data.email,
                fullName: response.data.fullName
            };
            localStorage.setItem('user', JSON.stringify(updatedUser));

            // Show success state and close modal after delay
            setSuccess(true);
            setTimeout(() => {
                onUpdateSuccess?.(updatedUser); // Notify parent component
                onClose(); // Close modal
            }, 1500);
        } catch (err) {
            console.error('Update profile error:', err);
            setError(err.response?.data?.message || 'Failed to update profile. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    // Don't render anything if modal is closed
    if (!isOpen) return null;

    return (
        <AnimatePresence>
            {/* Modal overlay - click to close */}
            <div className="modal-overlay" onClick={onClose}>
                {/* Modal content container with animation */}
                <motion.div
                    className="modal-content edit-profile-modal"
                    initial={{ opacity: 0, scale: 0.95, y: 20 }}
                    animate={{ opacity: 1, scale: 1, y: 0 }}
                    exit={{ opacity: 0, scale: 0.95, y: 20 }}
                    onClick={e => e.stopPropagation()} // Prevent click propagation to overlay
                >
                    {/* Close button */}
                    <button className="modal-close-btn" onClick={onClose}>
                        <X size={20} />
                    </button>

                    {/* Modal header with icon and title */}
                    <div className="modal-header">
                        <div className="modal-icon-box">
                            <User size={24} />
                        </div>
                        <div>
                            <h2>Edit Profile</h2>
                            <p>Update your personal information</p>
                        </div>
                    </div>

                    {/* Conditional rendering based on state */}
                    {fetching ? (
                        // Loading state during initial data fetch
                        <div className="loading-container">
                            <Loader2 className="spinner" size={32} />
                            <p>Loading profile...</p>
                        </div>
                    ) : success ? (
                        // Success state after profile update
                        <div className="success-message">
                            <div className="success-icon">
                                <Check size={48} />
                            </div>
                            <h3>Profile Updated!</h3>
                            <p>Your changes have been saved successfully.</p>
                        </div>
                    ) : (
                        // Main form for editing profile
                        <form onSubmit={handleSubmit} className="edit-profile-form">
                            {/* Error display banner */}
                            {error && (
                                <div className="form-error-banner">
                                    <AlertCircle size={18} />
                                    <span>{error}</span>
                                </div>
                            )}

                            {/* Read-only Account Info Section */}
                            <div className="account-info-section">
                                <h3>Account Information</h3>
                                <div className="account-info-grid">
                                    <div className="info-item">
                                        <User size={16} />
                                        <span className="info-label">Username:</span>
                                        <span className="info-value">{profileInfo.username}</span>
                                    </div>
                                    <div className="info-item">
                                        <Shield size={16} />
                                        <span className="info-label">Role:</span>
                                        <span className="info-value role-tag">{profileInfo.role}</span>
                                    </div>
                                    {/* Conditionally show student number if available */}
                                    {profileInfo.studentNumber && (
                                        <div className="info-item student-number-item">
                                            <Hash size={16} />
                                            <span className="info-label">Student Number:</span>
                                            <span className="info-value student-number">{profileInfo.studentNumber}</span>
                                        </div>
                                    )}
                                </div>
                            </div>

                            {/* Editable form fields - Name */}
                            <div className="form-row">
                                <div className="form-group">
                                    <label>First Name</label>
                                    <div className="input-wrapper">
                                        <User size={18} />
                                        <input
                                            type="text"
                                            name="firstName"
                                            value={formData.firstName}
                                            onChange={handleChange}
                                            placeholder="John"
                                        />
                                    </div>
                                </div>
                                <div className="form-group">
                                    <label>Last Name</label>
                                    <div className="input-wrapper">
                                        <User size={18} />
                                        <input
                                            type="text"
                                            name="lastName"
                                            value={formData.lastName}
                                            onChange={handleChange}
                                            placeholder="Doe"
                                        />
                                    </div>
                                </div>
                            </div>

                            {/* Editable form fields - Email */}
                            <div className="form-group">
                                <label>Email Address</label>
                                <div className="input-wrapper">
                                    <Mail size={18} />
                                    <input
                                        type="email"
                                        name="email"
                                        value={formData.email}
                                        onChange={handleChange}
                                        placeholder="john.doe@example.com"
                                    />
                                </div>
                            </div>

                            {/* Password change section (optional) */}
                            <div className="divider">
                                <span>Change Password (Optional)</span>
                            </div>

                            <div className="form-row">
                                <div className="form-group">
                                    <label>New Password</label>
                                    <div className="input-wrapper">
                                        <Lock size={18} />
                                        <input
                                            type="password"
                                            name="password"
                                            value={formData.password}
                                            onChange={handleChange}
                                            placeholder="Min. 6 characters"
                                        />
                                    </div>
                                </div>
                                <div className="form-group">
                                    <label>Confirm Password</label>
                                    <div className="input-wrapper">
                                        <Lock size={18} />
                                        <input
                                            type="password"
                                            name="confirmPassword"
                                            value={formData.confirmPassword}
                                            onChange={handleChange}
                                            placeholder="Confirm new password"
                                        />
                                    </div>
                                </div>
                            </div>

                            {/* Form action buttons */}
                            <div className="modal-actions">
                                <button type="button" className="cancel-btn" onClick={onClose}>
                                    Cancel
                                </button>
                                <button
                                    type="submit"
                                    className="save-btn"
                                    disabled={loading}
                                >
                                    {loading ? <Loader2 className="spinner" size={18} /> : <Save size={18} />}
                                    Save Changes
                                </button>
                            </div>
                        </form>
                    )}
                </motion.div>
            </div>
        </AnimatePresence>
    );
};

export default EditProfileModal;