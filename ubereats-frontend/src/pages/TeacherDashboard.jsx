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
    BarChart3
} from 'lucide-react';
import CourseCard from '../components/CourseCard';
import CreateResourceModal from '../components/CreateResourceModal';
import { getTeacherCourses, createCourse, createProject, getCourseEnrollments, getStudentDashboard, getTeamByProject, getTeamPoints, getProjectsByCourse, getCurrentUser } from '../services/api';

/**
 * TeacherDashboard - Professional academic dashboard
 * Displays teacher's courses and allows creating new courses/projects
 */
const TeacherDashboard = () => {
    const navigate = useNavigate();
    const [courses, setCourses] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    const [isModalOpen, setIsModalOpen] = useState(false);

    // Rankings state
    const [studentRankings, setStudentRankings] = useState([]);
    const [teamRankings, setTeamRankings] = useState([]);
    const [rankingsLoading, setRankingsLoading] = useState(false);
    const [selectedCourseFilter, setSelectedCourseFilter] = useState('all');

    // Fetch courses on mount
    useEffect(() => {
        fetchCourses();
    }, []);

    // Fetch rankings when courses change
    useEffect(() => {
        if (courses.length > 0) {
            fetchRankings();
        }
    }, [courses]);

    const fetchCourses = async () => {
        try {
            setIsLoading(true);
            setError(null);
            const response = await getTeacherCourses();
            setCourses(response.data || []);
        } catch (err) {
            console.error('Failed to fetch courses:', err);
            setError('Failed to load your courses. Please try again.');
            setCourses([]); // No demo data - only real DB data
        } finally {
            setIsLoading(false);
        }
    };

    const fetchRankings = async () => {
        setRankingsLoading(true);
        try {
            const studentMap = new Map();
            const teamMap = new Map();

            // For each course, get enrollments and projects
            for (const course of courses) {
                try {
                    // Get students via enrollments
                    const enrollmentsRes = await getCourseEnrollments(course.id);
                    const enrollments = enrollmentsRes.data || [];

                    for (const enrollment of enrollments) {
                        if (enrollment.studentId && !studentMap.has(enrollment.studentId)) {
                            try {
                                const dashRes = await getStudentDashboard(enrollment.studentId);
                                const stats = dashRes.data;
                                studentMap.set(enrollment.studentId, {
                                    id: enrollment.studentId,
                                    name: enrollment.studentName || `Student ${enrollment.studentId}`,
                                    globalScore: stats?.globalScore || 0,
                                    courseName: course.name
                                });
                            } catch {
                                // Skip if can't get dashboard
                            }
                        }
                    }

                    // Get teams via projects
                    const projectsRes = await getProjectsByCourse(course.id);
                    const projects = projectsRes.data || [];

                    for (const project of projects) {
                        try {
                            const teamRes = await getTeamByProject(project.id);
                            const team = teamRes.data;

                            if (team && !teamMap.has(team.id)) {
                                try {
                                    const pointsRes = await getTeamPoints(team.id);
                                    const points = pointsRes.data?.totalPoints || 0;
                                    teamMap.set(team.id, {
                                        id: team.id,
                                        name: team.name,
                                        points: points,
                                        projectName: project.name,
                                        courseName: course.name
                                    });
                                } catch {
                                    teamMap.set(team.id, {
                                        id: team.id,
                                        name: team.name,
                                        points: 0,
                                        projectName: project.name,
                                        courseName: course.name
                                    });
                                }
                            }
                        } catch {
                            // Skip if can't get team
                        }
                    }
                } catch {
                    // Skip if can't get course data
                }
            }

            // Convert to arrays and sort
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
        } finally {
            setRankingsLoading(false);
        }
    };

    const handleCreateCourse = async (courseData) => {
        try {
            await createCourse(courseData);
            await fetchCourses(); // Refresh courses
        } catch (err) {
            console.error('Failed to create course:', err);
            throw err;
        }
    };

    const handleCreateProject = async (projectData) => {
        try {
            await createProject(projectData);
            await fetchCourses(); // Refresh to update project counts
        } catch (err) {
            console.error('Failed to create project:', err);
            throw err;
        }
    };

    const handleManageCourse = (courseId) => {
        navigate(`/teacher/courses/${courseId}`);
    };

    // Get user name from localStorage
    const getUserName = () => {
        try {
            const userData = localStorage.getItem('user');
            if (userData) {
                const user = JSON.parse(userData);
                return user.fullName || user.firstName || 'Teacher';
            }
        } catch {
            // Ignore parsing errors
        }
        return 'Teacher';
    };

    // Logout handler
    const handleLogout = () => {
        localStorage.removeItem('user');
        localStorage.removeItem('token');
        navigate('/');
    };

    // Calculate stats
    const activeCourses = courses.filter(c => c.isActive !== false).length;
    const totalProjects = courses.reduce((sum, c) => sum + (c.projectCount || 0), 0);

    return (
        <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-100">
            {/* Animated Background Pattern */}
            <div className="fixed inset-0 overflow-hidden pointer-events-none">
                {/* Animated gradient orbs - softer, more elegant */}
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
                {/* Subtle dots pattern */}
                <div className="absolute inset-0 bg-[radial-gradient(circle_at_1px_1px,_rgba(99,102,241,0.08)_1px,_transparent_1px)] bg-[size:32px_32px]" />
            </div>

            <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                {/* Header Section */}
                <motion.header
                    initial={{ opacity: 0, y: -20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.5 }}
                    className="mb-10"
                >
                    {/* Welcome Banner */}
                    <div className="relative overflow-hidden rounded-3xl bg-gradient-to-r from-violet-600/90 via-indigo-600/90 to-purple-600/90 backdrop-blur-xl p-8 shadow-2xl shadow-violet-500/20 border border-white/10">
                        {/* Decorative Elements */}
                        <div className="absolute inset-0 overflow-hidden">
                            <div className="absolute -top-20 -right-20 w-64 h-64 bg-white/10 rounded-full blur-3xl" />
                            <div className="absolute -bottom-20 -left-20 w-48 h-48 bg-white/10 rounded-full blur-3xl" />
                            <Sparkles className="absolute top-4 right-8 text-white/20" size={40} />
                        </div>

                        <div className="relative flex flex-col md:flex-row md:items-center md:justify-between gap-4">
                            <div>
                                <div className="flex items-center gap-3 mb-3">
                                    <div className="flex items-center justify-center w-12 h-12 rounded-xl bg-white/20 backdrop-blur-sm">
                                        <GraduationCap size={24} className="text-white" />
                                    </div>
                                    <span className="px-3 py-1 rounded-full bg-white/20 text-white/90 text-sm font-medium backdrop-blur-sm">
                                        Teacher Dashboard
                                    </span>
                                </div>
                                <h1 className="text-3xl md:text-4xl font-bold text-white mb-2">
                                    Welcome, {getUserName()}!
                                </h1>
                                <p className="text-violet-200 text-lg">
                                    Manage your courses and track student progress
                                </p>
                            </div>

                            {/* Quick Stats & Actions */}
                            <div className="flex flex-wrap gap-3">
                                <div className="flex items-center gap-3 px-4 py-3 rounded-xl bg-white/10 backdrop-blur-sm border border-white/10">
                                    <BookOpen className="text-white" size={24} />
                                    <div>
                                        <p className="text-white/70 text-xs">Active Courses</p>
                                        <p className="text-2xl font-bold text-white">{activeCourses}</p>
                                    </div>
                                </div>
                                <div className="flex items-center gap-3 px-4 py-3 rounded-xl bg-white/10 backdrop-blur-sm border border-white/10">
                                    <FolderOpen className="text-white" size={24} />
                                    <div>
                                        <p className="text-white/70 text-xs">Total Projects</p>
                                        <p className="text-2xl font-bold text-white">{totalProjects}</p>
                                    </div>
                                </div>
                                <button
                                    onClick={() => navigate('/teacher/analytics')}
                                    className="flex items-center gap-2 px-4 py-3 rounded-xl bg-indigo-500/20 hover:bg-indigo-500/30 backdrop-blur-sm transition-all border border-indigo-400/30"
                                >
                                    <BarChart3 className="text-indigo-400" size={24} />
                                    <div className="text-left">
                                        <p className="text-indigo-100/80 text-xs">View</p>
                                        <p className="text-base font-bold text-indigo-400">Analytics</p>
                                    </div>
                                </button>
                                <button
                                    onClick={() => navigate('/teacher/badges')}
                                    className="flex items-center gap-2 px-4 py-3 rounded-xl bg-amber-500/20 hover:bg-amber-500/30 backdrop-blur-sm transition-all border border-amber-400/30"
                                >
                                    <Award className="text-amber-400" size={24} />
                                    <div className="text-left">
                                        <p className="text-amber-100/80 text-xs">Manage</p>
                                        <p className="text-base font-bold text-amber-400">Badges</p>
                                    </div>
                                </button>
                            </div>
                        </div>
                        {/* Logout Button */}
                        <button
                            onClick={handleLogout}
                            className="flex items-center gap-2 px-4 py-3 rounded-xl bg-rose-500/20 hover:bg-rose-500/30 backdrop-blur-sm transition-all border border-rose-400/30"
                        >
                            <LogOut className="text-rose-400" size={24} />
                            <div className="text-left">
                                <p className="text-rose-100/80 text-xs">Sign Out</p>
                                <p className="text-base font-bold text-rose-400">Logout</p>
                            </div>
                        </button>
                    </div>
                </motion.header>

                {/* Main Content - Side by Side Layout */}
                <div className="grid grid-cols-1 xl:grid-cols-3 gap-8">
                    {/* Left Column - Courses (2/3 width) */}
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

                        {/* Loading State */}
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

                        {/* Error State */}
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
                                    onClick={fetchCourses}
                                    className="text-sm font-medium text-rose-600 hover:text-rose-700 underline"
                                >
                                    Try again
                                </button>
                            </motion.div>
                        )}

                        {/* Courses Grid */}
                        {!isLoading && courses.length > 0 && (
                            <motion.div
                                initial={{ opacity: 0 }}
                                animate={{ opacity: 1 }}
                                transition={{ duration: 0.3 }}
                                className="grid grid-cols-1 md:grid-cols-2 gap-4"
                            >
                                {courses.map((course, index) => (
                                    <CourseCard
                                        key={course.id}
                                        course={course}
                                        index={index}
                                        onManage={handleManageCourse}
                                    />
                                ))}
                            </motion.div>
                        )}

                        {/* Empty State */}
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
                                    onClick={() => setIsModalOpen(true)}
                                    className="px-6 py-3 rounded-xl bg-gradient-to-r from-violet-600 to-indigo-600 text-white font-semibold shadow-lg shadow-violet-500/30 flex items-center gap-2"
                                >
                                    <PlusCircle size={20} />
                                    Create First Course
                                </motion.button>
                            </motion.div>
                        )}
                    </div>

                    {/* Right Column - Leaderboards (1/3 width) */}
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
                            {/* Course Filter Dropdown */}
                            <select
                                value={selectedCourseFilter}
                                onChange={(e) => setSelectedCourseFilter(e.target.value)}
                                className="px-3 py-2 rounded-xl bg-white border border-slate-200 text-sm text-slate-700 shadow-sm focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent"
                            >
                                <option value="all">All Courses</option>
                                {courses.map(course => (
                                    <option key={course.id} value={course.name}>{course.name}</option>
                                ))}
                            </select>
                        </div>

                        {/* Student Rankings */}
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

                            {rankingsLoading ? (
                                <div className="flex justify-center py-6">
                                    <div className="w-6 h-6 rounded-full border-2 border-violet-200 border-t-violet-500 animate-spin" />
                                </div>
                            ) : studentRankings.length > 0 ? (
                                <div className="space-y-2">
                                    {studentRankings
                                        .slice(0, 5).map((student, index) => (
                                            <div
                                                key={student.id}
                                                className="flex items-center gap-3 p-2.5 rounded-xl bg-slate-50 hover:bg-slate-100 transition-colors"
                                            >
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
                                                <div className="flex items-center gap-1 px-2 py-1 rounded-full bg-violet-100 text-violet-700 font-semibold text-xs">
                                                    <Award size={12} />
                                                    {student.globalScore}
                                                </div>
                                            </div>
                                        ))}
                                </div>
                            ) : (
                                <div className="text-center py-6 text-slate-400">
                                    <Users size={28} className="mx-auto mb-2 text-slate-300" />
                                    <p className="text-sm">No student data yet</p>
                                </div>
                            )}
                        </div>

                        {/* Team Rankings */}
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

                            {rankingsLoading ? (
                                <div className="flex justify-center py-6">
                                    <div className="w-6 h-6 rounded-full border-2 border-emerald-200 border-t-emerald-500 animate-spin" />
                                </div>
                            ) : teamRankings.length > 0 ? (
                                <div className="space-y-2">
                                    {teamRankings
                                        .filter(team => selectedCourseFilter === 'all' || team.courseName === selectedCourseFilter)
                                        .slice(0, 5).map((team, index) => (
                                            <div
                                                key={team.id}
                                                className="flex items-center gap-3 p-2.5 rounded-xl bg-slate-50 hover:bg-slate-100 transition-colors"
                                            >
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
                                                <div className="flex items-center gap-1 px-2 py-1 rounded-full bg-emerald-100 text-emerald-700 font-semibold text-xs">
                                                    <Trophy size={12} />
                                                    {team.points}
                                                </div>
                                            </div>
                                        ))}
                                </div>
                            ) : (
                                <div className="text-center py-6 text-slate-400">
                                    <Users size={28} className="mx-auto mb-2 text-slate-300" />
                                    <p className="text-sm">No team data yet</p>
                                </div>
                            )}
                        </div>
                    </div>
                </div>

                {/* Floating Action Button */}
                <motion.button
                    initial={{ scale: 0, opacity: 0 }}
                    animate={{ scale: 1, opacity: 1 }}
                    transition={{ delay: 0.5, type: "spring", stiffness: 200 }}
                    whileHover={{ scale: 1.1 }}
                    whileTap={{ scale: 0.9 }}
                    onClick={() => setIsModalOpen(true)}
                    className="fixed bottom-8 right-8 w-16 h-16 rounded-full bg-gradient-to-r from-violet-600 to-indigo-600 text-white shadow-xl shadow-violet-500/40 hover:shadow-2xl hover:shadow-violet-500/50 transition-shadow flex items-center justify-center z-40 group"
                >
                    <PlusCircle size={28} className="group-hover:rotate-90 transition-transform duration-300" />

                    {/* Tooltip */}
                    <span className="absolute right-full mr-3 px-3 py-1.5 rounded-lg bg-slate-800 text-white text-sm font-medium whitespace-nowrap opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none">
                        New Course / Project
                    </span>
                </motion.button>

                {/* Create Resource Modal */}
                <CreateResourceModal
                    isOpen={isModalOpen}
                    onClose={() => setIsModalOpen(false)}
                    onSubmitCourse={handleCreateCourse}
                    onSubmitProject={handleCreateProject}
                    courses={courses}
                />


            </div>
        </div>
    );
};

export default TeacherDashboard;
