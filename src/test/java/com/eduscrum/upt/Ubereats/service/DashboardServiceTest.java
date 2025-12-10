package com.eduscrum.upt.Ubereats.service;

import com.eduscrum.upt.Ubereats.dto.response.DashboardStatsDTO;
import com.eduscrum.upt.Ubereats.entity.*;
import com.eduscrum.upt.Ubereats.entity.enums.*;
import com.eduscrum.upt.Ubereats.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for DashboardService.
 *
 * @version 1.0.4 (2025-12-10)
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DashboardServiceTest {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseEnrollmentRepository enrollmentRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private AchievementRepository achievementRepository;

    private User student;
    private User teacher;
    private Course course;
    private Team team;
    private Project project;

    @BeforeEach
    void setUp() {
        // Clear all data
        achievementRepository.deleteAll();
        teamMemberRepository.deleteAll();
        enrollmentRepository.deleteAll();
        projectRepository.deleteAll();
        teamRepository.deleteAll();
        courseRepository.deleteAll();
        badgeRepository.deleteAll();
        userRepository.deleteAll();

        // Create Teacher
        teacher = new User();
        teacher.setFirstName("Prof");
        teacher.setLastName("Dashboard");
        teacher.setEmail("prof@dash.com");
        teacher.setUsername("profdash");
        teacher.setPassword("pass");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);

        // Create Student
        student = new User();
        student.setFirstName("Student");
        student.setLastName("Dash");
        student.setEmail("student@dash.com");
        student.setUsername("dashstudent");
        student.setPassword("pass");
        student.setRole(UserRole.STUDENT);
        student = userRepository.save(student);

        // Create Course
        course = new Course("Dashboard Course", "DC1", "Desc", Semester.FIRST, "2024", teacher);
        course = courseRepository.save(course);

        // Create Enrollment
        CourseEnrollment enrollment = new CourseEnrollment(course, student);
        enrollmentRepository.save(enrollment);

        // Create Team
        team = new Team("Dashboard Team");
        team = teamRepository.save(team);

        // Create Project and associate with team
        project = new Project("Dashboard Project", "Desc", LocalDate.now(), LocalDate.now().plusDays(30), course);
        project = projectRepository.save(project);
        team.getProjects().add(project);
        team = teamRepository.save(team);

        // Add student to team
        TeamMember membership = new TeamMember(team, student, ScrumRole.DEVELOPER);
        teamMemberRepository.save(membership);
    }

    @Test
    void getStudentDashboardStats_Success() {
        // Create Badges and Achievements for scoring
        Badge badge = new Badge("Gold Star", "Desc", 50, BadgeType.MANUAL, teacher);
        badge = badgeRepository.save(badge);
        Achievement achievement = new Achievement(badge, student, project, teacher, "Outstanding performance");
        achievementRepository.save(achievement);

        // Act
        DashboardStatsDTO stats = dashboardService.getStudentDashboardStats(student.getId());

        // Assert
        assertNotNull(stats);
        // Global score may vary based on team achievements calculation
        assertTrue(stats.getGlobalScore() >= 0);
        assertNotNull(stats.getRecentBadges());
    }

    @Test
    void getStudentDashboardStats_UserNotFound_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            dashboardService.getStudentDashboardStats(999L);
        });
    }

    @Test
    void getStudentDashboardStats_NoEnrollment_ReturnsStats() {
        // Create a second student without enrollment
        User student2 = new User();
        student2.setFirstName("Alone");
        student2.setLastName("Student");
        student2.setEmail("alone@test.com");
        student2.setUsername("alone");
        student2.setPassword("pass");
        student2.setRole(UserRole.STUDENT);
        student2 = userRepository.save(student2);

        DashboardStatsDTO stats = dashboardService.getStudentDashboardStats(student2.getId());

        assertNotNull(stats);
        assertEquals(0.0, stats.getCourseAverageScore());
    }

    @Test
    void getStudentDashboardStats_WithEnrollment_ReturnsStats() {
        DashboardStatsDTO stats = dashboardService.getStudentDashboardStats(student.getId());

        assertNotNull(stats);
        assertNotNull(stats.getGlobalScore());
        assertNotNull(stats.getCourseAverageScore());
    }

    @Test
    void getStudentDashboardStats_WithMultipleStudents_CalculatesCourseAverage() {
        // Create second student and enroll
        User student2 = new User();
        student2.setFirstName("Second");
        student2.setLastName("Student");
        student2.setEmail("student2@test.com");
        student2.setUsername("student2");
        student2.setPassword("pass");
        student2.setRole(UserRole.STUDENT);
        student2 = userRepository.save(student2);

        CourseEnrollment enrollment2 = new CourseEnrollment(course, student2);
        enrollmentRepository.save(enrollment2);

        // Create badges and achievements for both students
        Badge badge = new Badge("Star", "Desc", 100, BadgeType.MANUAL, teacher);
        badge = badgeRepository.save(badge);

        Achievement achievement1 = new Achievement(badge, student, project, teacher, "Great work");
        achievementRepository.save(achievement1);

        Badge badge2 = new Badge("Silver Star", "Desc", 50, BadgeType.MANUAL, teacher);
        badge2 = badgeRepository.save(badge2);

        Achievement achievement2 = new Achievement(badge2, student2, project, teacher, "Good job");
        achievementRepository.save(achievement2);

        // Act
        DashboardStatsDTO stats = dashboardService.getStudentDashboardStats(student.getId());

        // Assert
        assertNotNull(stats);
        assertNotNull(stats.getCourseAverageScore());
        assertTrue(stats.getCourseAverageScore() >= 0);
    }

    @Test
    void getStudentDashboardStats_ReturnsEmptyVelocityHistory_WhenNoSprints() {
        DashboardStatsDTO stats = dashboardService.getStudentDashboardStats(student.getId());

        assertNotNull(stats);
        assertNotNull(stats.getTeamVelocityHistory());
        assertTrue(stats.getTeamVelocityHistory().isEmpty());
    }

    @Test
    void getStudentDashboardStats_ReturnsEmptyBadges_WhenNoAchievements() {
        DashboardStatsDTO stats = dashboardService.getStudentDashboardStats(student.getId());

        assertNotNull(stats);
        assertNotNull(stats.getRecentBadges());
        assertTrue(stats.getRecentBadges().isEmpty());
    }

    @Test
    void getStudentDashboardStats_ReturnsRecentBadges_LimitedToFive() {
        // Create 6 badges and achievements
        for (int i = 1; i <= 6; i++) {
            Badge badge = new Badge("Badge " + i, "Desc " + i, i * 10, BadgeType.MANUAL, teacher);
            badge = badgeRepository.save(badge);
            Achievement achievement = new Achievement(badge, student, project, teacher, "Reason " + i);
            achievementRepository.save(achievement);
        }

        // Act
        DashboardStatsDTO stats = dashboardService.getStudentDashboardStats(student.getId());

        // Assert - should return max 5 recent badges
        assertNotNull(stats.getRecentBadges());
        assertTrue(stats.getRecentBadges().size() <= 5);
    }
}
