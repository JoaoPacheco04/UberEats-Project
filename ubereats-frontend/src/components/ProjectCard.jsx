import React from 'react';
import { motion } from 'framer-motion';
import {
    Calendar,
    Users,
    Clock,
    CheckCircle2,
    AlertTriangle,
    PlayCircle,
    Archive,
    ChevronRight
} from 'lucide-react';
import './ProjectCard.css';

// ProjectCard exibe informações resumidas de um projeto e lida com a interação de clique.
const ProjectCard = ({ project, onClick }) => {
    // Desestruturação das propriedades do objeto project para uso fácil.
    const {
        id,
        name,
        description,
        startDate,
        endDate,
        status,
        courseName // Não usado diretamente, mas mantido para contexto futuro
    } = project;

    // Função auxiliar para mapear o status do projeto para configurações visuais (ícone, cor, label).
    const getStatusConfig = (status) => {
        const configs = {
            PLANNED: {
                label: 'Planned',
                icon: Clock, // Ícone de relógio
                color: '#64748b',
                bg: '#f1f5f9'
            },
            ACTIVE: {
                label: 'Active',
                icon: PlayCircle, // Ícone de reprodução/ativo
                color: '#059669',
                bg: '#d1fae5'
            },
            COMPLETED: {
                label: 'Completed',
                icon: CheckCircle2, // Ícone de verificação
                color: '#7c3aed',
                bg: '#ede9fe'
            },
            ARCHIVED: {
                label: 'Archived',
                icon: Archive, // Ícone de arquivo
                color: '#94a3b8',
                bg: '#f8fafc'
            }
        };
        // Retorna a configuração correspondente ou PLANNED como fallback.
        return configs[status] || configs.PLANNED;
    };

    // Aplica a configuração de status ao projeto atual.
    const statusConfig = getStatusConfig(status);
    // Armazena o componente de ícone para renderização dinâmica.
    const StatusIcon = statusConfig.icon;

    // Função auxiliar para formatar strings de data em um formato de leitura amigável.
    const formatDate = (dateStr) => {
        if (!dateStr) return 'Not set';
        return new Date(dateStr).toLocaleDateString('en-US', {
            month: 'short',
            day: 'numeric',
            year: 'numeric'
        });
    };

    // Função de cálculo para determinar a porcentagem de progresso baseado no tempo decorrido
    // entre a data de início e a data de fim.
    const calculateProgress = () => {
        // Se as datas não estiverem definidas, retorna 0.
        if (!startDate || !endDate) return 0;
        
        const start = new Date(startDate);
        const end = new Date(endDate);
        const now = new Date();

        // Se a data atual for anterior ao início, o progresso é 0%.
        if (now < start) return 0;
        // Se a data atual for posterior ao fim, o progresso é 100%.
        if (now > end) return 100;

        // Cálculo da proporção de tempo decorrido.
        const total = end - start;
        const elapsed = now - start;
        return Math.round((elapsed / total) * 100);
    };

    // Calcula a porcentagem de progresso e os dias restantes.
    const progress = calculateProgress();
    const daysRemaining = endDate ?
        // Calcula os dias restantes (arredonda para cima)
        Math.max(0, Math.ceil((new Date(endDate) - new Date()) / (1000 * 60 * 60 * 24))) :
        null; // null se a data final não estiver definida

    // ----------------------------------------------------
    // RENDERIZAÇÃO DO COMPONENTE
    // ----------------------------------------------------
    return (
        // motion.div do Framer Motion para animações.
        <motion.div
            className="project-card"
            // Animação de hover: Move ligeiramente para cima e aumenta a sombra.
            whileHover={{ y: -4, boxShadow: '0 12px 24px rgba(0,0,0,0.1)' }}
            onClick={onClick} // Chama a função onClick ao clicar no cartão.
        >
            {/* Status Badge */}
            <div
                className="project-status-badge"
                // Aplicação dinâmica de cor de fundo e texto via style, baseada em statusConfig.
                style={{ background: statusConfig.bg, color: statusConfig.color }}
            >
                <StatusIcon size={14} /> {/* Renderiza o ícone dinâmico */}
                <span>{statusConfig.label}</span>
            </div>

            {/* Content */}
            <div className="project-content">
                <h3 className="project-name">{name}</h3>
                {/* Exibe a descrição apenas se ela existir */}
                {description && (
                    <p className="project-description">{description}</p>
                )}
            </div>

            {/* Timeline: Oculta a seção de timeline para projetos 'ARCHIVED' */}
            {status !== 'ARCHIVED' && (
                <div className="project-timeline">
                    {/* Datas de Início e Fim */}
                    <div className="timeline-dates">
                        <span><Calendar size={14} /> {formatDate(startDate)}</span>
                        <span>→</span>
                        <span>{formatDate(endDate)}</span>
                    </div>

                    {/* Barra de Progresso: Visível apenas para status 'ACTIVE' */}
                    {status === 'ACTIVE' && (
                        <div className="progress-section">
                            <div className="progress-bar">
                                <div
                                    className="progress-fill"
                                    // Define a largura da barra de progresso com base no cálculo 'progress'
                                    style={{ width: `${progress}%` }}
                                />
                            </div>
                            <span className="progress-text">{progress}% timeline elapsed</span>
                        </div>
                    )}
                </div>
            )}

            {/* Footer */}
            <div className="project-footer">
                {/* Exibe dias restantes se estiver no status 'ACTIVE' e endDate estiver definido */}
                {daysRemaining !== null && status === 'ACTIVE' && (
                    <span className={`days-remaining ${daysRemaining < 7 ? 'warning' : ''}`}>
                        {/* Lógica condicional para exibir status de dias restantes */}
                        {daysRemaining === 0 ? (
                            <><AlertTriangle size={14} /> Due today</> // Vence hoje
                        ) : daysRemaining < 0 ? (
                            <><AlertTriangle size={14} /> Overdue</> // Atrasado
                        ) : (
                            <><Clock size={14} /> {daysRemaining} days left</> // Dias restantes
                        )}
                    </span>
                )}
                {/* Botão de Ação */}
                <button className="view-btn">
                    View Details <ChevronRight size={16} /> {/* Ícone de seta para indicar ação */}
                </button>
            </div>
        </motion.div>
    );
};

export default ProjectCard;