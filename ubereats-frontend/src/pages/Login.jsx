import React, { useState } from 'react';
import { login } from '../services/api'; // Importa função de login da API
import { useNavigate, Link } from 'react-router-dom'; // Hooks para navegação e links
import loginBg from '../assets/login-bg.jpg'; // Imagem de fundo do login
import applogo from '../assets/logo.png'; // Logo da aplicação

const Login = () => {
    // Estado para armazenar email do usuário
    const [email, setEmail] = useState('');
    // Estado para armazenar senha do usuário
    const [password, setPassword] = useState('');
    // Estado para armazenar mensagens de erro
    const [error, setError] = useState('');
    // Hook para navegação programática
    const navigate = useNavigate();

    /**
     * Função para lidar com o envio do formulário de login
     * @param {Event} e - Evento do formulário
     */
    const handleSubmit = async (e) => {
        e.preventDefault(); // Previne comportamento padrão do formulário
        setError(''); // Limpa erros anteriores

        try {
            // Chama API de login com email e senha
            const userData = await login(email, password);
            console.log("Login Success:", userData);

            // Navega para dashboard baseado no papel (role) do usuário
            if (userData.role === 'TEACHER') {
                navigate('/teacher/dashboard'); // Professor vai para dashboard de professor
            } else if (userData.role === 'STUDENT') {
                navigate('/student/dashboard'); // Estudante vai para dashboard de estudante
            } else {
                // Fallback padrão caso role não seja reconhecido
                navigate('/dashboard');
            }
        } catch (err) {
            // Exibe mensagem de erro em caso de falha no login
            setError('Invalid email or password');
            console.error(err);
        }
    };

    return (
        // Container principal - layout flex com altura total da viewport
        <div className="flex h-screen w-full bg-white">
            {/* ============================================
               LADO ESQUERDO - IMAGEM DE FUNDO COM SOBREPOSIÇÃO
               ============================================ */}
            <div className="hidden lg:flex lg:w-1/2 relative">
                {/* Imagem de fundo */}
                <img
                    src={loginBg}
                    alt="Background"
                    className="absolute inset-0 w-full h-full object-cover" // Cobre todo o espaço disponível
                />
                {/* Overlay escuro com blur para melhor legibilidade do texto */}
                <div className="absolute inset-0 bg-blue-900/40 backdrop-blur-sm"></div>

                {/* Conteúdo textual sobreposto na imagem */}
                <div className="absolute inset-0 flex flex-col justify-center items-center text-white z-10 p-12 text-center">
                    {/* Logo branca */}
                    <img src={applogo} alt="Logo White" className="w-24 h-24 mb-6 drop-shadow-lg" />
                    
                    {/* Títulos da universidade */}
                    <h1 className="text-4xl font-serif tracking-wider mb-2">UNIVERSIDADE</h1>
                    <h1 className="text-4xl font-serif tracking-wider mb-6">PORTUCALENSE</h1>
                    
                    {/* Slogan com bordas decorativas */}
                    <p className="text-lg font-light tracking-widest uppercase border-t border-b border-white/50 py-2">
                        Do conhecimento à prática.
                    </p>

                    {/* Títulos do sistema */}
                    <div className="mt-20">
                        <h2 className="text-5xl font-serif tracking-widest ">UPT</h2>
                        <h2 className="text-5xl font-serif tracking-widest mt-2">EDUSCRUM</h2>
                        <h2 className="text-5xl font-serif tracking-widest mt-2">AWARDS.</h2>
                    </div>
                </div>
            </div>

            {/* ============================================
               LADO DIREITO - FORMULÁRIO DE LOGIN
               ============================================ */}
            <div className="w-full lg:w-1/2 flex items-center justify-center p-8 lg:p-16 relative bg-blue-50/30">
                {/* Card flutuante para o formulário */}
                <div className="w-full max-w-md">
                    {/* Cabeçalho do formulário */}
                    <div className="text-center mb-10">
                        {/* Logo visível apenas em mobile */}
                        <img src={applogo} alt="EduScrum Logo" className="w-20 h-20 mx-auto mb-4 lg:hidden" />

                        {/* Saudação */}
                        <h2 className="text-4xl font-serif text-gray-800 tracking-wide mb-2">OLÁ!</h2>
                        <p className="text-gray-500 uppercase tracking-widest text-sm">Bem vindo de volta</p>
                    </div>

                    {/* Formulário de login */}
                    <form onSubmit={handleSubmit} className="space-y-6">
                        {/* Exibição de erros */}
                        {error && (
                            <div className="bg-red-50 border-l-4 border-red-500 text-red-700 p-4 mb-6 rounded shadow-sm" role="alert">
                                <p className="font-bold">Error</p>
                                <p>{error}</p>
                            </div>
                        )}

                        {/* Campo de email */}
                        <div className="space-y-2">
                            <div className="relative">
                                {/* Label estilizada */}
                                <label className="block text-xs font-bold text-gray-600 uppercase tracking-wider mb-1 ml-1">Username</label>
                                <input
                                    type="email"
                                    placeholder="" // Placeholder vazio conforme design
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    required
                                    className="w-full bg-white text-gray-800 rounded-lg py-4 px-6 border border-gray-100 shadow-sm focus:outline-none focus:ring-2 focus:ring-teal-400 focus:border-transparent transition-all duration-200"
                                />
                            </div>
                        </div>

                        {/* Campo de senha */}
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

                        {/* Botão de submit */}
                        <button
                            type="submit"
                            className="w-full mt-8 bg-teal-400 hover:bg-teal-500 text-white font-bold py-4 rounded-lg shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 transition-all duration-200 uppercase tracking-widest"
                        >
                            Login
                        </button>
                    </form>

                    {/* Link para registro */}
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