import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import TeacherDashboard from './pages/TeacherDashboard';
import StudentDashboard from './pages/StudentDashboard';
import CourseManagement from './pages/CourseManagement';
import ProjectDetail from './pages/ProjectDetail';
import SprintBoard from './pages/SprintBoard';
import TeamDetail from './pages/TeamDetail';
import BadgeManagement from './pages/BadgeManagement';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Navigate to="/login" />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        {/* Teacher Routes */}
        <Route path="/teacher/dashboard" element={<TeacherDashboard />} />
        <Route path="/teacher/courses/:courseId" element={<CourseManagement />} />
        <Route path="/teacher/projects/:projectId" element={<ProjectDetail />} />
        <Route path="/teacher/projects/:projectId/sprints/:sprintId" element={<SprintBoard />} />
        <Route path="/teacher/teams/:teamId" element={<TeamDetail />} />
        <Route path="/teacher/badges" element={<BadgeManagement />} />
        {/* Student Routes */}
        <Route path="/student/dashboard" element={<StudentDashboard />} />
        <Route path="/student/courses/:courseId" element={<CourseManagement />} />
        <Route path="/student/projects/:projectId" element={<ProjectDetail />} />
        <Route path="/student/projects/:projectId/sprints/:sprintId" element={<SprintBoard />} />
        <Route path="/student/teams/:teamId" element={<TeamDetail />} />
      </Routes>
    </Router>
  );
}

export default App;