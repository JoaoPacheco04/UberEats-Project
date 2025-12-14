import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion'; // For animations
import {
    BarChart3,
    TrendingUp,
    Target,
    Users,
    ArrowLeft,
    CheckCircle2,
    Clock,
    Zap,
    Activity,
    PieChart,
    Download
} from 'lucide-react'; // Icon library
import {
    LineChart,
    Line,
    BarChart,
    Bar,
    PieChart as RechartsPie,
    Pie,
    Cell,
    AreaChart,
    Area,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Legend,
    ResponsiveContainer
} from 'recharts'; // Charting library
import {
    getTeacherCourses,
    getProjectsByCourse,
    getProjectAnalytics,
    getProjectBurndown,
    getSprintsByProject
} from '../services/api'; // API service calls
import './TeacherAnalytics.css'; // Component-specific styling

// Color palettes for charts
const COLORS = ['#6366f1', '#8b5cf6', '#a855f7', '#d946ef', '#ec4899', '#f43f5e'];
const MOOD_COLORS = {
    EXCELLENT: '#22c55e',
    GOOD: '#84cc16',
    NEUTRAL: '#eab308',
    CONCERNED: '#f97316',
    CRITICAL: '#ef4444'
};

const TeacherAnalytics = () => {
    // Hooks for navigation and state management
    const navigate = useNavigate();
    const [courses, setCourses] = useState([]);
    const [selectedCourse, setSelectedCourse] = useState(null);
    const [projects, setProjects] = useState([]);
    const [selectedProject, setSelectedProject] = useState(null);
    const [analytics, setAnalytics] = useState([]); // Raw analytics data (e.g., daily/sprint metrics)
    const [sprints, setSprints] = useState([]); // Sprint details for the table
    const [burndown, setBurndown] = useState(null); // Burndown chart data
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // --- EFFECT HOOKS ---

    // 1. Initial Load: Check role and fetch teacher's courses.
    useEffect(() => {
        const user = JSON.parse(localStorage.getItem('user') || '{}');
        // Role-based access control: Redirect non-teachers
        if (user.role !== 'TEACHER') {
            navigate('/login');
            return;
        }
        fetchCourses();
    }, [navigate]);

    // 2. Course Change: Fetch projects associated with the newly selected course.
    useEffect(() => {
        if (selectedCourse) {
            fetchProjects(selectedCourse);
        }
    }, [selectedCourse]);

    // 3. Project Change: Fetch detailed analytics (metrics, sprints, burndown) for the selected project.
    useEffect(() => {
        if (selectedProject) {
            fetchAnalytics(selectedProject);
        }
    }, [selectedProject]);

    // --- DATA FETCHING FUNCTIONS ---

    const fetchCourses = async () => {
        try {
            const res = await getTeacherCourses();
            setCourses(res.data || []);
            // Set the first course as default selection
            if (res.data?.length > 0) {
                setSelectedCourse(res.data[0].id);
            }
        } catch (err) {
            console.error('Error fetching courses:', err);
            setError('Failed to load courses');
        } finally {
            setLoading(false);
        }
    };

    const fetchProjects = async (courseId) => {
        try {
            const res = await getProjectsByCourse(courseId);
            setProjects(res.data || []);
            // Set the first project as default selection, or null if none
            if (res.data?.length > 0) {
                setSelectedProject(res.data[0].id);
            } else {
                setSelectedProject(null);
                setAnalytics([]); // Clear analytics if no projects
            }
        } catch (err) {
            console.error('Error fetching projects:', err);
        }
    };

    const fetchAnalytics = async (projectId) => {
        try {
            // Fetch all core analytics data concurrently using Promise.all
            const [analyticsRes, sprintsRes, burndownRes] = await Promise.all([
                getProjectAnalytics(projectId).catch(() => ({ data: [] })),
                getSprintsByProject(projectId).catch(() => ({ data: [] })),
                getProjectBurndown(projectId).catch(() => ({ data: null }))
            ]);
            setAnalytics(analyticsRes.data || []);
            setSprints(sprintsRes.data || []);
            setBurndown(burndownRes.data);
        } catch (err) {
            console.error('Error fetching analytics:', err);
        }
    };

    // --- DATA PROCESSING FOR CHARTS ---

    // 1. Prepares data for the Velocity Trend chart (AreaChart).
    const getVelocityData = () => {
        if (!analytics.length) return [];

        // Sort data by recorded date to show progression over time
        const sortedData = [...analytics].sort((a, b) =>
            new Date(a.recordedDate) - new Date(b.recordedDate)
        );

        // Group by date/sprint to ensure each point is the latest status for that period
        const dateMap = new Map();
        sortedData.forEach(a => {
            const dateKey = a.recordedDate ? a.recordedDate.split('T')[0] : a.sprintName;
            const label = a.sprintName + (a.recordedDate ? ` (${new Date(a.recordedDate).toLocaleDateString()})` : '');

            // Use the latest entry for a given date/sprint
            if (!dateMap.has(dateKey) ||
                (a.recordedDate && new Date(a.recordedDate) > new Date(dateMap.get(dateKey).recordedDate))) {
                dateMap.set(dateKey, {
                    ...a,
                    label: dateKey.length > 10 ? dateKey : label
                });
            }
        });

        const entries = Array.from(dateMap.values());

        // Map the entries to the format required by the Recharts component
        return entries.map((a, idx) => ({
            name: a.sprintName || `Day ${idx + 1}`,
            date: a.recordedDate ? new Date(a.recordedDate).toLocaleDateString() : '',
            velocity: Number(a.velocity) || 0, // Velocity: a key Agile metric (points/time period)
            completedPoints: Number(a.storyPointsCompleted) || 0,
            totalPoints: Number(a.totalStoryPoints) || 0,
            completionRate: a.totalStoryPoints > 0
                ? Math.round((a.storyPointsCompleted / a.totalStoryPoints) * 100)
                : 0
        }));
    };

    // 2. Prepares data for the Story Points by Sprint chart (BarChart).
    const getStoryPointsData = () => {
        if (!analytics.length) return [];

        // Get the latest analytics entry for *each* sprint to show the final state
        const sprintMap = new Map();
        analytics.forEach(a => {
            if (!sprintMap.has(a.sprintName) ||
                new Date(a.recordedDate) > new Date(sprintMap.get(a.sprintName).recordedDate)) {
                sprintMap.set(a.sprintName, a);
            }
        });

        // Map to format for stacked bar chart: Completed vs. Remaining Story Points
        return Array.from(sprintMap.values()).map(a => ({
            name: a.sprintName,
            completed: Number(a.storyPointsCompleted) || 0,
            remaining: (Number(a.totalStoryPoints) || 0) - (Number(a.storyPointsCompleted) || 0)
        }));
    };

    // 3. Prepares data for the overall Task Completion Pie chart.
    const getTaskCompletionData = () => {
        // Sum up total completed and total tasks from all analytics entries
        const totals = analytics.reduce((acc, a) => {
            acc.completed += a.completedTasks || 0;
            acc.total += a.totalTasks || 0;
            return acc;
        }, { completed: 0, total: 0 });

        const remaining = totals.total - totals.completed;

        if (totals.total === 0) return [];

        // Return data for the Pie chart
        return [
            { name: 'Completed', value: totals.completed, color: '#22c55e' },
            { name: 'Remaining', value: remaining, color: '#94a3b8' }
        ];
    };

    // 4. Prepares data for the Team Mood History Pie chart.
    const getTeamMoodData = () => {
        const moodCounts = {};
        // Count the occurrences of each team mood recorded in the analytics data
        analytics.forEach(a => {
            if (a.teamMood) {
                moodCounts[a.teamMood] = (moodCounts[a.teamMood] || 0) + 1;
            }
        });

        // Map counts to the required Pie chart format
        return Object.entries(moodCounts).map(([mood, count]) => ({
            name: mood,
            value: count,
            color: MOOD_COLORS[mood] || '#94a3b8'
        }));
    };

    // 5. Calculates the top-level summary statistics (Avg Velocity, Completion Rate, etc.).
    const getSummaryStats = () => {
        if (!analytics.length) return { velocity: 0, completion: 0, tasks: 0, sprints: 0 };

        // Get the latest recorded entry for each unique sprint
        const latestBySpprint = new Map();
        analytics.forEach(a => {
            if (!latestBySpprint.has(a.sprintId) ||
                new Date(a.recordedDate) > new Date(latestBySpprint.get(a.sprintId).recordedDate)) {
                latestBySpprint.set(a.sprintId, a);
            }
        });

        const entries = Array.from(latestBySpprint.values());
        
        // Calculate average velocity across all sprints
        const avgVelocity = entries.reduce((sum, a) => sum + (Number(a.velocity) || 0), 0) / entries.length;
        
        // Calculate total tasks completed and total tasks planned/created
        const totalCompleted = entries.reduce((sum, a) => sum + (a.completedTasks || 0), 0);
        const totalTasks = entries.reduce((sum, a) => sum + (a.totalTasks || 0), 0);
        
        // Calculate average completion rate
        const avgCompletion = totalTasks > 0 ? (totalCompleted / totalTasks) * 100 : 0;

        return {
            velocity: avgVelocity.toFixed(1),
            completion: avgCompletion.toFixed(0),
            tasks: totalCompleted,
            sprints: entries.length
        };
    };

    const stats = getSummaryStats();

    // --- RENDER LOGIC ---

    // Show a loading screen while data is being fetched
    if (loading) {
        return (
            <div className="analytics-loading">
                <div className="loading-spinner"></div>
                <p>Loading analytics...</p>
            </div>
        );
    }

    return (
        <div className="analytics-container">
            {/* Decorative background elements using CSS */}
            <div className="analytics-bg">
                <div className="bg-orb bg-orb-1"></div>
                <div className="bg-orb bg-orb-2"></div>
                <div className="bg-orb bg-orb-3"></div>
                <div className="bg-pattern"></div>
            </div>

            <div className="analytics-content">
                {/* Header section with navigation and project/course selectors */}
                <motion.header
                    className="analytics-header"
                    initial={{ opacity: 0, y: -20 }}
                    animate={{ opacity: 1, y: 0 }}
                >
                    <div className="header-left">
                        <button className="back-btn" onClick={() => navigate('/teacher/dashboard')}>
                            <ArrowLeft size={20} />
                            Back
                        </button>
                        <div className="header-title">
                            <BarChart3 className="title-icon" />
                            <h1>Analytics Dashboard</h1>
                        </div>
                    </div>

                    {/* Course and Project Selection Dropdowns */}
                    <div className="header-selectors">
                        <select
                            value={selectedCourse || ''}
                            onChange={(e) => setSelectedCourse(Number(e.target.value))}
                            className="selector"
                        >
                            {courses.map(course => (
                                <option key={course.id} value={course.id}>{course.name}</option>
                            ))}
                        </select>
                        <select
                            value={selectedProject || ''}
                            onChange={(e) => setSelectedProject(Number(e.target.value))}
                            className="selector"
                        >
                            {projects.map(project => (
                                <option key={project.id} value={project.id}>{project.name}</option>
                            ))}
                        </select>
                    </div>
                </motion.header>

                {/* Summary Statistics Cards (Velocity, Completion, Tasks, Sprints) */}
                <motion.div
                    className="stats-grid"
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: 0.1 }}
                >
                    {/* Stat Card: Average Velocity (Agile speed metric) */}
                    <div className="stat-card velocity">
                        <div className="stat-icon"><Zap size={24} /></div>
                        <div className="stat-info">
                            <span className="stat-value">{stats.velocity}</span>
                            <span className="stat-label">Avg Velocity</span>
                        </div>
                    </div>
                    {/* Stat Card: Completion Rate */}
                    <div className="stat-card completion">
                        <div className="stat-icon"><Target size={24} /></div>
                        <div className="stat-info">
                            <span className="stat-value">{stats.completion}%</span>
                            <span className="stat-label">Completion Rate</span>
                        </div>
                    </div>
                    {/* Stat Card: Total Tasks Done */}
                    <div className="stat-card tasks">
                        <div className="stat-icon"><CheckCircle2 size={24} /></div>
                        <div className="stat-info">
                            <span className="stat-value">{stats.tasks}</span>
                            <span className="stat-label">Tasks Done</span>
                        </div>
                    </div>
                    {/* Stat Card: Sprints Tracked */}
                    <div className="stat-card sprints">
                        <div className="stat-icon"><Activity size={24} /></div>
                        <div className="stat-info">
                            <span className="stat-value">{stats.sprints}</span>
                            <span className="stat-label">Sprints Tracked</span>
                        </div>
                    </div>
                </motion.div>

                {/* Conditional Rendering: Show charts if data exists, otherwise show 'No Data' message */}
                {analytics.length > 0 ? (
                    <div className="charts-grid">
                        {/* 1. Velocity Trend Chart (AreaChart) */}
                        <motion.div
                            className="chart-card full-width"
                            initial={{ opacity: 0, y: 20 }}
                            animate={{ opacity: 1, y: 0 }}
                            transition={{ delay: 0.2 }}
                        >
                            <div className="chart-header">
                                <TrendingUp className="chart-icon" />
                                <h3>Velocity Trend</h3>
                                
                            </div>
                            <div className="chart-body">
                                <ResponsiveContainer width="100%" height={300}>
                                    <AreaChart data={getVelocityData()}>
                                        <defs>{/* Gradient definition for the area chart fill */}</defs>
                                        <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
                                        <XAxis dataKey="name" stroke="#64748b" fontSize={12} />
                                        <YAxis stroke="#64748b" fontSize={12} />
                                        <Tooltip contentStyle={{/* Custom Tooltip Styling */}} />
                                        <Legend />
                                        <Area type="monotone" dataKey="velocity" stroke="#6366f1" strokeWidth={3} fill="url(#velocityGradient)" name="Velocity" />
                                        <Line type="monotone" dataKey="completedPoints" stroke="#22c55e" strokeWidth={2} dot={{ fill: '#22c55e', r: 4 }} name="Completed Points" />
                                    </AreaChart>
                                </ResponsiveContainer>
                            </div>
                        </motion.div>

                        {/* 2. Story Points Comparison Chart (Stacked BarChart) */}
                        <motion.div
                            className="chart-card"
                            initial={{ opacity: 0, y: 20 }}
                            animate={{ opacity: 1, y: 0 }}
                            transition={{ delay: 0.3 }}
                        >
                            <div className="chart-header">
                                <BarChart3 className="chart-icon" />
                                <h3>Story Points by Sprint</h3>
                                
                            </div>
                            <div className="chart-body">
                                <ResponsiveContainer width="100%" height={280}>
                                    <BarChart data={getStoryPointsData()}>
                                        <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
                                        <XAxis dataKey="name" stroke="#64748b" fontSize={12} />
                                        <YAxis stroke="#64748b" fontSize={12} />
                                        <Tooltip contentStyle={{/* Custom Tooltip Styling */}} />
                                        <Legend />
                                        {/* Stacked Bars: Completed (Green) and Remaining (Grey) */}
                                        <Bar dataKey="completed" stackId="a" fill="#22c55e" name="Completed" radius={[4, 4, 0, 0]} />
                                        <Bar dataKey="remaining" stackId="a" fill="#94a3b8" name="Remaining" radius={[4, 4, 0, 0]} />
                                    </BarChart>
                                </ResponsiveContainer>
                            </div>
                        </motion.div>

                        {/* 3. Task Completion Pie Chart */}
                        <motion.div
                            className="chart-card"
                            initial={{ opacity: 0, y: 20 }}
                            animate={{ opacity: 1, y: 0 }}
                            transition={{ delay: 0.4 }}
                        >
                            <div className="chart-header">
                                <PieChart className="chart-icon" />
                                <h3>Task Completion</h3>
                                
                            </div>
                            <div className="chart-body pie-chart-body">
                                <ResponsiveContainer width="100%" height={280}>
                                    <RechartsPie>
                                        <Pie
                                            data={getTaskCompletionData()}
                                            cx="50%"
                                            cy="50%"
                                            innerRadius={60}
                                            outerRadius={100}
                                            paddingAngle={5}
                                            dataKey="value"
                                            label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                                        >
                                            {/* Map colors to slices */}
                                            {getTaskCompletionData().map((entry, index) => (
                                                <Cell key={`cell-${index}`} fill={entry.color} />
                                            ))}
                                        </Pie>
                                        <Tooltip />
                                    </RechartsPie>
                                </ResponsiveContainer>
                            </div>
                        </motion.div>

                        {/* 4. Team Mood History Pie Chart (Only rendered if mood data exists) */}
                        {getTeamMoodData().length > 0 && (
                            <motion.div
                                className="chart-card"
                                initial={{ opacity: 0, y: 20 }}
                                animate={{ opacity: 1, y: 0 }}
                                transition={{ delay: 0.5 }}
                            >
                                <div className="chart-header">
                                    <Users className="chart-icon" />
                                    <h3>Team Mood History</h3>
                                    
                                </div>
                                <div className="chart-body pie-chart-body">
                                    <ResponsiveContainer width="100%" height={280}>
                                        <RechartsPie>
                                            <Pie
                                                data={getTeamMoodData()}
                                                cx="50%"
                                                cy="50%"
                                                innerRadius={60}
                                                outerRadius={100}
                                                paddingAngle={5}
                                                dataKey="value"
                                                label={({ name }) => name}
                                            >
                                                {/* Map mood colors to slices */}
                                                {getTeamMoodData().map((entry, index) => (
                                                    <Cell key={`cell-${index}`} fill={entry.color} />
                                                ))}
                                            </Pie>
                                            <Tooltip />
                                        </RechartsPie>
                                    </ResponsiveContainer>
                                </div>
                            </motion.div>
                        )}
                    </div>
                ) : (
                    // Fallback UI when no analytics data is available for the selected project
                    <motion.div
                        className="no-data"
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                    >
                        <BarChart3 size={64} />
                        <h3>No Analytics Data</h3>
                        <p>Analytics data will appear here as your team progresses through sprints.</p>
                    </motion.div>
                )}

                {/* Sprint Summary Table (Only rendered if sprint data exists) */}
                {sprints.length > 0 && (
                    <motion.div
                        className="sprint-table-card"
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ delay: 0.6 }}
                    >
                        <div className="chart-header">
                            <Clock className="chart-icon" />
                            <h3>Sprint Overview</h3>
                        </div>
                        <div className="table-container">
                            <table className="sprint-table">
                                <thead>
                                    <tr>
                                        <th>Sprint</th>
                                        <th>Status</th>
                                        <th>Start Date</th>
                                        <th>End Date</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {sprints.map(sprint => (
                                        <tr key={sprint.id}>
                                            <td className="sprint-name">{sprint.name}</td>
                                            <td>
                                                {/* Status badge with dynamic styling based on status value */}
                                                <span className={`status-badge ${sprint.status?.toLowerCase()}`}>
                                                    {sprint.status}
                                                </span>
                                            </td>
                                            <td>{sprint.startDate ? new Date(sprint.startDate).toLocaleDateString() : '-'}</td>
                                            <td>{sprint.endDate ? new Date(sprint.endDate).toLocaleDateString() : '-'}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    </motion.div>
                )}
            </div>
        </div>
    );
};

export default TeacherAnalytics;