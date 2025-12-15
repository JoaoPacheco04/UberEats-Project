/**
 * Course Management Page Component
 * Manages course details, projects, teams, and enrollments.
 * 
 * @author Yeswanth
 * @author Ana
 * @version 1.0.0
 */
import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion'; // For animation transitions
import {
    ArrowLeft,
    FolderKanban,
    Users,
    UsersRound,
    Plus,
    Calendar,
    Award,
    GraduationCap,
    AlertCircle,
    Loader2,
    Download
} from 'lucide-react'; // Icon library
import {
    getCourseById,
    getProjectsByCourse,
    getCourseEnrollments,
    getTeamByProject,
    createTeam,
    addMemberToTeam,
    exportCourseGrades,
    getCurrentUser
} from '../services/api'; // API service calls
import ProjectCard from '../components/ProjectCard'; // Custom components
import TeamCard from '../components/TeamCard';
import StudentDetailCard from '../components/StudentDetailCard';
import AwardBadgeModal from '../components/AwardBadgeModal';
import './CourseManagement.css';

// Defines the allowed Scrum roles for team creation
const SCRUM_ROLES = [
    { value: 'SCRUM_MASTER', label: 'Scrum Master' },
    { value: 'PRODUCT_OWNER', label: 'Product Owner' },
    { value: 'DEVELOPER', label: 'Developer' }
];

