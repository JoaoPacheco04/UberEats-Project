/**
 * Student Dashboard Page Component
 * Main dashboard for students showing enrollments, achievements, and team info.
 * 
 * @author Ana
 * @author Bruna
 * @version 1.0.0
 */
// Import necessary modules from React and external libraries
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion'; // For animations and transitions
import {
    // Lucide icons for visual representation
    GraduationCap,
    BookOpen,
    Users,
    Award,
    Trophy,
    TrendingUp,
    ChevronRight,
    AlertCircle,
    Plus,
    Target,
    LogOut,
    UserPlus,
    Calendar,
    Loader2,
    Sparkles,
    Settings,
} from 'lucide-react';
import {
    // Recharts components for the score comparison chart
    BarChart,
    Bar,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    ResponsiveContainer,
    Cell,
    Legend
} from 'recharts';
import {
    // API service calls for fetching and modifying student data
    getStudentDashboard,
    getUserAchievements,
    getUserTeams,
    getAvailableCourses,
    getEnrollmentsByStudent,
    enrollStudent,
    getCurrentUser
} from '../services/api';
// Custom component imports
import EditProfileModal from '../components/EditProfileModal';
import './StudentDashboard.css';

// Main Student Dashboard component
const StudentDashboard = () => {
    // Hook to handle navigation (e.g., redirecting to login or course details)
    const navigate = useNavigate();

    // --- State Variables ---
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Dashboard specific data states
    const [dashboardData, setDashboardData] = useState(null); // High-level stats (e.g., global score, average)
    const [achievements, setAchievements] = useState([]); // List of badges/awards earned
    const [teams, setTeams] = useState([]); // List of teams the student is part of
    const [enrollments, setEnrollments] = useState([]); // List of courses the student is enrolled in
    const [availableCourses, setAvailableCourses] = useState([]); // Courses not yet enrolled in

    // Modal state management
    const [showEnrollModal, setShowEnrollModal] = useState(false);
    const [showEditProfileModal, setShowEditProfileModal] = useState(false);
    const [enrolling, setEnrolling] = useState(false); // Loading state for enrollment action

    // Get current user information from local storage or context
    const user = getCurrentUser();

    // --- Effects (Data Fetching) ---
    useEffect(() => {
        // Only attempt to fetch data if a user ID is available
        if (user?.id) {
            fetchAllData();
        }
    }, [user?.id]); // Re-run if user ID changes (though usually only runs once on mount)

    // Function to fetch all necessary dashboard data concurrently
    const fetchAllData = async () => {
        try {
            setLoading(true);

            // Use Promise.all to fetch data in parallel,
            // providing fallback data ({ data: [] } or { data: {} }) for robust error handling
            const [dashRes, achieveRes, teamsRes, enrollRes, coursesRes] = await Promise.all([
                getStudentDashboard(user.id).catch(() => ({ data: {} })),
                getUserAchievements(user.id).catch(() => ({ data: [] })),
                getUserTeams(user.id).catch(() => ({ data: [] })),
                getEnrollmentsByStudent(user.id).catch(() => ({ data: [] })),
                getAvailableCourses().catch(() => ({ data: [] }))
            ]);

            // Set the fetched data to state
            setDashboardData(dashRes.data || {});
            setAchievements(achieveRes.data || []);
            setTeams(teamsRes.data || []);
            setEnrollments(enrollRes.data || []);

            // Filter available courses to show only those the student is NOT already enrolled in
            const enrolledCourseIds = (enrollRes.data || []).map(e => e.courseId);
            const available = (coursesRes.data || []).filter(c => !enrolledCourseIds.includes(c.id));
            setAvailableCourses(available);

        } catch (err) {
            console.error('Error fetching dashboard data:', err);
            setError('Failed to load dashboard data');
        } finally {
            setLoading(false);
        }
    };

    // Handler for course enrollment action
    const handleEnroll = async (courseId) => {
        try {
            setEnrolling(true);
            await enrollStudent(courseId, user.id);
            // Re-fetch all data to update the 'My Courses' and 'Available Courses' sections
            await fetchAllData();
            setShowEnrollModal(false); // Close the modal on success
        } catch (err) {
            console.error('Error enrolling:', err);
            alert('Failed to enroll in course');
        } finally {
            setEnrolling(false);
        }
    };

    // Handler for user logout
    const handleLogout = () => {
        // Clear user session data
        localStorage.removeItem('user');
        localStorage.removeItem('token');
        // Redirect to the login page
        navigate('/login');
    };

    // Utility function to safely get the user's name for display
    const getUserName = () => {
        return user?.fullName || user?.firstName || 'Student';
    };

    // --- Calculated Stats ---
    // Safely calculate total points, prioritizing dashboardData.globalScore
    const totalPoints = dashboardData?.globalScore || achievements.reduce((sum, a) => sum + Number(a.points || 0), 0);
    const totalBadges = achievements.length;
    const courseAverage = dashboardData?.courseAverage || 0; // Average score across all courses/students

    // --- Loading State Render ---
    if (loading) {
        return (
            <div className="student-dashboard-loading">
                <Loader2 className="spinner" size={48} />
            </div>
        );
    }

    // --- Main Component Render ---
    return (
        <div className="student-dashboard">
            {/* Animated Background using framer-motion for visual effect */}
            <div className="dashboard-bg">
                {/* Motion divs for moving orbs/decorations */}
                <motion.div
                    animate={{ x: [0, 50, 0], y: [0, -30, 0], scale: [1, 1.1, 1] }}
                    transition={{ duration: 8, repeat: Infinity, ease: "easeInOut" }}
                    className="bg-orb bg-orb-1"
                />
                <motion.div
                    animate={{ x: [0, -40, 0], y: [0, 40, 0], scale: [1, 0.9, 1] }}
                    transition={{ duration: 10, repeat: Infinity, ease: "easeInOut" }}
                    className="bg-orb bg-orb-2"
                />
                <motion.div
                    animate={{ x: [0, 30, 0], y: [0, 30, 0], scale: [1, 1.05, 1] }}
                    transition={{ duration: 12, repeat: Infinity, ease: "easeInOut" }}
                    className="bg-orb bg-orb-3"
                />
            </div>

            <div className="dashboard-container">
                {/* Header Section with Welcome Message and Quick Stats */}
                <motion.header
                    initial={{ opacity: 0, y: -20 }}
                    animate={{ opacity: 1, y: 0 }}
                    className="dashboard-header"
                >
                    <div className="header-banner">
                        <div className="header-decorations">
                            {/* Decorative elements (circles, sparkles) */}
                            <div className="decoration-circle top-right" />
                            <div className="decoration-circle bottom-left" />
                            <Sparkles className="sparkle-icon" size={40} />
                        </div>

                        <div className="header-content">
                            <div className="header-info">
                                <div className="header-badge">
                                    <GraduationCap size={24} />
                                    <div className="badge-pulse" />
                                </div>
                                <span className="header-label">Student Dashboard</span>
                            </div>
                            <h1>Welcome, {getUserName()}!</h1>
                            <p>Track your progress and achievements</p>
                        </div>

                        {/* Quick Stats: Points, Badges, Teams */}
                        <div className="header-stats">
                            <div className="stat-box">
                                <Trophy size={24} />
                                <div className="stat-content">
                                    <span className="stat-value">{totalPoints}</span>
                                    <span className="stat-label">Points</span>
                                </div>
                            </div>
                            <div className="stat-box">
                                <Award size={24} />
                                <div className="stat-content">
                                    <span className="stat-value">{totalBadges}</span>
                                    <span className="stat-label">Badges</span>
                                </div>
                            </div>
                            <div className="stat-box">
                                <Users size={24} />
                                <div className="stat-content">
                                    <span className="stat-value">{teams.length}</span>
                                    <span className="stat-label">Teams</span>
                                </div>
                            </div>
                        </div>

                        {/* Action Buttons: Edit Profile and Logout */}
                        <div className="header-actions">
                            <button
                                onClick={() => setShowEditProfileModal(true)}
                                className="action-btn secondary"
                            >
                                <Settings size={18} />
                                Edit Profile
                            </button>
                            <button onClick={handleLogout} className="logout-btn">
                                <LogOut size={18} />
                                Logout
                            </button>
                        </div>
                    </div>
                </motion.header>

                {/* Error Banner */}
                {
                    error && (
                        <div className="error-banner">
                            <AlertCircle size={18} />
                            {error}
                        </div>
                    )
                }

                {/* Main Content Grid Layout */}
                <div className="dashboard-grid">
                    {/* Left Column - Courses & Teams */}
                    <div className="main-column">
                        {/* My Courses Section */}
                        <section className="dashboard-section">
                            <div className="section-header">
                                <div className="section-title">
                                    <div className="section-icon">
                                        <BookOpen size={20} />
                                    </div>
                                    <div>
                                        <h2>My Courses</h2>
                                        <p>Courses you're enrolled in</p>
                                    </div>
                                </div>
                                {/* Button to open the enrollment modal */}
                                <button
                                    className="action-btn primary"
                                    onClick={() => setShowEnrollModal(true)}
                                >
                                    <Plus size={16} /> Enroll
                                </button>
                            </div>

                            {/* Display enrolled courses or an empty state */}
                            {enrollments.length > 0 ? (
                                <div className="courses-grid">
                                    {enrollments.map((enrollment) => (
                                        <motion.div
                                            key={enrollment.id}
                                            className="course-card"
                                            whileHover={{ y: -4 }} // Hover effect
                                            onClick={() => navigate(`/student/courses/${enrollment.courseId}`)}
                                        >
                                            <div className="course-card-header">
                                                <div className="course-icon">
                                                    <GraduationCap size={24} />
                                                </div>
                                                <ChevronRight size={20} className="course-arrow" />
                                            </div>
                                            <h3>{enrollment.courseName}</h3>
                                            <div className="course-meta">
                                                <span className="course-code">{enrollment.courseCode}</span>
                                                <span className="enrolled-date">
                                                    <Calendar size={12} />
                                                    {new Date(enrollment.enrolledAt).toLocaleDateString()}
                                                </span>
                                            </div>
                                        </motion.div>
                                    ))}
                                </div>
                            ) : (
                                // Empty state when no courses are enrolled
                                <div className="empty-state">
                                    <BookOpen size={48} />
                                    <h3>No Courses Yet</h3>
                                    <p>Enroll in a course to get started</p>
                                </div>
                            )}
                        </section>

                        {/* My Teams Section */}
                        <section className="dashboard-section">
                            <div className="section-header">
                                <div className="section-title">
                                    <div className="section-icon teams-icon">
                                        <Users size={20} />
                                    </div>
                                    <div>
                                        <h2>My Teams</h2>
                                        <p>Teams you're part of</p>
                                    </div>
                                </div>
                            </div>

                            {/* Display student's teams or an empty state */}
                            {teams.length > 0 ? (
                                <div className="teams-grid">
                                    {teams.map((team) => (
                                        <motion.div
                                            key={team.id}
                                            className="team-card"
                                            whileHover={{ y: -4 }} // Hover effect
                                            onClick={() => navigate(`/student/teams/${team.id}`)}
                                        >
                                            <div className="team-card-header">
                                                <div className="team-icon">
                                                    <Users size={20} />
                                                </div>
                                                <ChevronRight size={18} className="team-arrow" />
                                            </div>
                                            <h3>{team.name}</h3>
                                            <div className="team-meta">
                                                <span className="team-role">{team.role || 'Member'}</span>
                                                <span className="member-count">
                                                    {team.memberCount || 0} members
                                                </span>
                                            </div>
                                        </motion.div>
                                    ))}
                                </div>
                            ) : (
                                // Empty state when not part of any team
                                <div className="empty-state">
                                    <Users size={48} />
                                    <h3>No Teams Yet</h3>
                                    <p>Join a team through your course</p>
                                </div>
                            )}
                        </section>
                    </div>

                    {/* Right Column - Stats & Achievements */}
                    <div className="side-column">
                        {/* Score Comparison Card */}
                        <div className="stat-card score-card">
                            <div className="stat-card-header">
                                <div className="stat-card-icon">
                                    <Target size={20} />
                                </div>
                                <div>
                                    <h3>Score Comparison</h3>
                                    <p>Your performance vs course average</p>
                                </div>
                            </div>
                            <div className="score-chart-container">
                                {/* Bar Chart using Recharts */}
                                <ResponsiveContainer width="100%" height={200}>
                                    <BarChart
                                        data={[
                                            {
                                                name: 'Your Score',
                                                value: totalPoints,
                                                fill: '#6366f1' // Default color
                                            },
                                            {
                                                name: 'Course Avg',
                                                value: courseAverage,
                                                fill: '#94a3b8' // Average color
                                            }
                                        ]}
                                        layout="vertical"
                                        margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
                                    >
                                        <CartesianGrid strokeDasharray="3 3" horizontal={true} vertical={false} />
                                        <XAxis type="number" domain={[0, 'dataMax + 20']} />
                                        <YAxis
                                            type="category"
                                            dataKey="name"
                                            width={80}
                                            tick={{ fontSize: 12 }}
                                        />
                                        <Tooltip
                                            formatter={(value) => [`${value} pts`, 'Score']}
                                            contentStyle={{
                                                backgroundColor: 'rgba(255,255,255,0.95)',
                                                border: 'none',
                                                borderRadius: '8px',
                                                boxShadow: '0 4px 12px rgba(0,0,0,0.1)'
                                            }}
                                        />
                                        <Bar
                                            dataKey="value"
                                            radius={[0, 8, 8, 0]}
                                            barSize={30}
                                        >
                                            {/* Dynamically set color for 'Your Score' bar: green if above average, default otherwise */}
                                            {[
                                                { name: 'Your Score', fill: totalPoints >= courseAverage ? '#22c55e' : '#6366f1' },
                                                { name: 'Course Avg', fill: '#94a3b8' }
                                            ].map((entry, index) => (
                                                <Cell key={`cell-${index}`} fill={entry.fill} />
                                            ))}
                                        </Bar>
                                    </BarChart>
                                </ResponsiveContainer>
                                <div className="score-comparison-summary">
                                    {/* Conditional message based on performance vs average */}
                                    {totalPoints >= courseAverage ? (
                                        <span className="above-average">
                                            <TrendingUp size={16} />
                                            You're above average!
                                        </span>
                                    ) : (
                                        <span className="below-average">
                                            Keep working to reach the average
                                        </span>
                                    )}
                                </div>
                            </div>
                        </div>

                        {/* Recent Achievements Card */}
                        <div className="stat-card achievements-card">
                            <div className="stat-card-header">
                                <div className="stat-card-icon achievements-icon">
                                    <Award size={20} />
                                </div>
                                <div>
                                    <h3>Recent Achievements</h3>
                                    <p>Badges you've earned</p>
                                </div>
                            </div>

                            {/* Display recent achievements */}
                            {achievements.length > 0 ? (
                                <div className="achievements-list">
                                    {/* Slice(0, 5) shows only the 5 most recent achievements */}
                                    {achievements.slice(0, 5).map((achievement, index) => (
                                        <motion.div
                                            key={achievement.id}
                                            className="achievement-item"
                                            initial={{ opacity: 0, x: -10 }}
                                            animate={{ opacity: 1, x: 0 }}
                                            transition={{ delay: index * 0.1 }}
                                        >
                                            <div className="achievement-icon">
                                                <Trophy size={16} />
                                            </div>
                                            <div className="achievement-info">
                                                <span className="achievement-name">{achievement.badgeName}</span>
                                                <span className="achievement-points">+{achievement.points} pts</span>
                                            </div>
                                        </motion.div>
                                    ))}
                                </div>
                            ) : (
                                <div className="empty-achievements">
                                    <Trophy size={32} />
                                    <p>Complete tasks to earn badges!</p>
                                </div>
                            )}
                        </div>

                        {/* Progression Timeline Card (Summary) */}
                        <div className="stat-card progression-card">
                            <div className="stat-card-header">
                                <div className="stat-card-icon progression-icon">
                                    <TrendingUp size={20} />
                                </div>
                                <div>
                                    <h3>Your Progression</h3>
                                    <p>Overall activity summary</p>
                                </div>
                            </div>
                            <div className="progression-stats">
                                {/* Calculated counts for overall progression summary */}
                                <div className="progression-stat">
                                    <span className="prog-value">{enrollments.length}</span>
                                    <span className="prog-label">Courses</span>
                                </div>
                                <div className="progression-stat">
                                    <span className="prog-value">{teams.length}</span>
                                    <span className="prog-label">Teams</span>
                                </div>
                                <div className="progression-stat">
                                    <span className="prog-value">{achievements.length}</span>
                                    <span className="prog-label">Badges</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div >

            {/* Enroll Modal (Hidden by default) */}
            {
                showEnrollModal && (
                    <div className="modal-overlay" onClick={() => setShowEnrollModal(false)}>
                        <motion.div
                            className="modal-content enroll-modal"
                            initial={{ scale: 0.9, opacity: 0 }}
                            animate={{ scale: 1, opacity: 1 }}
                            onClick={e => e.stopPropagation()} // Prevents closing when clicking inside modal
                        >
                            <h2>Enroll in a Course</h2>
                            <p className="modal-subtitle">Select a course to join</p>

                            {availableCourses.length > 0 ? (
                                <div className="available-courses">
                                    {availableCourses.map(course => (
                                        <div key={course.id} className="available-course-item">
                                            <div className="course-info">
                                                <h4>{course.name}</h4>
                                                <span className="course-code">{course.code}</span>
                                            </div>
                                            <button
                                                className="enroll-course-btn"
                                                onClick={() => handleEnroll(course.id)}
                                                disabled={enrolling}
                                            >
                                                {/* Show spinner when enrolling */}
                                                {enrolling ? <Loader2 className="spinner" size={16} /> : <UserPlus size={16} />}
                                                Enroll
                                            </button>
                                        </div>
                                    ))}
                                </div>
                            ) : (
                                <div className="no-courses-available">
                                    <p>No courses available for enrollment</p>
                                </div>
                            )}

                            <button
                                className="modal-close-btn"
                                onClick={() => setShowEnrollModal(false)}
                            >
                                Close
                            </button>
                        </motion.div>
                    </div>
                )
            }

            {/* Edit Profile Modal (External Component) */}
            <EditProfileModal
                isOpen={showEditProfileModal}
                onClose={() => setShowEditProfileModal(false)}
                userId={user?.id}
                onUpdateSuccess={() => {
                    setShowEditProfileModal(false);
                    // Refresh the page to ensure the updated name is displayed globally
                    window.location.reload();
                }}
            />
        </div >
    );
};

export default StudentDashboard;