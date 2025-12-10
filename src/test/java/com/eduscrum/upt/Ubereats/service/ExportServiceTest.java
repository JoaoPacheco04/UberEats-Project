package com.eduscrum.upt.Ubereats.service;

import com.eduscrum.upt.Ubereats.entity.*;
import com.eduscrum.upt.Ubereats.entity.enums.*;
import com.eduscrum.upt.Ubereats.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ExportService.
 * Uses H2 in-memory database for testing.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ExportServiceTest {

    @Autowired
    private ExportService exportService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseEnrollmentRepository courseEnrollmentRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private User teacher;
    private Course course;
    private Project project;

    @BeforeEach
    void setUp() {
        // Create teacher
        teacher = new User("teacher", "teacher@test.com", "password", UserRole.TEACHER, "Prof", "Teacher");
        teacher = userRepository.save(teacher);

        // Create course
        course = new Course("Test Course", "TC001", "Course Description", Semester.FIRST, "2024", teacher);
        course = courseRepository.save(course);

        // Create project for achievements
        project = new Project("Test Project", "Desc", LocalDate.now(), LocalDate.now().plusDays(30), course);
        project = projectRepository.save(project);
    }

    // ===================== GENERATE COURSE CSV TESTS =====================

    @Test
    void generateCourseCsv_NoStudents_ReturnsHeaderOnly() {
        byte[] csvBytes = exportService.generateCourseCsv(course.getId());
        String csv = new String(csvBytes, StandardCharsets.UTF_8);

        assertTrue(csv.contains("Name,Student Number,Global Score,Badges Count"));
        String[] lines = csv.split("\n");
        assertEquals(1, lines.length); // Only header
    }

    @Test
    void generateCourseCsv_WithStudents_ReturnsStudentData() {
        // Create student
        User student = new User("student", "student@test.com", "password", UserRole.STUDENT, "John", "Doe", "STU001");
        student.setIndividualAchievements(new ArrayList<>());
        student = userRepository.save(student);

        // Enroll student in course
        CourseEnrollment enrollment = new CourseEnrollment(course, student);
        courseEnrollmentRepository.save(enrollment);

        byte[] csvBytes = exportService.generateCourseCsv(course.getId());
        String csv = new String(csvBytes, StandardCharsets.UTF_8);

        assertTrue(csv.contains("Name,Student Number,Global Score,Badges Count"));
        assertTrue(csv.contains("John Doe"));
        assertTrue(csv.contains("STU001"));
    }

    @Test
    void generateCourseCsv_WithStudentWithBadges_IncludesBadgeCount() {
        // Create student
        User student = new User("student", "student@test.com", "password", UserRole.STUDENT, "Jane", "Smith", "STU002");
        student.setIndividualAchievements(new ArrayList<>());
        student = userRepository.save(student);

        // Enroll student in course
        CourseEnrollment enrollment = new CourseEnrollment(course, student);
        courseEnrollmentRepository.save(enrollment);

        // Create badge
        Badge badge = new Badge();
        badge.setName("Test Badge");
        badge.setDescription("Test Description");
        badge.setPoints(100);
        badge.setBadgeType(BadgeType.MANUAL);
        badge.setCreatedBy(teacher);
        badge = badgeRepository.save(badge);

        // Award badge to student using correct constructor
        Achievement achievement = new Achievement(badge, student, project, teacher, "Test achievement");
        achievement = achievementRepository.save(achievement);

        // Update student's achievements
        student.getIndividualAchievements().add(achievement);
        userRepository.save(student);

        byte[] csvBytes = exportService.generateCourseCsv(course.getId());
        String csv = new String(csvBytes, StandardCharsets.UTF_8);

        assertTrue(csv.contains("Jane Smith"));
        assertTrue(csv.contains("STU002"));
    }

    @Test
    void generateCourseCsv_StudentWithNullStudentNumber_HandlesGracefully() {
        // Create student without student number
        User student = new User("student", "student@test.com", "password", UserRole.STUDENT, "No", "Number");
        student.setStudentNumber(null);
        student.setIndividualAchievements(new ArrayList<>());
        student = userRepository.save(student);

        // Enroll student in course
        CourseEnrollment enrollment = new CourseEnrollment(course, student);
        courseEnrollmentRepository.save(enrollment);

        byte[] csvBytes = exportService.generateCourseCsv(course.getId());
        String csv = new String(csvBytes, StandardCharsets.UTF_8);

        assertTrue(csv.contains("No Number"));
        // Should not throw exception
        assertNotNull(csv);
    }

    @Test
    void generateCourseCsv_MultipleStudents_ReturnsAllStudents() {
        // Create multiple students
        for (int i = 1; i <= 3; i++) {
            User student = new User("student" + i, "student" + i + "@test.com", "password",
                    UserRole.STUDENT, "Student", "Number" + i, "STU00" + i);
            student.setIndividualAchievements(new ArrayList<>());
            student = userRepository.save(student);

            CourseEnrollment enrollment = new CourseEnrollment(course, student);
            courseEnrollmentRepository.save(enrollment);
        }

        byte[] csvBytes = exportService.generateCourseCsv(course.getId());
        String csv = new String(csvBytes, StandardCharsets.UTF_8);

        String[] lines = csv.split("\n");
        assertEquals(4, lines.length); // Header + 3 students
    }

    @Test
    void generateCourseCsv_StudentNameWithComma_EscapesCorrectly() {
        // Create student with comma in name
        User student = new User("student", "student@test.com", "password", UserRole.STUDENT, "John, Jr", "Doe");
        student.setStudentNumber("STU001");
        student.setIndividualAchievements(new ArrayList<>());
        student = userRepository.save(student);

        CourseEnrollment enrollment = new CourseEnrollment(course, student);
        courseEnrollmentRepository.save(enrollment);

        byte[] csvBytes = exportService.generateCourseCsv(course.getId());
        String csv = new String(csvBytes, StandardCharsets.UTF_8);

        // Name with comma should be quoted
        assertTrue(csv.contains("\"John, Jr Doe\""));
    }

    @Test
    void generateCourseCsv_StudentNameWithQuotes_EscapesCorrectly() {
        // Create student with quotes in name
        User student = new User("student", "student@test.com", "password", UserRole.STUDENT, "John \"Johnny\"", "Doe");
        student.setStudentNumber("STU001");
        student.setIndividualAchievements(new ArrayList<>());
        student = userRepository.save(student);

        CourseEnrollment enrollment = new CourseEnrollment(course, student);
        courseEnrollmentRepository.save(enrollment);

        byte[] csvBytes = exportService.generateCourseCsv(course.getId());
        String csv = new String(csvBytes, StandardCharsets.UTF_8);

        // Name with quotes should be escaped
        assertTrue(csv.contains("\"\"Johnny\"\""));
    }

    @Test
    void generateCourseCsv_ReturnsUtf8Bytes() {
        byte[] csvBytes = exportService.generateCourseCsv(course.getId());

        assertNotNull(csvBytes);
        assertTrue(csvBytes.length > 0);

        // Verify it's valid UTF-8
        String csv = new String(csvBytes, StandardCharsets.UTF_8);
        assertNotNull(csv);
    }
}
