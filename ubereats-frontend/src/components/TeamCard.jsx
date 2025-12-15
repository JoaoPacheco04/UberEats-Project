/**
 * Team Card Component
 * Displays team information including members, roles, and progress stats.
 * 
 * @author Bruna
 * @author Ana
 * @version 1.0.0
 */
import React from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import {
    Users,
    Crown,
    Star,
    TrendingUp,
    BarChart3,
    ChevronRight
} from 'lucide-react';
import './TeamCard.css';

const TeamCard = ({ team, onClick }) => {
    const navigate = useNavigate();
    const {
        id,
        name,
        memberCount,
        scrumMaster,
        productOwner,
        currentProgress,
        performanceRating,
        projectNames
    } = team;

    const progress = currentProgress ? Number(currentProgress) : 0;
    const rating = performanceRating ? Number(performanceRating) : 0;

    const handleClick = () => {
        if (onClick) {
            onClick();
        } else {
            navigate(`/teacher/teams/${id}`);
        }
    };

    return (
        <motion.div
            className="team-card"
            whileHover={{ y: -4 }}
            onClick={handleClick}
        >
            {/* Header */}
            <div className="team-header">
                <div className="team-icon">
                    <Users size={20} />
                </div>
                <div className="team-info">
                    <h3 className="team-name">{name}</h3>
                    <span className="member-count">{memberCount || 0} members</span>
                </div>
                <ChevronRight size={20} className="team-arrow" />
            </div>

            {/* Roles */}
            <div className="team-roles">
                <div className="role">
                    <Crown size={14} className="role-icon scrum-master" />
                    <div className="role-info">
                        <span className="role-label">Scrum Master</span>
                        <span className="role-name">{scrumMaster || 'Not assigned'}</span>
                    </div>
                </div>
                <div className="role">
                    <Star size={14} className="role-icon product-owner" />
                    <div className="role-info">
                        <span className="role-label">Product Owner</span>
                        <span className="role-name">{productOwner || 'Not assigned'}</span>
                    </div>
                </div>
            </div>

            {/* Stats */}
            <div className="team-stats">
                <div className="stat">
                    <TrendingUp size={14} />
                    <span className="stat-value">{progress.toFixed(0)}%</span>
                    <span className="stat-label">Progress</span>
                </div>
                <div className="stat">
                    <BarChart3 size={14} />
                    <span className="stat-value">{rating.toFixed(1)}</span>
                    <span className="stat-label">Rating</span>
                </div>
            </div>

            {/* Projects */}
            {projectNames && projectNames.length > 0 && (
                <div className="team-projects">
                    {projectNames.slice(0, 2).map((projectName, index) => (
                        <span key={index} className="project-tag">{projectName}</span>
                    ))}
                    {projectNames.length > 2 && (
                        <span className="more-projects">+{projectNames.length - 2}</span>
                    )}
                </div>
            )}
        </motion.div>
    );
};

export default TeamCard;
