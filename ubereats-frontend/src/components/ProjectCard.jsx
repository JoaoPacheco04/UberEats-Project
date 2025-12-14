import React from 'react';
// Import motion from framer-motion for smooth animations (like the hover effect)
import { motion } from 'framer-motion';
// Import icons from lucide-react for visual elements
import {
    Calendar,       // Used for start/end dates
    Users,          // (Not used in final render, but imported)
    Clock,          // Used for PLANNED status and days remaining
    CheckCircle2,   // Used for COMPLETED status
    AlertTriangle,  // Used for overdue/due-today warnings
    PlayCircle,     // Used for ACTIVE status
    Archive,        // Used for ARCHIVED status
    ChevronRight    // Used for the View Details button icon
} from 'lucide-react';
import './ProjectCard.css'; // Import the corresponding CSS file

// ProjectCard functional component receiving project data and an onClick handler
const ProjectCard = ({ project, onClick }) => {
    // Destructure project properties for easier access
    const {
        id,
        name,
        description,
        startDate,
        endDate,
        status,
        courseName // (Not used in final render, but available)
    } = project;

    // --- Utility Function: Get Status Configuration ---
    const getStatusConfig = (status) => {
        // Defines the visual styles (label, icon, colors) for each possible project status
        const configs = {
            PLANNED: {
                label: 'Planned',
                icon: Clock,
                color: '#64748b',
                bg: '#f1f5f9'
            },
            ACTIVE: {
                label: 'Active',
                icon: PlayCircle,
                color: '#059669',
                bg: '#d1fae5'
            },
            COMPLETED: {
                label: 'Completed',
                icon: CheckCircle2,
                color: '#7c3aed',
                bg: '#ede9fe'
            },
            ARCHIVED: {
                label: 'Archived',
                icon: Archive,
                color: '#94a3b8',
                bg: '#f8fafc'
            }
        };
        // Return the config for the current status, defaulting to PLANNED if status is unknown
        return configs[status] || configs.PLANNED;
    };

    const statusConfig = getStatusConfig(status);
    const StatusIcon = statusConfig.icon; // Get the specific Lucide icon component

    // --- Utility Function: Format Date String ---
    const formatDate = (dateStr) => {
        if (!dateStr) return 'Not set';
        // Formats the date into a readable string (e.g., 'Dec 15, 2025')
        return new Date(dateStr).toLocaleDateString('en-US', {
            month: 'short',
            day: 'numeric',
            year: 'numeric'
        });
    };

    // --- Utility Function: Calculate Timeline Progress ---
    const calculateProgress = () => {
        if (!startDate || !endDate) return 0;

        const start = new Date(startDate);
        const end = new Date(endDate);
        const now = new Date();

        // Handle cases outside the defined timeline
        if (now < start) return 0;
        if (now > end) return 100;

        // Calculate progress percentage
        const total = end - start;
        const elapsed = now - start;
        return Math.round((elapsed / total) * 100);
    };

    const progress = calculateProgress();

    // --- Calculate Days Remaining ---
    const daysRemaining = endDate ?
        // Calculate the difference in milliseconds, convert to days, and round up
        Math.max(0, Math.ceil((new Date(endDate) - new Date()) / (1000 * 60 * 60 * 24))) :
        null;

    // --- Render Component ---
    return (
        <motion.div
            className="project-card"
            // framer-motion animation: lift the card and increase shadow on hover
            whileHover={{ y: -4, boxShadow: '0 12px 24px rgba(0,0,0,0.1)' }}
            onClick={onClick} // Handle card click event
        >
            {/* Status Badge */}
            <div
                className="project-status-badge"
                // Apply dynamic background and text color based on status config
                style={{ background: statusConfig.bg, color: statusConfig.color }}
            >
                <StatusIcon size={14} />
                <span>{statusConfig.label}</span>
            </div>

            {/* Content */}
            <div className="project-content">
                <h3 className="project-name">{name}</h3>
                {description && (
                    // Description is rendered if it exists
                    <p className="project-description">{description}</p>
                )}
            </div>

            {/* Timeline Section - Hidden for ARCHIVED status */}
            {status !== 'ARCHIVED' && (
                <div className="project-timeline">
                    <div className="timeline-dates">
                        {/* Start Date */}
                        <span><Calendar size={14} /> {formatDate(startDate)}</span>
                        <span>→</span>
                        {/* End Date */}
                        <span>{formatDate(endDate)}</span>
                    </div>

                    {/* Progress Bar - Only visible for ACTIVE status */}
                    {status === 'ACTIVE' && (
                        <div className="progress-section">
                            <div className="progress-bar">
                                <div
                                    className="progress-fill"
                                    // Progress bar width is dynamically set by the calculated percentage
                                    style={{ width: `${progress}%` }}
                                />
                            </div>
                            <span className="progress-text">{progress}% timeline elapsed</span>
                        </div>
                    )}
                </div>
            )}

            {/* Footer Section */}
            <div className="project-footer">
                {/* Days Remaining Display - Only visible for ACTIVE status and if endDate is set */}
                {daysRemaining !== null && status === 'ACTIVE' && (
                    <span className={`days-remaining ${daysRemaining < 7 ? 'warning' : ''}`}>
                        {/* Logic to display appropriate warning/message based on days remaining */}
                        {daysRemaining === 0 ? (
                            <><AlertTriangle size={14} /> Due today</>
                        ) : daysRemaining < 0 ? (
                            <><AlertTriangle size={14} /> Overdue</>
                        ) : (
                            <><Clock size={14} /> {daysRemaining} days left</>
                        )}
                    </span>
                )}
                {/* View Details Button */}
                <button className="view-btn">
                    View Details <ChevronRight size={16} />
                </button>
            </div>
        </motion.div>
    );
};

export default ProjectCard;