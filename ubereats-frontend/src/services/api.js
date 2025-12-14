import axios from 'axios';

// 1. Base URL setup 
// Using direct URL since CORS is now configured on backend
const api = axios.create({
    baseURL: 'http://localhost:8080/api',
    headers: {
        'Content-Type': 'application/json',
    },
    withCredentials: true, // Enable sending credentials for CORS
});

// 2. Interceptor: Automatically add the JWT Token to every request
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// 3. Login Service
export const login = async (email, password) => {
    try {
        // Sends POST request to your backend's AuthController
        const response = await api.post('/auth/login', { email, password });

        // If successful, save the token and user details in the browser
        if (response.data.token) {
            localStorage.setItem('token', response.data.token);
            localStorage.setItem('user', JSON.stringify(response.data));
        }
        return response.data;
    } catch (error) {
        throw error.response ? error.response.data : error;
    }
};

// 4. Registration Service
export const register = async (userData) => {
    try {
        const response = await api.post('/auth/register', userData);
        return response.data;
    } catch (error) {
        throw error.response ? error.response.data : error;
    }
};

// 5. Teacher Course Services
export const getTeacherCourses = () => api.get('/courses/teacher/my-courses');

export const createCourse = async (courseData) => {
    try {
        const response = await api.post('/courses', courseData);
        return response.data;
    } catch (error) {
        throw error.response ? error.response.data : error;
    }
};

// 5b. Student Course Services  
export const getAvailableCourses = () => api.get('/courses/available');
export const getUserTeams = (userId) => api.get(`/teams/user/${userId}`);

// 6. Course Services
export const getCourseById = (courseId) => api.get(`/courses/${courseId}`);
export const getCourseStudents = (courseId) => api.get(`/enrollments/course/${courseId}`);

// 7. Project Services
export const getProjectsByCourse = (courseId) => api.get(`/projects/course/${courseId}`);
export const getProjectById = (projectId) => api.get(`/projects/${projectId}`);
export const createProject = async (projectData) => {
    try {
        const response = await api.post('/projects', projectData);
        return response.data;
    } catch (error) {
        throw error.response ? error.response.data : error;
    }
};
export const updateProject = (projectId, projectData) => api.put(`/projects/${projectId}`, projectData);
export const completeProject = (projectId) => api.put(`/projects/${projectId}/complete`);
export const deleteProject = (projectId) => api.delete(`/projects/${projectId}`);

// 8. Team Services
export const getTeamByProject = (projectId) => api.get(`/teams/project/${projectId}`);
export const getTeamById = (teamId) => api.get(`/teams/${teamId}`);
export const getTeamMembers = (teamId) => api.get(`/teams/${teamId}/members`);
export const createTeam = (teamData) => api.post('/teams', teamData);
export const addTeamToProject = (teamId, projectId) => api.post(`/teams/${teamId}/projects/${projectId}`);
export const addMemberToTeam = (teamId, memberData) => api.post(`/teams/${teamId}/members`, memberData);
export const removeMemberFromTeam = (teamId, userId) => api.delete(`/teams/${teamId}/members/${userId}`);
export const deleteTeam = (teamId) => api.delete(`/teams/${teamId}`);

// 9. Sprint Services
export const getSprintsByProject = (projectId) => api.get(`/sprints/project/${projectId}`);
export const getSprintById = (sprintId) => api.get(`/sprints/${sprintId}`);
export const createSprint = (sprintData) => api.post('/sprints', sprintData);
export const updateSprint = (sprintId, sprintData) => api.put(`/sprints/${sprintId}`, sprintData);
export const startSprint = (sprintId) => api.put(`/sprints/${sprintId}/start`);
export const completeSprint = (sprintId, completionDate, teamMood) => {
    let url = `/sprints/${sprintId}/complete?`;
    if (completionDate) url += `completionDate=${completionDate}&`;
    if (teamMood) url += `teamMood=${teamMood}`;
    return api.patch(url);
};
export const cancelSprint = (sprintId) => api.put(`/sprints/${sprintId}/cancel`);
export const deleteSprint = (sprintId) => api.delete(`/sprints/${sprintId}`);

