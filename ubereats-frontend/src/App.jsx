import React from 'react';
// Import necessary components from react-router-dom
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';

// Import all page components used in the routing
import Login from './pages/Login';
import Register from './pages/Register';
import TeacherDashboard from './pages/TeacherDashboard';
import StudentDashboard from './pages/StudentDashboard';
import CourseManagement from './pages/CourseManagement';
import ProjectDetail from './pages/ProjectDetail';
import SprintBoard from './pages/SprintBoard';
import TeamDetail from './pages/TeamDetail';
import BadgeManagement from './pages/BadgeManagement';
import TeacherAnalytics from './pages/TeacherAnalytics';

function App() {
    return (
        // Router: Uses the HTML5 history API to keep your UI in sync with the URL.
        <Router>
            {/* Routes: Contains all the individual route definitions */}
            <Routes>
                {/* Default Route: Redirects the root path '/' to the '/login' page */}
                <Route path="/" element={<Navigate to="/login" />} />
                
                {/* Authentication Routes */}
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                
                {/* ======================================
                  Teacher Routes (Protected/Role-Specific)
                  ======================================
                */}
                <Route path="/teacher/dashboard" element={<TeacherDashboard />} />
                
                {/* Course Management (specific course ID) */}
                <Route path="/teacher/courses/:courseId" element={<CourseManagement />} />
                
                {/* Project Details (specific project ID) */}
                <Route path="/teacher/projects/:projectId" element={<ProjectDetail />} />
                
                {/* Sprint Board (nested under project, requires project and sprint IDs) */}
                <Route path="/teacher/projects/:projectId/sprints/:sprintId" element={<SprintBoard />} />
                
                {/* Team Details (specific team ID) */}
                <Route path="/teacher/teams/:teamId" element={<TeamDetail />} />
                
                {/* Global Teacher Features */}
                <Route path="/teacher/badges" element={<BadgeManagement />} />
                <Route path="/teacher/analytics" element={<TeacherAnalytics />} />
                
                {/* ======================================
                  Student Routes (Protected/Role-Specific)
                  Note: Some pages are shared (e.g., CourseManagement, ProjectDetail) 
                  but rendered differently based on the user's role (handled within the component) 
                  or path prefix.
                  ======================================
                */}
                <Route path="/student/dashboard" element={<StudentDashboard />} />
                
                {/* Student access to course details */}
                <Route path="/student/courses/:courseId" element={<CourseManagement />} />
                
                {/* Student access to project details */}
                <Route path="/student/projects/:projectId" element={<ProjectDetail />} />
                
                {/* Student access to sprint board */}
                <Route path="/student/projects/:projectId/sprints/:sprintId" element={<SprintBoard />} />
                
                {/* Student access to team details */}
                <Route path="/student/teams/:teamId" element={<TeamDetail />} />
            </Routes>
        </Router>
    );
}

export default App;