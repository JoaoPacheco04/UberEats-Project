import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { X, User, Mail, Lock, AlertCircle, Check, Loader2, Save } from 'lucide-react';
import { updateUser } from '../services/api';
import './EditProfileModal.css';

// O modal recebe propriedades para controlar seu estado (isOpen), fechamento (onClose),
// dados do usuário atual (currentUser) e uma função de callback em caso de sucesso (onUpdateSuccess).
const EditProfileModal = ({ isOpen, onClose, currentUser, onUpdateSuccess }) => {
    // 1. ESTADO DO FORMULÁRIO (useState)
    // Inicializa o estado do formulário com os dados do usuário atual.
    // Os campos de senha são inicializados vazios, pois são opcionais.
    const [formData, setFormData] = useState({
        firstName: currentUser?.firstName || '',
        lastName: currentUser?.lastName || '',
        email: currentUser?.email || '',
        password: '',
        confirmPassword: ''
    });

    // 2. ESTADOS DE INTERAÇÃO (useState)
    const [loading, setLoading] = useState(false); // Indica o status de envio da requisição (para desabilitar o botão)
    const [error, setError] = useState(null);     // Armazena mensagens de erro de validação ou de API
    const [success, setSuccess] = useState(false); // Indica o sucesso do envio para exibir a mensagem de confirmação

    // 3. HANDLERS
    
    // Função para atualizar o estado do formulário quando os campos mudam.
    // Limpa a mensagem de erro sempre que o usuário começa a digitar novamente.
    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
        if (error) setError(null);
    };

    // Função de validação local dos campos do formulário.
    const validateForm = () => {
        // Validação de Nome e Sobrenome
        if (!formData.firstName.trim() || !formData.lastName.trim()) {
            setError('First name and last name are required');
            return false;
        }
        // Validação de Email
        if (!formData.email.trim().includes('@')) {
            setError('Please enter a valid email address');
            return false;
        }
        // Validação de Senha (somente se o usuário digitou algo no campo 'password')
        if (formData.password) {
            // Requisito de comprimento mínimo
            if (formData.password.length < 6) {
                setError('Password must be at least 6 characters long');
                return false;
            }
            // Confirmação de Senha
            if (formData.password !== formData.confirmPassword) {
                setError('Passwords do not match');
                return false;
            }
        }
        // Retorna true se todas as validações passarem
        return true;
    };

    // Função assíncrona para lidar com o envio do formulário.
    const handleSubmit = async (e) => {
        e.preventDefault();

        // 1. Executa a validação local; se falhar, interrompe o envio.
        if (!validateForm()) return;

        // 2. Inicia o estado de carregamento e limpa erros anteriores.
        setLoading(true);
        setError(null);

        try {
            // 3. Monta o payload de atualização:
            // Inclui a senha APENAS se o campo 'password' não estiver vazio.
            const updatePayload = {
                firstName: formData.firstName,
                lastName: formData.lastName,
                email: formData.email,
                ...(formData.password ? { password: formData.password } : {})
            };

            // 4. Chama o serviço de API para atualizar o usuário.
            const response = await updateUser(currentUser.id, updatePayload);

            // 5. ATUALIZAÇÃO DO ESTADO GLOBAL/LOCAL:
            // Cria um objeto de usuário atualizado e salva-o no Local Storage.
            const updatedUser = { ...currentUser, ...response.data };
            localStorage.setItem('user', JSON.stringify(updatedUser));

            // 6. Exibe mensagem de sucesso e fecha o modal após um pequeno atraso.
            setSuccess(true);
            setTimeout(() => {
                onUpdateSuccess(updatedUser); // Notifica o componente pai sobre a atualização
                onClose();
            }, 1500);

        } catch (err) {
            // 7. Em caso de erro de API, exibe a mensagem de erro.
            console.error('Update profile error:', err);
            setError(err.message || 'Failed to update profile. Please try again.');
        } finally {
            // 8. Finaliza o estado de carregamento.
            setLoading(false);
        }
    };

    // Se o modal não estiver aberto (isOpen=false), não renderiza nada.
    if (!isOpen) return null;

    // 4. RENDERIZAÇÃO
    return (
        // AnimatePresence gerencia a animação de saída dos elementos
        <AnimatePresence>
            {/* Camada de sobreposição (overlay) do modal */}
            <div className="modal-overlay" onClick={onClose}>
                {/* O conteúdo do modal usa motion.div para animações do Framer Motion */}
                <motion.div
                    className="modal-content edit-profile-modal"
                    initial={{ opacity: 0, scale: 0.95, y: 20 }} // Estado inicial (início da animação)
                    animate={{ opacity: 1, scale: 1, y: 0 }}     // Estado animado (fim da animação, modal visível)
                    exit={{ opacity: 0, scale: 0.95, y: 20 }}    // Estado de saída (fechamento do modal)
                    onClick={e => e.stopPropagation()}           // Impede que o clique no modal feche-o via overlay
                >
                    {/* Botão de Fechar */}
                    <button className="modal-close-btn" onClick={onClose}>
                        <X size={20} />
                    </button>

                    {/* Cabeçalho do Modal (usa classes CSS definidas) */}
                    <div className="modal-header">
                        <div className="modal-icon-box">
                            <User size={24} />
                        </div>
                        <div>
                            <h2>Edit Profile</h2>
                            <p>Update your personal information</p>
                        </div>
                    </div>

                    {/* Lógica condicional: se sucesso for true, exibe a mensagem de sucesso; caso contrário, exibe o formulário. */}
                    {success ? (
                        <div className="success-message">
                            <div className="success-icon">
                                <Check size={48} />
                            </div>
                            <h3>Profile Updated!</h3>
                            <p>Your changes have been saved successfully.</p>
                        </div>
                    ) : (
                        <form onSubmit={handleSubmit} className="edit-profile-form">
                            {/* Banner de Erro: exibe se houver uma mensagem de erro */}
                            {error && (
                                <div className="form-error-banner">
                                    <AlertCircle size={18} />
                                    <span>{error}</span>
                                </div>
                            )}

                            {/* LINHA DE CAMPOS: Nome e Sobrenome */}
                            <div className="form-row">
                                <div className="form-group">
                                    <label>First Name</label>
                                    <div className="input-wrapper">
                                        <User size={18} />
                                        <input
                                            type="text"
                                            name="firstName"
                                            value={formData.firstName}
                                            onChange={handleChange}
                                            placeholder="John"
                                        />
                                    </div>
                                </div>
                                <div className="form-group">
                                    <label>Last Name</label>
                                    <div className="input-wrapper">
                                        <User size={18} />
                                        <input
                                            type="text"
                                            name="lastName"
                                            value={formData.lastName}
                                            onChange={handleChange}
                                            placeholder="Doe"
                                        />
                                    </div>
                                </div>
                            </div>

                            {/* CAMPO: Email */}
                            <div className="form-group">
                                <label>Email Address</label>
                                <div className="input-wrapper">
                                    <Mail size={18} />
                                    <input
                                        type="email"
                                        name="email"
                                        value={formData.email}
                                        onChange={handleChange}
                                        placeholder="john.doe@example.com"
                                    />
                                </div>
                            </div>

                            {/* Divisor de Seção de Senha */}
                            <div className="divider">
                                <span>Change Password (Optional)</span>
                            </div>

                            {/* LINHA DE CAMPOS: Nova Senha e Confirmação de Senha */}
                            <div className="form-row">
                                <div className="form-group">
                                    <label>New Password</label>
                                    <div className="input-wrapper">
                                        <Lock size={18} />
                                        <input
                                            type="password"
                                            name="password"
                                            value={formData.password}
                                            onChange={handleChange}
                                            placeholder="Min. 6 characters"
                                        />
                                    </div>
                                </div>
                                <div className="form-group">
                                    <label>Confirm Password</label>
                                    <div className="input-wrapper">
                                        <Lock size={18} />
                                        <input
                                            type="password"
                                            name="confirmPassword"
                                            value={formData.confirmPassword}
                                            onChange={handleChange}
                                            placeholder="Confirm new password"
                                        />
                                    </div>
                                </div>
                            </div>

                            {/* Ações do Modal (Botões) */}
                            <div className="modal-actions">
                                <button type="button" className="cancel-btn" onClick={onClose}>
                                    Cancel
                                </button>
                                <button
                                    type="submit"
                                    className="save-btn"
                                    disabled={loading} // Desabilita o botão durante o carregamento
                                >
                                    {/* Exibe o ícone de 'carregamento' (spinner) ou 'salvar' dependendo do estado 'loading' */}
                                    {loading ? <Loader2 className="spinner" size={18} /> : <Save size={18} />}
                                    Save Changes
                                </button>
                            </div>
                        </form>
                    )}
                </motion.div>
            </div>
        </AnimatePresence>
    );
};

export default EditProfileModal;