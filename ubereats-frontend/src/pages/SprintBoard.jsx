import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import {
    ArrowLeft,
    Plus,
    Target,
    CheckCircle2,
    AlertCircle,
    Loader2,
    PlayCircle,
    ListTodo,
    Users,
    Lock
} from 'lucide-react';
import {
    getSprintById,
    getProjectById,
    getTeamByProject,
    getTeamMembers,
    getUserStoriesBySprint,
    createUserStory,
    deleteUserStory,
    assignUserStory,
    unassignUserStory,
    moveToNextStatus,
    moveToPreviousStatus,
    getSprintStats,
    getCurrentUser,
    startSprint,
    completeSprint,
    cancelSprint
} from '../services/api';
import UserStoryCard from '../components/UserStoryCard';
import './SprintBoard.css';

const SprintBoard = () => {
    const { projectId, sprintId } = useParams();
    const navigate = useNavigate();

    const [sprint, setSprint] = useState(null);
    const [project, setProject] = useState(null);
    const [team, setTeam] = useState(null);
    const [teamMembers, setTeamMembers] = useState([]);
    const [stories, setStories] = useState([]);
    const [stats, setStats] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showCreateModal, setShowCreateModal] = useState(false);
    const [createError, setCreateError] = useState(null);
    const [currentUser, setCurrentUser] = useState(null);
    const [sprintActionLoading, setSprintActionLoading] = useState(false);

    // Create Story Form State - includes required teamId and createdByUserId
    const [newStory, setNewStory] = useState({
        title: '',
        description: '',
        storyPoints: 3,
        priority: 'MEDIUM',
        teamId: ''
    });

    useEffect(() => {
        setCurrentUser(getCurrentUser());
        fetchData();
    }, [sprintId, projectId]);

    const fetchData = async () => {
        try {
            setLoading(true);
            setError(null);

            const [sprintRes, projectRes, teamRes, storiesRes, statsRes] = await Promise.all([
                getSprintById(sprintId),
                getProjectById(projectId),
                getTeamByProject(projectId).catch(() => ({ data: null })),
                getUserStoriesBySprint(sprintId).catch(() => ({ data: [] })),
                getSprintStats(sprintId).catch(() => ({ data: null }))
            ]);

            setSprint(sprintRes.data);
            setProject(projectRes.data);
            setTeam(teamRes.data);
            setStories(storiesRes.data || []);
            setStats(statsRes.data);

            // Set team ID and fetch team members if team exists
            if (teamRes.data?.id) {
                console.log('Team found with ID:', teamRes.data.id);
                setNewStory(prev => ({ ...prev, teamId: teamRes.data.id }));
                // Fetch team members for assignment dropdown
                try {
                    console.log('Fetching team members for team:', teamRes.data.id);
                    const membersRes = await getTeamMembers(teamRes.data.id);
                    console.log('Team members API response:', membersRes);
                    console.log('Team members data:', membersRes.data);
                    setTeamMembers(membersRes.data || []);
                } catch (err) {
                    console.error('Error fetching team members:', err);
                    setTeamMembers([]);
                }
            } else {
                console.log('No team found. teamRes.data:', teamRes.data);
            }
        } catch (err) {
            console.error('Error fetching sprint data:', err);
            setError('Failed to load sprint data. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    // Assignment handlers
    const handleAssign = async (storyId, userId) => {
        console.log('handleAssign called with storyId:', storyId, 'userId:', userId);
        try {
            const response = await assignUserStory(storyId, userId);
            console.log('Assignment response:', response);
            console.log('Refreshing data after assignment...');
            await fetchData();
            console.log('Data refreshed successfully');
        } catch (err) {
            console.error('Error assigning story:', err);
            console.error('Error details:', err.response?.data);
            alert('Failed to assign user story: ' + (err.response?.data?.message || err.message));
        }
    };

    const handleUnassign = async (storyId) => {
        console.log('handleUnassign called with storyId:', storyId);
        try {
            await unassignUserStory(storyId);
            console.log('Refreshing data after unassign...');
            await fetchData();
            console.log('Data refreshed successfully');
        } catch (err) {
            console.error('Error unassigning story:', err);
            alert('Failed to unassign user story: ' + (err.response?.data?.message || err.message));
        }
    };

    const handleDelete = async (storyId) => {
        console.log('handleDelete called with storyId:', storyId);
        try {
            await deleteUserStory(storyId);
            console.log('Story deleted, refreshing data...');
            await fetchData();
        } catch (err) {
            console.error('Error deleting story:', err);
            alert('Failed to delete user story: ' + (err.response?.data?.message || err.message));
        }
    };

    // Sprint control handlers
    const handleStartSprint = async () => {
        setSprintActionLoading(true);
        try {
            await startSprint(sprintId);
            fetchData();
        } catch (err) {
            console.error('Error starting sprint:', err);
        } finally {
            setSprintActionLoading(false);
        }
    };

    const handleCompleteSprint = async () => {
        setSprintActionLoading(true);
        try {
            await completeSprint(sprintId);
            fetchData();
        } catch (err) {
            console.error('Error completing sprint:', err);
        } finally {
            setSprintActionLoading(false);
        }
    };

    const handleCancelSprint = async () => {
        setSprintActionLoading(true);
        try {
            await cancelSprint(sprintId);
            fetchData();
        } catch (err) {
            console.error('Error cancelling sprint:', err);
        } finally {
            setSprintActionLoading(false);
        }
    };

    const handleCreateStory = async (e) => {
        e.preventDefault();
        setCreateError(null);

        // Get current user
        const currentUser = getCurrentUser();
        if (!currentUser?.id) {
            setCreateError('You must be logged in to create stories.');
            return;
        }

        // Only students can create user stories
        if (currentUser.role !== 'STUDENT') {
            setCreateError('Only students can create user stories.');
            return;
        }

        if (!newStory.teamId) {
            setCreateError('Please select a team.');
            return;
        }

        try {
            const storyData = {
                title: newStory.title,
                description: newStory.description,
                storyPoints: parseInt(newStory.storyPoints),
                priority: newStory.priority,
                sprintId: parseInt(sprintId),
                teamId: parseInt(newStory.teamId),
                createdByUserId: currentUser.id
            };

            await createUserStory(storyData);
            setShowCreateModal(false);
            setNewStory({ title: '', description: '', storyPoints: 3, priority: 'MEDIUM', teamId: team?.id || '' });
            setCreateError(null);
            fetchData();
        } catch (err) {
            console.error('Error creating story:', err);
            setCreateError(err.response?.data?.message || 'Failed to create story. Please try again.');
        }
    };

    const handleMoveNext = async (storyId) => {
        try {
            await moveToNextStatus(storyId);
            fetchData();
        } catch (err) {
            console.error('Error moving story:', err);
        }
    };

    const handleMovePrev = async (storyId) => {
        try {
            await moveToPreviousStatus(storyId);
            fetchData();
        } catch (err) {
            console.error('Error moving story:', err);
        }
    };

    const getStoriesByStatus = (status) => {
        return stories.filter(s => s.status === status);
    };

    const columns = [
        { id: 'TODO', label: 'To Do', icon: ListTodo, color: '#64748b' },
        { id: 'IN_PROGRESS', label: 'In Progress', icon: PlayCircle, color: '#f59e0b' },
        { id: 'IN_REVIEW', label: 'In Review', icon: AlertCircle, color: '#8b5cf6' },
        { id: 'DONE', label: 'Done', icon: CheckCircle2, color: '#10b981' }
    ];

    if (loading) {
        return (
            <div className="sprint-board-loading">
                <Loader2 className="spinner" size={48} />
                <p>Loading sprint board...</p>
            </div>
        );
    }

    if (error) {
        return (
            <div className="sprint-board-error">
                <AlertCircle size={48} />
                <p>{error}</p>
                <button onClick={fetchData} className="retry-btn">Retry</button>
            </div>
        );
    }

    const progress = stats?.completionPercentage || 0;

    return (
        <div className="sprint-board">
            {/* Header */}
            <header className="sprint-board-header">
                <button className="back-btn" onClick={() => navigate(-1)}>
                    <ArrowLeft size={20} />
                    <span>Back to Project</span>
                </button>

                <div className="sprint-title-section">
                    <div className="sprint-badges">
                        <span className="project-badge">{project?.name}</span>
                        <span className={`status-badge ${sprint?.status?.toLowerCase()}`}>
                            {sprint?.status}
                        </span>
                    </div>
                    <h1>
                        <Target size={28} />
                        {sprint?.name || sprint?.displayName || `Sprint ${sprint?.sprintNumber}`}
                    </h1>
                    {sprint?.goal && (
                        <p className="sprint-goal">{sprint.goal}</p>
                    )}
                </div>

                {/* Progress Section */}
                <div className="sprint-progress">
                    <div className="progress-header">
                        <span>Sprint Progress</span>
                        <span className="progress-value">{Math.round(progress)}%</span>
                    </div>
                    <div className="progress-bar">
                        <div
                            className="progress-fill"
                            style={{ width: `${progress}%` }}
                        />
                    </div>
                    <div className="progress-stats">
                        <span>{stats?.completedStoryPoints || 0} / {stats?.totalStoryPoints || 0} story points</span>
                    </div>
                </div>
            </header>

            {/* Board Toolbar */}
            <div className="board-toolbar">
                <div className="toolbar-info">
                    <Users size={16} />
                    <span>{team ? '1 team' : 'No team'}</span>
                </div>

                {/* Sprint Control Buttons */}
                <div className="sprint-controls">
                    {sprint?.status === 'PLANNED' && (
                        <button
                            className="sprint-action-btn start"
                            onClick={handleStartSprint}
                            disabled={sprintActionLoading}
                        >
                            <PlayCircle size={16} />
                            {sprintActionLoading ? 'Starting...' : 'Start Sprint'}
                        </button>
                    )}
                    {sprint?.status === 'IN_PROGRESS' && (
                        <button
                            className="sprint-action-btn complete"
                            onClick={handleCompleteSprint}
                            disabled={sprintActionLoading}
                        >
                            <CheckCircle2 size={16} />
                            {sprintActionLoading ? 'Completing...' : 'Complete Sprint'}
                        </button>
                    )}
                    {(sprint?.status === 'PLANNED' || sprint?.status === 'IN_PROGRESS') && (
                        <button
                            className="sprint-action-btn cancel"
                            onClick={handleCancelSprint}
                            disabled={sprintActionLoading}
                        >
                            <AlertCircle size={16} />
                            Cancel
                        </button>
                    )}
                    {(sprint?.status === 'COMPLETED' || sprint?.status === 'CANCELLED') && (
                        <span className="sprint-locked-badge">
                            <Lock size={14} />
                            {sprint?.status === 'COMPLETED' ? 'Sprint Completed' : 'Sprint Cancelled'}
                        </span>
                    )}
                </div>

                {currentUser?.role === 'STUDENT' && sprint?.status !== 'COMPLETED' && sprint?.status !== 'CANCELLED' && (
                    <button
                        className="create-story-btn"
                        onClick={() => setShowCreateModal(true)}
                        disabled={!team}
                        title={!team ? 'No team available' : 'Add User Story'}
                    >
                        <Plus size={20} />
                        Add User Story
                    </button>
                )}
            </div>

            {/* Kanban Board */}
            <main className="kanban-board">
                {columns.map(column => (
                    <div key={column.id} className="kanban-column">
                        <div className="column-header" style={{ '--column-color': column.color }}>
                            <column.icon size={18} />
                            <span>{column.label}</span>
                            <span className="column-count">{getStoriesByStatus(column.id).length}</span>
                        </div>

                        <div className="column-content">
                            <AnimatePresence>
                                {getStoriesByStatus(column.id).map(story => {
                                    const isSprintFrozen = sprint?.status === 'COMPLETED' || sprint?.status === 'CANCELLED';
                                    return (
                                        <UserStoryCard
                                            key={story.id}
                                            story={story}
                                            teamMembers={isSprintFrozen ? [] : teamMembers}
                                            onAssign={isSprintFrozen ? null : (userId) => handleAssign(story.id, userId)}
                                            onUnassign={isSprintFrozen ? null : () => handleUnassign(story.id)}
                                            onMoveNext={isSprintFrozen || column.id === 'DONE' ? null : () => handleMoveNext(story.id)}
                                            onMovePrev={isSprintFrozen || column.id === 'TODO' || column.id === 'DONE' ? null : () => handleMovePrev(story.id)}
                                            onDelete={isSprintFrozen || story.status === 'DONE' ? null : () => handleDelete(story.id)}
                                        />
                                    );
                                })}
                            </AnimatePresence>

                            {getStoriesByStatus(column.id).length === 0 && (
                                <div className="column-empty">
                                    No stories
                                </div>
                            )}
                        </div>
                    </div>
                ))}
            </main>

            {/* Create Story Modal */}
            <AnimatePresence>
                {showCreateModal && (
                    <motion.div
                        className="modal-overlay"
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        exit={{ opacity: 0 }}
                        onClick={() => setShowCreateModal(false)}
                    >
                        <motion.div
                            className="modal-content"
                            initial={{ scale: 0.9, opacity: 0 }}
                            animate={{ scale: 1, opacity: 1 }}
                            exit={{ scale: 0.9, opacity: 0 }}
                            onClick={e => e.stopPropagation()}
                        >
                            <h2>Create User Story</h2>

                            {createError && (
                                <div className="form-error">
                                    <AlertCircle size={16} />
                                    {createError}
                                </div>
                            )}

                            <form onSubmit={handleCreateStory}>
                                <div className="form-group">
                                    <label>Team *</label>
                                    <input
                                        type="text"
                                        value={team?.name || 'No team assigned'}
                                        disabled
                                        style={{ background: '#f1f5f9', cursor: 'not-allowed' }}
                                    />
                                    <input type="hidden" value={newStory.teamId} />
                                </div>

                                <div className="form-group">
                                    <label>Title *</label>
                                    <input
                                        type="text"
                                        value={newStory.title}
                                        onChange={e => setNewStory({ ...newStory, title: e.target.value })}
                                        placeholder="As a user, I want to..."
                                        required
                                    />
                                </div>

                                <div className="form-group">
                                    <label>Description</label>
                                    <textarea
                                        value={newStory.description}
                                        onChange={e => setNewStory({ ...newStory, description: e.target.value })}
                                        placeholder="Acceptance criteria and details..."
                                        rows={3}
                                    />
                                </div>

                                <div className="form-row">
                                    <div className="form-group">
                                        <label>Story Points</label>
                                        <select
                                            value={newStory.storyPoints}
                                            onChange={e => setNewStory({ ...newStory, storyPoints: e.target.value })}
                                        >
                                            <option value="1">1</option>
                                            <option value="2">2</option>
                                            <option value="3">3</option>
                                            <option value="5">5</option>
                                            <option value="8">8</option>
                                            <option value="13">13</option>
                                        </select>
                                    </div>
                                    <div className="form-group">
                                        <label>Priority</label>
                                        <select
                                            value={newStory.priority}
                                            onChange={e => setNewStory({ ...newStory, priority: e.target.value })}
                                        >
                                            <option value="LOW">Low</option>
                                            <option value="MEDIUM">Medium</option>
                                            <option value="HIGH">High</option>
                                            <option value="CRITICAL">Critical</option>
                                        </select>
                                    </div>
                                </div>

                                <div className="modal-actions">
                                    <button type="button" className="cancel-btn" onClick={() => setShowCreateModal(false)}>
                                        Cancel
                                    </button>
                                    <button type="submit" className="submit-btn">
                                        Create Story
                                    </button>
                                </div>
                            </form>
                        </motion.div>
                    </motion.div>
                )}
            </AnimatePresence>
        </div>
    );
};

export default SprintBoard;
