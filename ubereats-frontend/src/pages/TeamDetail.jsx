import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import {
    ArrowLeft,
    Users,
    Crown,
    Star,
    Award,
    TrendingUp,
    Target,
    AlertCircle,
    Loader2,
    Gift,
    Code
} from 'lucide-react';
import {
    getTeamById,
    getTeamMembers,
    getTeamAchievements,
    getTeamPoints
} from '../services/api';
import AwardBadgeModal from '../components/AwardBadgeModal';
import './TeamDetail.css';

const TeamDetail = () => {
    const { teamId } = useParams();
    const navigate = useNavigate();

    const [team, setTeam] = useState(null);
    const [members, setMembers] = useState([]);
    const [achievements, setAchievements] = useState([]);
    const [totalPoints, setTotalPoints] = useState(0);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [awardModalOpen, setAwardModalOpen] = useState(false);

    useEffect(() => {
        fetchData();
    }, [teamId]);

    const fetchData = async () => {
        try {
            setLoading(true);
            setError(null);

            const [teamRes, membersRes, achievementsRes, pointsRes] = await Promise.all([
                getTeamById(teamId),
                getTeamMembers(teamId).catch(() => ({ data: [] })),
                getTeamAchievements(teamId).catch(() => ({ data: [] })),
                getTeamPoints(teamId).catch(() => ({ data: { points: 0 } }))
            ]);

            setTeam(teamRes.data);
            setMembers(membersRes.data || []);
            setAchievements(achievementsRes.data || []);
            setTotalPoints(pointsRes.data?.points || 0);
        } catch (err) {
            console.error('Error fetching team data:', err);
            setError('Failed to load team data. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    const handleAwardModalClose = (success) => {
        setAwardModalOpen(false);
        if (success) {
            fetchData();
        }
    };

    // Get role display info - use proper field names from TeamMemberResponse
    const getRoleInfo = (role) => {
        const roles = {
            SCRUM_MASTER: { label: 'Scrum Master', icon: Crown, color: '#f59e0b' },
            PRODUCT_OWNER: { label: 'Product Owner', icon: Star, color: '#6366f1' },
            DEVELOPER: { label: 'Developer', icon: Code, color: '#64748b' }
        };
        return roles[role] || roles.DEVELOPER;
    };

    // Get member display name - handle both userName and name fields
    const getMemberName = (member) => {
        return member.userName || member.name || member.userEmail || 'Unknown';
    };

    // Get member email
    const getMemberEmail = (member) => {
        return member.userEmail || member.email || '';
    };

    if (loading) {
        return (
            <div className="team-detail-loading">
                <Loader2 className="spinner" size={48} />
                <p>Loading team data...</p>
            </div>
        );
    }

    if (error) {
        return (
            <div className="team-detail-error">
                <AlertCircle size={48} />
                <p>{error}</p>
                <button onClick={fetchData} className="retry-btn">Retry</button>
            </div>
        );
    }

    return (
        <div className="team-detail">
            {/* Header */}
            <header className="team-header">
                <button className="back-btn" onClick={() => navigate(-1)}>
                    <ArrowLeft size={20} />
                    <span>Back</span>
                </button>

                <div className="team-title-section">
                    <div className="team-icon-large">
                        <Users size={32} />
                    </div>
                    <div className="team-title-info">
                        <h1>{team?.name}</h1>
                        <p className="team-meta">
                            <span><Users size={16} /> {members.length} Members</span>
                            <span><Award size={16} /> {achievements.length} Achievements</span>
                            <span><TrendingUp size={16} /> {totalPoints} Points</span>
                        </p>
                    </div>
                </div>

                <button
                    className="award-team-btn"
                    onClick={() => setAwardModalOpen(true)}
                >
                    <Gift size={20} />
                    Award Badge to Team
                </button>
            </header>

            {/* Stats Cards */}
            <div className="team-stats-row">
                <div className="stat-card">
                    <div className="stat-icon"><TrendingUp size={24} /></div>
                    <div className="stat-info">
                        <span className="stat-value">{totalPoints}</span>
                        <span className="stat-label">Total Points</span>
                    </div>
                </div>
                <div className="stat-card">
                    <div className="stat-icon"><Award size={24} /></div>
                    <div className="stat-info">
                        <span className="stat-value">{achievements.length}</span>
                        <span className="stat-label">Badges Earned</span>
                    </div>
                </div>
                <div className="stat-card">
                    <div className="stat-icon"><Target size={24} /></div>
                    <div className="stat-info">
                        <span className="stat-value">{team?.projectCount || 0}</span>
                        <span className="stat-label">Projects</span>
                    </div>
                </div>
            </div>

            {/* Content Grid */}
            <div className="team-content-grid">
                {/* Members Section */}
                <section className="team-section members-section">
                    <h2><Users size={20} /> Team Members</h2>
                    {members.length === 0 ? (
                        <div className="empty-state-small">
                            No members assigned yet.
                        </div>
                    ) : (
                        <div className="members-list">
                            {members.map(member => {
                                const roleInfo = getRoleInfo(member.role);
                                const RoleIcon = roleInfo.icon;
                                return (
                                    <div key={member.id} className="member-card">
                                        <div className="member-avatar">
                                            {getMemberName(member).charAt(0).toUpperCase()}
                                        </div>
                                        <div className="member-info">
                                            <span className="member-name">{getMemberName(member)}</span>
                                            <span className="member-email">{getMemberEmail(member)}</span>
                                        </div>
                                        <div
                                            className="member-role"
                                            style={{ color: roleInfo.color }}
                                        >
                                            <RoleIcon size={14} />
                                            {roleInfo.label}
                                        </div>
                                    </div>
                                );
                            })}
                        </div>
                    )}
                </section>

                {/* Achievements Section */}
                <section className="team-section achievements-section">
                    <h2><Award size={20} /> Team Achievements</h2>
                    {achievements.length === 0 ? (
                        <div className="empty-state-small">
                            No achievements yet. Award badges to the team!
                        </div>
                    ) : (
                        <div className="achievements-list">
                            {achievements.map(achievement => (
                                <div key={achievement.id} className="achievement-card">
                                    <div className="achievement-badge">
                                        <Star size={20} />
                                    </div>
                                    <div className="achievement-content">
                                        <span className="achievement-name">{achievement.badgeName}</span>
                                        <span className="achievement-reason">{achievement.reason}</span>
                                        <span className="achievement-points">+{achievement.points || 0} pts</span>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </section>
            </div>

            {/* Award Badge Modal */}
            <AwardBadgeModal
                isOpen={awardModalOpen}
                onClose={handleAwardModalClose}
                recipient={team}
                recipientType="team"
                projectId={team?.projectId}
                existingAchievements={achievements}
            />
        </div>
    );
};

export default TeamDetail;
