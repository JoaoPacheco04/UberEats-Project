import React from 'react';
import { motion } from 'framer-motion';
import { Users, Scroll, Trophy, Sparkles, ChevronRight } from 'lucide-react';

/**
 * CourseCard - A gamified "Level" card for displaying course information
 * Features health bar, player count, missions, and animated hover effects
 */
const CourseCard = ({ course, index, onManage }) => {
    const {
        id,
        name,
        code,
        semester,
        studentCount,
        projectCount,
        averageTeamScore,
        isActive
    } = course;

    // Determine health bar color based on score
    const getScoreConfig = (score) => {
        if (score >= 80) {
            return {
                color: 'bg-emerald-500',
                bgColor: 'bg-emerald-100',
                textColor: 'text-emerald-600',
                label: 'Excellent',
                glowColor: 'shadow-emerald-500/30',
            };
        } else if (score >= 50) {
            return {
                color: 'bg-amber-500',
                bgColor: 'bg-amber-100',
                textColor: 'text-amber-600',
                label: 'Good',
                glowColor: 'shadow-amber-500/30',
            };
        } else {
            return {
                color: 'bg-rose-500',
                bgColor: 'bg-rose-100',
                textColor: 'text-rose-600',
                label: 'Needs Attention',
                glowColor: 'shadow-rose-500/30',
            };
        }
    };

    const scoreConfig = getScoreConfig(averageTeamScore || 0);
    const displayScore = averageTeamScore?.toFixed(1) || '0.0';

    // Format semester display
    const formatSemester = (sem) => {
        if (!sem) return 'Unknown';
        return sem.replace('_', ' ').replace('SEMESTER', 'Sem');
    };

    return (
        <motion.div
            initial={{ opacity: 0, y: 20, scale: 0.95 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            transition={{
                duration: 0.4,
                delay: index * 0.1,
                ease: [0.25, 0.46, 0.45, 0.94]
            }}
            whileHover={{
                y: -8,
                scale: 1.02,
                transition: { duration: 0.2 }
            }}
            className="relative group"
        >
            {/* Card Container */}
            <div className={`
        relative overflow-hidden rounded-2xl bg-white
        border border-slate-200/50
        shadow-lg shadow-slate-200/50
        hover:shadow-xl hover:shadow-slate-300/50
        transition-all duration-300
        ${!isActive ? 'opacity-60' : ''}
      `}>

                {/* Top Accent Bar */}
                <div className={`h-1.5 ${scoreConfig.color} w-full`} />

                {/* Card Content */}
                <div className="p-5">

                    {/* Header Section */}
                    <div className="flex items-start justify-between mb-4">
                        <div className="flex-1">
                            {/* Course Code Badge */}
                            <div className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-slate-100 text-slate-600 text-xs font-semibold mb-2">
                                <Sparkles size={12} className="text-violet-500" />
                                {code}
                            </div>

                            {/* Course Name */}
                            <h3 className="text-lg font-bold text-slate-800 leading-tight mb-1 line-clamp-2">
                                {name}
                            </h3>

                            {/* Semester Badge */}
                            <span className="inline-flex items-center px-2 py-0.5 rounded-md bg-violet-100 text-violet-700 text-xs font-medium">
                                {formatSemester(semester)}
                            </span>
                        </div>

                        {/* Active/Inactive Status */}
                        <div className={`
              flex items-center gap-1 px-2 py-1 rounded-full text-xs font-medium
              ${isActive
                                ? 'bg-emerald-100 text-emerald-700'
                                : 'bg-slate-100 text-slate-500'}
            `}>
                            <span className={`w-1.5 h-1.5 rounded-full ${isActive ? 'bg-emerald-500 animate-pulse' : 'bg-slate-400'}`} />
                            {isActive ? 'Active' : 'Inactive'}
                        </div>
                    </div>

                    {/* Health Bar Section */}
                    <div className="mb-5">
                        <div className="flex items-center justify-between mb-2">
                            <div className="flex items-center gap-2">
                                <Trophy size={16} className={scoreConfig.textColor} />
                                <span className="text-sm font-semibold text-slate-700">Class Health</span>
                            </div>
                            <div className={`flex items-center gap-1 text-sm font-bold ${scoreConfig.textColor}`}>
                                <span>{displayScore}</span>
                                <span className="text-slate-400 font-normal">/ 100 XP</span>
                            </div>
                        </div>

                        {/* XP Progress Bar */}
                        <div className={`relative h-4 rounded-full ${scoreConfig.bgColor} overflow-hidden`}>
                            <motion.div
                                initial={{ width: 0 }}
                                animate={{ width: `${averageTeamScore || 0}%` }}
                                transition={{ duration: 1, delay: index * 0.1 + 0.3, ease: "easeOut" }}
                                className={`absolute inset-y-0 left-0 ${scoreConfig.color} rounded-full`}
                            />
                            {/* Shine effect */}
                            <div className="absolute inset-0 bg-gradient-to-r from-transparent via-white/30 to-transparent -translate-x-full group-hover:translate-x-full transition-transform duration-1000" />
                        </div>

                        <p className={`text-xs mt-1.5 ${scoreConfig.textColor} font-medium`}>
                            {scoreConfig.label}
                        </p>
                    </div>

                    {/* Stats Grid */}
                    <div className="grid grid-cols-2 gap-3 mb-5">
                        {/* Players Stat */}
                        <div className="flex items-center gap-3 p-3 rounded-xl bg-gradient-to-br from-blue-50 to-indigo-50 border border-blue-100/50">
                            <div className="flex items-center justify-center w-10 h-10 rounded-lg bg-blue-500 text-white shadow-lg shadow-blue-500/30">
                                <Users size={18} />
                            </div>
                            <div>
                                <p className="text-xs text-slate-500 font-medium">Active Players</p>
                                <p className="text-xl font-bold text-slate-800">{studentCount || 0}</p>
                            </div>
                        </div>

                        {/* Missions Stat */}
                        <div className="flex items-center gap-3 p-3 rounded-xl bg-gradient-to-br from-purple-50 to-pink-50 border border-purple-100/50">
                            <div className="flex items-center justify-center w-10 h-10 rounded-lg bg-purple-500 text-white shadow-lg shadow-purple-500/30">
                                <Scroll size={18} />
                            </div>
                            <div>
                                <p className="text-xs text-slate-500 font-medium">Missions</p>
                                <p className="text-xl font-bold text-slate-800">{projectCount || 0}</p>
                            </div>
                        </div>
                    </div>

                    {/* Manage Button */}
                    <motion.button
                        onClick={() => onManage?.(id)}
                        whileHover={{ scale: 1.02 }}
                        whileTap={{ scale: 0.98 }}
                        className={`
              w-full py-3 px-4 rounded-xl
              bg-gradient-to-r from-violet-600 to-indigo-600
              hover:from-violet-700 hover:to-indigo-700
              text-white font-semibold text-sm
              shadow-lg shadow-violet-500/30
              hover:shadow-xl hover:shadow-violet-500/40
              transition-all duration-200
              flex items-center justify-center gap-2
              group/btn
            `}
                    >
                        <span>Manage Course</span>
                        <ChevronRight size={16} className="group-hover/btn:translate-x-1 transition-transform" />
                    </motion.button>
                </div>

                {/* Decorative Corner Elements */}
                <div className="absolute top-0 right-0 w-20 h-20 bg-gradient-to-br from-violet-500/5 to-transparent rounded-bl-full pointer-events-none" />
            </div>
        </motion.div>
    );
};

export default CourseCard;
