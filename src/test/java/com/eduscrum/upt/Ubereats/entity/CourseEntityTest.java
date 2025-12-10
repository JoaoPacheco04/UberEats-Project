package com.eduscrum.upt.Ubereats.entity;

import com.eduscrum.upt.Ubereats.entity.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure unit tests for Course entity business logic methods.
 * No Spring context needed - tests entity methods directly.
 */
class CourseEntityTest {

    private Course course;
    private User teacher;

    @BeforeEach
    void setUp() {
        teacher = new User();
        teacher.setId(1L);
        teacher.setFirstName("Prof");
        teacher.setLastName("Test");
        teacher.setEmail("prof@test.com");
        teacher.setUsername("proftest");
        teacher.setRole(UserRole.TEACHER);

        course = new Course("Test Course", "TC101", "Description", Semester.FIRST, "2024", teacher);
        course.setId(1L);
        course.setProjects(new ArrayList<>());
        course.setEnrollments(new ArrayList<>());
    }

    // ===================== STUDENT COUNT TESTS =====================

    @Test
    void getStudentCount_NoEnrollments_ReturnsZero() {
        assertEquals(0, course.getStudentCount());
    }

    @Test
    void getStudentCount_WithActiveEnrollments_ReturnsCount() {
        User student1 = createStudent(1L, "student1");
        User student2 = createStudent(2L, "student2");

        course.getEnrollments().add(new CourseEnrollment(course, student1));
        course.getEnrollments().add(new CourseEnrollment(course, student2));

        assertEquals(2, course.getStudentCount());
    }

    // ===================== PROJECT COUNT TESTS =====================

    @Test
    void getProjectCount_NoProjects_ReturnsZero() {
        assertEquals(0, course.getProjectCount());
    }

    @Test
    void getProjectCount_WithProjects_ReturnsCount() {
        course.getProjects().add(createProject("Project 1"));
        course.getProjects().add(createProject("Project 2"));

        assertEquals(2, course.getProjectCount());
    }

    // ===================== ENROLLED STUDENTS TESTS =====================

    @Test
    void getEnrolledStudents_NoEnrollments_ReturnsEmptyList() {
        assertTrue(course.getEnrolledStudents().isEmpty());
    }

    @Test
    void getEnrolledStudents_WithEnrollments_ReturnsStudents() {
        User student1 = createStudent(1L, "student1");
        User student2 = createStudent(2L, "student2");

        course.getEnrollments().add(new CourseEnrollment(course, student1));
        course.getEnrollments().add(new CourseEnrollment(course, student2));

        assertEquals(2, course.getEnrolledStudents().size());
    }

    // ===================== IS ACTIVE TESTS =====================

    @Test
    void isActive_WhenTrue_ReturnsTrue() {
        course.setIsActive(true);
        assertTrue(course.isActive());
    }

    @Test
    void isActive_WhenFalse_ReturnsFalse() {
        course.setIsActive(false);
        assertFalse(course.isActive());
    }

    @Test
    void isActive_WhenNull_ReturnsFalse() {
        course.setIsActive(null);
        assertFalse(course.isActive());
    }

    // ===================== ACTIVE PROJECTS TESTS =====================

    @Test
    void getActiveProjects_NoProjects_ReturnsEmptyList() {
        assertTrue(course.getActiveProjects().isEmpty());
    }

    @Test
    void getActiveProjects_OnlyActiveReturned() {
        Project activeProject = createProject("Active");
        activeProject.setStatus(ProjectStatus.ACTIVE);

        Project completedProject = createProject("Completed");
        completedProject.setStatus(ProjectStatus.COMPLETED);

        course.getProjects().add(activeProject);
        course.getProjects().add(completedProject);

        assertEquals(1, course.getActiveProjects().size());
        assertEquals("Active", course.getActiveProjects().get(0).getName());
    }

    // ===================== COMPLETED PROJECTS TESTS =====================

    @Test
    void getCompletedProjects_NoProjects_ReturnsEmptyList() {
        assertTrue(course.getCompletedProjects().isEmpty());
    }

    @Test
    void getCompletedProjects_OnlyCompletedReturned() {
        Project activeProject = createProject("Active");
        activeProject.setStatus(ProjectStatus.ACTIVE);

        Project completedProject = createProject("Completed");
        completedProject.setStatus(ProjectStatus.COMPLETED);

        course.getProjects().add(activeProject);
        course.getProjects().add(completedProject);

        assertEquals(1, course.getCompletedProjects().size());
        assertEquals("Completed", course.getCompletedProjects().get(0).getName());
    }

    // ===================== IS STUDENT ENROLLED TESTS =====================

    @Test
    void isStudentEnrolled_NotEnrolled_ReturnsFalse() {
        User student = createStudent(1L, "student1");
        assertFalse(course.isStudentEnrolled(student));
    }

    @Test
    void isStudentEnrolled_Enrolled_ReturnsTrue() {
        User student = createStudent(1L, "student1");
        course.getEnrollments().add(new CourseEnrollment(course, student));

        assertTrue(course.isStudentEnrolled(student));
    }

    // ===================== DISPLAY NAME TESTS =====================

    @Test
    void getDisplayName_ReturnsCodeAndName() {
        assertEquals("TC101 - Test Course", course.getDisplayName());
    }

    // ===================== AVERAGE TEAM SCORE TESTS =====================

    @Test
    void getAverageTeamScore_NoProjects_ReturnsZero() {
        assertEquals(0.0, course.getAverageTeamScore());
    }

    // ===================== EQUALS AND HASHCODE TESTS =====================

    @Test
    void equals_SameId_ReturnsTrue() {
        Course course2 = new Course("Different Name", "TC101", "Desc", Semester.SECOND, "2024", teacher);
        course2.setId(1L);

        assertEquals(course, course2);
    }

    @Test
    void equals_DifferentId_ReturnsFalse() {
        Course course2 = new Course("Test Course", "TC101", "Desc", Semester.FIRST, "2024", teacher);
        course2.setId(2L);

        assertNotEquals(course, course2);
    }

    @Test
    void hashCode_SameIdAndCode_SameHash() {
        Course course2 = new Course("Different", "TC101", "Desc", Semester.SECOND, "2025", teacher);
        course2.setId(1L);

        assertEquals(course.hashCode(), course2.hashCode());
    }

    // ===================== TOSTRING TESTS =====================

    @Test
    void toString_ContainsKeyFields() {
        String str = course.toString();
        assertTrue(str.contains("TC101"));
        assertTrue(str.contains("Test Course"));
    }

    // ===================== HELPER METHODS =====================

    private User createStudent(Long id, String username) {
        User student = new User();
        student.setId(id);
        student.setUsername(username);
        student.setEmail(username + "@test.com");
        student.setFirstName("First");
        student.setLastName("Last");
        student.setRole(UserRole.STUDENT);
        return student;
    }

    private Project createProject(String name) {
        Project project = new Project(name, "Desc", LocalDate.now(), LocalDate.now().plusDays(30), course);
        project.setStatus(ProjectStatus.PLANNING);
        return project;
    }
}
