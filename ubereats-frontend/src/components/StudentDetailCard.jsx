import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import {
    User,
    Mail,
    Award,
    TrendingUp,
    ChevronDown,
    ChevronUp,
    Gift
} from 'lucide-react';
import { getUserAchievements, getStudentDashboard } from '../services/api';
import './StudentDetailCard.css';

const StudentDetailCard = ({ student, onAwardBadge, showAwardButton = true }) => {
    const { id, studentId, studentName, studentEmail, enrolledAt } = student;
    const userId = studentId || id;

    const [expanded, setExpanded] = useState(false);
    const [achievements, setAchievements] = useState([]);
    const [stats, setStats] = useState(null);
    const [loading, setLoading] = useState(false);
    const [loaded, setLoaded] = useState(false);

    useEffect(() => {
        if (expanded && userId && !loaded) {
            fetchStudentDetails();
        }
    }, [expanded, userId]);

    const fetchStudentDetails = async () => {
        try {
            setLoading(true);
            const [achievementsRes, statsRes] = await Promise.all([
                getUserAchievements(userId).catch(() => ({ data: [] })),
                getStudentDashboard(userId).catch(() => ({ data: null }))
            ]);
            setAchievements(achievementsRes.data || []);
            setStats(statsRes.data);
            setLoaded(true);
        } catch (err) {
            console.error('Error fetching student details:', err);
        } finally {
            setLoading(false);
        }
    };

    const formatDate = (dateStr) => {
        if (!dateStr) return 'Unknown';
        return new Date(dateStr).toLocaleDateString('en-US', {
            month: 'short',
            day: 'numeric',
            year: 'numeric'
        });
    };

    return (
        <div className={`student-card ${expanded ? 'expanded' : ''}`}>
            {/* Main Row - Always Visible */}
            <div className="student-main" onClick={() => setExpanded(!expanded)}>
                <div className="student-avatar">
                    <User size={20} />
                </div>

                <div className="student-info">
                    <h4 className="student-name">{studentName || 'Unknown Student'}</h4>
                    <span className="student-email">
                        <Mail size={12} />
                        {studentEmail}
                    </span>
                </div>

                <div className="student-stats-mini">
                    {(stats || loaded) && (
                        <>
                            <span className="stat-badge score">
                                <TrendingUp size={14} />
                                {stats?.globalScore?.toFixed(0) || 0}
                            </span>
                            <span className="stat-badge badges">
                                <Award size={14} />
                                {stats?.totalBadges || achievements.length || 0}
                            </span>
                        </>
                    )}
                </div>

                <div className="student-actions">
                    {showAwardButton && (
                        <button
                            className="award-btn"
                            onClick={(e) => { e.stopPropagation(); onAwardBadge?.(student); }}
                            title="Award Badge"
                        >
                            <Gift size={16} />
                        </button>
                    )}
                    <button className="expand-btn">
                        {expanded ? <ChevronUp size={20} /> : <ChevronDown size={20} />}
                    </button>
                </div>
            </div>

            {/* Expanded Details - No animation to avoid squeeze */}
            {expanded && (
                <div className="student-details">
                    <div className="details-section">
                        <h5>Enrollment</h5>
                        <p>Enrolled on {formatDate(enrolledAt)}</p>
                    </div>

                    {loading ? (
                        <div className="details-loading">Loading...</div>
                    ) : (
                        <>
                            {stats && (
                                <div className="details-section stats-grid">
                                    <div className="detail-stat">
                                        <span className="stat-label">Global Score</span>
                                        <span className="stat-value">{stats.globalScore?.toFixed(1) || 0}</span>
                                    </div>
                                    <div className="detail-stat">
                                        <span className="stat-label">Stories Done</span>
                                        <span className="stat-value">{stats.storiesCompleted || 0}</span>
                                    </div>
                                    <div className="detail-stat">
                                        <span className="stat-label">Story Points</span>
                                        <span className="stat-value">{stats.totalStoryPoints || 0}</span>
                                    </div>
                                    <div className="detail-stat">
                                        <span className="stat-label">Badges</span>
                                        <span className="stat-value">{stats.totalBadges || 0}</span>
                                    </div>
                                </div>
                            )}

                            {achievements.length > 0 && (
                                <div className="details-section">
                                    <h5>Recent Achievements</h5>
                                    <div className="achievements-list">
                                        {achievements.slice(0, 5).map(achievement => (
                                            <div key={achievement.id} className="achievement-item">
                                                <Award size={14} className="achievement-icon" />
                                                <span className="achievement-name">{achievement.badgeName}</span>
                                                <span className="achievement-reason">{achievement.reason}</span>
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            )}

                            {achievements.length === 0 && !stats && (
                                <div className="details-empty">
                                    No achievements or stats available yet.
                                </div>
                            )}
                        </>
                    )}
                </div>
            )}
        </div>
    );
};

export default StudentDetailCard;
