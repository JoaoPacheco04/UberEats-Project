import React, { useState } from 'react';
import { login } from '../services/api';
import { useNavigate, Link } from 'react-router-dom';
import loginBg from '../assets/login-bg.jpg';
import applogo from '../assets/logo.png';

const Login = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        try {
            const userData = await login(email, password);
            console.log("Login Success:", userData);

            // Navigate based on user role
            if (userData.role === 'TEACHER') {
                navigate('/teacher/dashboard');
            } else if (userData.role === 'STUDENT') {
                navigate('/student/dashboard'); // TODO: Create student dashboard
            } else {
                // Default fallback
                navigate('/dashboard');
            }
        } catch (err) {
            setError('Invalid email or password');
            console.error(err);
        }
    };

    return (
        <div className="flex h-screen w-full bg-white">
            {/* Left Side - Background Image & Overlay */}
            <div className="hidden lg:flex lg:w-1/2 relative">
                <img
                    src={loginBg}
                    alt="Background"
                    className="absolute inset-0 w-full h-full object-cover"
                />
                <div className="absolute inset-0 bg-blue-900/40 backdrop-blur-sm"></div>

                {/* Optional Overlaid Text on Image if needed, perfectly matching reference */}
                <div className="absolute inset-0 flex flex-col justify-center items-center text-white z-10 p-12 text-center">
                    <img src={applogo} alt="Logo White" className="w-24 h-24 mb-6 brightness-0 invert drop-shadow-lg" />
                    <h1 className="text-4xl font-serif tracking-wider mb-2">UNIVERSIDADE</h1>
                    <h1 className="text-4xl font-serif tracking-wider mb-6">PORTUCALENSE</h1>
                    <p className="text-lg font-light tracking-widest uppercase border-t border-b border-white/50 py-2">
                        Do conhecimento à prática.
                    </p>

                    <div className="mt-20">
                        <h2 className="text-5xl font-serif tracking-widest ">UPT</h2>
                        <h2 className="text-5xl font-serif tracking-widest mt-2">EDUSCRUM</h2>
                        <h2 className="text-5xl font-serif tracking-widest mt-2">AWARDS.</h2>
                    </div>
                </div>
            </div>

            {/* Right Side - Login Form */}
            <div className="w-full lg:w-1/2 flex items-center justify-center p-8 lg:p-16 relative bg-blue-50/30">
                {/* Floating Card Design matching reference */}
                <div className="w-full max-w-md">
                    <div className="text-center mb-10">
                        {/* Mobile Logo shows here only on small screens */}
                        <img src={applogo} alt="EduScrum Logo" className="w-20 h-20 mx-auto mb-4 lg:hidden" />

                        <h2 className="text-4xl font-serif text-gray-800 tracking-wide mb-2">OLÁ!</h2>
                        <p className="text-gray-500 uppercase tracking-widest text-sm">Bem vindo de volta</p>
                    </div>

                    <form onSubmit={handleSubmit} className="space-y-6">
                        {error && (
                            <div className="bg-red-50 border-l-4 border-red-500 text-red-700 p-4 mb-6 rounded shadow-sm" role="alert">
                                <p className="font-bold">Error</p>
                                <p>{error}</p>
                            </div>
                        )}

                        <div className="space-y-2">
                            {/* No visible label as per design, using placeholder as visual cue or minimal label if needed 
                                 Design has inputs that look like white rounded rectangles with uppercase text inside
                             */}
                            <div className="relative">
                                <label className="block text-xs font-bold text-gray-600 uppercase tracking-wider mb-1 ml-1">Username</label>
                                <input
                                    type="email"
                                    placeholder=""
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    required
                                    className="w-full bg-white text-gray-800 rounded-lg py-4 px-6 border border-gray-100 shadow-sm focus:outline-none focus:ring-2 focus:ring-teal-400 focus:border-transparent transition-all duration-200"
                                />
                            </div>
                        </div>

                        <div className="space-y-2">
                            <div className="relative">
                                <label className="block text-xs font-bold text-gray-600 uppercase tracking-wider mb-1 ml-1">Password</label>
                                <input
                                    type="password"
                                    placeholder=""
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    required
                                    className="w-full bg-white text-gray-800 rounded-lg py-4 px-6 border border-gray-100 shadow-sm focus:outline-none focus:ring-2 focus:ring-teal-400 focus:border-transparent transition-all duration-200"
                                />
                            </div>
                        </div>

                        <button
                            type="submit"
                            className="w-full mt-8 bg-teal-400 hover:bg-teal-500 text-white font-bold py-4 rounded-lg shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 transition-all duration-200 uppercase tracking-widest"
                        >
                            Login
                        </button>
                    </form>

                    {/* Register Link */}
                    <p className="mt-6 text-center text-gray-500">
                        Don't have an account?{' '}
                        <Link to="/register" className="text-teal-500 font-semibold hover:text-teal-600 transition-colors">
                            Create Account
                        </Link>
                    </p>
                </div>
            </div>
        </div>
    );
};

export default Login;