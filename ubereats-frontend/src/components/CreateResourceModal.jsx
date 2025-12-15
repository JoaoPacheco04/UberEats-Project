/**
 * Create Resource Modal Component
 * Modal for creating courses, projects, sprints, and teams.
 * 
 * @author Francisco
 * @author Bruna
 * @version 1.0.0
 */
import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { X, BookOpen, FolderOpen, Calendar, FileText, Plus, GraduationCap } from 'lucide-react';

/**
 * CreateResourceModal - Modal for creating Courses or Projects
 * Features tab toggle between Course and Project forms
 */
const CreateResourceModal = ({ isOpen, onClose, onSubmitCourse, onSubmitProject, courses = [] }) => {
    const [activeTab, setActiveTab] = useState('course'); // 'course' or 'project'

    // Course form state
    const [courseForm, setCourseForm] = useState({
        name: '',
        code: '',
        description: '',
        semester: 'FIRST',
        academicYear: '',
    });

    // Project form state
    const [projectForm, setProjectForm] = useState({
        name: '',
        description: '',
        startDate: '',
        endDate: '',
        courseId: '',
    });

    const [isSubmitting, setIsSubmitting] = useState(false);
    const [errors, setErrors] = useState({});

    const handleCourseChange = (e) => {
        const { name, value } = e.target;
        setCourseForm(prev => ({ ...prev, [name]: value }));
        if (errors[name]) setErrors(prev => ({ ...prev, [name]: '' }));
    };

    const handleProjectChange = (e) => {
        const { name, value } = e.target;
        setProjectForm(prev => ({ ...prev, [name]: value }));
        if (errors[name]) setErrors(prev => ({ ...prev, [name]: '' }));
    };

    const validateCourse = () => {
        const newErrors = {};
        if (!courseForm.name.trim()) newErrors.name = 'Course name is required';
        if (!courseForm.code.trim()) newErrors.code = 'Course code is required';
        if (!courseForm.semester) newErrors.semester = 'Semester is required';
        if (!courseForm.academicYear.trim()) newErrors.academicYear = 'Academic year is required';
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const validateProject = () => {
        const newErrors = {};
        if (!projectForm.name.trim()) newErrors.name = 'Project name is required';
        if (!projectForm.courseId) newErrors.courseId = 'Please select a course';
        if (!projectForm.startDate) newErrors.startDate = 'Start date is required';
        if (!projectForm.endDate) newErrors.endDate = 'End date is required';
        if (projectForm.startDate && projectForm.endDate && projectForm.startDate > projectForm.endDate) {
            newErrors.endDate = 'End date must be after start date';
        }
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsSubmitting(true);

        try {
            if (activeTab === 'course') {
                if (!validateCourse()) {
                    setIsSubmitting(false);
                    return;
                }
                await onSubmitCourse?.(courseForm);
                setCourseForm({ name: '', code: '', description: '', semester: 'FIRST', academicYear: '' });
            } else {
                if (!validateProject()) {
                    setIsSubmitting(false);
                    return;
                }
                await onSubmitProject?.(projectForm);
                setProjectForm({ name: '', description: '', startDate: '', endDate: '', courseId: '' });
            }
            setErrors({});
            onClose();
        } catch (error) {
            console.error('Failed to create resource:', error);
            setErrors({ submit: error.message || 'Failed to create. Please try again.' });
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleBackdropClick = (e) => {
        if (e.target === e.currentTarget) {
            onClose();
        }
    };

    const resetForms = () => {
        setCourseForm({ name: '', code: '', description: '', semester: 'FIRST', academicYear: '' });
        setProjectForm({ name: '', description: '', startDate: '', endDate: '', courseId: '' });
        setErrors({});
    };

    const handleClose = () => {
        resetForms();
        onClose();
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
                        className="relative w-full max-w-lg bg-white rounded-2xl shadow-2xl overflow-hidden max-h-[90vh] flex flex-col"
                    >
                        {/* Header */}
                        <div className="relative bg-gradient-to-r from-violet-600 via-indigo-600 to-purple-600 p-6">
                            <button
                                onClick={handleClose}
                                type="button"
                                className="absolute top-4 right-4 z-10 p-2 rounded-full bg-white/10 hover:bg-white/20 text-white transition-colors cursor-pointer"
                            >
                                <X size={20} />
                            </button>

                            <div className="flex items-center gap-4">
                                <div className="flex items-center justify-center w-14 h-14 rounded-xl bg-white/20 backdrop-blur-sm">
                                    <GraduationCap size={28} className="text-white" />
                                </div>
                                <div>
                                    <h2 className="text-2xl font-bold text-white">Create New</h2>
                                    <p className="text-violet-200 text-sm mt-0.5">Add a new course or project</p>
                                </div>
                            </div>

                            {/* Tab Toggle */}
                            <div className="flex mt-6 bg-white/10 rounded-xl p-1">
                                <button
                                    type="button"
                                    onClick={() => { setActiveTab('course'); setErrors({}); }}
                                    className={`flex-1 flex items-center justify-center gap-2 py-2.5 px-4 rounded-lg font-semibold text-sm transition-all ${activeTab === 'course'
                                        ? 'bg-white text-violet-700 shadow-lg'
                                        : 'text-white/80 hover:text-white'
                                        }`}
                                >
                                    <BookOpen size={16} />
                                    New Course
                                </button>
                                <button
                                    type="button"
                                    onClick={() => { setActiveTab('project'); setErrors({}); }}
                                    className={`flex-1 flex items-center justify-center gap-2 py-2.5 px-4 rounded-lg font-semibold text-sm transition-all ${activeTab === 'project'
                                        ? 'bg-white text-violet-700 shadow-lg'
                                        : 'text-white/80 hover:text-white'
                                        }`}
                                >
                                    <FolderOpen size={16} />
                                    New Project
                                </button>
                            </div>
                        </div>

                        {/* Form Content - Scrollable */}
                        <form onSubmit={handleSubmit} className="p-6 space-y-5 overflow-y-auto flex-1">
                            {errors.submit && (
                                <div className="bg-rose-50 border border-rose-200 text-rose-700 p-3 rounded-xl text-sm">
                                    {errors.submit}
                                </div>
                            )}

                            {activeTab === 'course' ? (
                                /* COURSE FORM */
                                <>
                                    <div>
                                        <label className="flex items-center gap-2 text-sm font-semibold text-slate-700 mb-2">
                                            <BookOpen size={16} className="text-violet-500" />
                                            Course Name *
                                        </label>
                                        <input
                                            type="text"
                                            name="name"
                                            value={courseForm.name}
                                            onChange={handleCourseChange}
                                            placeholder="e.g., Software Engineering"
                                            className={`w-full px-4 py-3 rounded-xl border-2 bg-slate-50 focus:bg-white focus:border-violet-500 focus:ring-4 focus:ring-violet-500/10 transition-all duration-200 outline-none ${errors.name ? 'border-rose-400 bg-rose-50' : 'border-slate-200'}`}
                                        />
                                        {errors.name && <p className="mt-1 text-sm text-rose-500">{errors.name}</p>}
                                    </div>

                                    <div>
                                        <label className="flex items-center gap-2 text-sm font-semibold text-slate-700 mb-2">
                                            Course Code *
                                        </label>
                                        <input
                                            type="text"
                                            name="code"
                                            value={courseForm.code}
                                            onChange={handleCourseChange}
                                            placeholder="e.g., SE2024"
                                            className={`w-full px-4 py-3 rounded-xl border-2 bg-slate-50 focus:bg-white focus:border-violet-500 focus:ring-4 focus:ring-violet-500/10 transition-all duration-200 outline-none ${errors.code ? 'border-rose-400 bg-rose-50' : 'border-slate-200'}`}
                                        />
                                        {errors.code && <p className="mt-1 text-sm text-rose-500">{errors.code}</p>}
                                    </div>

                                    <div className="grid grid-cols-2 gap-4">
                                        <div>
                                            <label className="flex items-center gap-2 text-sm font-semibold text-slate-700 mb-2">
                                                Semester *
                                            </label>
                                            <select
                                                name="semester"
                                                value={courseForm.semester}
                                                onChange={handleCourseChange}
                                                className={`w-full px-4 py-3 rounded-xl border-2 bg-slate-50 focus:bg-white focus:border-violet-500 focus:ring-4 focus:ring-violet-500/10 transition-all duration-200 outline-none cursor-pointer ${errors.semester ? 'border-rose-400 bg-rose-50' : 'border-slate-200'}`}
                                            >
                                                <option value="FIRST">1st Semester</option>
                                                <option value="SECOND">2nd Semester</option>
                                                <option value="ANNUAL">Annual</option>
                                            </select>
                                            {errors.semester && <p className="mt-1 text-sm text-rose-500">{errors.semester}</p>}
                                        </div>
                                        <div>
                                            <label className="flex items-center gap-2 text-sm font-semibold text-slate-700 mb-2">
                                                Academic Year *
                                            </label>
                                            <input
                                                type="text"
                                                name="academicYear"
                                                value={courseForm.academicYear}
                                                onChange={handleCourseChange}
                                                placeholder="e.g., 2024/2025"
                                                className={`w-full px-4 py-3 rounded-xl border-2 bg-slate-50 focus:bg-white focus:border-violet-500 focus:ring-4 focus:ring-violet-500/10 transition-all duration-200 outline-none ${errors.academicYear ? 'border-rose-400 bg-rose-50' : 'border-slate-200'}`}
                                            />
                                            {errors.academicYear && <p className="mt-1 text-sm text-rose-500">{errors.academicYear}</p>}
                                        </div>
                                    </div>

                                    <div>
                                        <label className="flex items-center gap-2 text-sm font-semibold text-slate-700 mb-2">
                                            <FileText size={16} className="text-blue-500" />
                                            Description
                                        </label>
                                        <textarea
                                            name="description"
                                            value={courseForm.description}
                                            onChange={handleCourseChange}
                                            rows={3}
                                            placeholder="Course description (optional)"
                                            className="w-full px-4 py-3 rounded-xl border-2 border-slate-200 bg-slate-50 focus:bg-white focus:border-violet-500 focus:ring-4 focus:ring-violet-500/10 transition-all duration-200 outline-none resize-none"
                                        />
                                    </div>
                                </>
                            ) : (
                                /* PROJECT FORM */
                                <>
                                    <div>
                                        <label className="flex items-center gap-2 text-sm font-semibold text-slate-700 mb-2">
                                            <FolderOpen size={16} className="text-purple-500" />
                                            Project Name *
                                        </label>
                                        <input
                                            type="text"
                                            name="name"
                                            value={projectForm.name}
                                            onChange={handleProjectChange}
                                            placeholder="e.g., Sprint 1 - User Authentication"
                                            className={`w-full px-4 py-3 rounded-xl border-2 bg-slate-50 focus:bg-white focus:border-violet-500 focus:ring-4 focus:ring-violet-500/10 transition-all duration-200 outline-none ${errors.name ? 'border-rose-400 bg-rose-50' : 'border-slate-200'}`}
                                        />
                                        {errors.name && <p className="mt-1 text-sm text-rose-500">{errors.name}</p>}
                                    </div>

                                    <div>
                                        <label className="flex items-center gap-2 text-sm font-semibold text-slate-700 mb-2">
                                            <BookOpen size={16} className="text-violet-500" />
                                            Select Course *
                                        </label>
                                        <select
                                            name="courseId"
                                            value={projectForm.courseId}
                                            onChange={handleProjectChange}
                                            className={`w-full px-4 py-3 rounded-xl border-2 bg-slate-50 focus:bg-white focus:border-violet-500 focus:ring-4 focus:ring-violet-500/10 transition-all duration-200 outline-none cursor-pointer ${errors.courseId ? 'border-rose-400 bg-rose-50' : 'border-slate-200'}`}
                                        >
                                            <option value="">Choose a course...</option>
                                            {courses.map(course => (
                                                <option key={course.id} value={course.id}>
                                                    {course.code} - {course.name}
                                                </option>
                                            ))}
                                        </select>
                                        {errors.courseId && <p className="mt-1 text-sm text-rose-500">{errors.courseId}</p>}
                                        {courses.length === 0 && (
                                            <p className="mt-1 text-sm text-amber-600">No courses available. Create a course first.</p>
                                        )}
                                    </div>

                                    <div>
                                        <label className="flex items-center gap-2 text-sm font-semibold text-slate-700 mb-2">
                                            <FileText size={16} className="text-blue-500" />
                                            Description
                                        </label>
                                        <textarea
                                            name="description"
                                            value={projectForm.description}
                                            onChange={handleProjectChange}
                                            rows={3}
                                            placeholder="Project description (optional)"
                                            className="w-full px-4 py-3 rounded-xl border-2 border-slate-200 bg-slate-50 focus:bg-white focus:border-violet-500 focus:ring-4 focus:ring-violet-500/10 transition-all duration-200 outline-none resize-none"
                                        />
                                    </div>

                                    <div className="grid grid-cols-2 gap-4">
                                        <div>
                                            <label className="flex items-center gap-2 text-sm font-semibold text-slate-700 mb-2">
                                                <Calendar size={16} className="text-emerald-500" />
                                                Start Date *
                                            </label>
                                            <input
                                                type="date"
                                                name="startDate"
                                                value={projectForm.startDate}
                                                onChange={handleProjectChange}
                                                className={`w-full px-4 py-3 rounded-xl border-2 bg-slate-50 focus:bg-white focus:border-violet-500 focus:ring-4 focus:ring-violet-500/10 transition-all duration-200 outline-none ${errors.startDate ? 'border-rose-400 bg-rose-50' : 'border-slate-200'}`}
                                            />
                                            {errors.startDate && <p className="mt-1 text-xs text-rose-500">{errors.startDate}</p>}
                                        </div>
                                        <div>
                                            <label className="flex items-center gap-2 text-sm font-semibold text-slate-700 mb-2">
                                                <Calendar size={16} className="text-rose-500" />
                                                End Date *
                                            </label>
                                            <input
                                                type="date"
                                                name="endDate"
                                                value={projectForm.endDate}
                                                onChange={handleProjectChange}
                                                className={`w-full px-4 py-3 rounded-xl border-2 bg-slate-50 focus:bg-white focus:border-violet-500 focus:ring-4 focus:ring-violet-500/10 transition-all duration-200 outline-none ${errors.endDate ? 'border-rose-400 bg-rose-50' : 'border-slate-200'}`}
                                            />
                                            {errors.endDate && <p className="mt-1 text-xs text-rose-500">{errors.endDate}</p>}
                                        </div>
                                    </div>
                                </>
                            )}

                            {/* Submit Button */}
                            <motion.button
                                type="submit"
                                disabled={isSubmitting}
                                whileHover={{ scale: 1.02 }}
                                whileTap={{ scale: 0.98 }}
                                className="relative w-full py-4 px-6 rounded-xl font-bold text-lg text-white bg-gradient-to-r from-violet-600 to-indigo-600 hover:from-violet-700 hover:to-indigo-700 shadow-xl shadow-violet-500/30 hover:shadow-2xl hover:shadow-violet-500/40 transition-all duration-200 flex items-center justify-center gap-3 disabled:opacity-50 disabled:cursor-not-allowed"
                            >
                                <Plus size={22} />
                                <span>{isSubmitting ? 'Creating...' : `Create ${activeTab === 'course' ? 'Course' : 'Project'}`}</span>
                            </motion.button>
                        </form>
                    </motion.div>
                </motion.div>
            )}
        </AnimatePresence>
    );
};

export default CreateResourceModal;
