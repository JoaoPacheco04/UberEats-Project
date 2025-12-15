/**
 * Register Page Component
 * Registration page for new users (students and teachers).
 * Includes password validation and role selection.
 * 
 * @author Ana
 * @author Francisco
 * @version 1.0.0
 */
import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { register } from '../services/api';
import { GraduationCap, User, Mail, Lock, BookOpen } from 'lucide-react';
const Register = () => {
    // Estado único para todos os campos do formulário
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        username: '',
        email: '',
        password: '',
        confirmPassword: '',
        role: 'STUDENT', // Valor padrão: estudante
    });
    const [error, setError] = useState(''); // Estado para mensagens de erro
    const [isLoading, setIsLoading] = useState(false); // Estado para indicar carregamento
    const navigate = useNavigate(); // Hook para navegação programática

    /**
     * Manipula mudanças em qualquer campo do formulário
     * Atualiza o estado e limpa mensagens de erro quando o usuário começa a digitar
     * @param {Event} e - Evento de mudança do input
     */
    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
        // Limpa erro quando usuário começa a digitar novamente
        if (error) setError('');
    };

    /**
     * Manipula o envio do formulário de registro
     * Realiza validações no lado do cliente antes de chamar a API
     * @param {Event} e - Evento de submit do formulário
     */
    const handleSubmit = async (e) => {
        e.preventDefault(); // Previne comportamento padrão
        setError(''); // Limpa erros anteriores

        // Validação 1: Confirmação de senha
        if (formData.password !== formData.confirmPassword) {
            setError('Passwords do not match');
            return;
        }

        // Validação 2: Comprimento mínimo da senha
        if (formData.password.length < 6) {
            setError('Password must be at least 6 characters');
            return;
        }

        setIsLoading(true); // Ativa estado de carregamento
        try {
            // Prepara payload para API (remove campo de confirmação de senha)
            const payload = {
                firstName: formData.firstName,
                lastName: formData.lastName,
                username: formData.username,
                email: formData.email,
                password: formData.password,
                role: formData.role,
            };

            await register(payload); // Chama API de registro

            // Redireciona para login com mensagem de sucesso
            navigate('/login', {
                state: { message: 'Registration successful! Please login.' }
            });
        } catch (err) {
            // Captura e exibe erro da API
            setError(err.message || 'Registration failed. Please try again.');
        } finally {
            setIsLoading(false); // Desativa estado de carregamento
        }
    };

    return (
        // Container principal com gradiente de fundo
        <div className="min-h-screen bg-gradient-to-br from-slate-50 via-white to-blue-50 flex items-center justify-center p-4">
            {/* Container central com largura máxima */}
            <div className="w-full max-w-md">
                {/* Cabeçalho com ícone e títulos */}
                <div className="text-center mb-8">
                    <div className="inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-gradient-to-r from-violet-600 to-indigo-600 mb-4 shadow-lg shadow-violet-500/30">
                        <GraduationCap size={32} className="text-white" />
                    </div>
                    <h1 className="text-3xl font-bold text-slate-800">Create Account</h1>
                    <p className="text-slate-500 mt-2">Join the EduScrum platform</p>
                </div>

                {/* Card do formulário com sombras e bordas */}
                <div className="bg-white rounded-2xl shadow-xl shadow-slate-200/50 p-8 border border-slate-100">
                    {/* Mensagem de erro (se houver) */}
                    {error && (
                        <div className="bg-rose-50 border-l-4 border-rose-500 text-rose-700 p-4 mb-6 rounded-r-lg">
                            <p className="text-sm font-medium">{error}</p>
                        </div>
                    )}

                    {/* Formulário de registro */}
                    <form onSubmit={handleSubmit} className="space-y-5">
                        {/* Linha de nome (primeiro e último nome) */}
                        <div className="grid grid-cols-2 gap-4">
                            <div>
                                <label className="block text-xs font-semibold text-slate-600 uppercase tracking-wider mb-2">
                                    First Name
                                </label>
                                <input
                                    type="text"
                                    name="firstName"
                                    value={formData.firstName}
                                    onChange={handleChange}
                                    required
                                    className="w-full bg-slate-50 text-slate-800 rounded-xl py-3 px-4 border border-slate-200 focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent transition-all"
                                    placeholder="John"
                                />
                            </div>
                            <div>
                                <label className="block text-xs font-semibold text-slate-600 uppercase tracking-wider mb-2">
                                    Last Name
                                </label>
                                <input
                                    type="text"
                                    name="lastName"
                                    value={formData.lastName}
                                    onChange={handleChange}
                                    required
                                    className="w-full bg-slate-50 text-slate-800 rounded-xl py-3 px-4 border border-slate-200 focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent transition-all"
                                    placeholder="Doe"
                                />
                            </div>
                        </div>

                        {/* Campo de username com ícone */}
                        <div>
                            <label className="block text-xs font-semibold text-slate-600 uppercase tracking-wider mb-2">
                                <User size={14} className="inline mr-1" />
                                Username
                            </label>
                            <input
                                type="text"
                                name="username"
                                value={formData.username}
                                onChange={handleChange}
                                required
                                className="w-full bg-slate-50 text-slate-800 rounded-xl py-3 px-4 border border-slate-200 focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent transition-all"
                                placeholder="johndoe"
                            />
                        </div>

                        {/* Campo de email com ícone */}
                        <div>
                            <label className="block text-xs font-semibold text-slate-600 uppercase tracking-wider mb-2">
                                <Mail size={14} className="inline mr-1" />
                                Email
                            </label>
                            <input
                                type="email"
                                name="email"
                                value={formData.email}
                                onChange={handleChange}
                                required
                                className="w-full bg-slate-50 text-slate-800 rounded-xl py-3 px-4 border border-slate-200 focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent transition-all"
                                placeholder="john@example.com"
                            />
                        </div>

                        {/* Seletor de papel (role) com ícone */}
                        <div>
                            <label className="block text-xs font-semibold text-slate-600 uppercase tracking-wider mb-2">
                                <BookOpen size={14} className="inline mr-1" />
                                Role
                            </label>
                            <select
                                name="role"
                                value={formData.role}
                                onChange={handleChange}
                                className="w-full bg-slate-50 text-slate-800 rounded-xl py-3 px-4 border border-slate-200 focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent transition-all cursor-pointer"
                            >
                                <option value="STUDENT">Student</option>
                                <option value="TEACHER">Teacher</option>
                            </select>
                        </div>

                        {/* Campo de senha com ícone */}
                        <div>
                            <label className="block text-xs font-semibold text-slate-600 uppercase tracking-wider mb-2">
                                <Lock size={14} className="inline mr-1" />
                                Password
                            </label>
                            <input
                                type="password"
                                name="password"
                                value={formData.password}
                                onChange={handleChange}
                                required
                                minLength={6} // Validação HTML5
                                className="w-full bg-slate-50 text-slate-800 rounded-xl py-3 px-4 border border-slate-200 focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent transition-all"
                                placeholder="••••••••"
                            />
                        </div>

                        {/* Campo de confirmação de senha com ícone */}
                        <div>
                            <label className="block text-xs font-semibold text-slate-600 uppercase tracking-wider mb-2">
                                <Lock size={14} className="inline mr-1" />
                                Confirm Password
                            </label>
                            <input
                                type="password"
                                name="confirmPassword"
                                value={formData.confirmPassword}
                                onChange={handleChange}
                                required
                                className="w-full bg-slate-50 text-slate-800 rounded-xl py-3 px-4 border border-slate-200 focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent transition-all"
                                placeholder="••••••••"
                            />
                        </div>

                        {/* Botão de submit com estados de loading e disabled */}
                        <button
                            type="submit"
                            disabled={isLoading}
                            className="w-full mt-6 bg-gradient-to-r from-violet-600 to-indigo-600 hover:from-violet-700 hover:to-indigo-700 text-white font-bold py-4 rounded-xl shadow-lg shadow-violet-500/30 hover:shadow-xl transform hover:-translate-y-0.5 transition-all duration-200 uppercase tracking-wider disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                            {isLoading ? 'Creating Account...' : 'Create Account'}
                        </button>
                    </form>

                    {/* Link para página de login */}
                    <p className="mt-6 text-center text-slate-500">
                        Already have an account?{' '}
                        <Link to="/login" className="text-violet-600 font-semibold hover:text-violet-700 transition-colors">
                            Sign In
                        </Link>
                    </p>
                </div>
            </div>
        </div>
    );
};

export default Register;