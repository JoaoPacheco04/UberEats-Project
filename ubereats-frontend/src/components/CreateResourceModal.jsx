import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { X, BookOpen, FolderOpen, Calendar, FileText, Plus, GraduationCap } from 'lucide-react';

/**
 * CreateResourceModal
 * -------------------
 * Modal component used to create either:
 *  - a Course
 *  - a Project
 *
 * Features:
 * - Animated modal (Framer Motion)
 * - Tab-based toggle between Course & Project forms
 * - Client-side validation
 * - Reusable and controlled via props
 */
const CreateResourceModal = ({
    isOpen,
    onClose,
    onSubmitCourse,
    onSubmitProject,
    courses = []
}) => {

    /* ----------------------------------
       UI STATE
    ---------------------------------- */

    // Active tab: determines which form is shown
    const [activeTab, setActiveTab] = useState('course'); // 'course' | 'project'

    /* ----------------------------------
       COURSE FORM STATE
    ---------------------------------- */
    const [courseForm, setCourseForm] = useState({
        name: '',
        code: '',
        description: '',
        semester: 'FIRST',
        academicYear: '',
    });

    /* ----------------------------------
       PROJECT FORM STATE
    ---------------------------------- */
    const [projectForm, setProjectForm] = useState({
        name: '',
        description: '',
        startDate: '',
        endDate: '',
        courseId: '',
    });

    // Submission & validation state
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [errors, setErrors] = useState({});

    /* ----------------------------------
       INPUT HANDLERS
    ---------------------------------- */

    // Update course form fields
    const handleCourseChange = (e) => {
        const { name, value } = e.target;
        setCourseForm(prev => ({ ...prev, [name]: value }));

        // Clear field-specific error when user edits
        if (errors[name]) setErrors(prev => ({ ...prev, [name]: '' }));
    };

    // Update project form fields
    const handleProjectChange = (e) => {
        const { name, value } = e.target;
        setProjectForm(prev => ({ ...prev, [name]: value }));

        if (errors[name]) setErrors(prev => ({ ...prev, [name]: '' }));
    };

    /* ----------------------------------
       VALIDATION LOGIC
    ---------------------------------- */

    // Validate course form fields
    const validateCourse = () => {
        const newErrors = {};

        if (!courseForm.name.trim()) newErrors.name = 'Course name is required';
        if (!courseForm.code.trim()) newErrors.code = 'Course code is required';
        if (!courseForm.semester) newErrors.semester = 'Semester is required';
        if (!courseForm.academicYear.trim()) newErrors.academicYear = 'Academic year is required';

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    // Validate project form fields
    const validateProject = () => {
        const newErrors = {};

        if (!projectForm.name.trim()) newErrors.name = 'Project name is required';
        if (!projectForm.courseId) newErrors.courseId = 'Please select a course';
        if (!projectForm.startDate) newErrors.startDate = 'Start date is required';
        if (!projectForm.endDate) newErrors.endDate = 'End date is required';

        // Ensure end date is after start date
        if (
            projectForm.startDate &&
            projectForm.endDate &&
            projectForm.startDate > projectForm.endDate
        ) {
            newErrors.endDate = 'End date must be after start date';
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    /* ----------------------------------
       FORM SUBMISSION
    ---------------------------------- */
    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsSubmitting(true);

        try {
            if (activeTab === 'course') {
                // Validate and submit course
                if (!validateCourse()) return setIsSubmitting(false);
                await onSubmitCourse?.(courseForm);
                setCourseForm({ name: '', code: '', description: '', semester: 'FIRST', academicYear: '' });
            } else {
                // Validate and submit project
                if (!validateProject()) return setIsSubmitting(false);
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

    /* ----------------------------------
       MODAL BEHAVIOR
    ---------------------------------- */

    // Close modal only when clicking backdrop
    const handleBackdropClick = (e) => {
        if (e.target === e.currentTarget) onClose();
    };

    // Reset all forms and errors
    const resetForms = () => {
        setCourseForm({ name: '', code: '', description: '', semester: 'FIRST', academicYear: '' });
        setProjectForm({ name: '', description: '', startDate: '', endDate: '', courseId: '' });
        setErrors({});
    };

    const handleClose = () => {
        resetForms();
        onClose();
    };

    /* ----------------------------------
       RENDER
    ---------------------------------- */
    return (
        <AnimatePresence>
            {isOpen && (
                <motion.div
                    className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-sm"
                    onClick={handleBackdropClick}
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    exit={{ opacity: 0 }}
                >

                    {/* Modal Container */}
                    <motion.div
                        className="relative w-full max-w-lg bg-white rounded-2xl shadow-2xl overflow-hidden max-h-[90vh] flex flex-col"
                        initial={{ opacity: 0, scale: 0.9, y: 20 }}
                        animate={{ opacity: 1, scale: 1, y: 0 }}
                        exit={{ opacity: 0, scale: 0.9, y: 20 }}
                    >

                        {/* Header */}
                        <div className="bg-gradient-to-r from-violet-600 to-indigo-600 p-6 relative">
                            <button onClick={handleClose} className="absolute top-4 right-4 text-white">
                                <X size={20} />
                            </button>

                            <h2 className="text-2xl font-bold text-white">Create New</h2>
                            <p className="text-violet-200 text-sm">Add a new course or project</p>

                            {/* Tab Toggle */}
                            <div className="flex mt-4 bg-white/10 rounded-xl p-1">
                                <button
                                    type="button"
                                    onClick={() => setActiveTab('course')}
                                    className={activeTab === 'course' ? 'bg-white text-violet-700' : 'text-white'}
                                >
                                    <BookOpen size={16} /> Course
                                </button>

                                <button
                                    type="button"
                                    onClick={() => setActiveTab('project')}
                                    className={activeTab === 'project' ? 'bg-white text-violet-700' : 'text-white'}
                                >
                                    <FolderOpen size={16} /> Project
                                </button>
                            </div>
                        </div>

                        {/* Form */}
                        <form onSubmit={handleSubmit} className="p-6 space-y-5 overflow-y-auto flex-1">
                            {/* Form content omitted for brevity — unchanged */}
                        </form>
                    </motion.div>
                </motion.div>
            )}
        </AnimatePresence>
    );
};

export default CreateResourceModal;
