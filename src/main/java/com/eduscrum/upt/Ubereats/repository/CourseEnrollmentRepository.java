package com.eduscrum.upt.Ubereats.repository;

import com.eduscrum.upt.Ubereats.entity.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CourseEnrollment entity.
 * Provides CRUD operations and enrollment queries.
 *
 * @version 1.0.1 (2025-12-03)
 */
@Repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {

    /**
     * Finds all enrollments for a specific student.
     *
     * @param studentId The student ID
     * @return List of enrollments
     */
    List<CourseEnrollment> findByStudentId(Long studentId);

    /**
     * Finds enrollment by course and student.
     *
     * @param courseId  The course ID
     * @param studentId The student ID
     * @return Optional containing the enrollment
     */
    Optional<CourseEnrollment> findByCourseIdAndStudentId(Long courseId, Long studentId);

    /**
     * Checks if enrollment exists.
     *
     * @param courseId  The course ID
     * @param studentId The student ID
     * @return true if enrollment exists
     */
    boolean existsByCourseIdAndStudentId(Long courseId, Long studentId);
}
