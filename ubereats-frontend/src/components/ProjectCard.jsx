import React from 'react';
import { motion } from 'framer-motion';
import {
    Calendar,
    Users,
    Clock,
    CheckCircle2,
    AlertTriangle,
    PlayCircle,
    Archive,
    ChevronRight
} from 'lucide-react';
import './ProjectCard.css';

const ProjectCard = ({ project, onClick }) => {
    const {
        id,
        name,
        description,
        startDate,
        endDate,
        status,
        courseName
    } = project;

    const getStatusConfig = (status) => {
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
        return configs[status] || configs.PLANNED;
    };

    const statusConfig = getStatusConfig(status);
    const StatusIcon = statusConfig.icon;

    const formatDate = (dateStr) => {
        if (!dateStr) return 'Not set';
        return new Date(dateStr).toLocaleDateString('en-US', {
            month: 'short',
            day: 'numeric',
            year: 'numeric'
        });
    };

    const calculateProgress = () => {
        if (!startDate || !endDate) return 0;
        const start = new Date(startDate);
        const end = new Date(endDate);
        const now = new Date();

        if (now < start) return 0;
        if (now > end) return 100;

        const total = end - start;
        const elapsed = now - start;
        return Math.round((elapsed / total) * 100);
    };

    const progress = calculateProgress();
    const daysRemaining = endDate ?
        Math.max(0, Math.ceil((new Date(endDate) - new Date()) / (1000 * 60 * 60 * 24))) :
        null;

    return (
        <motion.div
            className="project-card"
            whileHover={{ y: -4, boxShadow: '0 12px 24px rgba(0,0,0,0.1)' }}
            onClick={onClick}
        >
            {/* Status Badge */}
            <div
                className="project-status-badge"
                style={{ background: statusConfig.bg, color: statusConfig.color }}
            >
                <StatusIcon size={14} />
                <span>{statusConfig.label}</span>
            </div>

            {/* Content */}
            <div className="project-content">
                <h3 className="project-name">{name}</h3>
                {description && (
                    <p className="project-description">{description}</p>
                )}
            </div>

            {/* Timeline */}
            {status !== 'ARCHIVED' && (
                <div className="project-timeline">
                    <div className="timeline-dates">
                        <span><Calendar size={14} /> {formatDate(startDate)}</span>
                        <span>→</span>
                        <span>{formatDate(endDate)}</span>
                    </div>

                    {status === 'ACTIVE' && (
                        <div className="progress-section">
                            <div className="progress-bar">
                                <div
                                    className="progress-fill"
                                    style={{ width: `${progress}%` }}
                                />
                            </div>
                            <span className="progress-text">{progress}% timeline elapsed</span>
                        </div>
                    )}
                </div>
            )}

            {/* Footer */}
            <div className="project-footer">
                {daysRemaining !== null && status === 'ACTIVE' && (
                    <span className={`days-remaining ${daysRemaining < 7 ? 'warning' : ''}`}>
                        {daysRemaining === 0 ? (
                            <><AlertTriangle size={14} /> Due today</>
                        ) : daysRemaining < 0 ? (
                            <><AlertTriangle size={14} /> Overdue</>
                        ) : (
                            <><Clock size={14} /> {daysRemaining} days left</>
                        )}
                    </span>
                )}
                <button className="view-btn">
                    View Details <ChevronRight size={16} />
                </button>
            </div>
        </motion.div>
    );
};

export default ProjectCard;
