import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { X, User, Mail, Lock, AlertCircle, Check, Loader2, Save, Hash, Shield } from 'lucide-react';
import { getUserById, updateUserProfile } from '../services/api';
import './EditProfileModal.css';

const EditProfileModal = ({ isOpen, onClose, currentUser, onUpdateSuccess }) => {
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        confirmPassword: ''
    });

    // Read-only profile data
    const [profileInfo, setProfileInfo] = useState({
        username: '',
        role: '',
        studentNumber: ''
    });

    const [loading, setLoading] = useState(false);
    const [fetching, setFetching] = useState(true);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(false);

    // Fetch full profile data when modal opens
    useEffect(() => {
        if (isOpen && currentUser?.id) {
            fetchProfileData();
        }
    }, [isOpen, currentUser?.id]);

    const fetchProfileData = async () => {
        try {
            setFetching(true);
            const response = await getUserById(currentUser.id);
            const userData = response.data;

            setFormData({
                firstName: userData.firstName || '',
                lastName: userData.lastName || '',
                email: userData.email || '',
                password: '',
                confirmPassword: ''
            });

            setProfileInfo({
                username: userData.username || '',
                role: userData.role || '',
                studentNumber: userData.studentNumber || ''
            });
        } catch (err) {
            console.error('Failed to fetch profile:', err);
            // Fallback to currentUser data
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

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
        if (error) setError(null);
    };

    const validateForm = () => {
        if (!formData.firstName.trim() || !formData.lastName.trim()) {
            setError('First name and last name are required');
            return false;
        }
        if (!formData.email.trim().includes('@')) {
            setError('Please enter a valid email address');
            return false;
        }
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

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validateForm()) return;

        setLoading(true);
        setError(null);

        try {
            const updatePayload = {
                firstName: formData.firstName,
                lastName: formData.lastName,
                email: formData.email,
                ...(formData.password ? { password: formData.password } : {})
            };

            const response = await updateUserProfile(currentUser.id, updatePayload);

            // Update local storage with new user data
            const storedUser = JSON.parse(localStorage.getItem('user') || '{}');
            const updatedUser = {
                ...storedUser,
                firstName: response.data.firstName,
                lastName: response.data.lastName,
                email: response.data.email,
                fullName: response.data.fullName
            };
            localStorage.setItem('user', JSON.stringify(updatedUser));

            setSuccess(true);
            setTimeout(() => {
                onUpdateSuccess?.(updatedUser);
                onClose();
            }, 1500);
        } catch (err) {
            console.error('Update profile error:', err);
            setError(err.response?.data?.message || 'Failed to update profile. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    if (!isOpen) return null;

    return (
        <AnimatePresence>
            <div className="modal-overlay" onClick={onClose}>
                <motion.div
                    className="modal-content edit-profile-modal"
                    initial={{ opacity: 0, scale: 0.95, y: 20 }}
                    animate={{ opacity: 1, scale: 1, y: 0 }}
                    exit={{ opacity: 0, scale: 0.95, y: 20 }}
                    onClick={e => e.stopPropagation()}
                >
                    <button className="modal-close-btn" onClick={onClose}>
                        <X size={20} />
                    </button>

                    <div className="modal-header">
                        <div className="modal-icon-box">
                            <User size={24} />
                        </div>
                        <div>
                            <h2>Edit Profile</h2>
                            <p>Update your personal information</p>
                        </div>
                    </div>

                    {fetching ? (
                        <div className="loading-container">
                            <Loader2 className="spinner" size={32} />
                            <p>Loading profile...</p>
                        </div>
                    ) : success ? (
                        <div className="success-message">
                            <div className="success-icon">
                                <Check size={48} />
                            </div>
                            <h3>Profile Updated!</h3>
                            <p>Your changes have been saved successfully.</p>
                        </div>
                    ) : (
                        <form onSubmit={handleSubmit} className="edit-profile-form">
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
                                    {profileInfo.studentNumber && (
                                        <div className="info-item student-number-item">
                                            <Hash size={16} />
                                            <span className="info-label">Student Number:</span>
                                            <span className="info-value student-number">{profileInfo.studentNumber}</span>
                                        </div>
                                    )}
                                </div>
                            </div>

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
