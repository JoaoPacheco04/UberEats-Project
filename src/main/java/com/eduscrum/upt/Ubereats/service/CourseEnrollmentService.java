package com.eduscrum.upt.Ubereats.service;

import com.eduscrum.upt.Ubereats.dto.response.CourseEnrollmentResponseDTO;
import com.eduscrum.upt.Ubereats.entity.Course;
import com.eduscrum.upt.Ubereats.entity.CourseEnrollment;
import com.eduscrum.upt.Ubereats.entity.User;
import com.eduscrum.upt.Ubereats.repository.CourseEnrollmentRepository;
import com.eduscrum.upt.Ubereats.repository.CourseRepository;
import com.eduscrum.upt.Ubereats.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing course enrollments in the EduScrum platform.
 * Handles student enrollment, verification, and enrollment retrieval.
 *
 * @author Joao Pacheco
 * @author Bruna
 * @version 0.8.0 (2025-11-20)
 */
@Service
@Transactional
public class CourseEnrollmentService {

    private final CourseEnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    /**
     * Constructs a new CourseEnrollmentService with required dependencies.
     *
     * @param enrollmentRepository Repository for enrollment data access
     * @param courseRepository     Repository for course data access
     * @param userRepository       Repository for user data access
     */
    public CourseEnrollmentService(CourseEnrollmentRepository enrollmentRepository,
            CourseRepository courseRepository,
            UserRepository userRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    /**
     * Enrolls a student into a course with validation.
     *
     * @param courseId  The ID of the course
     * @param studentId The ID of the student to enroll
     * @return The enrollment as a response DTO
     * @throws IllegalArgumentException if course or student not found
     * @throws IllegalStateException    if student is already enrolled
     */
    public CourseEnrollmentResponseDTO enrollStudent(Long courseId, Long studentId) {
        // 1. Fetch entities
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + courseId));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + studentId));

        // 2. Validate business rules
        if (!student.isStudent()) {
            throw new IllegalArgumentException("User is not a student.");
        }

        if (enrollmentRepository.existsByCourseIdAndStudentId(courseId, studentId)) {
            throw new IllegalStateException("Student is already enrolled in this course.");
        }

        // 3. Create and save enrollment
        CourseEnrollment enrollment = new CourseEnrollment(course, student);
        CourseEnrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return convertToDTO(savedEnrollment);
    }

    /**
     * Checks if a student is actively enrolled in a course.
     *
     * @param courseId  The ID of the course
     * @param studentId The ID of the student
     * @return true if student is actively enrolled, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean isStudentEnrolled(Long courseId, Long studentId) {
        return enrollmentRepository.findByCourseIdAndStudentId(courseId, studentId)
                .map(CourseEnrollment::isActiveEnrollment)
                .orElse(false);
    }

    /**
     * Retrieves all course enrollments for a specific student.
     *
     * @param studentId The ID of the student
     * @return List of enrollment response DTOs
     */
    @Transactional(readOnly = true)
    public List<CourseEnrollmentResponseDTO> getStudentEnrollments(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Gets all students enrolled in a course as DTOs.
     */
    @Transactional(readOnly = true)
    public List<CourseEnrollmentResponseDTO> getCourseEnrollments(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private CourseEnrollmentResponseDTO convertToDTO(CourseEnrollment enrollment) {
        return new CourseEnrollmentResponseDTO(
                enrollment.getId(),
                enrollment.getEnrolledAt(),
                enrollment.getCourse().getId(),
                enrollment.getCourse().getName(),
                enrollment.getStudent().getId(),
                enrollment.getStudent().getFullName());
    }
}
