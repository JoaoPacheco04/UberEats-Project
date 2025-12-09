import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { X, Gamepad2, Trophy, Calendar, FileText, Rocket, Sparkles } from 'lucide-react';

/**
 * CreateProjectModal - A gamified modal for creating new projects
 * Features "Start Game" styled button and smooth animations
 */
const CreateProjectModal = ({ isOpen, onClose, onSubmit, courses = [] }) => {
    const [formData, setFormData] = useState({
        name: '',
        description: '',
        courseId: '',
        startDate: '',
        endDate: '',
        maxTeamSize: 5,
    });
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [errors, setErrors] = useState({});

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
        // Clear error when user starts typing
        if (errors[name]) {
            setErrors(prev => ({ ...prev, [name]: '' }));
        }
    };

    const validate = () => {
        const newErrors = {};
        if (!formData.name.trim()) newErrors.name = 'Quest name is required';
        if (!formData.courseId) newErrors.courseId = 'Select a realm (course)';
        if (!formData.startDate) newErrors.startDate = 'Start date is required';
        if (!formData.endDate) newErrors.endDate = 'End date is required';
        if (formData.startDate && formData.endDate && formData.startDate > formData.endDate) {
            newErrors.endDate = 'End date must be after start date';
        }
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!validate()) return;

        setIsSubmitting(true);
        try {
            await onSubmit?.(formData);
            // Reset form
            setFormData({
                name: '',
                description: '',
                courseId: '',
                startDate: '',
                endDate: '',
                maxTeamSize: 5,
            });
            onClose();
        } catch (error) {
            console.error('Failed to create project:', error);
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleBackdropClick = (e) => {
        if (e.target === e.currentTarget) {
            onClose();
        }
    };

    return (
        <AnimatePresence>
            {isOpen && (
                <motion.div
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    exit={{ opacity: 0 }}
                    transition={{ duration: 0.2 }}
                    onClick={handleBackdropClick}
                    className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-sm"
                >
                    <motion.div
                        initial={{ opacity: 0, scale: 0.9, y: 20 }}
                        animate={{ opacity: 1, scale: 1, y: 0 }}
                        exit={{ opacity: 0, scale: 0.9, y: 20 }}
                        transition={{ type: "spring", damping: 25, stiffness: 300 }}
                        className="relative w-full max-w-lg bg-white rounded-2xl shadow-2xl overflow-hidden"
                    >
                        {/* Header Gradient */}
                        <div className="relative bg-gradient-to-r from-violet-600 via-indigo-600 to-purple-600 p-6 pb-8">
                            {/* Decorative elements */}
                            <div className="absolute inset-0 overflow-hidden">
                                <div className="absolute -top-10 -right-10 w-40 h-40 bg-white/10 rounded-full blur-2xl" />
                                <div className="absolute -bottom-10 -left-10 w-32 h-32 bg-white/10 rounded-full blur-2xl" />
                            </div>

                            {/* Close button */}
                            <button
                                onClick={onClose}
                                className="absolute top-4 right-4 p-2 rounded-full bg-white/10 hover:bg-white/20 text-white transition-colors"
                            >
                                <X size={20} />
                            </button>

                            {/* Header content */}
                            <div className="relative flex items-center gap-4">
                                <div className="flex items-center justify-center w-14 h-14 rounded-xl bg-white/20 backdrop-blur-sm">
                                    <Gamepad2 size={28} className="text-white" />
                                </div>
                                <div>
                                    <h2 className="text-2xl font-bold text-white">Launch New Quest</h2>
                                    <p className="text-violet-200 text-sm mt-0.5">Create an epic project adventure</p>
                                </div>
                            </div>
                        </div>

                        {/* Form Content */}
                        <form onSubmit={handleSubmit} className="p-6 space-y-5">
                            {/* Quest Name */}
                            <div>
                                <label className="flex items-center gap-2 text-sm font-semibold text-slate-700 mb-2">
                                    <Trophy size={16} className="text-amber-500" />
                                    Quest Name
                                </label>
                                <input
                                    type="text"
                                    name="name"
                                    value={formData.name}
                                    onChange={handleChange}
                                    placeholder="Epic Sprint Challenge"
                                    className={`
                    w-full px-4 py-3 rounded-xl border-2 bg-slate-50
                    focus:bg-white focus:border-violet-500 focus:ring-4 focus:ring-violet-500/10
                    transition-all duration-200 outline-none
                    ${errors.name ? 'border-rose-400 bg-rose-50' : 'border-slate-200'}
                  `}
                                />
                                {errors.name && (
                                    <p className="mt-1.5 text-sm text-rose-500 flex items-center gap-1">
                                        <span>⚠️</span> {errors.name}
                                    </p>
                                )}
                            </div>

                            {/* Course Selection */}
                            <div>
                                <label className="flex items-center gap-2 text-sm font-semibold text-slate-700 mb-2">
                                    <Sparkles size={16} className="text-violet-500" />
                                    Select Realm (Course)
                                </label>
                                <select
                                    name="courseId"
                                    value={formData.courseId}
                                    onChange={handleChange}
                                    className={`
                    w-full px-4 py-3 rounded-xl border-2 bg-slate-50
                    focus:bg-white focus:border-violet-500 focus:ring-4 focus:ring-violet-500/10
                    transition-all duration-200 outline-none cursor-pointer
                    ${errors.courseId ? 'border-rose-400 bg-rose-50' : 'border-slate-200'}
                  `}
                                >
                                    <option value="">Choose a course...</option>
                                    {courses.map(course => (
                                        <option key={course.id} value={course.id}>
                                            {course.code} - {course.name}
                                        </option>
                                    ))}
                                </select>
                                {errors.courseId && (
                                    <p className="mt-1.5 text-sm text-rose-500 flex items-center gap-1">
                                        <span>⚠️</span> {errors.courseId}
                                    </p>
                                )}
                            </div>

                            {/* Description */}
                            <div>
                                <label className="flex items-center gap-2 text-sm font-semibold text-slate-700 mb-2">
                                    <FileText size={16} className="text-blue-500" />
                                    Quest Description
                                </label>
                                <textarea
                                    name="description"
                                    value={formData.description}
                                    onChange={handleChange}
                                    rows={3}
                                    placeholder="Describe the objectives of this quest..."
                                    className="
                    w-full px-4 py-3 rounded-xl border-2 border-slate-200 bg-slate-50
                    focus:bg-white focus:border-violet-500 focus:ring-4 focus:ring-violet-500/10
                    transition-all duration-200 outline-none resize-none
                  "
                                />
                            </div>

                            {/* Date Range */}
                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className="flex items-center gap-2 text-sm font-semibold text-slate-700 mb-2">
                                        <Calendar size={16} className="text-emerald-500" />
                                        Start Date
                                    </label>
                                    <input
                                        type="date"
                                        name="startDate"
                                        value={formData.startDate}
                                        onChange={handleChange}
                                        className={`
                      w-full px-4 py-3 rounded-xl border-2 bg-slate-50
                      focus:bg-white focus:border-violet-500 focus:ring-4 focus:ring-violet-500/10
                      transition-all duration-200 outline-none
                      ${errors.startDate ? 'border-rose-400 bg-rose-50' : 'border-slate-200'}
                    `}
                                    />
                                    {errors.startDate && (
                                        <p className="mt-1 text-xs text-rose-500">{errors.startDate}</p>
                                    )}
                                </div>
                                <div>
                                    <label className="flex items-center gap-2 text-sm font-semibold text-slate-700 mb-2">
                                        <Calendar size={16} className="text-rose-500" />
                                        End Date
                                    </label>
                                    <input
                                        type="date"
                                        name="endDate"
                                        value={formData.endDate}
                                        onChange={handleChange}
                                        className={`
                      w-full px-4 py-3 rounded-xl border-2 bg-slate-50
                      focus:bg-white focus:border-violet-500 focus:ring-4 focus:ring-violet-500/10
                      transition-all duration-200 outline-none
                      ${errors.endDate ? 'border-rose-400 bg-rose-50' : 'border-slate-200'}
                    `}
                                    />
                                    {errors.endDate && (
                                        <p className="mt-1 text-xs text-rose-500">{errors.endDate}</p>
                                    )}
                                </div>
                            </div>

                            {/* Team Size */}
                            <div>
                                <label className="flex items-center gap-2 text-sm font-semibold text-slate-700 mb-2">
                                    <Gamepad2 size={16} className="text-indigo-500" />
                                    Max Team Size
                                </label>
                                <input
                                    type="number"
                                    name="maxTeamSize"
                                    value={formData.maxTeamSize}
                                    onChange={handleChange}
                                    min={2}
                                    max={10}
                                    className="
                    w-full px-4 py-3 rounded-xl border-2 border-slate-200 bg-slate-50
                    focus:bg-white focus:border-violet-500 focus:ring-4 focus:ring-violet-500/10
                    transition-all duration-200 outline-none
                  "
                                />
                            </div>

                            {/* Submit Button */}
                            <motion.button
                                type="submit"
                                disabled={isSubmitting}
                                whileHover={{ scale: 1.02 }}
                                whileTap={{ scale: 0.98 }}
                                className={`
                  relative w-full py-4 px-6 rounded-xl font-bold text-lg text-white
                  bg-gradient-to-r from-emerald-500 via-green-500 to-teal-500
                  hover:from-emerald-600 hover:via-green-600 hover:to-teal-600
                  shadow-xl shadow-emerald-500/30
                  hover:shadow-2xl hover:shadow-emerald-500/40
                  transition-all duration-200
                  flex items-center justify-center gap-3
                  disabled:opacity-50 disabled:cursor-not-allowed
                  overflow-hidden
                `}
                            >
                                {/* Shine effect */}
                                <div className="absolute inset-0 bg-gradient-to-r from-transparent via-white/20 to-transparent -translate-x-full hover:translate-x-full transition-transform duration-700" />

                                <Rocket size={22} className={isSubmitting ? 'animate-bounce' : ''} />
                                <span>{isSubmitting ? 'Launching...' : 'Start Quest!'}</span>
                                <Sparkles size={18} className="text-yellow-200" />
                            </motion.button>
                        </form>
                    </motion.div>
                </motion.div>
            )}
        </AnimatePresence>
    );
};

export default CreateProjectModal;
