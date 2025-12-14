// React and hooks
import React, { useState, useEffect } from 'react';

// Framer Motion for animations
import { motion, AnimatePresence } from 'framer-motion';

// Icons from lucide-react
import {
    X,
    Award,
    Gift,
    AlertCircle,
    Loader2
} from 'lucide-react';

// API service functions
import {
    getActiveBadgesByRecipientType,
    createAchievement,
    getCurrentUser
} from '../services/api';

// Component-specific styles
import './AwardBadgeModal.css';

/**
 * AwardBadgeModal
 * ----------------
 * Modal used by teachers to award badges to students or teams
 */
const AwardBadgeModal = ({
    isOpen,                    // Controls modal visibility
    onClose,                   // Callback when modal closes
    recipient,                 // Student or team receiving badge
    recipientType = 'user',    // 'user' or 'team'
    projectId,                 // Related project ID
    existingAchievements = []  // Already-awarded badges
}) => {

    /* =========================
       Component State
       ========================= */

    const [badges, setBadges] = useState([]);          // Available badges
    const [selectedBadge, setSelectedBadge] = useState(''); // Selected badge ID
    const [reason, setReason] = useState('');          // Reason for awarding
    const [loading, setLoading] = useState(false);     // Badge loading state
    const [submitting, setSubmitting] = useState(false); // Submission state
    const [error, setError] = useState(null);          // Error message
    const [success, setSuccess] = useState(false);     // Success state

    /* =========================
       Effect: Load badges when modal opens
       ========================= */

    useEffect(() => {
        if (isOpen) {
            fetchBadges();         // Load available badges
            setSelectedBadge('');  // Reset selected badge
            setReason('');         // Reset reason
            setError(null);        // Clear errors
            setSuccess(false);     // Reset success state
        }

        // existingAchievements intentionally excluded to avoid re-fetch loops
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isOpen, recipientType]);

    /* =========================
       Fetch available badges
       ========================= */

    const fetchBadges = async () => {
        try {
            setLoading(true);

            // Convert frontend recipient type to backend enum
            // user -> INDIVIDUAL, team -> TEAM
            const backendRecipientType =
                recipientType === 'user' ? 'INDIVIDUAL' : 'TEAM';

            const response =
                await getActiveBadgesByRecipientType(backendRecipientType);

            // Extract badge IDs already awarded to this recipient
            const existingBadgeIds = existingAchievements.map(
                a => a.badgeId || a.badge?.id
            );

            // Remove badges that were already awarded
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

    /* =========================
       Handle badge award submission
       ========================= */

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);

        // Validation
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

            // Build payload for achievement creation
            const achievementData = {
                badgeId: parseInt(selectedBadge),
                reason: reason.trim(),
                projectId: projectId,
                awardedByUserId: currentUser?.id,

                // Conditional payload based on recipient type
                ...(recipientType === 'user'
                    ? { awardedToUserId: recipient.studentId || recipient.id }
                    : { awardedToTeamId: recipient.id })
            };

            // API call to award badge
            await createAchievement(achievementData);

            setSuccess(true);

            // Close modal after short delay
            setTimeout(() => {
                onClose(true); // true indicates success
            }, 1500);

        } catch (err) {
            console.error('Error awarding badge:', err);
            setError(
                err.response?.data?.message || 'Failed to award badge.'
            );
        } finally {
            setSubmitting(false);
        }
    };

    // Do not render modal if closed
    if (!isOpen) return null;

    // Determine recipient display name
    const recipientName =
        recipientType === 'user'
            ? (recipient.studentName || recipient.name || 'Student')
            : (recipient.name || 'Team');

    /* =========================
       Render
       ========================= */

    return (
        <AnimatePresence>
            {/* Modal overlay */}
            <motion.div
                className="award-modal-overlay"
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                exit={{ opacity: 0 }}
                onClick={() => onClose(false)}
            >
                {/* Modal content */}
                <motion.div
                    className="award-modal-content"
                    initial={{ scale: 0.9, opacity: 0 }}
                    animate={{ scale: 1, opacity: 1 }}
                    exit={{ scale: 0.9, opacity: 0 }}
                    onClick={e => e.stopPropagation()}
                >
                    {/* Close button */}
                    <button
                        className="modal-close"
                        onClick={() => onClose(false)}
                    >
                        <X size={20} />
                    </button>

                    {/* Modal header */}
                    <div className="modal-header">
                        <div className="modal-icon">
                            <Gift size={24} />
                        </div>
                        <h2>Award Badge</h2>
                        <p>
                            Award a badge to <strong>{recipientName}</strong>
                        </p>
                    </div>

                    {/* Success state */}
                    {success ? (
                        <div className="success-message">
                            <Award size={48} />
                            <h3>Badge Awarded!</h3>
                            <p>The badge has been successfully awarded.</p>
                        </div>
                    ) : (
                        /* Award form */
                        <form onSubmit={handleSubmit}>

                            {/* Error message */}
                            {error && (
                                <div className="form-error">
                                    <AlertCircle size={16} />
                                    {error}
                                </div>
                            )}

                            {/* Badge selection */}
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
                                                className={`badge-option ${
                                                    selectedBadge == badge.id
                                                        ? 'selected'
                                                        : ''
                                                }`}
                                                onClick={() =>
                                                    setSelectedBadge(badge.id)
                                                }
                                            >
                                                <span className="badge-icon">
                                                    <Award size={20} />
                                                </span>
                                                <span className="badge-name">
                                                    {badge.name}
                                                </span>
                                                <span className="badge-points">
                                                    +{badge.points} pts
                                                </span>
                                            </button>
                                        ))}
                                    </div>
                                )}

                                {/* No badges available */}
                                {badges.length === 0 && !loading && (
                                    <p className="no-badges">
                                        No badges available. Create badges first.
                                    </p>
                                )}
                            </div>

                            {/* Reason input */}
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

                            {/* Action buttons */}
                            <div className="modal-actions">
                                <button
                                    type="button"
                                    className="cancel-btn"
                                    onClick={() => onClose(false)}
                                >
                                    Cancel
                                </button>

                                <button
                                    type="submit"
                                    className="submit-btn"
                                    disabled={
                                        submitting ||
                                        !selectedBadge ||
                                        !reason.trim()
                                    }
                                >
                                    {submitting ? (
                                        <>
                                            <Loader2
                                                size={16}
                                                className="spinner"
                                            />
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
