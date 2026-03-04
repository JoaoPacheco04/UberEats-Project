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
    getStudentEnrollments,
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
                getStudentEnrollments(user.id).catch(() => ({ data: [] })),
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
    const courseAverage = dashboardData?.courseAverageScore || 0; // Average score across all courses/students

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
                    transition={{ duration: 0.5 }}
                    className="mb-10"
                >
                    <div className="relative overflow-hidden rounded-3xl backdrop-blur-xl p-8 shadow-2xl border border-white/10" style={{ background: 'linear-gradient(135deg, #91E2F2 0%, #BAB5F5 100%)', boxShadow: '0 25px 50px -12px rgba(145, 226, 242, 0.35)' }}>
                        {/* Decorative Elements */}
                        <div className="absolute inset-0 overflow-hidden">
                            <div className="absolute -top-20 -right-20 w-64 h-64 bg-white/10 rounded-full blur-3xl" />
                            <div className="absolute -bottom-20 -left-20 w-48 h-48 bg-white/10 rounded-full blur-3xl" />
                            <Sparkles className="absolute top-4 right-8 text-white/20" size={40} />
                        </div>

                        <div className="relative flex flex-col gap-6">
                            {/* Top row: Welcome text and stats */}
                            <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
                                <div>
                                    <div className="flex items-center gap-3 mb-3">
                                        <div className="flex items-center justify-center w-12 h-12 rounded-xl bg-white/40 backdrop-blur-sm">
                                            <GraduationCap size={24} className="text-slate-700" />
                                        </div>
                                        <span className="px-3 py-1 rounded-full bg-white/40 text-slate-700 text-sm font-medium backdrop-blur-sm">
                                            Student Dashboard
                                        </span>
                                    </div>
                                    <h1 className="text-3xl md:text-4xl font-bold text-slate-800 mb-2">
                                        Welcome, {getUserName()}!
                                    </h1>
                                    <p className="text-slate-600 text-lg">
                                        Track your progress and achievements
                                    </p>
                                </div>

                                {/* Quick Stats Cards */}
                                <div className="flex flex-wrap gap-3">
                                    <div className="flex items-center gap-3 px-4 py-3 rounded-xl bg-white/50 backdrop-blur-sm border border-white/30">
                                        <Trophy className="text-slate-700" size={24} />
                                        <div>
                                            <p className="text-slate-500 text-xs">Points</p>
                                            <p className="text-2xl font-bold text-slate-800">{totalPoints}</p>
                                        </div>
                                    </div>
                                    <div className="flex items-center gap-3 px-4 py-3 rounded-xl bg-white/50 backdrop-blur-sm border border-white/30">
                                        <Award className="text-slate-700" size={24} />
                                        <div>
                                            <p className="text-slate-500 text-xs">Badges</p>
                                            <p className="text-2xl font-bold text-slate-800">{totalBadges}</p>
                                        </div>
                                    </div>
                                    <div className="flex items-center gap-3 px-4 py-3 rounded-xl bg-white/50 backdrop-blur-sm border border-white/30">
                                        <Users className="text-slate-700" size={24} />
                                        <div>
                                            <p className="text-slate-500 text-xs">Teams</p>
                                            <p className="text-2xl font-bold text-slate-800">{teams.length}</p>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            {/* Bottom row: Action buttons */}
                            <div className="flex flex-wrap items-center justify-end gap-3">
                                {/* Profile and Logout buttons */}
                                <div className="flex gap-3">
                                    <button
                                        onClick={() => setShowEditProfileModal(true)}
                                        className="flex items-center gap-2 px-4 py-3 rounded-xl bg-slate-500/20 hover:bg-slate-500/30 backdrop-blur-sm transition-all border border-slate-400/30"
                                    >
                                        <Settings className="text-slate-600" size={20} />
                                        <span className="font-semibold text-slate-600">Edit Profile</span>
                                    </button>
                                    <button
                                        onClick={handleLogout}
                                        className="flex items-center gap-2 px-4 py-3 rounded-xl bg-rose-500/20 hover:bg-rose-500/30 backdrop-blur-sm transition-all border border-rose-400/30"
                                    >
                                        <LogOut className="text-rose-500" size={20} />
                                        <span className="font-semibold text-rose-500">Logout</span>
                                    </button>
                                </div>
                            </div>
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

                {/* Main Content - Bento Box Layout */}
                <div className="flex flex-col gap-6">
                    {/* Top Row: Quick Stats & Analytics */}
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                        {/* Progression Timeline Card (Summary) */}
                        <div className="bg-white rounded-2xl shadow-xl shadow-sky-500/10 border border-slate-200/60 p-5 flex flex-col justify-between">
                            <div>
                                <div className="flex items-center gap-3 mb-4">
                                    <div className="flex items-center justify-center w-9 h-9 rounded-xl bg-gradient-to-br from-sky-400 to-blue-500 shadow-lg shadow-sky-500/30">
                                        <TrendingUp size={18} className="text-white" />
                                    </div>
                                    <div>
                                        <h3 className="text-base font-semibold text-slate-800">Your Progression</h3>
                                        <p className="text-xs text-slate-500">Overall activity summary</p>
                                    </div>
                                </div>
                                <div className="grid grid-cols-3 gap-3 mt-auto">
                                    {/* Calculated counts for overall progression summary */}
                                    <div className="flex flex-col items-center p-3 bg-slate-50 rounded-xl">
                                        <span className="text-xl font-bold text-slate-800">{enrollments.length}</span>
                                        <span className="text-xs text-slate-500">Courses</span>
                                    </div>
                                    <div className="flex flex-col items-center p-3 bg-slate-50 rounded-xl">
                                        <span className="text-xl font-bold text-slate-800">{teams.length}</span>
                                        <span className="text-xs text-slate-500">Teams</span>
                                    </div>
                                    <div className="flex flex-col items-center p-3 bg-slate-50 rounded-xl">
                                        <span className="text-xl font-bold text-slate-800">{achievements.length}</span>
                                        <span className="text-xs text-slate-500">Badges</span>
                                    </div>
                                </div>
                            </div>
                        </div>

                        {/* Recent Achievements Card */}
                        <div className="bg-white rounded-2xl shadow-xl shadow-violet-500/10 border border-slate-200/60 p-5">
                            <div className="flex items-center gap-3 mb-4">
                                <div className="flex items-center justify-center w-9 h-9 rounded-xl bg-gradient-to-br from-violet-500 to-purple-600 shadow-lg shadow-violet-500/30">
                                    <Award size={18} className="text-white" />
                                </div>
                                <div>
                                    <h3 className="text-base font-semibold text-slate-800">Recent Achievements</h3>
                                    <p className="text-xs text-slate-500">Badges you've earned</p>
                                </div>
                            </div>

                            {/* Display recent achievements */}
                            {achievements.length > 0 ? (
                                <div className="space-y-2">
                                    {/* Slice(0, 5) shows only the 5 most recent achievements */}
                                    {achievements.slice(0, 5).map((achievement, index) => (
                                        <motion.div
                                            key={achievement.id}
                                            className="flex items-center gap-3 p-2.5 rounded-xl bg-slate-50 hover:bg-slate-100 transition-colors"
                                            initial={{ opacity: 0, x: -10 }}
                                            animate={{ opacity: 1, x: 0 }}
                                            transition={{ delay: index * 0.1 }}
                                        >
                                            <div className="flex-shrink-0 w-8 h-8 rounded-lg flex items-center justify-center bg-gradient-to-br from-amber-400 to-amber-500 text-white shadow-md shadow-amber-500/20">
                                                <Trophy size={14} />
                                            </div>
                                            <div className="flex-1 min-w-0">
                                                <span className="block font-medium text-slate-800 text-sm truncate">{achievement.badgeName}</span>
                                            </div>
                                            <div className="flex items-center gap-1 px-2 py-1 rounded-full bg-violet-100 text-violet-700 font-semibold text-xs">
                                                +{achievement.points} pts
                                            </div>
                                        </motion.div>
                                    ))}
                                </div>
                            ) : (
                                <div className="text-center py-6 text-slate-400">
                                    <Trophy size={28} className="mx-auto mb-2 text-slate-300" />
                                    <p className="text-sm">Complete tasks to earn badges!</p>
                                </div>
                            )}
                        </div>

                        {/* Score Comparison Card - span block or full width depending on screen size */}
                        <div className="bg-white rounded-2xl shadow-xl shadow-indigo-500/10 border border-slate-200/60 p-5 md:col-span-2 lg:col-span-1">
                            <div className="flex items-center gap-3 mb-4">
                                <div className="flex items-center justify-center w-9 h-9 rounded-xl bg-gradient-to-br from-amber-400 to-orange-500 shadow-lg shadow-amber-500/30">
                                    <Target size={18} className="text-white" />
                                </div>
                                <div>
                                    <h3 className="text-base font-semibold text-slate-800">Score Comparison</h3>
                                    <p className="text-xs text-slate-500">Your performance vs course average</p>
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
                                <div className="score-comparison-summary mt-2 pt-2 border-t border-slate-100 flex justify-center w-full">
                                    {/* Conditional message based on performance vs average */}
                                    {totalPoints >= courseAverage ? (
                                        <span className="flex items-center gap-1 px-3 py-1.5 rounded-lg bg-emerald-50 text-emerald-700 font-medium text-xs">
                                            <TrendingUp size={16} />
                                            You're above average!
                                        </span>
                                    ) : (
                                        <span className="text-xs text-slate-500 font-medium">
                                            Keep working to reach the average
                                        </span>
                                    )}
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Bottom Row: Main Lists */}
                    <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                        {/* My Courses Section */}
                        <section className="bg-white rounded-2xl shadow-xl shadow-indigo-500/10 border border-slate-200/60 p-5 flex flex-col">
                            <div className="flex items-center justify-between mb-6">
                                <div className="flex items-center gap-3">
                                    <div className="flex items-center justify-center w-10 h-10 rounded-xl bg-gradient-to-br from-violet-500 to-indigo-600 shadow-lg shadow-violet-500/30">
                                        <BookOpen size={20} className="text-white" />
                                    </div>
                                    <div>
                                        <h2 className="text-xl font-bold text-slate-800">My Courses</h2>
                                        <p className="text-sm text-slate-500">Courses you're enrolled in</p>
                                    </div>
                                </div>
                                {/* Button to open the enrollment modal */}
                                <button
                                    className="flex items-center gap-2 px-4 py-2 rounded-xl bg-gradient-to-r from-violet-600 to-indigo-600 text-white font-semibold shadow-lg shadow-violet-500/30 hover:scale-105 transition-all outline-none border-none"
                                    onClick={() => setShowEnrollModal(true)}
                                >
                                    <Plus size={16} /> Enroll
                                </button>
                            </div>

                            {/* Display enrolled courses or an empty state */}
                            {enrollments.length > 0 ? (
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 flex-1">
                                    {enrollments.map((enrollment) => (
                                        <motion.div
                                            key={enrollment.id}
                                            className="course-card h-full"
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
                                <div className="empty-state m-auto">
                                    <BookOpen size={48} />
                                    <h3>No Courses Yet</h3>
                                    <p>Enroll in a course to get started</p>
                                </div>
                            )}
                        </section>

                        {/* My Teams Section */}
                        <section className="bg-white rounded-2xl shadow-xl shadow-indigo-500/10 border border-slate-200/60 p-5 flex flex-col">
                            <div className="flex items-center justify-between mb-6">
                                <div className="flex items-center gap-3">
                                    <div className="flex items-center justify-center w-10 h-10 rounded-xl bg-gradient-to-br from-emerald-500 to-teal-600 shadow-lg shadow-emerald-500/30">
                                        <Users size={20} className="text-white" />
                                    </div>
                                    <div>
                                        <h2 className="text-xl font-bold text-slate-800">My Teams</h2>
                                        <p className="text-sm text-slate-500">Teams you're part of</p>
                                    </div>
                                </div>
                            </div>

                            {/* Display student's teams or an empty state */}
                            {teams.length > 0 ? (
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 flex-1">
                                    {teams.map((team) => (
                                        <motion.div
                                            key={team.id}
                                            className="team-card h-full"
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
                                <div className="empty-state m-auto">
                                    <Users size={48} />
                                    <h3>No Teams Yet</h3>
                                    <p>Join a team through your course</p>
                                </div>
                            )}
                        </section>
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
                currentUser={user}
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