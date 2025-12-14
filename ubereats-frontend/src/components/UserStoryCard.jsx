import React from 'react';
import { useNavigate } from 'react-router-dom'; // Hook for programmatic navigation
import { motion } from 'framer-motion'; // Library for animation and interaction (used here for hover effect)
import {
    Users,      // Icon for the team itself
    Crown,      // Icon for Scrum Master role
    Star,       // Icon for Product Owner role
    TrendingUp, // Icon for progress/trend
    BarChart3,  // Icon for performance rating
    ChevronRight // Icon for indicating clickable navigation
} from 'lucide-react'; // Importing icons
import './TeamCard.css'; // Importing component-specific styles

/**
 * TeamCard Component
 * Displays a summary card for a single project team, including key roles, stats, 
 * and associated projects. It is clickable to navigate to the team's detail page.
 *
 * @param {object} team - The team data object.
 * @param {function} [onClick] - Optional custom click handler. If not provided,
 * it navigates to the team detail page.
 */
const TeamCard = ({ team, onClick }) => {
    const navigate = useNavigate(); // Initialize navigate function from react-router-dom

    // Destructure properties from the team object
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

    // Convert progress and rating to numbers, defaulting to 0 if null/undefined
    const progress = currentProgress ? Number(currentProgress) : 0;
    const rating = performanceRating ? Number(performanceRating) : 0;

    /**
     * Handles the click event on the card.
     * Prioritizes the custom `onClick` prop if provided, otherwise navigates 
     * to the team details route.
     */
    const handleClick = () => {
        if (onClick) {
            onClick();
        } else {
            // Default navigation to the team's detail page using its ID
            navigate(`/teacher/teams/${id}`);
        }
    };

    return (
        // motion.div enables framer-motion animations
        <motion.div
            className="team-card"
            whileHover={{ y: -4 }} // Simple lift animation on hover
            onClick={handleClick}  // Attach the click handler to the entire card
        >
            
            {/* 1. Header Section: Team Name and Member Count */}
            <div className="team-header">
                {/* Team Icon */}
                <div className="team-icon">
                    <Users size={20} />
                </div>
                {/* Name and Member Count Display */}
                <div className="team-info">
                    <h3 className="team-name">{name}</h3>
                    <span className="member-count">{memberCount || 0} members</span>
                </div>
                {/* Navigation Indicator Arrow */}
                <ChevronRight size={20} className="team-arrow" />
            </div>

            {/* 2. Roles Section: Scrum Master and Product Owner */}
            <div className="team-roles">
                {/* Scrum Master Role */}
                <div className="role">
                    <Crown size={14} className="role-icon scrum-master" />
                    <div className="role-info">
                        <span className="role-label">Scrum Master</span>
                        <span className="role-name">{scrumMaster || 'Not assigned'}</span>
                    </div>
                </div>
                {/* Product Owner Role */}
                <div className="role">
                    <Star size={14} className="role-icon product-owner" />
                    <div className="role-info">
                        <span className="role-label">Product Owner</span>
                        <span className="role-name">{productOwner || 'Not assigned'}</span>
                    </div>
                </div>
            </div>

            {/* 3. Stats Section: Progress and Rating */}
            <div className="team-stats">
                {/* Progress Stat */}
                <div className="stat">
                    <TrendingUp size={14} />
                    <span className="stat-value">{progress.toFixed(0)}%</span> {/* Display as whole percentage */}
                    <span className="stat-label">Progress</span>
                </div>
                {/* Performance Rating Stat */}
                <div className="stat">
                    <BarChart3 size={14} />
                    <span className="stat-value">{rating.toFixed(1)}</span> {/* Display rating with one decimal */}
                    <span className="stat-label">Rating</span>
                </div>
            </div>

            {/* 4. Projects Section: Tags */}
            {projectNames && projectNames.length > 0 && ( // Only render if project names exist
                <div className="team-projects">
                    {/* Map and display the first two project names as tags */}
                    {projectNames.slice(0, 2).map((projectName, index) => (
                        <span key={index} className="project-tag">{projectName}</span>
                    ))}
                    {/* Display a counter for remaining projects if there are more than two */}
                    {projectNames.length > 2 && (
                        <span className="more-projects">+{projectNames.length - 2}</span>
                    )}
                </div>
            )}
        </motion.div>
    );
};

export default TeamCard;