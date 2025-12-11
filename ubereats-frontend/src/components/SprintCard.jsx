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

const SprintCard = ({ sprint, onViewBoard, onStart, onComplete }) => {
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

    const getStatusConfig = (status) => {
        const configs = {
            PLANNED: {
                label: 'Planned',
                icon: Clock,
                color: '#64748b',
                bg: '#f1f5f9',
                gradient: 'from-slate-400 to-slate-500'
            },
            ACTIVE: {
                label: 'In Progress',
                icon: PlayCircle,
                color: '#059669',
                bg: '#d1fae5',
                gradient: 'from-emerald-500 to-teal-500'
            },
            COMPLETED: {
                label: 'Completed',
                icon: CheckCircle2,
                color: '#7c3aed',
                bg: '#ede9fe',
                gradient: 'from-violet-500 to-purple-500'
            },
            CANCELLED: {
                label: 'Cancelled',
                icon: PauseCircle,
                color: '#ef4444',
                bg: '#fee2e2',
                gradient: 'from-red-400 to-rose-500'
            }
        };
        return configs[status] || configs.PLANNED;
    };

    const statusConfig = getStatusConfig(status);
    const StatusIcon = statusConfig.icon;

    const formatDate = (dateStr) => {
        if (!dateStr) return 'Not set';
        return new Date(dateStr).toLocaleDateString('en-US', {
            month: 'short',
            day: 'numeric'
        });
    };

    const progress = timeProgressPercentage || 0;

    return (
        <motion.div
            className="sprint-card"
            whileHover={{ y: -2 }}
        >
            {/* Left Accent */}
            <div
                className={`sprint-accent bg-gradient-to-b ${statusConfig.gradient}`}
            />

            {/* Content */}
            <div className="sprint-content">
                {/* Header */}
                <div className="sprint-header">
                    <div className="sprint-info">
                        <div className="sprint-number">
                            <Target size={16} />
                            Sprint {sprintNumber || id}
                        </div>
                        <h3 className="sprint-name">{name || displayName}</h3>
                        {goal && <p className="sprint-goal">{goal}</p>}
                    </div>

                    <div
                        className="sprint-status"
                        style={{ background: statusConfig.bg, color: statusConfig.color }}
                    >
                        <StatusIcon size={14} />
                        <span>{statusConfig.label}</span>
                    </div>
                </div>

                {/* Timeline */}
                <div className="sprint-timeline">
                    <div className="timeline-dates">
                        <span><Calendar size={14} /> {formatDate(startDate)}</span>
                        <span className="date-separator">→</span>
                        <span>{formatDate(endDate)}</span>
                    </div>

                    {status === 'ACTIVE' && (
                        <div className="progress-section">
                            <div className="progress-bar">
                                <div
                                    className="progress-fill"
                                    style={{ width: `${Math.min(progress, 100)}%` }}
                                />
                            </div>
                            <div className="progress-info">
                                <span>{Math.round(progress)}% time elapsed</span>
                                {daysRemaining !== null && (
                                    <span className={`days-remaining ${overdue ? 'overdue' : ''}`}>
                                        {overdue ? (
                                            <><AlertTriangle size={12} /> Overdue</>
                                        ) : (
                                            <><Clock size={12} /> {daysRemaining} days left</>
                                        )}
                                    </span>
                                )}
                            </div>
                        </div>
                    )}
                </div>

                {/* Actions */}
                <div className="sprint-actions">
                    {status === 'PLANNED' && onStart && (
                        <button className="action-btn start" onClick={onStart}>
                            <Zap size={16} />
                            Start Sprint
                        </button>
                    )}

                    {status === 'ACTIVE' && onComplete && (
                        <button className="action-btn complete" onClick={onComplete}>
                            <CheckCircle2 size={16} />
                            Complete
                        </button>
                    )}

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
