import React from 'react';
import { motion } from 'framer-motion';
import {
    Calendar,
    Clock,
    Target,
    PlayCircle,
    CheckCircle2,
    AlertTriangle,
    PauseCircle,
    ChevronRight,
    Zap
} from 'lucide-react';
import './SprintCard.css';

// Card component for displaying sprint information
const SprintCard = ({ sprint, onViewBoard, onStart, onComplete }) => {
    // Destructure sprint props for easier access
    const {
        id,
        sprintNumber,
        name,
        goal,
        startDate,
        endDate,
        status,
        daysRemaining,
        timeProgressPercentage,
        overdue,
        displayName
    } = sprint;

    // Configuration object for different sprint statuses
    const getStatusConfig = (status) => {
        const configs = {
            PLANNED: {
                label: 'Planned',
                icon: Clock,
                color: '#64748b', // slate-500
                bg: '#f1f5f9', // slate-100
                gradient: 'from-slate-400 to-slate-500'
            },
            ACTIVE: {
                label: 'In Progress',
                icon: PlayCircle,
                color: '#059669', // emerald-600
                bg: '#d1fae5', // emerald-100
                gradient: 'from-emerald-500 to-teal-500'
            },
            COMPLETED: {
                label: 'Completed',
                icon: CheckCircle2,
                color: '#7c3aed', // violet-600
                bg: '#ede9fe', // violet-100
                gradient: 'from-violet-500 to-purple-500'
            },
            CANCELLED: {
                label: 'Cancelled',
                icon: PauseCircle,
                color: '#ef4444', // red-500
                bg: '#fee2e2', // red-100
                gradient: 'from-red-400 to-rose-500'
            }
        };
        // Return the config for the given status or default to PLANNED
        return configs[status] || configs.PLANNED;
    };

    // Get configuration for current sprint status
    const statusConfig = getStatusConfig(status);
    const StatusIcon = statusConfig.icon; // Dynamic icon based on status

    // Format date string to readable format
    const formatDate = (dateStr) => {
        if (!dateStr) return 'Not set'; // Handle empty dates
        return new Date(dateStr).toLocaleDateString('en-US', {
            month: 'short',
            day: 'numeric'
        });
    };

    // Calculate progress percentage, capped at 100%
    const progress = timeProgressPercentage || 0;

    return (
        // Card container with hover animation
        <motion.div
            className="sprint-card"
            whileHover={{ y: -2 }} // Lift effect on hover
        >
            {/* Colored accent bar on the left side */}
            <div
                className={`sprint-accent bg-gradient-to-b ${statusConfig.gradient}`}
            />

            {/* Main card content */}
            <div className="sprint-content">
                {/* Header section with sprint info and status */}
                <div className="sprint-header">
                    {/* Sprint metadata */}
                    <div className="sprint-info">
                        <div className="sprint-number">
                            <Target size={16} />
                            Sprint {sprintNumber || id} {/* Fallback to id if number not available */}
                        </div>
                        <h3 className="sprint-name">{name || displayName}</h3>
                        {/* Sprint goal - only shown if exists */}
                        {goal && <p className="sprint-goal">{goal}</p>}
                    </div>

                    {/* Status badge with dynamic styling */}
                    <div
                        className="sprint-status"
                        style={{ background: statusConfig.bg, color: statusConfig.color }}
                    >
                        <StatusIcon size={14} />
                        <span>{statusConfig.label}</span>
                    </div>
                </div>

                {/* Timeline and progress section */}
                <div className="sprint-timeline">
                    {/* Date range display */}
                    <div className="timeline-dates">
                        <span><Calendar size={14} /> {formatDate(startDate)}</span>
                        <span className="date-separator">→</span>
                        <span>{formatDate(endDate)}</span>
                    </div>

                    {/* Progress bar - only shown for active sprints */}
                    {status === 'ACTIVE' && (
                        <div className="progress-section">
                            <div className="progress-bar">
                                <div
                                    className="progress-fill"
                                    style={{ width: `${Math.min(progress, 100)}%` }} // Cap at 100%
                                />
                            </div>
                            <div className="progress-info">
                                <span>{Math.round(progress)}% time elapsed</span>
                                {/* Days remaining/overdue indicator */}
                                {daysRemaining !== null && (
                                    <span className={`days-remaining ${overdue ? 'overdue' : ''}`}>
                                        {overdue ? (
                                            // Overdue state
                                            <><AlertTriangle size={12} /> Overdue</>
                                        ) : (
                                            // Normal days remaining
                                            <><Clock size={12} /> {daysRemaining} days left</>
                                        )}
                                    </span>
                                )}
                            </div>
                        </div>
                    )}
                </div>

                {/* Action buttons at the bottom of the card */}
                <div className="sprint-actions">
                    {/* Start button - only shown for planned sprints */}
                    {status === 'PLANNED' && onStart && (
                        <button className="action-btn start" onClick={onStart}>
                            <Zap size={16} />
                            Start Sprint
                        </button>
                    )}

                    {/* Complete button - only shown for active sprints */}
                    {status === 'ACTIVE' && onComplete && (
                        <button className="action-btn complete" onClick={onComplete}>
                            <CheckCircle2 size={16} />
                            Complete
                        </button>
                    )}

                    {/* Always show view board button */}
                    <button className="action-btn view" onClick={onViewBoard}>
                        View Board
                        <ChevronRight size={16} />
                    </button>
                </div>
            </div>
        </motion.div>
    );
};

export default SprintCard;