const CourseManagement = () => {
    // Hooks for routing parameters and navigation
    const { courseId } = useParams();
    const navigate = useNavigate();

    // --- Component State Management ---
    const [course, setCourse] = useState(null);
    const [projects, setProjects] = useState([]);
    const [students, setStudents] = useState([]);
    const [teams, setTeams] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [activeTab, setActiveTab] = useState('projects'); // Controls which tab content is visible
    const [showCreateModal, setShowCreateModal] = useState(false); // State for the 'Create Project' modal

    // Get current user details for role-based features (e.g., 'TEACHER' permissions)
    const currentUser = getCurrentUser();

    // Award Badge Modal State
    const [awardModalOpen, setAwardModalOpen] = useState(false);
    const [selectedStudent, setSelectedStudent] = useState(null);

    // Create Project Form State
    const [newProject, setNewProject] = useState({
        name: '',
        description: '',
        startDate: '',
        endDate: '',
        courseId: courseId
    });

    // Create Team Modal State
    const [showTeamModal, setShowTeamModal] = useState(false);
    const [creatingTeam, setCreatingTeam] = useState(false);
    const [teamError, setTeamError] = useState(null);
    const [newTeam, setNewTeam] = useState({
        name: '',
        projectId: '', // Project the team belongs to
        members: [] // { userId, role }
    });

    // --- Data Fetching Logic (runs on component mount and courseId change) ---
    useEffect(() => {
        fetchData();
    }, [courseId]);

    const fetchData = async () => {
        try {
            setLoading(true);
            setError(null);

            // Fetch course, projects, and enrollments concurrently
            const [courseRes, projectsRes, studentsRes] = await Promise.all([
                getCourseById(courseId),
                getProjectsByCourse(courseId).catch(() => ({ data: [] })), // Handle potential project error gracefully
                getCourseEnrollments(courseId).catch(() => ({ data: [] })) // Handle potential enrollment error gracefully
            ]);

            setCourse(courseRes.data);
            setProjects(projectsRes.data || []);
            setStudents(studentsRes.data || []);

            // Fetch teams for all fetched projects
            const projectList = projectsRes.data || [];
            if (projectList.length > 0) {
                const teamPromises = projectList.map(p =>
                    getTeamByProject(p.id).catch(() => ({ data: null }))
                );
                const teamResults = await Promise.all(teamPromises);
                // Filter out null results (projects without a team) and update teams state
                const allTeams = teamResults
                    .map(r => r.data)
                    .filter(team => team != null);
                setTeams(allTeams);
            } else {
                setTeams([]);
            }
        } catch (err) {
            console.error('Error fetching course data:', err);
            setError('Failed to load course data. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    // --- Handler Functions ---

    const handleCreateProject = async (e) => {
        e.preventDefault();
        try {
            // Dynamic import of createProject is unusual, assuming it's required by the setup
            const { createProject } = await import('../services/api');
            const projectData = {
                ...newProject,
                courseId: parseInt(courseId) // Ensure courseId is an integer
            };
            await createProject(projectData);
            setShowCreateModal(false);
            setNewProject({ name: '', description: '', startDate: '', endDate: '', courseId }); // Reset form
            fetchData(); // Refresh data to show the new project
        } catch (err) {
            console.error('Error creating project:', err);
            setError('Failed to create project. Please try again.');
        }
    };

    const handleExportCsv = async () => {
        try {
            const response = await exportCourseGrades(courseId);
            // Create a Blob from the response data for download
            const blob = new Blob([response.data], { type: 'text/csv' });
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;
            link.download = `${course?.code || 'course'}_grades.csv`;
            document.body.appendChild(link);
            link.click(); // Trigger the download
            document.body.removeChild(link);
            window.URL.revokeObjectURL(url);
        } catch (err) {
            console.error('Error exporting grades:', err);
            setError('Failed to export grades. Please try again.');
        }
    };

    const handleAwardBadge = (student) => {
        setSelectedStudent(student);
        setAwardModalOpen(true);
    };

    const handleAwardModalClose = (success) => {
        setAwardModalOpen(false);
        setSelectedStudent(null);
        if (success) {
            fetchData(); // Refresh data if a badge was awarded
        }
    };

    // Team Creation Handlers
    const handleAddTeamMember = (userId) => {
        // Prevent adding the same user twice
        if (newTeam.members.find(m => m.userId === userId)) return;
        setNewTeam(prev => ({
            ...prev,
            // Add new member with default role 'DEVELOPER'
            members: [...prev.members, { userId: parseInt(userId), role: 'DEVELOPER' }]
        }));
    };

    const handleRemoveTeamMember = (userId) => {
        setNewTeam(prev => ({
            ...prev,
            members: prev.members.filter(m => m.userId !== userId)
        }));
    };

    const handleMemberRoleChange = (userId, role) => {
        setNewTeam(prev => ({
            ...prev,
            // Update the role for the matching member
            members: prev.members.map(m =>
                m.userId === userId ? { ...m, role } : m
            )
        }));
    };

    const handleCreateTeam = async (e) => {
        e.preventDefault();
        setTeamError(null);
        setCreatingTeam(true);

        // Basic validation
        if (!newTeam.projectId) {
            setTeamError('Please select a project');
            setCreatingTeam(false);
            return;
        }

        if (newTeam.members.length === 0) {
            setTeamError('Please add at least one team member');
            setCreatingTeam(false);
            return;
        }

        try {
            // 1. Create the team entity
            const teamRes = await createTeam({
                name: newTeam.name,
                projectId: parseInt(newTeam.projectId)
            });
            const teamId = teamRes.data?.id;

            if (teamId) {
                // 2. Add members with their assigned roles sequentially
                for (const member of newTeam.members) {
                    await addMemberToTeam(teamId, {
                        userId: member.userId,
                        role: member.role
                    });
                }
            }

            // Close modal, reset form, and refresh data
            setShowTeamModal(false);
            setNewTeam({ name: '', projectId: '', members: [] });
            fetchData();
        } catch (err) {
            console.error('Error creating team:', err);
            setTeamError(err.response?.data?.message || 'Failed to create team');
        } finally {
            setCreatingTeam(false);
        }
    };

    // Helper to get student name from their ID, used in the team creation modal
    const getStudentName = (studentId) => {
        const student = students.find(s => s.studentId === studentId || s.id === studentId);
        return student?.studentName || student?.name || 'Unknown';
    };

    // Helper to determine the correct dashboard route based on user role
    const getDashboardRoute = () => {
        if (currentUser?.role === 'STUDENT') {
            return '/student/dashboard';
        }
        return '/teacher/dashboard';
    };

    // Configuration for the main tabs
    const tabs = [
        { id: 'projects', label: 'Projects', icon: FolderKanban, count: projects.length },
        { id: 'students', label: 'Students', icon: Users, count: students.length },
        { id: 'teams', label: 'Teams', icon: UsersRound, count: teams.length }
    ];

    // --- Conditional Loading and Error Rendering ---

    if (loading) {
        return (
            <div className="course-management-loading">
                <Loader2 className="spinner" size={48} />
                <p>Loading course data...</p>
            </div>
        );
    }

    if (error && !course) {
        return (
            <div className="course-management-error">
                <AlertCircle size={48} />
                <p>{error}</p>
                <button onClick={fetchData} className="retry-btn">Retry</button>
            </div>
        );
    }

    // --- Main Component Rendering ---
    return (
        <div className="course-management">
            {/* Header Section */}
            <header className="course-header">
                {/* Back button, navigates based on user role */}
                <button className="back-btn" onClick={() => navigate(getDashboardRoute())}>
                    <ArrowLeft size={20} />
                    <span>Back to Dashboard</span>
                </button>

                <div className="course-title-section">
                    <div className="course-badge">{course?.code}</div>
                    <h1>{course?.name}</h1>
                    <p className="course-meta">
                        {/* Course metadata */}
                        <span><Calendar size={16} /> {course?.semester} {course?.academicYear}</span>
                        <span><Users size={16} /> {students.length} Students</span>
                        <span><FolderKanban size={16} /> {projects.length} Projects</span>
                        <span><UsersRound size={16} /> {teams.length} Teams</span>
                    </p>
                </div>

                {/* Export button, only visible to TEACHERS */}
                {currentUser?.role === 'TEACHER' && (
                    <button className="export-btn" onClick={handleExportCsv}>
                        <Download size={18} />
                        Export Grades
                    </button>
                )}
            </header>

            {/* Tabs Navigation */}
            <nav className="course-tabs">
                {tabs.map(tab => (
                    <button
                        key={tab.id}
                        className={`tab-btn ${activeTab === tab.id ? 'active' : ''}`}
                        onClick={() => setActiveTab(tab.id)}
                    >
                        <tab.icon size={18} />
                        <span>{tab.label}</span>
                        <span className="tab-count">{tab.count}</span>
                    </button>
                ))}
            </nav>

            {/* Main Content Area - Uses AnimatePresence for tab transitions */}
            <main className="course-content">
                <AnimatePresence mode="wait">
                    {/* Projects Tab Content */}
                    {activeTab === 'projects' && (
                        <motion.div
                            key="projects"
                            initial={{ opacity: 0, y: 10 }}
                            animate={{ opacity: 1, y: 0 }}
                            exit={{ opacity: 0, y: -10 }}
                            className="projects-section"
                        >
                            <div className="section-header">
                                <h2>Projects</h2>
                                {/* Create Project button, only for TEACHERS */}
                                {currentUser?.role === 'TEACHER' && (
                                    <button
                                        className="create-btn"
                                        onClick={() => setShowCreateModal(true)}
                                    >
                                        <Plus size={20} />
                                        New Project
                                    </button>
                                )}
                            </div>

                            {/* Conditional rendering for empty state or project list */}
                            {projects.length === 0 ? (
                                <div className="empty-state">
                                    <FolderKanban size={64} strokeWidth={1} />
                                    <h3>No projects yet</h3>
                                    <p>Create your first project to get started with sprints and user stories.</p>
                                    {currentUser?.role === 'TEACHER' && (
                                        <button
                                            className="create-btn primary"
                                            onClick={() => setShowCreateModal(true)}
                                        >
                                            <Plus size={20} />
                                            Create First Project
                                        </button>
                                    )}
                                </div>
                            ) : (
                                <div className="projects-grid">
                                    {projects.map(project => (
                                        // ProjectCard component
                                        <ProjectCard
                                            key={project.id}
                                            project={project}
                                            onClick={() => navigate(`/teacher/projects/${project.id}`)}
                                        />
                                    ))}
                                </div>
                            )}
                        </motion.div>
                    )}

                    {/* Students Tab Content */}
                    {activeTab === 'students' && (
                        <motion.div
                            key="students"
                            initial={{ opacity: 0, y: 10 }}
                            animate={{ opacity: 1, y: 0 }}
                            exit={{ opacity: 0, y: -10 }}
                            className="students-section"
                        >
                            <div className="section-header">
                                <h2>Enrolled Students</h2>
                                <div className="section-info">
                                    <Award size={16} />
                                    <span>Click on a student to see details and award badges</span>
                                </div>
                            </div>

                            {/* Conditional rendering for empty state or student list */}
                            {students.length === 0 ? (
                                <div className="empty-state">
                                    <GraduationCap size={64} strokeWidth={1} />
                                    <h3>No students enrolled</h3>
                                    <p>Students will appear here once they enroll in this course.</p>
                                </div>
                            ) : (
                                <div className="students-list">
                                    {students.map(student => (
                                        // StudentDetailCard component
                                        <StudentDetailCard
                                            key={student.id}
                                            student={student}
                                            onAwardBadge={handleAwardBadge}
                                            showAwardButton={currentUser?.role === 'TEACHER'} // Only show award button for teachers
                                        />
                                    ))}
                                </div>
                            )}
                        </motion.div>
                    )}

                    {/* Teams Tab Content */}
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
                                {/* Create Team button */}
                                <button className="create-btn" onClick={() => setShowTeamModal(true)}>
                                    <Plus size={18} />
                                    Create Team
                                </button>
                            </div>

                            {/* Conditional rendering for empty state or team list */}
                            {teams.length === 0 ? (
                                <div className="empty-state">
                                    <UsersRound size={64} strokeWidth={1} />
                                    <h3>No teams yet</h3>
                                    <p>Create a team to get started with Scrum</p>
                                    <button className="create-btn" onClick={() => setShowTeamModal(true)}>
                                        <Plus size={18} />
                                        Create Your First Team
                                    </button>
                                </div>
                            ) : (
                                <div className="teams-grid">
                                    {teams.map(team => (
                                        // TeamCard component
                                        <TeamCard key={team.id} team={team} />
                                    ))}
                                </div>
                            )}
                        </motion.div>
                    )}
                </AnimatePresence>
            </main>

            {/* Create Project Modal */}
            <AnimatePresence>
                {showCreateModal && (
                    <motion.div
                        className="modal-overlay"
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        exit={{ opacity: 0 }}
                        onClick={() => setShowCreateModal(false)} // Close modal on overlay click
                    >
                        <motion.div
                            className="modal-content"
                            initial={{ scale: 0.9, opacity: 0 }}
                            animate={{ scale: 1, opacity: 1 }}
                            exit={{ scale: 0.9, opacity: 0 }}
                            onClick={e => e.stopPropagation()} // Prevent closing on content click
                        >
                            <h2>Create New Project</h2>
                            <form onSubmit={handleCreateProject}>
                                {/* Project Name Input */}
                                <div className="form-group">
                                    <label>Project Name</label>
                                    <input
                                        type="text"
                                        value={newProject.name}
                                        onChange={e => setNewProject({ ...newProject, name: e.target.value })}
                                        placeholder="e.g., EduScrum Sprint Project"
                                        required
                                    />
                                </div>
                                {/* Description Textarea */}
                                <div className="form-group">
                                    <label>Description</label>
                                    <textarea
                                        value={newProject.description}
                                        onChange={e => setNewProject({ ...newProject, description: e.target.value })}
                                        placeholder="Brief description of the project..."
                                        rows={3}
                                    />
                                </div>
                                {/* Start and End Date Inputs */}
                                <div className="form-row">
                                    <div className="form-group">
                                        <label>Start Date</label>
                                        <input
                                            type="date"
                                            value={newProject.startDate}
                                            onChange={e => setNewProject({ ...newProject, startDate: e.target.value })}
                                            required
                                        />
                                    </div>
                                    <div className="form-group">
                                        <label>End Date</label>
                                        <input
                                            type="date"
                                            value={newProject.endDate}
                                            onChange={e => setNewProject({ ...newProject, endDate: e.target.value })}
                                            required
                                        />
                                    </div>
                                </div>
                                {/* Modal Actions */}
                                <div className="modal-actions">
                                    <button type="button" className="cancel-btn" onClick={() => setShowCreateModal(false)}>
                                        Cancel
                                    </button>
                                    <button type="submit" className="submit-btn">
                                        Create Project
                                    </button>
                                </div>
                            </form>
                        </motion.div>
                    </motion.div>
                )}
            </AnimatePresence>

            {/* Award Badge Modal (reusable component) */}
            <AwardBadgeModal
                isOpen={awardModalOpen}
                onClose={handleAwardModalClose}
                recipient={selectedStudent}
                recipientType="user"
                // Passes the ID of the first project for badge association (simplification)
                projectId={projects[0]?.id} 
            />

            {/* Create Team Modal */}
            <AnimatePresence>
                {showTeamModal && (
                    <motion.div
                        className="modal-overlay"
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        exit={{ opacity: 0 }}
                        onClick={() => setShowTeamModal(false)}
                    >
                        <motion.div
                            className="modal-content team-modal"
                            initial={{ scale: 0.9, opacity: 0 }}
                            animate={{ scale: 1, opacity: 1 }}
                            exit={{ scale: 0.9, opacity: 0 }}
                            onClick={e => e.stopPropagation()}
                        >
                            <h2>Create New Team</h2>
                            <form onSubmit={handleCreateTeam}>
                                {/* Display team creation error */}
                                {teamError && (
                                    <div className="form-error">
                                        <AlertCircle size={16} />
                                        {teamError}
                                    </div>
                                )}

                                {/* Team Name Input */}
                                <div className="form-group">
                                    <label>Team Name</label>
                                    <input
                                        type="text"
                                        value={newTeam.name}
                                        onChange={e => setNewTeam({ ...newTeam, name: e.target.value })}
                                        placeholder="e.g., Alpha Squad"
                                        required
                                    />
                                </div>

                                {/* Project Selection */}
                                <div className="form-group">
                                    <label>Project</label>
                                    <select
                                        value={newTeam.projectId}
                                        onChange={e => setNewTeam({ ...newTeam, projectId: e.target.value })}
                                        required
                                    >
                                        <option value="">Select a project...</option>
                                        {projects.map(p => (
                                            <option key={p.id} value={p.id}>{p.name}</option>
                                        ))}
                                    </select>
                                </div>

                                {/* Add Team Members Selector */}
                                <div className="form-group">
                                    <label>Add Team Members</label>
                                    <select
                                        onChange={e => {
                                            if (e.target.value) {
                                                handleAddTeamMember(e.target.value);
                                                e.target.value = ''; // Reset select after adding
                                            }
                                        }}
                                    >
                                        <option value="">Select a student to add...</option>
                                        {students
                                            // Filter out students already added to the team
                                            .filter(s => !newTeam.members.find(m => m.userId === (s.studentId || s.id)))
                                            .map(s => (
                                                <option key={s.studentId || s.id} value={s.studentId || s.id}>
                                                    {s.studentName || s.name}
                                                </option>
                                            ))
                                        }
                                    </select>
                                </div>

                                {/* Display Selected Team Members and Roles */}
                                {newTeam.members.length > 0 && (
                                    <div className="team-members-list">
                                        <label>Team Members ({newTeam.members.length})</label>
                                        {newTeam.members.map(member => (
                                            <div key={member.userId} className="team-member-row">
                                                <span className="member-name">
                                                    {getStudentName(member.userId)}
                                                </span>
                                                {/* Role Selection for Member */}
                                                <select
                                                    value={member.role}
                                                    onChange={e => handleMemberRoleChange(member.userId, e.target.value)}
                                                    className="role-select"
                                                >
                                                    {SCRUM_ROLES.map(role => (
                                                        <option key={role.value} value={role.value}>
                                                            {role.label}
                                                        </option>
                                                    ))}
                                                </select>
                                                {/* Remove Member Button */}
                                                <button
                                                    type="button"
                                                    className="remove-member-btn"
                                                    onClick={() => handleRemoveTeamMember(member.userId)}
                                                >
                                                    ×
                                                </button>
                                            </div>
                                        ))}
                                    </div>
                                )}

                                {/* Team Creation Actions */}
                                <div className="modal-actions">
                                    <button type="button" className="cancel-btn" onClick={() => setShowTeamModal(false)}>
                                        Cancel
                                    </button>
                                    <button type="submit" className="submit-btn" disabled={creatingTeam}>
                                        {creatingTeam ? 'Creating...' : 'Create Team'}
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

export default CourseManagement;