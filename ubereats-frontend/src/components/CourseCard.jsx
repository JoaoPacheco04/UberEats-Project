import React from 'react';
import { motion } from 'framer-motion'; // Biblioteca para animações
import { Users, FolderOpen, TrendingUp, BookOpen, ChevronRight } from 'lucide-react'; // Ícones

/**
 * CourseCard - Cartão profissional acadêmico para exibir informações de cursos
 * Inclui barra de performance, contagem de estudantes, projetos e design limpo
 * 
 * @param {Object} props - Propriedades do componente
 * @param {Object} props.course - Dados do curso
 * @param {number} props.index - Índice do cartão para animação sequencial
 * @param {Function} props.onManage - Callback para gerenciar o curso
 */
const CourseCard = ({ course, index, onManage }) => {
    // Desestruturação dos dados do curso
    const {
        id,
        name,
        code,
        semester,
        academicYear,
        studentCount,
        projectCount,
        averageTeamScore,
    } = course;

    // Lida com diferentes nomes de campo do backend (isActive vs active)
    const isActive = course.isActive !== undefined ? course.isActive : course.active;

    /**
     * Determina a configuração visual baseada na pontuação do curso
     * @param {number} score - Pontuação média do curso
     * @returns {Object} Configuração de cores e labels
     */
    const getScoreConfig = (score) => {
        if (score >= 80) {
            return {
                color: 'bg-emerald-500', // Verde para excelente
                bgColor: 'bg-emerald-100', // Fundo verde claro
                textColor: 'text-emerald-600', // Texto verde
                label: 'Excellent',
            };
        } else if (score >= 50) {
            return {
                color: 'bg-amber-500', // Âmbar para bom
                bgColor: 'bg-amber-100', // Fundo âmbar claro
                textColor: 'text-amber-600', // Texto âmbar
                label: 'Good',
            };
        } else {
            return {
                color: 'bg-rose-500', // Rosa para precisa melhorar
                bgColor: 'bg-rose-100', // Fundo rosa claro
                textColor: 'text-rose-600', // Texto rosa
                label: 'Needs Improvement',
            };
        }
    };

    // Obtém configuração baseada na pontuação atual
    const scoreConfig = getScoreConfig(averageTeamScore || 0);
    // Formata pontuação com 1 casa decimal
    const displayScore = averageTeamScore?.toFixed(1) || '0.0';

    /**
     * Formata a exibição do semestre
     * @param {string} sem - Valor do semestre do backend
     * @returns {string} Semestre formatado
     */
    const formatSemester = (sem) => {
        if (!sem) return '';
        const semesterMap = {
            'FIRST': '1st Semester',
            'SECOND': '2nd Semester',
            'ANNUAL': 'Annual',
        };
        return semesterMap[sem] || sem;
    };

    return (
        // Container principal com animações
        <motion.div
            initial={{ opacity: 0, y: 20, scale: 0.95 }} // Estado inicial da animação
            animate={{ opacity: 1, y: 0, scale: 1 }} // Estado animado
            transition={{
                duration: 0.4,
                delay: index * 0.1, // Delay sequencial baseado no índice
                ease: [0.25, 0.46, 0.45, 0.94] // Curva de easing personalizada
            }}
            whileHover={{
                y: -4, // Levanta ao passar o mouse
                transition: { duration: 0.2 }
            }}
            className="relative group" // Classes do Tailwind
        >
            {/* Container do cartão */}
            <div className={`
        relative overflow-hidden rounded-2xl bg-white
        border border-slate-200/50 // Borda sutil
        shadow-lg shadow-slate-200/50 // Sombra padrão
        hover:shadow-xl hover:shadow-slate-300/50 // Sombra no hover
        transition-all duration-300 // Transições suaves
        ${!isActive ? 'opacity-60' : ''} // Opacidade reduzida para cursos inativos
      `}>

                {/* Barra de destaque superior (indicador de performance) */}
                <div className={`h-1.5 ${scoreConfig.color} w-full`} />

                {/* Conteúdo do cartão */}
                <div className="p-5">

                    {/* Seção do cabeçalho */}
                    <div className="flex items-start justify-between mb-4">
                        <div className="flex-1">
                            {/* Badge do código do curso */}
                            <div className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-slate-100 text-slate-600 text-xs font-semibold mb-2">
                                <BookOpen size={12} className="text-violet-500" />
                                {code}
                            </div>

                            {/* Nome do curso */}
                            <h3 className="text-lg font-bold text-slate-800 leading-tight mb-1 line-clamp-2">
                                {name}
                            </h3>

                            {/* Semestre e ano acadêmico */}
                            <div className="flex items-center gap-2 flex-wrap">
                                {semester && (
                                    <span className="inline-flex items-center px-2 py-0.5 rounded-md bg-violet-100 text-violet-700 text-xs font-medium">
                                        {formatSemester(semester)}
                                    </span>
                                )}
                                {academicYear && (
                                    <span className="text-xs text-slate-500">{academicYear}</span>
                                )}
                            </div>
                        </div>

                        {/* Indicador de status ativo/inativo */}
                        <div className={`
              flex items-center gap-1 px-2 py-1 rounded-full text-xs font-medium
              ${isActive
                                ? 'bg-emerald-100 text-emerald-700' // Verde para ativo
                                : 'bg-slate-100 text-slate-500'} // Cinza para inativo
            `}>
                            <span className={`w-1.5 h-1.5 rounded-full ${isActive ? 'bg-emerald-500' : 'bg-slate-400'}`} />
                            {isActive ? 'Active' : 'Inactive'}
                        </div>
                    </div>

                    {/* Seção de pontuação de performance */}
                    <div className="mb-5">
                        <div className="flex items-center justify-between mb-2">
                            <div className="flex items-center gap-2">
                                <TrendingUp size={16} className={scoreConfig.textColor} />
                                <span className="text-sm font-semibold text-slate-700">Performance Score</span>
                            </div>
                            <div className={`flex items-center gap-1 text-sm font-bold ${scoreConfig.textColor}`}>
                                <span>{displayScore}</span>
                                <span className="text-slate-400 font-normal">/ 100</span>
                            </div>
                        </div>

                        {/* Barra de progresso animada */}
                        <div className={`relative h-3 rounded-full ${scoreConfig.bgColor} overflow-hidden`}>
                            <motion.div
                                initial={{ width: 0 }} // Começa com largura zero
                                animate={{ width: `${averageTeamScore || 0}%` }} // Anima até a pontuação real
                                transition={{ duration: 1, delay: index * 0.1 + 0.3, ease: "easeOut" }}
                                className={`absolute inset-y-0 left-0 ${scoreConfig.color} rounded-full`}
                            />
                        </div>

                        {/* Label descritivo da performance */}
                        <p className={`text-xs mt-1.5 ${scoreConfig.textColor} font-medium`}>
                            {scoreConfig.label}
                        </p>
                    </div>

                    {/* Grade de estatísticas */}
                    <div className="grid grid-cols-2 gap-3 mb-5">
                        {/* Estatística de estudantes */}
                        <div className="flex items-center gap-3 p-3 rounded-xl bg-gradient-to-br from-blue-50 to-indigo-50 border border-blue-100/50">
                            <div className="flex items-center justify-center w-10 h-10 rounded-lg bg-blue-500 text-white shadow-lg shadow-blue-500/30">
                                <Users size={18} />
                            </div>
                            <div>
                                <p className="text-xs text-slate-500 font-medium">Students</p>
                                <p className="text-xl font-bold text-slate-800">{studentCount || 0}</p>
                            </div>
                        </div>

                        {/* Estatística de projetos */}
                        <div className="flex items-center gap-3 p-3 rounded-xl bg-gradient-to-br from-purple-50 to-pink-50 border border-purple-100/50">
                            <div className="flex items-center justify-center w-10 h-10 rounded-lg bg-purple-500 text-white shadow-lg shadow-purple-500/30">
                                <FolderOpen size={18} />
                            </div>
                            <div>
                                <p className="text-xs text-slate-500 font-medium">Projects</p>
                                <p className="text-xl font-bold text-slate-800">{projectCount || 0}</p>
                            </div>
                        </div>
                    </div>

                    {/* Botão de gerenciar curso */}
                    <motion.button
                        onClick={() => onManage?.(id)}
                        whileHover={{ scale: 1.02 }} // Efeito de escala no hover
                        whileTap={{ scale: 0.98 }} // Efeito de pressão ao clicar
                        className={`
              w-full py-3 px-4 rounded-xl
              bg-gradient-to-r from-violet-600 to-indigo-600 // Gradiente violeta-indigo
              hover:from-violet-700 hover:to-indigo-700 // Gradiente mais escuro no hover
              text-white font-semibold text-sm
              shadow-lg shadow-violet-500/30 // Sombra com opacidade
              hover:shadow-xl hover:shadow-violet-500/40 // Sombra mais forte no hover
              transition-all duration-200 // Transições suaves
              flex items-center justify-center gap-2
              group/btn // Grupo para animar o ícone
            `}
                    >
                        <span>Manage Course</span>
                        <ChevronRight size={16} className="group-hover/btn:translate-x-1 transition-transform" />
                    </motion.button>
                </div>
            </div>
        </motion.div>
    );
};

export default CourseCard;