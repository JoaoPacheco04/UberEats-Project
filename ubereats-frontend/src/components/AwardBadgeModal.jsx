import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {
    X,
    Award,
    Gift,
    AlertCircle,
    Loader2
} from 'lucide-react';
import { getActiveBadgesByRecipientType, createAchievement, getCurrentUser } from '../services/api';
import './AwardBadgeModal.css';

const AwardBadgeModal = ({ isOpen, onClose, recipient, recipientType = 'user', projectId, existingAchievements = [] }) => {
    const [badges, setBadges] = useState([]);
    const [selectedBadge, setSelectedBadge] = useState('');
    const [reason, setReason] = useState('');
    const [loading, setLoading] = useState(false);
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(false);

    useEffect(() => {
        if (isOpen) {
            fetchBadges();
            setSelectedBadge('');
            setReason('');
            setError(null);
            setSuccess(false);
        }
    }, [isOpen, recipientType, existingAchievements]);

    const fetchBadges = async () => {
        try {
            setLoading(true);
            // Map frontend recipientType to backend enum
            // 'user' -> 'INDIVIDUAL', 'team' -> 'TEAM'
            const backendRecipientType = recipientType === 'user' ? 'INDIVIDUAL' : 'TEAM';
            const response = await getActiveBadgesByRecipientType(backendRecipientType);

            // Filter out badges that have already been awarded to this recipient
            const existingBadgeIds = existingAchievements.map(a => a.badgeId || a.badge?.id);
            const filteredBadges = (response.data || []).filter(
                badge => !existingBadgeIds.includes(badge.id)
            );

            setBadges(filteredBadges);
        } catch (err) {
            console.error('Error fetching badges:', err);
            setError('Failed to load badges.');
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);

        if (!selectedBadge) {
            setError('Please select a badge.');
            return;
        }

        if (!reason.trim()) {
            setError('Please provide a reason.');
            return;
        }

        const currentUser = getCurrentUser();

        try {
            setSubmitting(true);

            const achievementData = {
                badgeId: parseInt(selectedBadge),
                reason: reason.trim(),
                projectId: projectId,
                awardedByUserId: currentUser?.id,
                ...(recipientType === 'user'
                    ? { awardedToUserId: recipient.studentId || recipient.id }
                    : { awardedToTeamId: recipient.id })
            };

            await createAchievement(achievementData);
            setSuccess(true);

            setTimeout(() => {
                onClose(true); // Pass true to indicate success
            }, 1500);

        } catch (err) {
            console.error('Error awarding badge:', err);
            setError(err.response?.data?.message || 'Failed to award badge.');
        } finally {
            setSubmitting(false);
        }
    };

    if (!isOpen) return null;

    const recipientName = recipientType === 'user'
        ? (recipient.studentName || recipient.name || 'Student')
        : (recipient.name || 'Team');

    return (
        <AnimatePresence>
            <motion.div
                className="award-modal-overlay"
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                exit={{ opacity: 0 }}
                onClick={() => onClose(false)}
            >
                <motion.div
                    className="award-modal-content"
                    initial={{ scale: 0.9, opacity: 0 }}
                    animate={{ scale: 1, opacity: 1 }}
                    exit={{ scale: 0.9, opacity: 0 }}
                    onClick={e => e.stopPropagation()}
                >
                    <button className="modal-close" onClick={() => onClose(false)}>
                        <X size={20} />
                    </button>

                    <div className="modal-header">
                        <div className="modal-icon">
                            <Gift size={24} />
                        </div>
                        <h2>Award Badge</h2>
                        <p>Award a badge to <strong>{recipientName}</strong></p>
                    </div>

                    {success ? (
                        <div className="success-message">
                            <Award size={48} />
                            <h3>Badge Awarded!</h3>
                            <p>The badge has been successfully awarded.</p>
                        </div>
                    ) : (
                        <form onSubmit={handleSubmit}>
                            {error && (
                                <div className="form-error">
                                    <AlertCircle size={16} />
                                    {error}
                                </div>
                            )}

                            <div className="form-group">
                                <label>Select Badge *</label>
                                {loading ? (
                                    <div className="loading-badges">
                                        <Loader2 size={20} className="spinner" />
                                        Loading badges...
                                    </div>
                                ) : (
                                    <div className="badge-grid">
                                        {badges.map(badge => (
                                            <button
                                                key={badge.id}
                                                type="button"
                                                className={`badge-option ${selectedBadge == badge.id ? 'selected' : ''}`}
                                                onClick={() => setSelectedBadge(badge.id)}
                                            >
                                                <span className="badge-icon">
                                                    <Award size={20} />
                                                </span>
                                                <span className="badge-name">{badge.name}</span>
                                                <span className="badge-points">+{badge.points} pts</span>
                                            </button>
                                        ))}
                                    </div>
                                )}
                                {badges.length === 0 && !loading && (
                                    <p className="no-badges">No badges available. Create badges first.</p>
                                )}
                            </div>

                            <div className="form-group">
                                <label>Reason *</label>
                                <textarea
                                    value={reason}
                                    onChange={e => setReason(e.target.value)}
                                    placeholder="Why are you awarding this badge?"
                                    rows={3}
                                    required
                                />
                            </div>

                            <div className="modal-actions">
                                <button type="button" className="cancel-btn" onClick={() => onClose(false)}>
                                    Cancel
                                </button>
                                <button
                                    type="submit"
                                    className="submit-btn"
                                    disabled={submitting || !selectedBadge || !reason.trim()}
                                >
                                    {submitting ? (
                                        <>
                                            <Loader2 size={16} className="spinner" />
                                            Awarding...
                                        </>
                                    ) : (
                                        <>
                                            <Gift size={16} />
                                            Award Badge
                                        </>
                                    )}
                                </button>
                            </div>
                        </form>
                    )}
                </motion.div>
            </motion.div>
        </AnimatePresence>
    );
};

export default AwardBadgeModal;
