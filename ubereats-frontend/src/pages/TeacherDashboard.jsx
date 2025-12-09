import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {
    Trophy,
    PlusCircle,
    Gamepad2,
    Sparkles,
    Rocket,
    Compass,
    Crown,
    Zap
} from 'lucide-react';
import CourseCard from '../components/CourseCard';
import CreateProjectModal from '../components/CreateProjectModal';
import { getTeacherCourses, createProject } from '../services/api';

/**
 * TeacherDashboard - The "Game Master's View"
 * A gamified dashboard for teachers to manage courses and create projects
 */
const TeacherDashboard = () => {
    const [courses, setCourses] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    const [isModalOpen, setIsModalOpen] = useState(false);

    // Fetch courses on mount
    useEffect(() => {
        fetchCourses();
    }, []);

    const fetchCourses = async () => {
        try {
            setIsLoading(true);
            setError(null);
            const response = await getTeacherCourses();
            setCourses(response.data || []);
        } catch (err) {
            console.error('Failed to fetch courses:', err);
            setError('Failed to load your courses. Please try again.');
            // Demo data for development
            setCourses([
                {
                    id: 1,
                    name: 'Software Quality 2026',
                    code: 'SQ26',
                    semester: 'SEMESTER_1',
                    studentCount: 45,
                    projectCount: 12,
                    averageTeamScore: 85.5,
                    isActive: true,
                },
                {
                    id: 2,
                    name: 'Agile Development Fundamentals',
                    code: 'ADF24',
                    semester: 'SEMESTER_2',
                    studentCount: 32,
                    projectCount: 8,
                    averageTeamScore: 72.3,
                    isActive: true,
                },
                {
                    id: 3,
                    name: 'Web Technologies Advanced',
                    code: 'WTA25',
                    semester: 'SEMESTER_1',
                    studentCount: 28,
                    projectCount: 5,
                    averageTeamScore: 45.0,
                    isActive: true,
                },
                {
                    id: 4,
                    name: 'Database Systems',
                    code: 'DBS25',
                    semester: 'SEMESTER_2',
                    studentCount: 50,
                    projectCount: 15,
                    averageTeamScore: 91.2,
                    isActive: false,
                },
            ]);
        } finally {
            setIsLoading(false);
        }
    };

    const handleCreateProject = async (projectData) => {
        try {
            await createProject(projectData);
            // Refresh courses after creating project
            await fetchCourses();
        } catch (err) {
            console.error('Failed to create project:', err);
            throw err;
        }
    };

    const handleManageCourse = (courseId) => {
        console.log('Managing course:', courseId);
        // Navigate to course management page
        // navigate(`/teacher/courses/${courseId}`);
    };

    // Get user name from localStorage
    const getUserName = () => {
        try {
            const userData = localStorage.getItem('user');
            if (userData) {
                const user = JSON.parse(userData);
                return user.name || user.firstName || 'Game Master';
            }
        } catch {
            // Ignore parsing errors
        }
        return 'Game Master';
    };

    return (
        <div className="min-h-screen bg-gradient-to-br from-slate-50 via-white to-slate-100">
            {/* Background Pattern */}
            <div className="fixed inset-0 overflow-hidden pointer-events-none">
                <div className="absolute -top-40 -right-40 w-96 h-96 bg-violet-200/30 rounded-full blur-3xl" />
                <div className="absolute top-1/2 -left-40 w-80 h-80 bg-blue-200/30 rounded-full blur-3xl" />
                <div className="absolute -bottom-40 right-1/3 w-72 h-72 bg-emerald-200/20 rounded-full blur-3xl" />
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
                    <div className="relative overflow-hidden rounded-3xl bg-gradient-to-r from-violet-600 via-indigo-600 to-purple-600 p-8 shadow-2xl shadow-violet-500/20">
                        {/* Decorative Elements */}
                        <div className="absolute inset-0 overflow-hidden">
                            <div className="absolute -top-20 -right-20 w-64 h-64 bg-white/10 rounded-full blur-3xl" />
                            <div className="absolute -bottom-20 -left-20 w-48 h-48 bg-white/10 rounded-full blur-3xl" />
                            {/* Floating icons */}
                            <motion.div
                                animate={{ y: [0, -10, 0], rotate: [0, 5, 0] }}
                                transition={{ duration: 4, repeat: Infinity }}
                                className="absolute top-6 right-20 text-white/20"
                            >
                                <Trophy size={48} />
                            </motion.div>
                            <motion.div
                                animate={{ y: [0, 10, 0], rotate: [0, -5, 0] }}
                                transition={{ duration: 3, repeat: Infinity, delay: 0.5 }}
                                className="absolute bottom-6 right-40 text-white/15"
                            >
                                <Gamepad2 size={36} />
                            </motion.div>
                        </div>

                        <div className="relative flex flex-col md:flex-row md:items-center md:justify-between gap-4">
                            <div>
                                <div className="flex items-center gap-3 mb-3">
                                    <div className="flex items-center justify-center w-12 h-12 rounded-xl bg-white/20 backdrop-blur-sm">
                                        <Crown size={24} className="text-yellow-300" />
                                    </div>
                                    <span className="px-3 py-1 rounded-full bg-white/20 text-white/90 text-sm font-medium backdrop-blur-sm">
                                        Game Master
                                    </span>
                                </div>
                                <h1 className="text-3xl md:text-4xl font-bold text-white mb-2">
                                    Welcome back, {getUserName()}! 🎮
                                </h1>
                                <p className="text-violet-200 text-lg">
                                    Your adventure continues. Manage your realms and launch new quests.
                                </p>
                            </div>

                            {/* Quick Stats */}
                            <div className="flex gap-4">
                                <div className="flex items-center gap-3 px-4 py-3 rounded-xl bg-white/10 backdrop-blur-sm">
                                    <Zap className="text-yellow-300" size={24} />
                                    <div>
                                        <p className="text-white/70 text-xs">Active Realms</p>
                                        <p className="text-2xl font-bold text-white">
                                            {courses.filter(c => c.isActive).length}
                                        </p>
                                    </div>
                                </div>
                                <div className="flex items-center gap-3 px-4 py-3 rounded-xl bg-white/10 backdrop-blur-sm">
                                    <Sparkles className="text-emerald-300" size={24} />
                                    <div>
                                        <p className="text-white/70 text-xs">Total Quests</p>
                                        <p className="text-2xl font-bold text-white">
                                            {courses.reduce((sum, c) => sum + (c.projectCount || 0), 0)}
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </motion.header>

                {/* Section Title */}
                <div className="flex items-center justify-between mb-6">
                    <div className="flex items-center gap-3">
                        <div className="flex items-center justify-center w-10 h-10 rounded-xl bg-violet-100">
                            <Compass size={20} className="text-violet-600" />
                        </div>
                        <div>
                            <h2 className="text-xl font-bold text-slate-800">Your Realms</h2>
                            <p className="text-sm text-slate-500">Manage your courses and track progress</p>
                        </div>
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
                        <p className="mt-4 text-slate-500 font-medium">Loading your realms...</p>
                    </div>
                )}

                {/* Error State */}
                {error && !isLoading && (
                    <motion.div
                        initial={{ opacity: 0, y: 10 }}
                        animate={{ opacity: 1, y: 0 }}
                        className="bg-rose-50 border border-rose-200 rounded-xl p-4 mb-6"
                    >
                        <p className="text-rose-700 text-sm">{error}</p>
                        <button
                            onClick={fetchCourses}
                            className="mt-2 text-sm font-medium text-rose-600 hover:text-rose-700 underline"
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
                        className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6"
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
                {!isLoading && courses.length === 0 && (
                    <motion.div
                        initial={{ opacity: 0, scale: 0.95 }}
                        animate={{ opacity: 1, scale: 1 }}
                        transition={{ duration: 0.3 }}
                        className="flex flex-col items-center justify-center py-20"
                    >
                        <div className="relative mb-6">
                            <div className="w-32 h-32 rounded-full bg-gradient-to-br from-violet-100 to-indigo-100 flex items-center justify-center">
                                <Rocket size={48} className="text-violet-500" />
                            </div>
                            <motion.div
                                animate={{ scale: [1, 1.2, 1] }}
                                transition={{ duration: 2, repeat: Infinity }}
                                className="absolute -top-2 -right-2 w-8 h-8 rounded-full bg-amber-400 flex items-center justify-center"
                            >
                                <Sparkles size={16} className="text-white" />
                            </motion.div>
                        </div>
                        <h3 className="text-2xl font-bold text-slate-800 mb-2">Start a New Adventure!</h3>
                        <p className="text-slate-500 text-center max-w-md mb-6">
                            No realms discovered yet. Create your first course to begin your journey as a Game Master.
                        </p>
                        <motion.button
                            whileHover={{ scale: 1.05 }}
                            whileTap={{ scale: 0.95 }}
                            onClick={() => setIsModalOpen(true)}
                            className="px-6 py-3 rounded-xl bg-gradient-to-r from-violet-600 to-indigo-600 text-white font-semibold shadow-lg shadow-violet-500/30 hover:shadow-xl transition-shadow flex items-center gap-2"
                        >
                            <PlusCircle size={20} />
                            Create First Quest
                        </motion.button>
                    </motion.div>
                )}
            </div>

            {/* Floating Action Button */}
            <motion.button
                initial={{ scale: 0, opacity: 0 }}
                animate={{ scale: 1, opacity: 1 }}
                transition={{ delay: 0.5, type: "spring", stiffness: 200 }}
                whileHover={{ scale: 1.1 }}
                whileTap={{ scale: 0.9 }}
                onClick={() => setIsModalOpen(true)}
                className="fixed bottom-8 right-8 w-16 h-16 rounded-full bg-gradient-to-r from-emerald-500 to-teal-500 text-white shadow-xl shadow-emerald-500/40 hover:shadow-2xl hover:shadow-emerald-500/50 transition-shadow flex items-center justify-center z-40 group"
            >
                <PlusCircle size={28} className="group-hover:rotate-90 transition-transform duration-300" />

                {/* Tooltip */}
                <span className="absolute right-full mr-3 px-3 py-1.5 rounded-lg bg-slate-800 text-white text-sm font-medium whitespace-nowrap opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none">
                    New Quest
                </span>
            </motion.button>

            {/* Create Project Modal */}
            <CreateProjectModal
                isOpen={isModalOpen}
                onClose={() => setIsModalOpen(false)}
                onSubmit={handleCreateProject}
                courses={courses}
            />
        </div>
    );
};

export default TeacherDashboard;
