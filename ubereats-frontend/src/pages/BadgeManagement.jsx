import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import {
    ArrowLeft,
    Plus,
    Award,
    Edit2,
    Trash2,
    ToggleLeft,
    ToggleRight,
    Star,
    Target,
    Zap,
    Trophy,
    Loader2,
    AlertCircle,
    X,
    Check
} from 'lucide-react';
import {
    getAllBadges,
    createBadge,
    updateBadge,
    toggleBadgeStatus,
    deleteBadge,
    getCurrentUser
} from '../services/api';
import './BadgeManagement.css';

// Teachers can only create MANUAL badges - AUTOMATIC badges are system-defined
const BADGE_TYPE = 'MANUAL';
const BADGE_ICONS = [
    { name: '🏆', label: 'Trophy' },
    { name: '⭐', label: 'Star' },
    { name: '🎯', label: 'Target' },
    { name: '⚡', label: 'Lightning' },
    { name: '🥇', label: 'Medal' },
    { name: '🎖️', label: 'Badge' },
    { name: '🚀', label: 'Rocket' },
    { name: '💎', label: 'Diamond' }
];

const BadgeManagement = () => {
    const navigate = useNavigate();
    const [badges, setBadges] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showModal, setShowModal] = useState(false);
    const [editingBadge, setEditingBadge] = useState(null);
    const [submitError, setSubmitError] = useState(null);
    const [submitting, setSubmitting] = useState(false);
    const [deleteConfirm, setDeleteConfirm] = useState(null);

    const [formData, setFormData] = useState({
        name: '',
        description: '',
        points: 10,
        badgeType: 'MANUAL',
        recipientType: 'BOTH',
        icon: '🏆',
        color: '#FF5733',
        isActive: true
    });

    const currentUser = getCurrentUser();

    useEffect(() => {
        // Only professors can access this page
        if (currentUser?.role !== 'TEACHER' && currentUser?.role !== 'PROFESSOR') {
            navigate('/');
            return;
        }
        fetchBadges();
    }, []);

    const fetchBadges = async () => {
        try {
            setLoading(true);
            const response = await getAllBadges();
            setBadges(response.data || []);
        } catch (err) {
            console.error('Error fetching badges:', err);
            setError('Failed to load badges');
        } finally {
            setLoading(false);
        }
    };

    const resetForm = () => {
        setFormData({
            name: '',
            description: '',
            points: 10,
            badgeType: 'MANUAL',
            recipientType: 'BOTH',
            icon: '🏆',
            color: '#FF5733',
            isActive: true
        });
        setEditingBadge(null);
        setSubmitError(null);
    };

    const openCreateModal = () => {
        resetForm();
        setShowModal(true);
    };

    const openEditModal = (badge) => {
        setEditingBadge(badge);
        setFormData({
            name: badge.name || '',
            description: badge.description || '',
            points: badge.points || 10,
            badgeType: badge.badgeType || 'MANUAL',
            recipientType: badge.recipientType || 'BOTH',
            icon: badge.icon || '🏆',
            color: badge.color || '#FF5733',
            isActive: badge.isActive !== false
        });
        setShowModal(true);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setSubmitError(null);
        setSubmitting(true);

        if (!currentUser?.id) {
            setSubmitError('You must be logged in to create badges');
            setSubmitting(false);
            return;
        }

        try {
            // Build the request payload matching BadgeRequestDTO
            const badgeData = {
                name: formData.name,
                description: formData.description,
                points: formData.points,
                // Preserve original badgeType when editing, use MANUAL for new badges
                badgeType: editingBadge ? formData.badgeType : 'MANUAL',
                recipientType: formData.recipientType,
                icon: formData.icon,
                color: formData.color,
                createdByUserId: currentUser.id
            };

            if (editingBadge) {
                await updateBadge(editingBadge.id, badgeData);
            } else {
                await createBadge(badgeData);
            }
            setShowModal(false);
            resetForm();
            fetchBadges();
        } catch (err) {
            console.error('Error saving badge:', err);
            setSubmitError(err.response?.data?.message || err.response?.data || 'Failed to save badge. Please check all fields.');
        } finally {
            setSubmitting(false);
        }
    };

    const handleToggleStatus = async (badgeId) => {
        try {
            await toggleBadgeStatus(badgeId);
            fetchBadges();
        } catch (err) {
            console.error('Error toggling badge status:', err);
        }
    };

    const handleDelete = async (badgeId) => {
        try {
            await deleteBadge(badgeId);
            setDeleteConfirm(null);
            fetchBadges();
        } catch (err) {
            console.error('Error deleting badge:', err);
        }
    };

    // Icons are now emoji strings, no component mapping needed

    if (loading) {
        return (
            <div className="badge-management-loading">
                <Loader2 className="spinner" size={48} />
                <p>Loading badges...</p>
            </div>
        );
    }

    return (
        <div className="badge-management">
            <header className="badge-header">
                <button className="back-btn" onClick={() => navigate(-1)}>
                    <ArrowLeft size={20} />
                    <span>Back</span>
                </button>

                <div className="header-content">
                    <h1>
                        <Award size={28} />
                        Badge Management
                    </h1>
                    <p>Create and manage badges that can be awarded to students and teams</p>
                </div>

                <button className="create-badge-btn" onClick={openCreateModal}>
                    <Plus size={20} />
                    Create Badge
                </button>
            </header>

            {error && (
                <div className="error-banner">
                    <AlertCircle size={18} />
                    {error}
                </div>
            )}

            <div className="badges-grid">
                {badges.length === 0 ? (
                    <div className="empty-state">
                        <Award size={64} />
                        <h3>No Badges Yet</h3>
                        <p>Create your first badge to start awarding achievements</p>
                        <button className="create-btn" onClick={openCreateModal}>
                            <Plus size={18} />
                            Create Badge
                        </button>
                    </div>
                ) : (
                    badges.map(badge => {
                        return (
                            <motion.div
                                key={badge.id}
                                className={`badge-card ${!badge.isActive ? 'inactive' : ''}`}
                                initial={{ opacity: 0, y: 20 }}
                                animate={{ opacity: 1, y: 0 }}
                                layout
                            >
                                <div
                                    className="badge-icon-emoji"
                                    style={{ backgroundColor: badge.color || '#FF5733' }}
                                >
                                    <span>{badge.icon || '🏆'}</span>
                                </div>
                                <div className="badge-info">
                                    <h3>{badge.name}</h3>
                                    <p className="badge-desc">{badge.description}</p>
                                    <div className="badge-meta">
                                        <span className="points">
                                            <Star size={14} />
                                            {badge.points} pts
                                        </span>
                                        <span className={`type-badge ${badge.badgeType?.toLowerCase()}`}>
                                            {badge.badgeType}
                                        </span>
                                    </div>
                                </div>
                                <div className="badge-actions">
                                    <button
                                        className="action-btn toggle"
                                        onClick={() => handleToggleStatus(badge.id)}
                                        title={badge.isActive ? 'Deactivate' : 'Activate'}
                                    >
                                        {badge.isActive ? (
                                            <ToggleRight size={20} className="active" />
                                        ) : (
                                            <ToggleLeft size={20} />
                                        )}
                                    </button>
                                    <button
                                        className="action-btn edit"
                                        onClick={() => openEditModal(badge)}
                                        title="Edit"
                                    >
                                        <Edit2 size={18} />
                                    </button>
                                    <button
                                        className="action-btn delete"
                                        onClick={() => setDeleteConfirm(badge.id)}
                                        title="Delete"
                                    >
                                        <Trash2 size={18} />
                                    </button>
                                </div>

                                {/* Delete Confirmation */}
                                <AnimatePresence>
                                    {deleteConfirm === badge.id && (
                                        <motion.div
                                            className="delete-confirm"
                                            initial={{ opacity: 0 }}
                                            animate={{ opacity: 1 }}
                                            exit={{ opacity: 0 }}
                                        >
                                            <p>Delete this badge?</p>
                                            <div className="confirm-btns">
                                                <button onClick={() => handleDelete(badge.id)}>
                                                    <Check size={16} /> Yes
                                                </button>
                                                <button onClick={() => setDeleteConfirm(null)}>
                                                    <X size={16} /> No
                                                </button>
                                            </div>
                                        </motion.div>
                                    )}
                                </AnimatePresence>
                            </motion.div>
                        );
                    })
                )}
            </div>

            {/* Create/Edit Modal */}
            <AnimatePresence>
                {showModal && (
                    <motion.div
                        className="modal-overlay"
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        exit={{ opacity: 0 }}
                        onClick={() => setShowModal(false)}
                    >
                        <motion.div
                            className="modal-content"
                            initial={{ scale: 0.9, opacity: 0 }}
                            animate={{ scale: 1, opacity: 1 }}
                            exit={{ scale: 0.9, opacity: 0 }}
                            onClick={(e) => e.stopPropagation()}
                        >
                            <header className="modal-header">
                                <h2>
                                    {editingBadge ? 'Edit Badge' : 'Create New Badge'}
                                </h2>
                                <button className="close-btn" onClick={() => setShowModal(false)}>
                                    <X size={20} />
                                </button>
                            </header>

                            <form onSubmit={handleSubmit}>
                                {submitError && (
                                    <div className="form-error">
                                        <AlertCircle size={16} />
                                        {submitError}
                                    </div>
                                )}

                                <div className="form-group">
                                    <label>Badge Name</label>
                                    <input
                                        type="text"
                                        value={formData.name}
                                        onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                                        placeholder="e.g., Sprint Champion"
                                        required
                                    />
                                </div>

                                <div className="form-group">
                                    <label>Description</label>
                                    <textarea
                                        value={formData.description}
                                        onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                                        placeholder="Describe what this badge represents..."
                                        rows={3}
                                    />
                                </div>

                                <div className="form-row">
                                    <div className="form-group">
                                        <label>Points</label>
                                        <input
                                            type="number"
                                            min="1"
                                            max="1000"
                                            value={formData.points}
                                            onChange={(e) => setFormData({ ...formData, points: parseInt(e.target.value) || 0 })}
                                            required
                                        />
                                    </div>

                                    <div className="form-group">
                                        <label>Recipient Type</label>
                                        <select
                                            value={formData.recipientType}
                                            onChange={(e) => setFormData({ ...formData, recipientType: e.target.value })}
                                        >
                                            <option value="BOTH">Both (Students & Teams)</option>
                                            <option value="INDIVIDUAL">Individual (Students only)</option>
                                            <option value="TEAM">Team (Teams only)</option>
                                        </select>
                                    </div>
                                </div>

                                <div className="form-group">
                                    <label>Icon</label>
                                    <div className="icon-selector">
                                        {BADGE_ICONS.map(({ name, label }) => (
                                            <button
                                                key={name}
                                                type="button"
                                                className={`icon-option ${formData.icon === name ? 'selected' : ''}`}
                                                onClick={() => setFormData({ ...formData, icon: name })}
                                                title={label}
                                            >
                                                <span className="emoji-icon">{name}</span>
                                            </button>
                                        ))}
                                    </div>
                                </div>

                                <div className="form-group">
                                    <label>Color</label>
                                    <input
                                        type="color"
                                        value={formData.color}
                                        onChange={(e) => setFormData({ ...formData, color: e.target.value })}
                                        className="color-input"
                                    />
                                </div>

                                <div className="form-actions">
                                    <button
                                        type="button"
                                        className="cancel-btn"
                                        onClick={() => setShowModal(false)}
                                    >
                                        Cancel
                                    </button>
                                    <button
                                        type="submit"
                                        className="submit-btn"
                                        disabled={submitting}
                                    >
                                        {submitting ? (
                                            <>
                                                <Loader2 className="spinner" size={18} />
                                                Saving...
                                            </>
                                        ) : (
                                            <>
                                                <Check size={18} />
                                                {editingBadge ? 'Update Badge' : 'Create Badge'}
                                            </>
                                        )}
                                    </button>
                                </div>
                            </form>
                        </motion.div>
                    </motion.div>
                )}
            </AnimatePresence>
        </div>
    );
};

export default BadgeManagement;
