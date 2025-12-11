import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import {
    GraduationCap,
    BookOpen,
    Users,
    Award,
    Trophy,
    TrendingUp,
    LogOut,
    Sparkles,
    Target,
    Calendar,
    Clock,
    ChevronRight,
    Plus,
    UserPlus,
    FolderOpen,
    AlertCircle,
    Loader2,
    Star
} from 'lucide-react';
import {
    getStudentDashboard,
    getUserAchievements,
    getUserTeams,
    getStudentEnrollments,
    getAvailableCourses,
    enrollStudent,
    getCurrentUser
} from '../services/api';
import './StudentDashboard.css';

const StudentDashboard = () => {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Dashboard data
    const [dashboardData, setDashboardData] = useState(null);
    const [achievements, setAchievements] = useState([]);
    const [teams, setTeams] = useState([]);
    const [enrollments, setEnrollments] = useState([]);
    const [availableCourses, setAvailableCourses] = useState([]);

    // Modal state
    const [showEnrollModal, setShowEnrollModal] = useState(false);
    const [enrolling, setEnrolling] = useState(false);

    const user = getCurrentUser();

    useEffect(() => {
        if (user?.id) {
            fetchAllData();
        }
    }, [user?.id]);

    const fetchAllData = async () => {
        try {
            setLoading(true);
            setError(null);

            const [dashRes, achieveRes, teamsRes, enrollRes, coursesRes] = await Promise.all([
                getStudentDashboard(user.id).catch(() => ({ data: {} })),
                getUserAchievements(user.id).catch(() => ({ data: [] })),
                getUserTeams(user.id).catch(() => ({ data: [] })),
                getStudentEnrollments(user.id).catch(() => ({ data: [] })),
                getAvailableCourses().catch(() => ({ data: [] }))
            ]);

            setDashboardData(dashRes.data || {});
            setAchievements(achieveRes.data || []);
            setTeams(teamsRes.data || []);
            setEnrollments(enrollRes.data || []);

            // Filter out already enrolled courses
            const enrolledCourseIds = (enrollRes.data || []).map(e => e.courseId);
            const available = (coursesRes.data || []).filter(c => !enrolledCourseIds.includes(c.id));
            setAvailableCourses(available);
        } catch (err) {
            console.error('Error fetching dashboard data:', err);
            setError('Failed to load dashboard. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    const handleEnroll = async (courseId) => {
        try {
            setEnrolling(true);
            await enrollStudent({ courseId, studentId: user.id });
            await fetchAllData();
            setShowEnrollModal(false);
        } catch (err) {
            console.error('Error enrolling:', err);
            alert('Failed to enroll in course');
        } finally {
            setEnrolling(false);
        }
    };

    const handleLogout = () => {
        localStorage.removeItem('user');
        localStorage.removeItem('token');
        navigate('/login');
    };

    const getUserName = () => {
        return user?.fullName || user?.firstName || 'Student';
    };

    // Calculate stats
    const totalPoints = dashboardData?.globalScore || achievements.reduce((sum, a) => sum + (a.points || 0), 0);
    const totalBadges = achievements.length;
    const courseAverage = dashboardData?.courseAverage || 0;

    if (loading) {
        return (
            <div className="student-dashboard-loading">
                <Loader2 className="spinner" size={48} />
                <p>Loading your dashboard...</p>
            </div>
        );
    }

    return (
        <div className="student-dashboard">
            {/* Animated Background */}
            <div className="dashboard-bg">
                <motion.div
                    animate={{ x: [0, 50, 0], y: [0, -30, 0], scale: [1, 1.1, 1] }}
                    transition={{ duration: 8, repeat: Infinity, ease: "easeInOut" }}
                    className="bg-orb orb-1"
                />
                <motion.div
                    animate={{ x: [0, -30, 0], y: [0, 50, 0], scale: [1, 1.15, 1] }}
                    transition={{ duration: 10, repeat: Infinity, ease: "easeInOut", delay: 1 }}
                    className="bg-orb orb-2"
                />
                <motion.div
                    animate={{ x: [0, 40, 0], y: [0, -40, 0], scale: [1, 1.2, 1] }}
                    transition={{ duration: 12, repeat: Infinity, ease: "easeInOut", delay: 2 }}
                    className="bg-orb orb-3"
                />
                <div className="dots-pattern" />
            </div>

            <div className="dashboard-container">
                {/* Header Section */}
                <motion.header
                    initial={{ opacity: 0, y: -20 }}
                    animate={{ opacity: 1, y: 0 }}
                    className="dashboard-header"
                >
                    <div className="header-banner">
                        <div className="header-decorations">
                            <div className="decoration-circle top-right" />
                            <div className="decoration-circle bottom-left" />
                            <Sparkles className="sparkle-icon" size={40} />
                        </div>

                        <div className="header-content">
                            <div className="header-info">
                                <div className="header-badge-row">
                                    <div className="header-icon-box">
                                        <GraduationCap size={24} />
                                    </div>
                                    <span className="header-label">Student Dashboard</span>
                                </div>
                                <h1>Welcome, {getUserName()}!</h1>
                                <p>Track your progress and achievements</p>
                            </div>

                            {/* Quick Stats */}
                            <div className="header-stats">
                                <div className="stat-box">
                                    <Trophy size={24} />
                                    <div>
                                        <span className="stat-label">Total Points</span>
                                        <span className="stat-value">{totalPoints}</span>
                                    </div>
                                </div>
                                <div className="stat-box">
                                    <Award size={24} />
                                    <div>
                                        <span className="stat-label">Badges</span>
                                        <span className="stat-value">{totalBadges}</span>
                                    </div>
                                </div>
                                <div className="stat-box">
                                    <Users size={24} />
                                    <div>
                                        <span className="stat-label">Teams</span>
                                        <span className="stat-value">{teams.length}</span>
                                    </div>
                                </div>
                                <button onClick={handleLogout} className="logout-btn">
                                    <LogOut size={24} />
                                    <div>
                                        <span className="stat-label">Sign Out</span>
                                        <span className="stat-value">Logout</span>
                                    </div>
                                </button>
                            </div>
                        </div>
                    </div>
                </motion.header>

                {error && (
                    <div className="error-banner">
                        <AlertCircle size={20} />
                        <span>{error}</span>
                        <button onClick={fetchAllData}>Retry</button>
                    </div>
                )}

                {/* Main Content Grid */}
                <div className="dashboard-grid">
                    {/* Left Column - Main Content */}
                    <div className="main-column">
                        {/* My Courses Section */}
                        <section className="dashboard-section">
                            <div className="section-header">
                                <div className="section-title">
                                    <div className="section-icon courses-icon">
                                        <BookOpen size={20} />
                                    </div>
                                    <div>
                                        <h2>My Courses</h2>
                                        <p>Courses you're enrolled in</p>
                                    </div>
                                </div>
                                <button
                                    className="action-btn primary"
                                    onClick={() => setShowEnrollModal(true)}
                                >
                                    <Plus size={18} />
                                    Enroll in Course
                                </button>
                            </div>

                            {enrollments.length > 0 ? (
                                <div className="courses-grid">
                                    {enrollments.map((enrollment) => (
                                        <motion.div
                                            key={enrollment.id}
                                            className="course-card"
                                            whileHover={{ y: -4 }}
                                            onClick={() => navigate(`/student/courses/${enrollment.courseId}`)}
                                        >
                                            <div className="course-card-header">
                                                <BookOpen size={24} />
                                                <span className="course-status">Enrolled</span>
                                            </div>
                                            <h3>{enrollment.courseName}</h3>
                                            <p className="course-teacher">
                                                {enrollment.teacherName || 'Teacher'}
                                            </p>
                                            <div className="course-footer">
                                                <span className="enrolled-date">
                                                    <Calendar size={14} />
                                                    {new Date(enrollment.enrolledAt).toLocaleDateString()}
                                                </span>
                                                <ChevronRight size={18} />
                                            </div>
                                        </motion.div>
                                    ))}
                                </div>
                            ) : (
                                <div className="empty-state">
                                    <BookOpen size={48} />
                                    <h3>No Courses Yet</h3>
                                    <p>Enroll in a course to get started</p>
                                    <button
                                        className="action-btn primary"
                                        onClick={() => setShowEnrollModal(true)}
                                    >
                                        <Plus size={18} />
                                        Browse Courses
                                    </button>
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
                                        <p>Teams you're a member of</p>
                                    </div>
                                </div>
                            </div>

                            {teams.length > 0 ? (
                                <div className="teams-grid">
                                    {teams.map((team) => (
                                        <motion.div
                                            key={team.id}
                                            className="team-card"
                                            whileHover={{ y: -4 }}
                                            onClick={() => navigate(`/student/teams/${team.id}`)}
                                        >
                                            <div className="team-card-header">
                                                <Users size={24} />
                                                <span className="member-count">
                                                    {team.memberCount || 0} members
                                                </span>
                                            </div>
                                            <h3>{team.name}</h3>
                                            <p className="team-project">{team.projectName || 'No project'}</p>
                                            <div className="team-footer">
                                                <span className="team-role">
                                                    <Star size={14} />
                                                    {team.role || 'Member'}
                                                </span>
                                                <ChevronRight size={18} />
                                            </div>
                                        </motion.div>
                                    ))}
                                </div>
                            ) : (
                                <div className="empty-state">
                                    <Users size={48} />
                                    <h3>No Teams Yet</h3>
                                    <p>Join a team through your enrolled courses</p>
                                </div>
                            )}
                        </section>
                    </div>

                    {/* Right Column - Stats & Achievements */}
                    <div className="side-column">
                        {/* Score Comparison */}
                        <div className="stat-card">
                            <div className="stat-card-header">
                                <div className="stat-card-icon comparison-icon">
                                    <TrendingUp size={18} />
                                </div>
                                <div>
                                    <h3>Your Performance</h3>
                                    <p>vs Course Average</p>
                                </div>
                            </div>
                            <div className="comparison-chart">
                                <div className="comparison-bar">
                                    <div className="bar-label">You</div>
                                    <div className="bar-track">
                                        <motion.div
                                            className="bar-fill your-score"
                                            initial={{ width: 0 }}
                                            animate={{ width: `${Math.min((totalPoints / Math.max(totalPoints, courseAverage, 100)) * 100, 100)}%` }}
                                            transition={{ duration: 1, delay: 0.5 }}
                                        />
                                    </div>
                                    <div className="bar-value">{totalPoints}</div>
                                </div>
                                <div className="comparison-bar">
                                    <div className="bar-label">Avg</div>
                                    <div className="bar-track">
                                        <motion.div
                                            className="bar-fill avg-score"
                                            initial={{ width: 0 }}
                                            animate={{ width: `${Math.min((courseAverage / Math.max(totalPoints, courseAverage, 100)) * 100, 100)}%` }}
                                            transition={{ duration: 1, delay: 0.7 }}
                                        />
                                    </div>
                                    <div className="bar-value">{courseAverage}</div>
                                </div>
                            </div>
                            {totalPoints > courseAverage && (
                                <div className="comparison-message success">
                                    <Trophy size={16} />
                                    You're above average! Keep it up!
                                </div>
                            )}
                        </div>

                        {/* Recent Achievements */}
                        <div className="stat-card achievements-card">
                            <div className="stat-card-header">
                                <div className="stat-card-icon achievements-icon">
                                    <Award size={18} />
                                </div>
                                <div>
                                    <h3>Recent Achievements</h3>
                                    <p>Your badge collection</p>
                                </div>
                            </div>

                            {achievements.length > 0 ? (
                                <div className="achievements-list">
                                    {achievements.slice(0, 5).map((achievement, index) => (
                                        <motion.div
                                            key={achievement.id}
                                            className="achievement-item"
                                            initial={{ opacity: 0, x: -20 }}
                                            animate={{ opacity: 1, x: 0 }}
                                            transition={{ delay: index * 0.1 }}
                                        >
                                            <div className="achievement-icon">
                                                <Award size={20} />
                                            </div>
                                            <div className="achievement-info">
                                                <span className="achievement-name">{achievement.badgeName}</span>
                                                <span className="achievement-date">
                                                    <Clock size={12} />
                                                    {new Date(achievement.awardedAt).toLocaleDateString()}
                                                </span>
                                            </div>
                                            <div className="achievement-points">
                                                +{achievement.points}
                                            </div>
                                        </motion.div>
                                    ))}
                                </div>
                            ) : (
                                <div className="empty-state small">
                                    <Award size={32} />
                                    <p>No achievements yet</p>
                                </div>
                            )}
                        </div>

                        {/* Progression Timeline */}
                        <div className="stat-card progression-card">
                            <div className="stat-card-header">
                                <div className="stat-card-icon progression-icon">
                                    <Target size={18} />
                                </div>
                                <div>
                                    <h3>Progression</h3>
                                    <p>Your journey so far</p>
                                </div>
                            </div>
                            <div className="progression-stats">
                                <div className="progression-stat">
                                    <span className="prog-value">{enrollments.length}</span>
                                    <span className="prog-label">Courses</span>
                                </div>
                                <div className="progression-stat">
                                    <span className="prog-value">{teams.length}</span>
                                    <span className="prog-label">Teams</span>
                                </div>
                                <div className="progression-stat">
                                    <span className="prog-value">{totalBadges}</span>
                                    <span className="prog-label">Badges</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Enroll Modal */}
            {showEnrollModal && (
                <div className="modal-overlay" onClick={() => setShowEnrollModal(false)}>
                    <motion.div
                        className="modal-content enroll-modal"
                        initial={{ scale: 0.9, opacity: 0 }}
                        animate={{ scale: 1, opacity: 1 }}
                        onClick={e => e.stopPropagation()}
                    >
                        <h2>Enroll in a Course</h2>
                        <p className="modal-subtitle">Select a course to join</p>

                        {availableCourses.length > 0 ? (
                            <div className="available-courses-list">
                                {availableCourses.map((course) => (
                                    <div key={course.id} className="available-course-item">
                                        <div className="course-info">
                                            <BookOpen size={20} />
                                            <div>
                                                <h4>{course.name}</h4>
                                                <p>{course.description || 'No description'}</p>
                                            </div>
                                        </div>
                                        <button
                                            className="enroll-btn"
                                            onClick={() => handleEnroll(course.id)}
                                            disabled={enrolling}
                                        >
                                            {enrolling ? <Loader2 className="spinner" size={16} /> : <UserPlus size={16} />}
                                            Enroll
                                        </button>
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <div className="empty-state">
                                <FolderOpen size={48} />
                                <h3>No Courses Available</h3>
                                <p>All courses are either full or you're already enrolled</p>
                            </div>
                        )}

                        <div className="modal-actions">
                            <button
                                className="cancel-btn"
                                onClick={() => setShowEnrollModal(false)}
                            >
                                Close
                            </button>
                        </div>
                    </motion.div>
                </div>
            )}
        </div>
    );
};

export default StudentDashboard;
