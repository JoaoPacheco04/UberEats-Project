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
 * @author UberEats
 * @version 1.0.1
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

<<<<<<< HEAD
    /**
     * Finds enrollment by course and student.
     *
     * @param courseId  The course ID
     * @param studentId The student ID
     * @return Optional containing the enrollment
     */
=======
    // Find all enrollments for a specific course
    List<CourseEnrollment> findByCourseId(Long courseId);

    // Find enrollment by course and student
>>>>>>> Yesh_Branch
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
