/**
 * Team Detail Page Component
 * Displays team information, members, and achievements.
 * 
 * @author Joao
 * @author Bruna
 * @version 1.0.0
 */
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
    getTeamPoints,
    getCurrentUser
} from '../services/api'; // API service calls for fetching team data
import AwardBadgeModal from '../components/AwardBadgeModal'; // Modal for awarding badges/points
import './TeamDetail.css'; // Component-specific styling

const TeamDetail = () => {
    // Hooks for routing: getting teamId from URL and navigation
    const { teamId } = useParams();
    const navigate = useNavigate();

    // State variables to hold fetched team data and UI status
    const [team, setTeam] = useState(null);
    const [members, setMembers] = useState([]);
    const [achievements, setAchievements] = useState([]);
    const [totalPoints, setTotalPoints] = useState(0);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [awardModalOpen, setAwardModalOpen] = useState(false); // State for controlling the Award Badge Modal

    // Get current user data (e.g., role) for conditional rendering
    const currentUser = getCurrentUser();

    // useEffect to fetch data when the component mounts or teamId changes
    useEffect(() => {
        fetchData();
    }, [teamId]); // Dependency array includes teamId to refetch if URL param changes

    // Asynchronous function to fetch all necessary team data concurrently
    const fetchData = async () => {
        try {
            setLoading(true);
            setError(null);

            // Fetch team details, members, achievements, and points in parallel using Promise.all
            // Added .catch to prevent a single failed API call from stopping the entire component from rendering
            const [teamRes, membersRes, achievementsRes, pointsRes] = await Promise.all([
                getTeamById(teamId),
                getTeamMembers(teamId).catch(() => ({ data: [] })),
                getTeamAchievements(teamId).catch(() => ({ data: [] })),
                getTeamPoints(teamId).catch(() => ({ data: { points: 0 } }))
            ]);

            // Update state with fetched data, handling potential missing data (e.g., membersRes.data being null)
            setTeam(teamRes.data);
            setMembers(membersRes.data || []);
            setAchievements(achievementsRes.data || []);
            setTotalPoints(pointsRes.data?.points || 0);
        } catch (err) {
            console.error('Error fetching team data:', err);
            setError('Failed to load team data. Please try again.');
        } finally {
            setLoading(false); // Ensure loading state is turned off regardless of success or failure
        }
    };

    // Handler for closing the Award Badge Modal
    const handleAwardModalClose = (success) => {
        setAwardModalOpen(false);
        // If an award was successfully given, refetch data to update points/achievements
        if (success) {
            fetchData();
        }
    };

    // Function to map backend role string to display info (label, icon, color)
    const getRoleInfo = (role) => {
        const roles = {
            SCRUM_MASTER: { label: 'Scrum Master', icon: Crown, color: '#f59e0b' },
            PRODUCT_OWNER: { label: 'Product Owner', icon: Star, color: '#6366f1' },
            DEVELOPER: { label: 'Developer', icon: Code, color: '#64748b' }
        };
        // Default to DEVELOPER if role is not recognized
        return roles[role] || roles.DEVELOPER;
    };

    // Function to determine the best display name for a member
    const getMemberName = (member) => {
        return member.userName || member.name || member.userEmail || 'Unknown';
    };

    // Function to get the member's email
    const getMemberEmail = (member) => {
        return member.userEmail || member.email || '';
    };

    // Display loading state while data is being fetched
    if (loading) {
        return (
            <div className="team-detail-loading">
                <Loader2 className="spinner" size={48} />
                <p>Loading team data...</p>
            </div>
        );
    }

    // Display error state if data fetching failed
    if (error) {
        return (
            <div className="team-detail-error">
                <AlertCircle size={48} />
                <p>{error}</p>
                <button onClick={fetchData} className="retry-btn">Retry</button>
            </div>
        );
    }

    // Main component rendering
    return (
        <div className="team-detail">
            {/* Header */}
            <header className="team-header">
                {/* Back button */}
                <button className="back-btn" onClick={() => navigate(-1)}>
                    <ArrowLeft size={20} />
                    <span>Back</span>
                </button>

                <div className="team-title-section">
                    <div className="team-icon-large">
                        <Users size={32} />
                    </div>
                    <div className="team-title-info">
                        {/* Team Name */}
                        <h1>{team?.name}</h1>
                        {/* Summary Stats in the header */}
                        <p className="team-meta">
                            <span><Users size={16} /> {members.length} Members</span>
                            <span><Award size={16} /> {achievements.length} Achievements</span>
                            <span><TrendingUp size={16} /> {totalPoints} Points</span>
                        </p>
                    </div>
                </div>

                {/* Button to open the Award Badge Modal - visible only to TEACHER role */}
                <button
                    className="award-team-btn"
                    onClick={() => setAwardModalOpen(true)}
                    // Conditional display based on current user role
                    style={{ display: currentUser?.role === 'TEACHER' ? 'flex' : 'none' }}
                >
                    <Gift size={20} />
                    Award Badge to Team
                </button>
            </header>

            {/* Stats Cards - Detailed view of key metrics */}
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
                        {/* Assumes team object has a projectCount field */}
                        <span className="stat-value">{team?.projectCount || 0}</span>
                        <span className="stat-label">Projects</span>
                    </div>
                </div>
            </div>

            {/* Content Grid for Members and Achievements */}
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
                                // Get role information using the helper function
                                const roleInfo = getRoleInfo(member.role);
                                const RoleIcon = roleInfo.icon;
                                return (
                                    // Individual member card
                                    <div key={member.id} className="member-card">
                                        {/* Member Avatar (simple initial) */}
                                        <div className="member-avatar">
                                            {getMemberName(member).charAt(0).toUpperCase()}
                                        </div>
                                        <div className="member-info">
                                            <span className="member-name">{getMemberName(member)}</span>
                                            <span className="member-email">{getMemberEmail(member)}</span>
                                        </div>
                                        {/* Member Role Display */}
                                        <div
                                            className="member-role"
                                            style={{ color: roleInfo.color }} // Apply role-specific color
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
                                // Individual achievement card
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

            {/* Award Badge Modal - Reusable component for giving awards */}
            <AwardBadgeModal
                isOpen={awardModalOpen}
                onClose={handleAwardModalClose}
                recipient={team} // Pass the team object as the recipient
                recipientType="team"
                projectId={team?.projectId} // Pass projectId if needed for context in the modal
                existingAchievements={achievements} // Optional: for display or checking duplicates
            />
        </div>
    );
};

export default TeamDetail;