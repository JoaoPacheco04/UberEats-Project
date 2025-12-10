import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import {
    ArrowLeft,
    Calendar,
    Users,
    Plus,
    CheckCircle2,
    AlertCircle,
    Loader2,
    Target,
    BarChart3
} from 'lucide-react';
import {
    getProjectById,
    getSprintsByProject,
    getTeamsByProject,
    createSprint,
    startSprint as startSprintApi,
    completeSprint as completeSprintApi
} from '../services/api';
import SprintCard from '../components/SprintCard';
import TeamCard from '../components/TeamCard';
import './ProjectDetail.css';

const ProjectDetail = () => {
    const { projectId } = useParams();
    const navigate = useNavigate();

    const [project, setProject] = useState(null);
    const [sprints, setSprints] = useState([]);
    const [teams, setTeams] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [createError, setCreateError] = useState(null);
    const [activeTab, setActiveTab] = useState('sprints');
    const [showCreateModal, setShowCreateModal] = useState(false);

    // Create Sprint Form State - includes sprintNumber
    const [newSprint, setNewSprint] = useState({
        sprintNumber: 1,
        name: '',
        goal: '',
        startDate: '',
        endDate: ''
    });

    useEffect(() => {
        fetchData();
    }, [projectId]);

    const fetchData = async () => {
        try {
            setLoading(true);
            setError(null);

            const [projectRes, sprintsRes, teamsRes] = await Promise.all([
                getProjectById(projectId),
                getSprintsByProject(projectId).catch(() => ({ data: [] })),
                getTeamsByProject(projectId).catch(() => ({ data: [] }))
            ]);

            setProject(projectRes.data);
            setSprints(sprintsRes.data || []);
            setTeams(teamsRes.data || []);

            // Auto-increment sprint number for next sprint
            if (sprintsRes.data?.length) {
                const maxNumber = Math.max(...sprintsRes.data.map(s => s.sprintNumber || 0));
                setNewSprint(prev => ({ ...prev, sprintNumber: maxNumber + 1 }));
            }
        } catch (err) {
            console.error('Error fetching project data:', err);
            setError('Failed to load project data. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    const handleCreateSprint = async (e) => {
        e.preventDefault();
        setCreateError(null);

        try {
            const sprintData = {
                sprintNumber: parseInt(newSprint.sprintNumber),
                name: newSprint.name,
                goal: newSprint.goal,
                startDate: newSprint.startDate,
                endDate: newSprint.endDate,
                projectId: parseInt(projectId)
            };
            await createSprint(sprintData);
            setShowCreateModal(false);
            setNewSprint({ sprintNumber: sprints.length + 2, name: '', goal: '', startDate: '', endDate: '' });
            fetchData();
        } catch (err) {
            console.error('Error creating sprint:', err);
            setCreateError(err.response?.data?.message || 'Failed to create sprint. Please try again.');
        }
    };

    const handleStartSprint = async (sprintId) => {
        try {
            await startSprintApi(sprintId);
            fetchData();
        } catch (err) {
            console.error('Error starting sprint:', err);
            setError('Failed to start sprint.');
        }
    };

    const handleCompleteSprint = async (sprintId) => {
        try {
            await completeSprintApi(sprintId);
            fetchData();
        } catch (err) {
            console.error('Error completing sprint:', err);
            setError('Failed to complete sprint.');
        }
    };

    const getStatusConfig = (status) => {
        const configs = {
            PLANNED: { label: 'Planned', color: '#64748b', bg: '#f1f5f9' },
            ACTIVE: { label: 'Active', color: '#059669', bg: '#d1fae5' },
            COMPLETED: { label: 'Completed', color: '#7c3aed', bg: '#ede9fe' },
            ARCHIVED: { label: 'Archived', color: '#94a3b8', bg: '#f8fafc' }
        };
        return configs[status] || configs.PLANNED;
    };

    const formatDate = (dateStr) => {
        if (!dateStr) return 'Not set';
        return new Date(dateStr).toLocaleDateString('en-US', {
            month: 'short', day: 'numeric', year: 'numeric'
        });
    };

    const tabs = [
        { id: 'sprints', label: 'Sprints', icon: Target, count: sprints.length },
        { id: 'teams', label: 'Teams', icon: Users, count: teams.length },
        { id: 'analytics', label: 'Analytics', icon: BarChart3, count: null }
    ];

    if (loading) {
        return (
            <div className="project-detail-loading">
                <Loader2 className="spinner" size={48} />
                <p>Loading project data...</p>
            </div>
        );
    }

    if (error && !project) {
        return (
            <div className="project-detail-error">
                <AlertCircle size={48} />
                <p>{error}</p>
                <button onClick={fetchData} className="retry-btn">Retry</button>
            </div>
        );
    }

    const statusConfig = getStatusConfig(project?.status);

    return (
        <div className="project-detail">
            {/* Header */}
            <header className="project-header">
                <button className="back-btn" onClick={() => navigate(-1)}>
                    <ArrowLeft size={20} />
                    <span>Back</span>
                </button>

                <div className="project-title-section">
                    <div className="project-badges">
                        <span
                            className="status-badge"
                            style={{ background: statusConfig.bg, color: statusConfig.color }}
                        >
                            {statusConfig.label}
                        </span>
                        {project?.courseName && (
                            <span className="course-badge">{project.courseName}</span>
                        )}
                    </div>
                    <h1>{project?.name}</h1>
                    {project?.description && (
                        <p className="project-description">{project.description}</p>
                    )}
                    <div className="project-meta">
                        <span><Calendar size={16} /> {formatDate(project?.startDate)} - {formatDate(project?.endDate)}</span>
                        <span><Target size={16} /> {sprints.length} Sprints</span>
                        <span><Users size={16} /> {teams.length} Teams</span>
                    </div>
                </div>
            </header>

            {/* Tabs */}
            <nav className="project-tabs">
                {tabs.map(tab => (
                    <button
                        key={tab.id}
                        className={`tab-btn ${activeTab === tab.id ? 'active' : ''}`}
                        onClick={() => setActiveTab(tab.id)}
                    >
                        <tab.icon size={18} />
                        <span>{tab.label}</span>
                        {tab.count !== null && <span className="tab-count">{tab.count}</span>}
                    </button>
                ))}
            </nav>

            {/* Content */}
            <main className="project-content">
                <AnimatePresence mode="wait">
                    {activeTab === 'sprints' && (
                        <motion.div
                            key="sprints"
                            initial={{ opacity: 0, y: 10 }}
                            animate={{ opacity: 1, y: 0 }}
                            exit={{ opacity: 0, y: -10 }}
                            className="sprints-section"
                        >
                            <div className="section-header">
                                <h2>Sprints</h2>
                                <button
                                    className="create-btn"
                                    onClick={() => setShowCreateModal(true)}
                                >
                                    <Plus size={20} />
                                    New Sprint
                                </button>
                            </div>

                            {sprints.length === 0 ? (
                                <div className="empty-state">
                                    <Target size={64} strokeWidth={1} />
                                    <h3>No sprints yet</h3>
                                    <p>Create your first sprint to start tracking progress.</p>
                                    <button
                                        className="create-btn primary"
                                        onClick={() => setShowCreateModal(true)}
                                    >
                                        <Plus size={20} />
                                        Create First Sprint
                                    </button>
                                </div>
                            ) : (
                                <div className="sprints-list">
                                    {sprints.map(sprint => (
                                        <SprintCard
                                            key={sprint.id}
                                            sprint={sprint}
                                            onViewBoard={() => navigate(`/teacher/projects/${projectId}/sprints/${sprint.id}`)}
                                            onStart={() => handleStartSprint(sprint.id)}
                                            onComplete={() => handleCompleteSprint(sprint.id)}
                                        />
                                    ))}
                                </div>
                            )}
                        </motion.div>
                    )}

                    {activeTab === 'teams' && (
                        <motion.div
                            key="teams"
                            initial={{ opacity: 0, y: 10 }}
                            animate={{ opacity: 1, y: 0 }}
                            exit={{ opacity: 0, y: -10 }}
                            className="teams-section"
                        >
                            <div className="section-header">
                                <h2>Teams</h2>
                            </div>

                            {teams.length === 0 ? (
                                <div className="empty-state">
                                    <Users size={64} strokeWidth={1} />
                                    <h3>No teams assigned</h3>
                                    <p>Teams haven't been assigned to this project yet.</p>
                                </div>
                            ) : (
                                <div className="teams-grid">
                                    {teams.map(team => (
                                        <TeamCard key={team.id} team={team} />
                                    ))}
                                </div>
                            )}
                        </motion.div>
                    )}

                    {activeTab === 'analytics' && (
                        <motion.div
                            key="analytics"
                            initial={{ opacity: 0, y: 10 }}
                            animate={{ opacity: 1, y: 0 }}
                            exit={{ opacity: 0, y: -10 }}
                            className="analytics-section"
                        >
                            <div className="empty-state">
                                <BarChart3 size={64} strokeWidth={1} />
                                <h3>Analytics Dashboard</h3>
                                <p>Project analytics coming soon.</p>
                            </div>
                        </motion.div>
                    )}
                </AnimatePresence>
            </main>

            {/* Create Sprint Modal */}
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
                            <h2>Create New Sprint</h2>

                            {createError && (
                                <div className="form-error">
                                    <AlertCircle size={16} />
                                    {createError}
                                </div>
                            )}

                            <form onSubmit={handleCreateSprint}>
                                <div className="form-row">
                                    <div className="form-group" style={{ flex: '0 0 100px' }}>
                                        <label>Sprint #</label>
                                        <input
                                            type="number"
                                            min="1"
                                            value={newSprint.sprintNumber}
                                            onChange={e => setNewSprint({ ...newSprint, sprintNumber: e.target.value })}
                                            required
                                        />
                                    </div>
                                    <div className="form-group" style={{ flex: 1 }}>
                                        <label>Sprint Name</label>
                                        <input
                                            type="text"
                                            value={newSprint.name}
                                            onChange={e => setNewSprint({ ...newSprint, name: e.target.value })}
                                            placeholder="e.g., User Authentication"
                                            required
                                        />
                                    </div>
                                </div>
                                <div className="form-group">
                                    <label>Sprint Goal</label>
                                    <textarea
                                        value={newSprint.goal}
                                        onChange={e => setNewSprint({ ...newSprint, goal: e.target.value })}
                                        placeholder="What should be accomplished in this sprint?"
                                        rows={3}
                                    />
                                </div>
                                <div className="form-row">
                                    <div className="form-group">
                                        <label>Start Date</label>
                                        <input
                                            type="date"
                                            value={newSprint.startDate}
                                            onChange={e => setNewSprint({ ...newSprint, startDate: e.target.value })}
                                            required
                                        />
                                    </div>
                                    <div className="form-group">
                                        <label>End Date</label>
                                        <input
                                            type="date"
                                            value={newSprint.endDate}
                                            onChange={e => setNewSprint({ ...newSprint, endDate: e.target.value })}
                                            required
                                        />
                                    </div>
                                </div>
                                <div className="modal-actions">
                                    <button type="button" className="cancel-btn" onClick={() => setShowCreateModal(false)}>
                                        Cancel
                                    </button>
                                    <button type="submit" className="submit-btn">
                                        Create Sprint
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

export default ProjectDetail;
