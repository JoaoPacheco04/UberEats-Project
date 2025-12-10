package com.eduscrum.upt.Ubereats.entity;

import com.eduscrum.upt.Ubereats.entity.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure unit tests for CourseEnrollment entity.
 * Unit tests for CourseEnrollment entity.
 *
 * @author UberEats
 * @version 0.7.1
 */
class CourseEnrollmentEntityTest {

    private CourseEnrollment enrollment;
    private Course course;
    private User student;
    private User teacher;

    @BeforeEach
    void setUp() {
        teacher = new User();
        teacher.setId(1L);
        teacher.setUsername("teacher");
        teacher.setRole(UserRole.TEACHER);

        course = new Course("Test Course", "TC101", "Description", Semester.FIRST, "2024", teacher);
        course.setId(1L);
        course.setIsActive(true);

        student = new User();
        student.setId(2L);
        student.setUsername("student");
        student.setEmail("student@test.com");
        student.setFirstName("Test");
        student.setLastName("Student");
        student.setRole(UserRole.STUDENT);

        enrollment = new CourseEnrollment(course, student);
        enrollment.setId(1L);
    }

    // ===================== ACTIVE ENROLLMENT CHECKS =====================

    @Test
    void isActiveEnrollment_WhenCourseIsActive_ReturnsTrue() {
        course.setIsActive(true);
        assertTrue(enrollment.isActiveEnrollment());
    }

    @Test
    void isActiveEnrollment_WhenCourseIsNotActive_ReturnsFalse() {
        course.setIsActive(false);
        assertFalse(enrollment.isActiveEnrollment());
    }

    @Test
    void isActiveEnrollment_WhenCourseIsNull_ReturnsFalse() {
        enrollment.setCourse(null);
        assertFalse(enrollment.isActiveEnrollment());
    }

    // ===================== GETTERS =====================

    @Test
    void getCourse_ReturnsCourse() {
        assertEquals(course, enrollment.getCourse());
    }

    @Test
    void getStudent_ReturnsStudent() {
        assertEquals(student, enrollment.getStudent());
    }

    @Test
    void getEnrolledAt_ReturnsEnrolledAt() {
        assertNotNull(enrollment.getEnrolledAt());
    }

    // ===================== CONSTRUCTOR =====================

    @Test
    void constructor_SetsFieldsCorrectly() {
        CourseEnrollment newEnrollment = new CourseEnrollment(course, student);

        assertEquals(course, newEnrollment.getCourse());
        assertEquals(student, newEnrollment.getStudent());
        assertNotNull(newEnrollment.getEnrolledAt());
    }

    // ===================== ENROLLMENT DURATION =====================

    @Test
    void getEnrollmentDuration_ReturnsNonNegative() {
        Long duration = enrollment.getEnrollmentDuration();
        assertTrue(duration >= 0);
    }

    // ===================== EQUALS AND HASHCODE =====================

    @Test
    void equals_SameIdCourseStudent_ReturnsTrue() {
        CourseEnrollment enrollment2 = new CourseEnrollment(course, student);
        enrollment2.setId(1L);

        assertEquals(enrollment, enrollment2);
    }

    @Test
    void equals_DifferentId_ReturnsFalse() {
        CourseEnrollment enrollment2 = new CourseEnrollment(course, student);
        enrollment2.setId(2L);

        assertNotEquals(enrollment, enrollment2);
    }

    @Test
    void equals_DifferentStudent_ReturnsFalse() {
        User anotherStudent = new User();
        anotherStudent.setId(3L);
        anotherStudent.setUsername("another");
        anotherStudent.setEmail("another@test.com");

        CourseEnrollment enrollment2 = new CourseEnrollment(course, anotherStudent);
        enrollment2.setId(1L);

        assertNotEquals(enrollment, enrollment2);
    }

    // ===================== TOSTRING =====================

    @Test
    void toString_ContainsCourseCode() {
        String str = enrollment.toString();
        assertTrue(str.contains("TC101"));
    }
}
