import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion'; // Biblioteca para animações
import {
    X,
    Award,
    Gift,
    AlertCircle,
    Loader2
} from 'lucide-react'; // Ícones do Lucide
import { getActiveBadgesByRecipientType, createAchievement, getCurrentUser } from '../services/api'; // Funções da API
import './AwardBadgeModal.css'; // Estilos do componente

/**
 * Modal para conceder badges a usuários ou equipes
 * @param {Object} props - Propriedades do componente
 * @param {boolean} props.isOpen - Controla se o modal está aberto
 * @param {Function} props.onClose - Função para fechar o modal
 * @param {Object} props.recipient - Destinatário do badge (usuário ou equipe)
 * @param {string} props.recipientType - Tipo de destinatário: 'user' ou 'team'
 * @param {number} props.projectId - ID do projeto relacionado (opcional)
 * @param {Array} props.existingAchievements - Conquistas já existentes para filtrar badges
 */
const AwardBadgeModal = ({ 
    isOpen, 
    onClose, 
    recipient, 
    recipientType = 'user', 
    projectId, 
    existingAchievements = [] 
}) => {
    // Estado para a lista de badges disponíveis
    const [badges, setBadges] = useState([]);
    // Estado para o badge selecionado
    const [selectedBadge, setSelectedBadge] = useState('');
    // Estado para o motivo da concessão
    const [reason, setReason] = useState('');
    // Estado para indicar carregamento dos badges
    const [loading, setLoading] = useState(false);
    // Estado para indicar envio do formulário
    const [submitting, setSubmitting] = useState(false);
    // Estado para mensagens de erro
    const [error, setError] = useState(null);
    // Estado para indicar sucesso na concessão
    const [success, setSuccess] = useState(false);

    /**
     * Efeito para inicializar o modal quando ele abre
     * - Busca badges disponíveis
     * - Reseta estados do formulário
     */
    useEffect(() => {
        if (isOpen) {
            fetchBadges(); // Busca badges disponíveis
            // Reseta estados do formulário
            setSelectedBadge('');
            setReason('');
            setError(null);
            setSuccess(false);
        }
        // Note: existingAchievements is intentionally not in deps - it's read in fetchBadges closure
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isOpen, recipientType]); // Executa quando o modal abre ou o tipo de destinatário muda

    /**
     * Função para buscar badges disponíveis para o tipo de destinatário
     * Filtra badges que já foram concedidos ao destinatário
     */
    const fetchBadges = async () => {
        try {
            setLoading(true);
            // Mapeia o tipo de destinatário do frontend para o enum do backend
            // 'user' -> 'INDIVIDUAL', 'team' -> 'TEAM'
            const backendRecipientType = recipientType === 'user' ? 'INDIVIDUAL' : 'TEAM';
            const response = await getActiveBadgesByRecipientType(backendRecipientType);

            // Filtra badges que já foram concedidos a este destinatário
            const existingBadgeIds = existingAchievements.map(a => a.badgeId || a.badge?.id);
            const filteredBadges = (response.data || []).filter(
                badge => !existingBadgeIds.includes(badge.id)
            );

            setBadges(filteredBadges);
        } catch (err) {
            console.error('Error fetching badges:', err);
            setError('Failed to load badges.');
        } finally {
            setLoading(false);
        }
    };

    /**
     * Função para lidar com o envio do formulário
     * @param {Event} e - Evento do formulário
     */
    const handleSubmit = async (e) => {
        e.preventDefault(); // Previne comportamento padrão
        setError(null); // Limpa erros anteriores

        // Validações do formulário
        if (!selectedBadge) {
            setError('Please select a badge.');
            return;
        }

        if (!reason.trim()) {
            setError('Please provide a reason.');
            return;
        }

        // Obtém informações do usuário atual
        const currentUser = getCurrentUser();

        try {
            setSubmitting(true);

            // Prepara dados da conquista para envio à API
            const achievementData = {
                badgeId: parseInt(selectedBadge), // Converte para número
                reason: reason.trim(), // Remove espaços extras
                projectId: projectId, // ID do projeto (opcional)
                awardedByUserId: currentUser?.id, // ID do usuário que está concedendo
                // Condicionalmente adiciona destinatário baseado no tipo
                ...(recipientType === 'user'
                    ? { awardedToUserId: recipient.studentId || recipient.id } // Para usuário
                    : { awardedToTeamId: recipient.id }) // Para equipe
            };

            // Chama API para criar a conquista
            await createAchievement(achievementData);
            setSuccess(true); // Indica sucesso

            // Fecha o modal após 1.5 segundos (para mostrar mensagem de sucesso)
            setTimeout(() => {
                onClose(true); // Passa true para indicar sucesso
            }, 1500);

        } catch (err) {
            console.error('Error awarding badge:', err);
            // Exibe mensagem de erro da API ou mensagem padrão
            setError(err.response?.data?.message || 'Failed to award badge.');
        } finally {
            setSubmitting(false);
        }
    };

    // Não renderiza nada se o modal não estiver aberto
    if (!isOpen) return null;

    // Determina o nome do destinatário baseado no tipo
    const recipientName = recipientType === 'user'
        ? (recipient.studentName || recipient.name || 'Student') // Para usuário
        : (recipient.name || 'Team'); // Para equipe

    return (
        <AnimatePresence>
            {/* Overlay do modal com animação de fade */}
            <motion.div
                className="award-modal-overlay"
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                exit={{ opacity: 0 }}
                onClick={() => onClose(false)} // Fecha ao clicar fora
            >
                {/* Conteúdo do modal com animação de escala */}
                <motion.div
                    className="award-modal-content"
                    initial={{ scale: 0.9, opacity: 0 }}
                    animate={{ scale: 1, opacity: 1 }}
                    exit={{ scale: 0.9, opacity: 0 }}
                    onClick={e => e.stopPropagation()} // Previne que clique no conteúdo feche o modal
                >
                    {/* Botão de fechar modal */}
                    <button className="modal-close" onClick={() => onClose(false)}>
                        <X size={20} />
                    </button>

                    {/* Cabeçalho do modal */}
                    <div className="modal-header">
                        <div className="modal-icon">
                            <Gift size={24} />
                        </div>
                        <h2>Award Badge</h2>
                        <p>Award a badge to <strong>{recipientName}</strong></p>
                    </div>

                    {/* Renderização condicional: sucesso vs formulário */}
                    {success ? (
                        // Mensagem de sucesso após concessão do badge
                        <div className="success-message">
                            <Award size={48} />
                            <h3>Badge Awarded!</h3>
                            <p>The badge has been successfully awarded.</p>
                        </div>
                    ) : (
                        // Formulário para conceder badge
                        <form onSubmit={handleSubmit}>
                            {/* Exibição de erros */}
                            {error && (
                                <div className="form-error">
                                    <AlertCircle size={16} />
                                    {error}
                                </div>
                            )}

                            {/* Seleção do badge */}
                            <div className="form-group">
                                <label>Select Badge *</label>
                                {loading ? (
                                    // Estado de carregamento
                                    <div className="loading-badges">
                                        <Loader2 size={20} className="spinner" />
                                        Loading badges...
                                    </div>
                                ) : (
                                    // Grade de badges disponíveis
                                    <div className="badge-grid">
                                        {badges.map(badge => (
                                            <button
                                                key={badge.id}
                                                type="button"
                                                className={`badge-option ${selectedBadge == badge.id ? 'selected' : ''}`}
                                                onClick={() => setSelectedBadge(badge.id)}
                                            >
                                                <span className="badge-icon">
                                                    <Award size={20} />
                                                </span>
                                                <span className="badge-name">{badge.name}</span>
                                                <span className="badge-points">+{badge.points} pts</span>
                                            </button>
                                        ))}
                                    </div>
                                )}
                                {/* Mensagem quando não há badges disponíveis */}
                                {badges.length === 0 && !loading && (
                                    <p className="no-badges">No badges available. Create badges first.</p>
                                )}
                            </div>

                            {/* Campo para motivo */}
                            <div className="form-group">
                                <label>Reason *</label>
                                <textarea
                                    value={reason}
                                    onChange={e => setReason(e.target.value)}
                                    placeholder="Why are you awarding this badge?"
                                    rows={3}
                                    required
                                />
                            </div>

                            {/* Ações do modal (botões) */}
                            <div className="modal-actions">
                                <button type="button" className="cancel-btn" onClick={() => onClose(false)}>
                                    Cancel
                                </button>
                                <button
                                    type="submit"
                                    className="submit-btn"
                                    disabled={submitting || !selectedBadge || !reason.trim()}
                                >
                                    {submitting ? (
                                        // Estado de envio
                                        <>
                                            <Loader2 size={16} className="spinner" />
                                            Awarding...
                                        </>
                                    ) : (
                                        // Texto normal
                                        <>
                                            <Gift size={16} />
                                            Award Badge
                                        </>
                                    )}
                                </button>
                            </div>
                        </form>
                    )}
                </motion.div>
            </motion.div>
        </AnimatePresence>
    );
};

export default AwardBadgeModal;