// 10. User Story Services
export const getUserStoriesBySprint = (sprintId) => api.get(`/user-stories/sprint/${sprintId}`);
export const getUserStoryById = (storyId) => api.get(`/user-stories/${storyId}`);
export const createUserStory = (storyData) => api.post('/user-stories', storyData);
export const updateUserStory = (storyId, storyData) => api.put(`/user-stories/${storyId}`, storyData);
export const assignUserStory = (storyId, userId) => api.put(`/user-stories/${storyId}/assign/${userId}`);
export const unassignUserStory = (storyId) => api.put(`/user-stories/${storyId}/unassign`);
export const moveToNextStatus = (storyId) => api.put(`/user-stories/${storyId}/next-status`);
export const moveToPreviousStatus = (storyId) => api.put(`/user-stories/${storyId}/previous-status`);
export const deleteUserStory = (storyId) => api.delete(`/user-stories/${storyId}`);
export const getSprintStats = (sprintId) => api.get(`/user-stories/sprint/${sprintId}/stats`);

// 11. Enrollment Services  
export const enrollStudent = (enrollmentData) => api.post('/enrollments', enrollmentData);
export const getStudentEnrollments = (studentId) => api.get(`/enrollments/student/${studentId}`);
export const getCourseEnrollments = (courseId) => api.get(`/enrollments/course/${courseId}`);

// 12. Badge Services
export const getAllBadges = () => api.get('/badges');
export const getActiveBadges = () => api.get('/badges/active');
export const getBadgeById = (badgeId) => api.get(`/badges/${badgeId}`);
export const getBadgesByType = (badgeType) => api.get(`/badges/type/${badgeType}`);
export const getActiveBadgesByRecipientType = (recipientType) => api.get(`/badges/recipient-type/${recipientType}`);
export const createBadge = (badgeData) => api.post('/badges', badgeData);
export const updateBadge = (badgeId, badgeData) => api.put(`/badges/${badgeId}`, badgeData);
export const toggleBadgeStatus = (badgeId) => api.patch(`/badges/${badgeId}/toggle`);
export const deleteBadge = (badgeId) => api.delete(`/badges/${badgeId}`);

// 13. Achievement Services
export const getAllAchievements = () => api.get('/achievements');
export const getAchievementById = (achievementId) => api.get(`/achievements/${achievementId}`);
export const getUserAchievements = (userId) => api.get(`/achievements/user/${userId}`);
export const getTeamAchievements = (teamId) => api.get(`/achievements/team/${teamId}`);
export const getProjectAchievements = (projectId) => api.get(`/achievements/project/${projectId}`);
export const getUserPointsInProject = (userId, projectId) =>
    api.get(`/achievements/user/${userId}/project/${projectId}/points`);
export const getTeamPoints = (teamId) => api.get(`/achievements/team/${teamId}/points`);
export const createAchievement = (achievementData) => api.post('/achievements', achievementData);
export const deleteAchievement = (achievementId) => api.delete(`/achievements/${achievementId}`);

// 14. Dashboard Services
export const getStudentDashboard = (studentId) => api.get(`/dashboard/student/${studentId}`);

// 15. Analytics Services
export const getProjectBurndown = (projectId) => api.get(`/analytics/project/${projectId}/burndown`);
export const getSprintAnalytics = (sprintId, teamId) =>
    api.get(`/analytics?sprintId=${sprintId}&teamId=${teamId}`);
export const getProjectAnalytics = (projectId) => api.get(`/analytics/project/${projectId}`);

// 16. Export Services
export const exportCourseGrades = (courseId) =>
    api.get(`/export/course/${courseId}`, { responseType: 'blob' });

// Helper to get current user from localStorage
export const getCurrentUser = () => {
    try {
        const userData = localStorage.getItem('user');
        return userData ? JSON.parse(userData) : null;
    } catch {
        return null;
    }
};



export default api;