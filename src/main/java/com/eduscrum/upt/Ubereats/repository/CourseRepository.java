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

    /**
     * Finds active courses by teacher.
     *
     * @param teacher The teacher
     * @return List of active courses for the teacher
     */
    List<Course> findByTeacherAndIsActiveTrue(User teacher);

    /**
     * Finds courses by teacher.
     *
     * @param teacher The teacher
     * @return List of courses for the teacher
     */
    List<Course> findByTeacher(User teacher);

    /**
     * Finds all active courses.
     *
     * @return List of active courses
     */
    List<Course> findByIsActiveTrue();

    /**
     * Finds a course by its code.
     *
     * @param code The course code
     * @return Optional containing the course if found
     */
    Optional<Course> findByCode(String code);

    /**
     * Checks if code already exists.
     *
     * @param code The course code
     * @return true if code exists
     */
    boolean existsByCode(String code);

    /**
     * Finds active courses by academic year and semester.
     *
     * @param academicYear The academic year
     * @param semester     The semester
     * @return List of matching courses
     */
    List<Course> findByAcademicYearAndSemesterAndIsActiveTrue(String academicYear, Semester semester);

    /**
     * Checks if teacher owns the course.
     *
     * @param courseId     The course ID
     * @param teacherEmail The teacher email
     * @return true if teacher owns the course
     */
    @Query("SELECT COUNT(c) > 0 FROM Course c WHERE c.id = :courseId AND c.teacher.email = :teacherEmail")
    boolean isCourseTeacher(@Param("courseId") Long courseId, @Param("teacherEmail") String teacherEmail);

    /**
     * Finds available courses for a student.
     *
     * @param studentEmail The student email
     * @return List of available courses
     */
    @Query("SELECT c FROM Course c WHERE c.isActive = true AND c NOT IN " +
            "(SELECT ce.course FROM CourseEnrollment ce WHERE ce.student.email = :studentEmail)")
    List<Course> findAvailableCoursesForStudent(@Param("studentEmail") String studentEmail);
}
