package com.eduscrum.upt.Ubereats.repository;

import com.eduscrum.upt.Ubereats.entity.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {

    // Find all enrollments for a specific student
    List<CourseEnrollment> findByStudentId(Long studentId);

    // Find enrollment by course and student
    Optional<CourseEnrollment> findByCourseIdAndStudentId(Long courseId, Long studentId);

    // Check if an enrollment exists (used for checking access/duplication)
    boolean existsByCourseIdAndStudentId(Long courseId, Long studentId);
}