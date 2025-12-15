/**
 * Student Detail Card Component
 * Displays student information including achievements and scores.
 * 
 * @author Bruna
 * @author Yeswanth
 * @version 1.0.0
 */
import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion'; // Importing motion for potential future animations
import {
    User,
    Mail,
    Award,
    TrendingUp,
    ChevronDown,
    ChevronUp,
    Gift
} from 'lucide-react'; // Importing icons for visual representation
import { getUserAchievements, getStudentDashboard } from '../services/api'; // API service functions for data fetching
import './StudentDetailCard.css'; // Importing component-specific styles

/**
 * StudentDetailCard Component
 * Displays key information about a single student, with an expandable section
 * for detailed statistics and achievements fetched from an API.
 *
 * @param {object} student - The student object containing id, name, email, etc.
 * @param {function} onAwardBadge - Callback function to open the badge award modal/flow.
 * @param {boolean} [showAwardButton=true] - Flag to conditionally render the 'Award Badge' button.
 */
const StudentDetailCard = ({ student, onAwardBadge, showAwardButton = true }) => {
    // Destructure student properties. 'id' is used as a fallback for 'studentId'.
    const { id, studentId, studentName, studentEmail, enrolledAt } = student;
    const userId = studentId || id; // Determine the definitive user ID for API calls

    // State for managing the card's expanded/collapsed status
    const [expanded, setExpanded] = useState(false);
    // State to store the list of student achievements (badges)
    const [achievements, setAchievements] = useState([]);
    // State to store detailed student statistics (global score, stories done, etc.)
    const [stats, setStats] = useState(null);
    // State to track if data fetching is currently in progress
    const [loading, setLoading] = useState(false);
    // State to track if the data has been successfully fetched at least once
    const [loaded, setLoaded] = useState(false);

    // Effect hook to fetch detailed data when the card is expanded
    useEffect(() => {
        // Only fetch if:
        // 1. The card is expanded.
        // 2. A valid userId exists.
        // 3. The data has not been loaded yet (`!loaded`).
        if (expanded && userId && !loaded) {
            fetchStudentDetails();
        }
    }, [expanded, userId, loaded]); // Dependencies: re-run when these values change

    /**
     * Fetches detailed student data (achievements and dashboard stats) concurrently.
     */
    const fetchStudentDetails = async () => {
        try {
            setLoading(true);
            // Use Promise.all to fetch both data endpoints simultaneously for efficiency
            const [achievementsRes, statsRes] = await Promise.all([
                // Catch errors for individual calls to prevent Promise.all failure
                getUserAchievements(userId).catch(() => ({ data: [] })),
                getStudentDashboard(userId).catch(() => ({ data: null }))
            ]);
            
            // Update state with fetched data, providing default empty/null values on error
            setAchievements(achievementsRes.data || []);
            setStats(statsRes.data);
            setLoaded(true); // Mark data as loaded
        } catch (err) {
            // Log any errors that occurred outside of the individual catch blocks
            console.error('Error fetching student details:', err);
        } finally {
            setLoading(false); // Reset loading state regardless of success or failure
        }
    };

    /**
     * Formats an ISO date string into a localized short date format (e.g., "Dec 14, 2025").
     * @param {string} dateStr - The date string to format.
     * @returns {string} The formatted date string or 'Unknown'.
     */
    const formatDate = (dateStr) => {
        if (!dateStr) return 'Unknown';
        // Create a Date object and format it using built-in methods
        return new Date(dateStr).toLocaleDateString('en-US', {
            month: 'short',
            day: 'numeric',
            year: 'numeric'
        });
    };

    return (
        // Main card container. Adds 'expanded' class for conditional styling.
        <div className={`student-card ${expanded ? 'expanded' : ''}`}>
            
            {/* Main Row - Always Visible - Click to toggle expansion */}
            <div className="student-main" onClick={() => setExpanded(!expanded)}>
                
                {/* Avatar/Icon Section */}
                <div className="student-avatar">
                    <User size={20} />
                </div>

                {/* Name and Email Section */}
                <div className="student-info">
                    <h4 className="student-name">{studentName || 'Unknown Student'}</h4>
                    <span className="student-email">
                        <Mail size={12} />
                        {studentEmail}
                    </span>
                </div>

                {/* Mini Stats (Visible in collapsed state) */}
                <div className="student-stats-mini">
                    {/* Only show mini stats if data has been fetched/loaded */}
                    {(stats || loaded) && (
                        <>
                            {/* Global Score Mini-Stat */}
                            <span className="stat-badge score">
                                <TrendingUp size={14} />
                                {/* Display score formatted to 0 decimal places or 0 if null */}
                                {stats?.globalScore?.toFixed(0) || 0} 
                            </span>
                            {/* Badges/Achievements Count Mini-Stat */}
                            <span className="stat-badge badges">
                                <Award size={14} />
                                {/* Use stats count if available, otherwise fall back to achievements array length */}
                                {stats?.totalBadges || achievements.length || 0}
                            </span>
                        </>
                    )}
                </div>

                {/* Actions (Award Button and Expand Toggle) */}
                <div className="student-actions">
                    {/* Conditionally render the Award Badge button */}
                    {showAwardButton && (
                        <button
                            className="award-btn"
                            // Stop propagation to prevent card expansion when button is clicked
                            onClick={(e) => { e.stopPropagation(); onAwardBadge?.(student); }}
                            title="Award Badge"
                        >
                            <Gift size={16} />
                        </button>
                    )}
                    {/* Expand/Collapse Toggle Button */}
                    <button className="expand-btn">
                        {/* Display ChevronUp if expanded, ChevronDown if collapsed */}
                        {expanded ? <ChevronUp size={20} /> : <ChevronDown size={20} />}
                    </button>
                </div>
            </div>

            {/* Expanded Details Section */}
            {expanded && (
                <div className="student-details">
                    
                    {/* Enrollment Detail */}
                    <div className="details-section">
                        <h5>Enrollment</h5>
                        <p>Enrolled on {formatDate(enrolledAt)}</p>
                    </div>

                    {/* Conditional rendering based on loading state */}
                    {loading ? (
                        <div className="details-loading">Loading...</div>
                    ) : (
                        <>
                            {/* Detailed Stats Grid (Only show if stats data is present) */}
                            {stats && (
                                <div className="details-section stats-grid">
                                    <div className="detail-stat">
                                        <span className="stat-label">Global Score</span>
                                        {/* Display global score formatted to 1 decimal place */}
                                        <span className="stat-value">{stats.globalScore?.toFixed(1) || 0}</span>
                                    </div>
                                    <div className="detail-stat">
                                        <span className="stat-label">Stories Done</span>
                                        <span className="stat-value">{stats.storiesCompleted || 0}</span>
                                    </div>
                                    <div className="detail-stat">
                                        <span className="stat-label">Story Points</span>
                                        <span className="stat-value">{stats.totalStoryPoints || 0}</span>
                                    </div>
                                    <div className="detail-stat">
                                        <span className="stat-label">Badges</span>
                                        <span className="stat-value">{stats.totalBadges || 0}</span>
                                    </div>
                                </div>
                            )}

                            {/* Recent Achievements List (Only show if achievements are present) */}
                            {achievements.length > 0 && (
                                <div className="details-section">
                                    <h5>Recent Achievements</h5>
                                    <div className="achievements-list">
                                        {/* Map and display up to 5 recent achievements */}
                                        {achievements.slice(0, 5).map(achievement => (
                                            <div key={achievement.id} className="achievement-item">
                                                <Award size={14} className="achievement-icon" />
                                                <span className="achievement-name">{achievement.badgeName}</span>
                                                <span className="achievement-reason">{achievement.reason}</span>
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            )}

                            {/* Empty State Message (Show if no data/achievements are found after loading) */}
                            {achievements.length === 0 && !stats && (
                                <div className="details-empty">
                                    No achievements or stats available yet.
                                </div>
                            )}
                        </>
                    )}
                </div>
            )}
        </div>
    );
};

export default StudentDetailCard;
