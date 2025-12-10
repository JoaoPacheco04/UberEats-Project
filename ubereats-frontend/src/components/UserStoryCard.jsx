import React from 'react';
import { motion } from 'framer-motion';
import {
    ChevronRight,
    ChevronLeft,
    Tag,
    AlertCircle,
    User
} from 'lucide-react';
import './UserStoryCard.css';

const UserStoryCard = ({ story, onMoveNext, onMovePrev }) => {
    const {
        id,
        title,
        description,
        storyPoints,
        priority,
        status,
        assignedToName
    } = story;

    const getPriorityConfig = (priority) => {
        const configs = {
            LOW: { label: 'Low', color: '#64748b', bg: '#f1f5f9' },
            MEDIUM: { label: 'Medium', color: '#f59e0b', bg: '#fef3c7' },
            HIGH: { label: 'High', color: '#ef4444', bg: '#fee2e2' },
            CRITICAL: { label: 'Critical', color: '#dc2626', bg: '#fecaca' }
        };
        return configs[priority] || configs.MEDIUM;
    };

    const priorityConfig = getPriorityConfig(priority);

    return (
        <motion.div
            className="user-story-card"
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -10 }}
            layout
        >
            {/* Header */}
            <div className="story-header">
                <span className="story-points">{storyPoints || '?'}</span>
                <span
                    className="story-priority"
                    style={{ background: priorityConfig.bg, color: priorityConfig.color }}
                >
                    {priorityConfig.label}
                </span>
            </div>

            {/* Content */}
            <div className="story-content">
                <h4 className="story-title">{title}</h4>
                {description && (
                    <p className="story-description">{description}</p>
                )}
            </div>

            {/* Footer */}
            <div className="story-footer">
                {assignedToName ? (
                    <div className="story-assignee">
                        <User size={14} />
                        <span>{assignedToName}</span>
                    </div>
                ) : (
                    <div className="story-assignee unassigned">
                        <User size={14} />
                        <span>Unassigned</span>
                    </div>
                )}

                <div className="story-actions">
                    {onMovePrev && (
                        <button
                            className="move-btn prev"
                            onClick={(e) => { e.stopPropagation(); onMovePrev(); }}
                            title="Move to previous column"
                        >
                            <ChevronLeft size={16} />
                        </button>
                    )}
                    {onMoveNext && (
                        <button
                            className="move-btn next"
                            onClick={(e) => { e.stopPropagation(); onMoveNext(); }}
                            title="Move to next column"
                        >
                            <ChevronRight size={16} />
                        </button>
                    )}
                </div>
            </div>
        </motion.div>
    );
};

export default UserStoryCard;
