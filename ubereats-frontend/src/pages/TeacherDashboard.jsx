/**
 * Teacher Dashboard Page Component
 * Main dashboard for teachers showing courses, projects, and management options.
 * 
 * @author Joao
 * @author Yeswanth
 * @version 1.2.0
 */
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import {
    PlusCircle,
    GraduationCap,
    Compass,
    BookOpen,
    FolderOpen,
    AlertCircle,
    Award,
    Trophy,
    Users,
    TrendingUp,
    LogOut,
    Sparkles,
    BarChart3,
    Settings
} from 'lucide-react';
// Import child components
import CourseCard from '../components/CourseCard';
import CreateResourceModal from '../components/CreateResourceModal'; // Used for creating a course/project
import EditProfileModal from '../components/EditProfileModal';
// Import API service functions
import { getTeacherCourses, createCourse, createProject, getCourseEnrollments, getStudentDashboard, getTeamByProject, getTeamPoints, getProjectsByCourse, getCurrentUser } from '../services/api';

/**
 * TeacherDashboard - Professional academic dashboard
 * Displays teacher's courses and allows creating new courses/projects.
 * It also fetches and displays aggregated student and team rankings across all courses.
 */
const TeacherDashboard = () => {
    // Hook for programmatic navigation (e.g., after logout or course selection)
    const navigate = useNavigate();

    // --- State Management for Courses and UI ---
    const [courses, setCourses] = useState([]);
    const [isLoading, setIsLoading] = useState(true); // Tracks initial course loading state
    const [error, setError] = useState(null); // Stores any course fetching error message
    const [isModalOpen, setIsModalOpen] = useState(false); // Controls visibility of the Create Resource Modal
    const [showEditProfileModal, setShowEditProfileModal] = useState(false); // Controls visibility of the Edit Profile Modal

    // --- State Management for Rankings/Leaderboards ---
    const [studentRankings, setStudentRankings] = useState([]);
    const [teamRankings, setTeamRankings] = useState([]);
    const [rankingsLoading, setRankingsLoading] = useState(false); // Tracks ranking data loading state
    const [selectedCourseFilter, setSelectedCourseFilter] = useState('all'); // Controls which course's data is shown in the team leaderboard filter

    // --- Effects ---

    // Effect 1: Fetches courses on component mount
    useEffect(() => {
        fetchCourses();
    }, []); // Empty dependency array means it runs once on mount

    // Effect 2: Fetches rankings whenever the list of courses changes (i.e., after initial fetch or a new course is created)
    useEffect(() => {
        if (courses.length > 0) {
            fetchRankings();
        }
    }, [courses]); // Depends on the 'courses' state

    // --- API Functions ---

    /**
     * Asynchronously fetches all courses associated with the current teacher.
     */
    const fetchCourses = async () => {
        try {
            setIsLoading(true);
            setError(null);
            const response = await getTeacherCourses();
            // Assuming response.data is an array of course objects
            setCourses(response.data || []);
        } catch (err) {
            console.error('Failed to fetch courses:', err);
            setError('Failed to load your courses. Please try again.');
            setCourses([]);
        } finally {
            setIsLoading(false);
        }
    };

    /**
     * Asynchronously fetches and compiles student and team rankings across all courses.
     * This is a complex, multi-step asynchronous operation that aggregates data.
     */
    const fetchRankings = async () => {
        setRankingsLoading(true);
        try {
            // Maps are used to ensure each student/team is counted only once, even if they appear in multiple enrollments/projects
            const studentMap = new Map();
            const teamMap = new Map();

            // Iterate over all fetched courses to gather data
            for (const course of courses) {
                try {
                    // 1. Get Enrollments (for Student Data)
                    const enrollmentsRes = await getCourseEnrollments(course.id);
                    const enrollments = enrollmentsRes.data || [];

                    for (const enrollment of enrollments) {
                        // Process student only if they have an ID and haven't been added to the map yet
                        if (enrollment.studentId && !studentMap.has(enrollment.studentId)) {
                            try {
                                // Get detailed student dashboard/score data
                                const dashRes = await getStudentDashboard(enrollment.studentId);
                                const stats = dashRes.data;
                                studentMap.set(enrollment.studentId, {
                                    id: enrollment.studentId,
                                    name: enrollment.studentName || `Student ${enrollment.studentId}`,
                                    globalScore: stats?.globalScore || 0, // Use globalScore for student ranking
                                    courseName: course.name // Store course name for filter/display
                                });
                            } catch {
                                // Gracefully skip this student if dashboard data fails
                            }
                        }
                    }

                    // 2. Get Projects (for Team Data)
                    const projectsRes = await getProjectsByCourse(course.id);
                    const projects = projectsRes.data || [];

                    for (const project of projects) {
                        try {
                            // Get team associated with the project
                            const teamRes = await getTeamByProject(project.id);
                            const team = teamRes.data;

                            if (team && !teamMap.has(team.id)) {
                                try {
                                    // Get team's points
                                    const pointsRes = await getTeamPoints(team.id);
                                    const points = pointsRes.data?.totalPoints || 0;
                                    teamMap.set(team.id, {
                                        id: team.id,
                                        name: team.name,
                                        points: points, // Use totalPoints for team ranking
                                        projectName: project.name,
                                        courseName: course.name // Store course name for filter
                                    });
                                } catch {
                                    // Set points to 0 if fetching points fails
                                    teamMap.set(team.id, { id: team.id, name: team.name, points: 0, projectName: project.name, courseName: course.name });
                                }
                            }
                        } catch {
                            // Gracefully skip if team data for the project fails
                        }
                    }
                } catch {
                    // Gracefully skip if course-specific data (enrollments/projects) fails
                }
            }

            // Convert Maps to arrays, sort descending, and take the top 10 for display
            const studentList = Array.from(studentMap.values())
                .sort((a, b) => b.globalScore - a.globalScore)
                .slice(0, 10);
            const teamList = Array.from(teamMap.values())
                .sort((a, b) => b.points - a.points)
                .slice(0, 10);

            setStudentRankings(studentList);
            setTeamRankings(teamList);
        } catch (err) {
            console.error('Failed to fetch rankings:', err);
            // Error state for rankings is generally silent to not block the main dashboard view
        } finally {
            setRankingsLoading(false);
        }
    };

    /**
     * Handles creation of a new course, then refreshes the course list.
     * @param {object} courseData - Data for the new course.
     */
    const handleCreateCourse = async (courseData) => {
        try {
            await createCourse(courseData);
            await fetchCourses(); // Refresh courses list to show the new course
        } catch (err) {
            console.error('Failed to create course:', err);
            throw err; // Re-throw to be handled by the modal component (e.g., show an error message)
        }
    };

    /**
     * Handles creation of a new project, then refreshes the course list (to update project counts).
     * @param {object} projectData - Data for the new project.
     */
    const handleCreateProject = async (projectData) => {
        try {
            await createProject(projectData);
            await fetchCourses(); // Refresh to update project counts in the header stats
        } catch (err) {
            console.error('Failed to create project:', err);
            throw err;
        }
    };

    /**
     * Navigates the teacher to the detailed course management page.
     * @param {string} courseId - ID of the course to manage.
     */
    const handleManageCourse = (courseId) => {
        navigate(`/teacher/courses/${courseId}`);
    };

    /**
     * Retrieves the current user's name from localStorage for the welcome message.
     */
    const getUserName = () => {
        try {
            const userData = localStorage.getItem('user');
            if (userData) {
                const user = JSON.parse(userData);
                return user.fullName || user.firstName || 'Teacher';
            }
        } catch {
            // Ignore parsing errors and return default
        }
        return 'Teacher';
    };

    /**
     * Clears user session data and redirects to the login/landing page.
     */
    const handleLogout = () => {
        localStorage.removeItem('user');
        localStorage.removeItem('token');
        navigate('/');
    };

    // --- Calculated Values (Derived State) ---
    const activeCourses = courses.filter(c => c.isActive !== false).length;
    // Calculate total projects by summing up 'projectCount' property from all courses
    const totalProjects = courses.reduce((sum, c) => sum + (c.projectCount || 0), 0);

    // --- Component JSX (Rendering) ---
    return (
        // Main container with a subtle background gradient
        <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-100">
            {/* Animated Background Pattern (Framer Motion for dynamic, smooth UI) */}
            <div className="fixed inset-0 overflow-hidden pointer-events-none">
                {/* Animated gradient orbs - positioned absolute and moving via Framer Motion's 'animate' property */}
                <motion.div
                    animate={{
                        x: [0, 50, 0],
                        y: [0, -30, 0],
                        scale: [1, 1.1, 1],
                    }}
                    transition={{ duration: 8, repeat: Infinity, ease: "easeInOut" }}
                    className="absolute -top-40 -right-40 w-[600px] h-[600px] bg-gradient-to-br from-indigo-200/50 to-purple-200/30 rounded-full blur-3xl"
                />
                <motion.div
                    animate={{
                        x: [0, -30, 0],
                        y: [0, 50, 0],
                        scale: [1, 1.15, 1],
                    }}
                    transition={{ duration: 10, repeat: Infinity, ease: "easeInOut", delay: 1 }}
                    className="absolute top-1/3 -left-40 w-[500px] h-[500px] bg-gradient-to-br from-cyan-200/40 to-blue-200/30 rounded-full blur-3xl"
                />
                <motion.div
                    animate={{
                        x: [0, 40, 0],
                        y: [0, -40, 0],
                        scale: [1, 1.2, 1],
                    }}
                    transition={{ duration: 12, repeat: Infinity, ease: "easeInOut", delay: 2 }}
                    className="absolute -bottom-40 right-1/4 w-[450px] h-[450px] bg-gradient-to-br from-violet-200/40 to-pink-200/30 rounded-full blur-3xl"
                />
                <motion.div
                    animate={{
                        x: [0, -20, 0],
                        y: [0, 30, 0],
                    }}
                    transition={{ duration: 6, repeat: Infinity, ease: "easeInOut" }}
                    className="absolute top-1/2 right-1/3 w-[300px] h-[300px] bg-gradient-to-br from-emerald-200/30 to-teal-200/20 rounded-full blur-3xl"
                />
                {/* Subtle dots pattern for texture */}
                <div className="absolute inset-0 bg-[radial-gradient(circle_at_1px_1px,_rgba(99,102,241,0.08)_1px,_transparent_1px)] bg-[size:32px_32px]" />
            </div>

            <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                {/* Header Section (Welcome Banner) */}
                <motion.header
                    initial={{ opacity: 0, y: -20 }} // Initial state for entry animation
                    animate={{ opacity: 1, y: 0 }} // Target state
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
                                            Teacher Dashboard
                                        </span>
                                    </div>
                                    <h1 className="text-3xl md:text-4xl font-bold text-slate-800 mb-2">
                                        Welcome, {getUserName()}!
                                    </h1>
                                    <p className="text-slate-600 text-lg">
                                        Manage your courses and track student progress
                                    </p>
                                </div>

                                {/* Quick Stats Cards */}
                                <div className="flex flex-wrap gap-3">
                                    <div className="flex items-center gap-3 px-4 py-3 rounded-xl bg-white/50 backdrop-blur-sm border border-white/30">
                                        <BookOpen className="text-slate-700" size={24} />
                                        <div>
                                            <p className="text-slate-500 text-xs">Active Courses</p>
                                            <p className="text-2xl font-bold text-slate-800">{activeCourses}</p>
                                        </div>
                                    </div>
                                    <div className="flex items-center gap-3 px-4 py-3 rounded-xl bg-white/50 backdrop-blur-sm border border-white/30">
                                        <FolderOpen className="text-slate-700" size={24} />
                                        <div>
                                            <p className="text-slate-500 text-xs">Total Projects</p>
                                            <p className="text-2xl font-bold text-slate-800">{totalProjects}</p>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            {/* Bottom row: Action buttons */}
                            <div className="flex flex-wrap items-center justify-between gap-3">
                                {/* Feature buttons */}
                                <div className="flex flex-wrap gap-3">
                                    <button
                                        onClick={() => navigate('/teacher/analytics')} // Navigate to Analytics page
                                        className="flex items-center gap-2 px-4 py-3 rounded-xl bg-indigo-500/20 hover:bg-indigo-500/30 backdrop-blur-sm transition-all border border-indigo-400/30"
                                    >
                                        <BarChart3 className="text-indigo-600" size={20} />
                                        <span className="font-semibold text-indigo-600">Analytics</span>
                                    </button>
                                    <button
                                        onClick={() => navigate('/teacher/badges')} // Navigate to Badges management page
                                        className="flex items-center gap-2 px-4 py-3 rounded-xl bg-amber-500/20 hover:bg-amber-500/30 backdrop-blur-sm transition-all border border-amber-400/30"
                                    >
                                        <Award className="text-amber-600" size={20} />
                                        <span className="font-semibold text-amber-600">Badges</span>
                                    </button>
                                </div>

                                {/* Profile and Logout buttons */}
                                <div className="flex gap-3">
                                    <button
                                        onClick={() => setShowEditProfileModal(true)} // Open Edit Profile modal
                                        className="flex items-center gap-2 px-4 py-3 rounded-xl bg-slate-500/20 hover:bg-slate-500/30 backdrop-blur-sm transition-all border border-slate-400/30"
                                    >
                                        <Settings className="text-slate-600" size={20} />
                                        <span className="font-semibold text-slate-600">Edit Profile</span>
                                    </button>
                                    <button
                                        onClick={handleLogout} // Trigger logout function
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

                {/* Main Content - Two-column layout (2/3 for Courses, 1/3 for Leaderboards) */}
                <div className="grid grid-cols-1 xl:grid-cols-3 gap-8">
                    {/* Left Column - Courses */}
                    <div className="xl:col-span-2 space-y-6">
                        {/* Section Title */}
                        <div className="flex items-center gap-3">
                            <div className="flex items-center justify-center w-10 h-10 rounded-xl bg-gradient-to-br from-violet-500 to-indigo-600 shadow-lg shadow-violet-500/30">
                                <Compass size={20} className="text-white" />
                            </div>
                            <div>
                                <h2 className="text-xl font-bold text-slate-800">Your Courses</h2>
                                <p className="text-sm text-slate-500">Manage courses and track performance</p>
                            </div>
                        </div>

                        {/* Loading State UI (Animated Spinner) */}
                        {isLoading && (
                            <div className="flex flex-col items-center justify-center py-20">
                                <motion.div
                                    animate={{ rotate: 360 }}
                                    transition={{ duration: 2, repeat: Infinity, ease: "linear" }}
                                    className="w-16 h-16 rounded-full border-4 border-violet-200 border-t-violet-600"
                                />
                                <p className="mt-4 text-slate-500 font-medium">Loading courses...</p>
                            </div>
                        )}

                        {/* Error State UI (Conditionally rendered when an error exists and not loading) */}
                        {error && !isLoading && (
                            <motion.div
                                initial={{ opacity: 0, y: 10 }}
                                animate={{ opacity: 1, y: 0 }}
                                className="bg-rose-50 border border-rose-200 rounded-xl p-4 flex items-center gap-3"
                            >
                                <AlertCircle className="text-rose-500" size={20} />
                                <div className="flex-1">
                                    <p className="text-rose-600 text-sm">{error}</p>
                                </div>
                                <button
                                    onClick={fetchCourses} // Button to re-trigger data fetch
                                    className="text-sm font-medium text-rose-600 hover:text-rose-700 underline"
                                >
                                    Try again
                                </button>
                            </motion.div>
                        )}

                        {/* Courses Grid (Conditionally rendered when loaded and courses exist) */}
                        {!isLoading && courses.length > 0 && (
                            <motion.div
                                initial={{ opacity: 0 }}
                                animate={{ opacity: 1 }}
                                transition={{ duration: 0.3 }}
                                className="grid grid-cols-1 md:grid-cols-2 gap-4"
                            >
                                {/* Map over courses and render a CourseCard for each */}
                                {courses.map((course, index) => (
                                    <CourseCard
                                        key={course.id}
                                        course={course}
                                        index={index}
                                        onManage={handleManageCourse} // Pass handler for navigation
                                    />
                                ))}
                            </motion.div>
                        )}

                        {/* Empty State UI (Conditionally rendered when loaded, no error, and no courses) */}
                        {!isLoading && !error && courses.length === 0 && (
                            <motion.div
                                initial={{ opacity: 0, scale: 0.95 }}
                                animate={{ opacity: 1, scale: 1 }}
                                transition={{ duration: 0.3 }}
                                className="flex flex-col items-center justify-center py-16 bg-white rounded-2xl border border-slate-200 shadow-lg"
                            >
                                <div className="w-24 h-24 rounded-full bg-gradient-to-br from-violet-100 to-indigo-100 flex items-center justify-center mb-4">
                                    <BookOpen size={40} className="text-violet-500" />
                                </div>
                                <h3 className="text-xl font-bold text-slate-800 mb-2">No Courses Yet</h3>
                                <p className="text-slate-500 text-center max-w-md mb-6">
                                    Create your first course to start managing projects.
                                </p>
                                <motion.button
                                    whileHover={{ scale: 1.05 }}
                                    whileTap={{ scale: 0.95 }}
                                    onClick={() => setIsModalOpen(true)} // Open Create Resource modal
                                    className="px-6 py-3 rounded-xl bg-gradient-to-r from-violet-600 to-indigo-600 text-white font-semibold shadow-lg shadow-violet-500/30 flex items-center gap-2"
                                >
                                    <PlusCircle size={20} />
                                    Create First Course
                                </motion.button>
                            </motion.div>
                        )}
                    </div>

                    {/* Right Column - Leaderboards */}
                    <div className="space-y-6">
                        {/* Leaderboard Header with Course Filter */}
                        <div className="flex items-center justify-between gap-3 mb-4">
                            <div className="flex items-center gap-3">
                                <div className="flex items-center justify-center w-10 h-10 rounded-xl bg-gradient-to-br from-amber-400 to-orange-500 shadow-lg shadow-amber-500/30">
                                    <TrendingUp size={20} className="text-white" />
                                </div>
                                <div>
                                    <h2 className="text-xl font-bold text-slate-800">Leaderboards</h2>
                                    <p className="text-sm text-slate-500">Top performers</p>
                                </div>
                            </div>
                            {/* Course Filter Dropdown - allows filtering the team leaderboard */}
                            <select
                                value={selectedCourseFilter}
                                onChange={(e) => setSelectedCourseFilter(e.target.value)}
                                className="px-3 py-2 rounded-xl bg-white border border-slate-200 text-sm text-slate-700 shadow-sm focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent"
                            >
                                <option value="all">All Courses</option>
                                {/* Dynamically generate options from the fetched courses */}
                                {courses.map(course => (
                                    <option key={course.id} value={course.name}>{course.name}</option>
                                ))}
                            </select>
                        </div>

                        {/* Student Rankings Card */}
                        <div className="bg-white rounded-2xl shadow-xl shadow-indigo-500/10 border border-slate-200/60 p-5">
                            <div className="flex items-center gap-3 mb-4">
                                <div className="flex items-center justify-center w-9 h-9 rounded-xl bg-gradient-to-br from-violet-500 to-indigo-600 shadow-lg shadow-violet-500/30">
                                    <Trophy size={18} className="text-white" />
                                </div>
                                <div>
                                    <h3 className="text-base font-semibold text-slate-800">Top Students</h3>
                                    <p className="text-xs text-slate-500">By Global Score</p>
                                </div>
                            </div>

                            {/* Conditional rendering for student rankings */}
                            {rankingsLoading ? (
                                <div className="flex justify-center py-6">
                                    <div className="w-6 h-6 rounded-full border-2 border-violet-200 border-t-violet-500 animate-spin" />
                                </div>
                            ) : studentRankings.length > 0 ? (
                                <div className="space-y-2">
                                    {studentRankings
                                        .slice(0, 5).map((student, index) => ( // Display top 5
                                            <div
                                                key={student.id}
                                                className="flex items-center gap-3 p-2.5 rounded-xl bg-slate-50 hover:bg-slate-100 transition-colors"
                                            >
                                                {/* Rank Badge styling based on index (1st, 2nd, 3rd place colors) */}
                                                <div className={`flex-shrink-0 w-7 h-7 rounded-full flex items-center justify-center font-bold text-xs ${index === 0 ? 'bg-gradient-to-br from-amber-400 to-yellow-500 text-amber-900 shadow-lg shadow-amber-400/40' :
                                                    index === 1 ? 'bg-gradient-to-br from-slate-300 to-slate-400 text-slate-700' :
                                                        index === 2 ? 'bg-gradient-to-br from-orange-300 to-orange-400 text-orange-800' :
                                                            'bg-slate-200 text-slate-600'
                                                    }`}>
                                                    {index + 1}
                                                </div>
                                                <div className="flex-1 min-w-0">
                                                    <p className="font-medium text-slate-800 text-sm truncate">{student.name}</p>
                                                    <p className="text-xs text-slate-500 truncate">{student.courseName}</p>
                                                </div>
                                                {/* Global Score Display */}
                                                <div className="flex items-center gap-1 px-2 py-1 rounded-full bg-violet-100 text-violet-700 font-semibold text-xs">
                                                    <Award size={12} />
                                                    {student.globalScore}
                                                </div>
                                            </div>
                                        ))}
                                </div>
                            ) : (
                                // No student data empty state
                                <div className="text-center py-6 text-slate-400">
                                    <Users size={28} className="mx-auto mb-2 text-slate-300" />
                                    <p className="text-sm">No student data yet</p>
                                </div>
                            )}
                        </div>

                        {/* Team Rankings Card */}
                        <div className="bg-white rounded-2xl shadow-xl shadow-emerald-500/10 border border-slate-200/60 p-5">
                            <div className="flex items-center gap-3 mb-4">
                                <div className="flex items-center justify-center w-9 h-9 rounded-xl bg-gradient-to-br from-emerald-500 to-teal-600 shadow-lg shadow-emerald-500/30">
                                    <Users size={18} className="text-white" />
                                </div>
                                <div>
                                    <h3 className="text-base font-semibold text-slate-800">Top Teams</h3>
                                    <p className="text-xs text-slate-500">By Total Points</p>
                                </div>
                            </div>

                            {/* Conditional rendering for team rankings */}
                            {rankingsLoading ? (
                                <div className="flex justify-center py-6">
                                    <div className="w-6 h-6 rounded-full border-2 border-emerald-200 border-t-emerald-500 animate-spin" />
                                </div>
                            ) : teamRankings.length > 0 ? (
                                <div className="space-y-2">
                                    {teamRankings
                                        // Filter teams based on the selected course dropdown value
                                        .filter(team => selectedCourseFilter === 'all' || team.courseName === selectedCourseFilter)
                                        .slice(0, 5).map((team, index) => ( // Display top 5 after filtering
                                            <div
                                                key={team.id}
                                                className="flex items-center gap-3 p-2.5 rounded-xl bg-slate-50 hover:bg-slate-100 transition-colors"
                                            >
                                                {/* Rank Badge styling */}
                                                <div className={`flex-shrink-0 w-7 h-7 rounded-full flex items-center justify-center font-bold text-xs ${index === 0 ? 'bg-gradient-to-br from-amber-400 to-yellow-500 text-amber-900 shadow-lg shadow-amber-400/40' :
                                                    index === 1 ? 'bg-gradient-to-br from-slate-300 to-slate-400 text-slate-700' :
                                                        index === 2 ? 'bg-gradient-to-br from-orange-300 to-orange-400 text-orange-800' :
                                                            'bg-slate-200 text-slate-600'
                                                    }`}>
                                                    {index + 1}
                                                </div>
                                                <div className="flex-1 min-w-0">
                                                    <p className="font-medium text-slate-800 text-sm truncate">{team.name}</p>
                                                    <p className="text-xs text-slate-500 truncate">{team.projectName}</p>
                                                </div>
                                                {/* Team Points Display */}
                                                <div className="flex items-center gap-1 px-2 py-1 rounded-full bg-emerald-100 text-emerald-700 font-semibold text-xs">
                                                    <Trophy size={12} />
                                                    {team.points}
                                                </div>
                                            </div>
                                        ))}
                                </div>
                            ) : (
                                // No team data empty state
                                <div className="text-center py-6 text-slate-400">
                                    <Users size={28} className="mx-auto mb-2 text-slate-300" />
                                    <p className="text-sm">No team data yet</p>
                                </div>
                            )}
                        </div>
                    </div>
                </div>

                {/* Modals are rendered outside the main layout flow */}
                <CreateResourceModal
                    isOpen={isModalOpen}
                    onClose={() => setIsModalOpen(false)}
                    onCreateCourse={handleCreateCourse}
                    onCreateProject={handleCreateProject}
                    courses={courses} // Pass courses to the modal so projects can be linked
                />
                <EditProfileModal
                    isOpen={showEditProfileModal}
                    onClose={() => setShowEditProfileModal(false)}
                />
            </div>
        </div>
    );
};

export default TeacherDashboard;