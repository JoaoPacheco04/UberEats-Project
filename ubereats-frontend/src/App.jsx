import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import TeacherDashboard from './pages/TeacherDashboard';
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
        <Route path="/teacher/dashboard" element={<TeacherDashboard />} />
        <Route path="/teacher/courses/:courseId" element={<CourseManagement />} />
        <Route path="/teacher/projects/:projectId" element={<ProjectDetail />} />
        <Route path="/teacher/projects/:projectId/sprints/:sprintId" element={<SprintBoard />} />
        <Route path="/teacher/teams/:teamId" element={<TeamDetail />} />
        <Route path="/teacher/badges" element={<BadgeManagement />} />
      </Routes>
    </Router>
  );
}

export default App;