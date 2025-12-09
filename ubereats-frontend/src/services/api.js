import axios from 'axios';

// 1. Base URL setup (Matches your Spring Boot port)
const api = axios.create({
    baseURL: 'http://localhost:8080/api',
    headers: {
        'Content-Type': 'application/json',
    },
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

export default api;