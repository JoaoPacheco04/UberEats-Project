import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import {
    User,
    Mail,
    Award,
    TrendingUp,
    ChevronDown,
    ChevronUp,
    Gift
} from 'lucide-react';
// Importação simulada dos serviços de API
import { getUserAchievements, getStudentDashboard } from '../services/api'; 
import './StudentDetailCard.css';

const StudentDetailCard = ({ student, onAwardBadge }) => {
    // 1. EXTRAÇÃO DE PROPRIEDADES E DADOS
    const { id, studentId, studentName, studentEmail, enrolledAt } = student;
    // Usa 'studentId' ou 'id' como fallback para o ID do usuário (necessário para APIs)
    const userId = studentId || id; 

    // 2. GERENCIAMENTO DE ESTADO
    const [expanded, setExpanded] = useState(false); // Controla a expansão do cartão
    const [achievements, setAchievements] = useState([]); // Armazena a lista de badges
    const [stats, setStats] = useState(null); // Armazena dados de dashboard (pontuação, etc.)
    const [loading, setLoading] = useState(false); // Indica se os detalhes expandidos estão sendo carregados
    const [loaded, setLoaded] = useState(false); // Indica se os dados já foram carregados pelo menos uma vez

    // 3. EFEITO DE CARREGAMENTO DE DADOS
    // Executa apenas quando o cartão é expandido E ainda não foi carregado ('loaded' é falso).
    useEffect(() => {
        if (expanded && userId && !loaded) {
            fetchStudentDetails();
        }
    }, [expanded, userId]); // Dependências: 'expanded' e 'userId' (o 'loaded' não é incluído para evitar loop)

    // Função assíncrona para buscar detalhes do estudante na API
    const fetchStudentDetails = async () => {
        try {
            setLoading(true);
            // Promise.all permite buscar achievements e stats em paralelo para melhor performance
            const [achievementsRes, statsRes] = await Promise.all([
                // Tratamento de erro dentro da Promise.all para que uma falha não interrompa a outra
                getUserAchievements(userId).catch(() => ({ data: [] })), 
                getStudentDashboard(userId).catch(() => ({ data: null }))
            ]);
            
            // Atualiza o estado com os dados recebidos (ou arrays/null vazios em caso de erro)
            setAchievements(achievementsRes.data || []);
            setStats(statsRes.data);
            setLoaded(true); // Marca como carregado para evitar buscas futuras desnecessárias
        } catch (err) {
            console.error('Error fetching student details:', err);
        } finally {
            setLoading(false); // Sempre desliga o estado de loading
        }
    };

    // Função auxiliar para formatar a data de matrícula
    const formatDate = (dateStr) => {
        if (!dateStr) return 'Unknown';
        return new Date(dateStr).toLocaleDateString('en-US', {
            month: 'short',
            day: 'numeric',
            year: 'numeric'
        });
    };

    // ----------------------------------------------------
    // RENDERIZAÇÃO DO COMPONENTE
    // ----------------------------------------------------
    return (
        <div className={`student-card ${expanded ? 'expanded' : ''}`}>
            {/* Linha Principal - Sempre Visível */}
            <div 
                className="student-main" 
                onClick={() => setExpanded(!expanded)} // Alterna o estado de expansão
            >
                {/* Avatar */}
                <div className="student-avatar">
                    <User size={20} /> {/* Ícone de usuário */}
                </div>

                {/* Informações Básicas */}
                <div className="student-info">
                    <h4 className="student-name">{studentName || 'Unknown Student'}</h4>
                    <span className="student-email">
                        <Mail size={12} />
                        {studentEmail}
                    </span>
                </div>

                {/* Estatísticas Mini (Visível em telas maiores - ver CSS @media) */}
                <div className="student-stats-mini">
                    {/* Exibe se houver dados de stats OU se já tentou carregar (loaded) */}
                    {(stats || loaded) && ( 
                        <>
                            {/* Score Global */}
                            <span className="stat-badge score">
                                <TrendingUp size={14} />
                                {/* Exibe a pontuação com 0 casas decimais ou 0 como fallback */}
                                {stats?.globalScore?.toFixed(0) || 0} 
                            </span>
                            {/* Badges/Conquistas */}
                            <span className="stat-badge badges">
                                <Award size={14} />
                                {/* Tenta usar totalBadges do stats, fallback para o comprimento do array de achievements, ou 0 */}
                                {stats?.totalBadges || achievements.length || 0} 
                            </span>
                        </>
                    )}
                </div>

                {/* Ações e Botão de Expansão */}
                <div className="student-actions">
                    {/* Botão para Conceder Badge */}
                    <button
                        className="award-btn"
                        // Impede a propagação do evento para não fechar/abrir o cartão ao clicar no botão
                        onClick={(e) => { e.stopPropagation(); onAwardBadge?.(student); }} 
                        title="Award Badge"
                    >
                        <Gift size={16} />
                    </button>
                    {/* Botão de Expansão (Muda o ícone baseado no estado 'expanded') */}
                    <button className="expand-btn">
                        {expanded ? <ChevronUp size={20} /> : <ChevronDown size={20} />}
                    </button>
                </div>
            </div>

            {/* Detalhes Expandidos (Conteúdo Condicional) */}
            {expanded && (
                // Nota: O uso de `motion.div` não foi incluído aqui para evitar complexidade com animações de altura (height auto)
                <div className="student-details">
                    
                    {/* Informação de Matrícula (Sempre disponível) */}
                    <div className="details-section">
                        <h5>Enrollment</h5>
                        <p>Enrolled on {formatDate(enrolledAt)}</p>
                    </div>

                    {/* Lógica de Carregamento/Conteúdo */}
                    {loading ? (
                        <div className="details-loading">Loading...</div>
                    ) : (
                        <>
                            {/* Estatísticas Detalhadas (Stats Grid) - Visível se stats existirem */}
                            {stats && (
                                <div className="details-section stats-grid">
                                    <div className="detail-stat">
                                        <span className="stat-label">Global Score</span>
                                        <span className="stat-value">{stats.globalScore?.toFixed(1) || 0}</span>
                                    </div>
                                    <div className="detail-stat">
                                        <span className="stat-label">Stories Done</span>
                                        <span className="stat-value">{stats.storiesCompleted || 0}</span>
                                    </div>
                                    <div className="detail-stat">
                                        <span className="stat-label">Story Points</span>
                                        <span className="stat-value">{stats.totalStoryPoints || 0}</span>
                                    </div>
                                    <div className="detail-stat">
                                        <span className="stat-label">Badges</span>
                                        <span className="stat-value">{stats.totalBadges || 0}</span>
                                    </div>
                                </div>
                            )}

                            {/* Achievements/Badges Recentes - Visível se houver achievements */}
                            {achievements.length > 0 && (
                                <div className="details-section">
                                    <h5>Recent Achievements</h5>
                                    <div className="achievements-list">
                                        {/* Limita a lista aos 5 primeiros achievements */}
                                        {achievements.slice(0, 5).map(achievement => ( 
                                            <div key={achievement.id} className="achievement-item">
                                                <Award size={14} className="achievement-icon" />
                                                <span className="achievement-name">{achievement.badgeName}</span>
                                                <span className="achievement-reason">{achievement.reason}</span>
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            )}

                            {/* Mensagem de Vazio - Visível se não houver stats nem achievements */}
                            {achievements.length === 0 && !stats && (
                                <div className="details-empty">
                                    No achievements or stats available yet.
                                </div>
                            )}
                        </>
                    )}
                </div>
            )}
        </div>
    );
};

export default StudentDetailCard;