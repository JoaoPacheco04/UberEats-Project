import React from 'react';
import { motion } from 'framer-motion';
import {
    Calendar,
    Clock,
    Target,
    PlayCircle,
    CheckCircle2,
    AlertTriangle,
    PauseCircle,
    ChevronRight,
    Zap
} from 'lucide-react';
import './SprintCard.css';

// SprintCard recebe o objeto 'sprint' e funções de callback para as ações (onViewBoard, onStart, onComplete).
const SprintCard = ({ sprint, onViewBoard, onStart, onComplete }) => {
    // 1. DESESTRUTURAÇÃO DAS PROPRIEDADES (PROPS)
    // Assume-se que os dados do sprint (exceto as funções) são passados prontos para uso.
    const {
        id,
        sprintNumber,
        name,
        goal,
        startDate,
        endDate,
        status,
        daysRemaining,          // Dias restantes (calculado externamente, ou 0 se vencido)
        timeProgressPercentage, // Progresso de tempo decorrido (calculado externamente)
        overdue,                // Booleano indicando se o sprint está atrasado
        displayName
    } = sprint;

    // 2. Mapeamento de Status para Configurações Visuais
    // Define cores, ícones e labels com base no status do sprint.
    const getStatusConfig = (status) => {
        const configs = {
            PLANNED: {
                label: 'Planned',
                icon: Clock,
                color: '#64748b',
                bg: '#f1f5f9',
                gradient: 'from-slate-400 to-slate-500' // Classes Tailwind (ou similar) para o s-accent
            },
            ACTIVE: {
                label: 'In Progress',
                icon: PlayCircle,
                color: '#059669',
                bg: '#d1fae5',
                gradient: 'from-emerald-500 to-teal-500'
            },
            COMPLETED: {
                label: 'Completed',
                icon: CheckCircle2,
                color: '#7c3aed',
                bg: '#ede9fe',
                gradient: 'from-violet-500 to-purple-500'
            },
            CANCELLED: {
                label: 'Cancelled',
                icon: PauseCircle,
                color: '#ef4444',
                bg: '#fee2e2',
                gradient: 'from-red-400 to-rose-500'
            }
        };
        return configs[status] || configs.PLANNED;
    };

    // Aplicação das configurações e seleção do ícone dinâmico
    const statusConfig = getStatusConfig(status);
    const StatusIcon = statusConfig.icon;

    // Função auxiliar para formatar datas (ex: 2024-12-14 -> Dec 14)
    const formatDate = (dateStr) => {
        if (!dateStr) return 'Not set';
        return new Date(dateStr).toLocaleDateString('en-US', {
            month: 'short',
            day: 'numeric'
        });
    };

    // Garante que o progresso seja um número e defina um valor padrão de 0.
    const progress = timeProgressPercentage || 0;

    // ----------------------------------------------------
    // RENDERIZAÇÃO DO COMPONENTE
    // ----------------------------------------------------
    return (
        // motion.div para aplicar animação de hover
        <motion.div
            className="sprint-card"
            whileHover={{ y: -2 }} // Efeito sutil de elevação no hover
        >
            {/* Barra de Acento Esquerda (Accent) */}
            <div
                // Aplica classes de gradiente dinâmico (assumindo que estas classes existam no CSS/Tailwind)
                className={`sprint-accent bg-gradient-to-b ${statusConfig.gradient}`}
            />

            {/* Conteúdo Principal */}
            <div className="sprint-content">
                
                {/* Cabeçalho */}
                <div className="sprint-header">
                    <div className="sprint-info">
                        {/* Número do Sprint/ID */}
                        <div className="sprint-number">
                            <Target size={16} />
                            Sprint {sprintNumber || id}
                        </div>
                        {/* Nome do Sprint */}
                        <h3 className="sprint-name">{name || displayName}</h3>
                        {/* Objetivo do Sprint (goal) - Renderização condicional */}
                        {goal && <p className="sprint-goal">{goal}</p>}
                    </div>

                    {/* Status Badge */}
                    <div
                        className="sprint-status"
                        // Aplicação de cores dinâmicas via estilo inline
                        style={{ background: statusConfig.bg, color: statusConfig.color }}
                    >
                        <StatusIcon size={14} />
                        <span>{statusConfig.label}</span>
                    </div>
                </div>

                {/* Timeline & Progresso */}
                <div className="sprint-timeline">
                    {/* Datas do Sprint */}
                    <div className="timeline-dates">
                        <span><Calendar size={14} /> {formatDate(startDate)}</span>
                        <span className="date-separator">→</span>
                        <span>{formatDate(endDate)}</span>
                    </div>

                    {/* Seção de Progresso (Visível apenas para sprints ATIVOS) */}
                    {status === 'ACTIVE' && (
                        <div className="progress-section">
                            <div className="progress-bar">
                                <div
                                    className="progress-fill"
                                    // Define a largura da barra de progresso (limita a 100%)
                                    style={{ width: `${Math.min(progress, 100)}%` }}
                                />
                            </div>
                            <div className="progress-info">
                                {/* Exibe a porcentagem de tempo decorrido */}
                                <span>{Math.round(progress)}% time elapsed</span>
                                
                                {/* Exibe os dias restantes se daysRemaining não for nulo */}
                                {daysRemaining !== null && (
                                    <span className={`days-remaining ${overdue ? 'overdue' : ''}`}>
                                        {overdue ? (
                                            <><AlertTriangle size={12} /> Overdue</> // Sprint atrasado
                                        ) : (
                                            <><Clock size={12} /> {daysRemaining} days left</> // Dias restantes
                                        )}
                                    </span>
                                )}
                            </div>
                        </div>
                    )}
                </div>

                {/* Ações (Botões) */}
                <div className="sprint-actions">
                    {/* Botão 'Start' (Iniciar): Visível se PLANNED e a função onStart estiver definida */}
                    {status === 'PLANNED' && onStart && (
                        <button className="action-btn start" onClick={onStart}>
                            <Zap size={16} />
                            Start Sprint
                        </button>
                    )}

                    {/* Botão 'Complete' (Concluir): Visível se ACTIVE e a função onComplete estiver definida */}
                    {status === 'ACTIVE' && onComplete && (
                        <button className="action-btn complete" onClick={onComplete}>
                            <CheckCircle2 size={16} />
                            Complete
                        </button>
                    )}

                    {/* Botão 'View Board' (Visualizar Board) - Sempre presente */}
                    <button className="action-btn view" onClick={onViewBoard}>
                        View Board
                        <ChevronRight size={16} />
                    </button>
                </div>
            </div>
        </motion.div>
    );
};

export default SprintCard;