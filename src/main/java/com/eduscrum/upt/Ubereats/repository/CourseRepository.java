package com.eduscrum.upt.Ubereats.repository;

import com.eduscrum.upt.Ubereats.entity.Course;
import com.eduscrum.upt.Ubereats.entity.User;
import com.eduscrum.upt.Ubereats.entity.enums.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Course entity.
 * Provides methods to perform CRUD operations and custom queries.
 *
 * @version 1.1.0 (2025-12-08)
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Find courses by teacher
    List<Course> findByTeacherAndIsActiveTrue(User teacher);

    List<Course> findByTeacher(User teacher);

    // Find active courses
    List<Course> findByIsActiveTrue();

    // Find course by code
    Optional<Course> findByCode(String code);

    // Check if code already exists
    boolean existsByCode(String code);

    // Find courses by academic year and semester
    List<Course> findByAcademicYearAndSemesterAndIsActiveTrue(String academicYear, Semester semester);

    // Check if teacher owns the course
    @Query("SELECT COUNT(c) > 0 FROM Course c WHERE c.id = :courseId AND c.teacher.email = :teacherEmail")
    boolean isCourseTeacher(@Param("courseId") Long courseId, @Param("teacherEmail") String teacherEmail);

    // Find available courses for students (active and not enrolled)
    @Query("SELECT c FROM Course c WHERE c.isActive = true AND c NOT IN " +
            "(SELECT ce.course FROM CourseEnrollment ce WHERE ce.student.email = :studentEmail)")
    List<Course> findAvailableCoursesForStudent(@Param("studentEmail") String studentEmail);
}
