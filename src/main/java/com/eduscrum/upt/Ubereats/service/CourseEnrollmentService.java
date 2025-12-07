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

@Service
@Transactional
public class CourseEnrollmentService {

    private final CourseEnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public CourseEnrollmentService(CourseEnrollmentRepository enrollmentRepository,
                                   CourseRepository courseRepository,
                                   UserRepository userRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    /**
     * Enrolls a student into a course.
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
     */
    @Transactional(readOnly = true)
    public boolean isStudentEnrolled(Long courseId, Long studentId) {
        return enrollmentRepository.findByCourseIdAndStudentId(courseId, studentId)
                .map(CourseEnrollment::isActiveEnrollment)
                .orElse(false);
    }

    /**
     * Gets all courses a student is enrolled in as DTOs.
     */
    @Transactional(readOnly = true)
    public List<CourseEnrollmentResponseDTO> getStudentEnrollments(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId).stream()
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
                enrollment.getStudent().getFullName()
        );
    }
}