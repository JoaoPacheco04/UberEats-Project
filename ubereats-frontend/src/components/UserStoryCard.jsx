/**
 * User Story Card Component
 * Displays user story details with assignment, priority, and movement controls.
 * 
 * @author Francisco
 * @author Joao
 * @version 1.0.0
 */
import React, { useState } from 'react';
import { motion } from 'framer-motion';
import {
    ChevronRight,
    ChevronLeft,
    User,
    UserPlus,
    UserMinus,
    Lock,
    Trash2
} from 'lucide-react';
import './UserStoryCard.css';

const UserStoryCard = ({ story, teamMembers = [], onAssign, onUnassign, onMoveNext, onMovePrev, onDelete }) => {
    const {
        id,
        title,
        description,
        storyPoints,
        priority,
        status,
        assignedUserName,
        assignedToUserId
    } = story;

    const [showAssignDropdown, setShowAssignDropdown] = useState(false);

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
    const isDone = status === 'DONE';

    const handleAssignClick = (e) => {
        e.stopPropagation();
        console.log('Assign click - teamMembers:', teamMembers, 'isDone:', isDone);
        setShowAssignDropdown(!showAssignDropdown);
    };

    const handleSelectMember = (e, memberId) => {
        e.stopPropagation();
        console.log('Selected member ID:', memberId, 'onAssign function:', !!onAssign);
        if (onAssign) {
            onAssign(memberId);
        } else {
            console.error('onAssign function is not provided!');
        }
        setShowAssignDropdown(false);
    };

    const handleUnassignClick = (e) => {
        e.stopPropagation();
        if (onUnassign) {
            onUnassign();
        }
        setShowAssignDropdown(false);
    };

    return (
        <motion.div
            className={`user-story-card ${isDone ? 'done' : ''}`}
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
                {isDone && (
                    <span className="story-locked" title="Completed - Cannot be moved back">
                        <Lock size={12} />
                    </span>
                )}
                {!isDone && onDelete && (
                    <button
                        className="delete-story-btn"
                        onClick={(e) => { e.stopPropagation(); if (confirm('Delete this user story?')) onDelete(); }}
                        title="Delete story"
                    >
                        <Trash2 size={14} />
                    </button>
                )}
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
                <div className="assignee-section">
                    {assignedUserName ? (
                        <div className="story-assignee assigned" onClick={handleAssignClick}>
                            <User size={14} />
                            <span>{assignedUserName}</span>
                            {!isDone && onUnassign && (
                                <button
                                    className="unassign-btn"
                                    onClick={handleUnassignClick}
                                    title="Unassign"
                                >
                                    <UserMinus size={12} />
                                </button>
                            )}
                        </div>
                    ) : (
                        <div
                            className="story-assignee unassigned"
                            onClick={!isDone ? handleAssignClick : undefined}
                        >
                            <User size={14} />
                            <span>Unassigned</span>
                            {!isDone && onAssign && teamMembers.length > 0 && (
                                <button className="assign-btn" title="Assign">
                                    <UserPlus size={12} />
                                </button>
                            )}
                        </div>
                    )}

                    {/* Assignment Dropdown */}
                    {showAssignDropdown && teamMembers.length > 0 && !isDone && (
                        <div className="assign-dropdown" onClick={(e) => e.stopPropagation()}>
                            <div className="assign-dropdown-header">Assign to:</div>
                            {teamMembers.map(member => (
                                <button
                                    key={member.userId || member.id}
                                    className="assign-option"
                                    onClick={(e) => handleSelectMember(e, member.userId || member.id)}
                                >
                                    <User size={14} />
                                    {member.userName || member.fullName || member.name || `User ${member.userId || member.id}`}
                                </button>
                            ))}
                            {assignedToUserId && (
                                <button
                                    className="assign-option unassign"
                                    onClick={handleUnassignClick}
                                >
                                    <UserMinus size={14} />
                                    Remove assignment
                                </button>
                            )}
                        </div>
                    )}
                </div>

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