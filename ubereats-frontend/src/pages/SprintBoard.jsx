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
    Users
} from 'lucide-react';
import {
    getSprintById,
    getProjectById,
    getTeamsByProject,
    getUserStoriesBySprint,
    createUserStory,
    moveToNextStatus,
    moveToPreviousStatus,
    getSprintStats,
    getCurrentUser
} from '../services/api';
import UserStoryCard from '../components/UserStoryCard';
import './SprintBoard.css';

const SprintBoard = () => {
    const { projectId, sprintId } = useParams();
    const navigate = useNavigate();

    const [sprint, setSprint] = useState(null);
    const [project, setProject] = useState(null);
    const [teams, setTeams] = useState([]);
    const [stories, setStories] = useState([]);
    const [stats, setStats] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showCreateModal, setShowCreateModal] = useState(false);
    const [createError, setCreateError] = useState(null);
    const [currentUser, setCurrentUser] = useState(null);

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

            const [sprintRes, projectRes, teamsRes, storiesRes, statsRes] = await Promise.all([
                getSprintById(sprintId),
                getProjectById(projectId),
                getTeamsByProject(projectId),
                getUserStoriesBySprint(sprintId).catch(() => ({ data: [] })),
                getSprintStats(sprintId).catch(() => ({ data: null }))
            ]);

            setSprint(sprintRes.data);
            setProject(projectRes.data);
            setTeams(teamsRes.data || []);
            setStories(storiesRes.data || []);
            setStats(statsRes.data);

            // Set default team if only one team
            if (teamsRes.data?.length === 1) {
                setNewStory(prev => ({ ...prev, teamId: teamsRes.data[0].id }));
            }
        } catch (err) {
            console.error('Error fetching sprint data:', err);
            setError('Failed to load sprint data. Please try again.');
        } finally {
            setLoading(false);
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
            setNewStory({ title: '', description: '', storyPoints: 3, priority: 'MEDIUM', teamId: teams[0]?.id || '' });
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
                    <span>{teams.length} team{teams.length !== 1 ? 's' : ''}</span>
                </div>
                {currentUser?.role === 'STUDENT' && (
                    <button
                        className="create-story-btn"
                        onClick={() => setShowCreateModal(true)}
                        disabled={teams.length === 0}
                        title={teams.length === 0 ? 'No teams available' : 'Add User Story'}
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
                                {getStoriesByStatus(column.id).map(story => (
                                    <UserStoryCard
                                        key={story.id}
                                        story={story}
                                        onMoveNext={column.id !== 'DONE' ? () => handleMoveNext(story.id) : null}
                                        onMovePrev={column.id !== 'TODO' ? () => handleMovePrev(story.id) : null}
                                    />
                                ))}
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
                                    <select
                                        value={newStory.teamId}
                                        onChange={e => setNewStory({ ...newStory, teamId: e.target.value })}
                                        required
                                    >
                                        <option value="">Select a team...</option>
                                        {teams.map(team => (
                                            <option key={team.id} value={team.id}>
                                                {team.name}
                                            </option>
                                        ))}
                                    </select>
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
