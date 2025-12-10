package com.eduscrum.upt.Ubereats.entity;

import com.eduscrum.upt.Ubereats.entity.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure unit tests for User entity business logic methods.
 * No Spring context needed - tests entity methods directly.
 */
class UserEntityTest {

    private User user;
    private User teacher;

    @BeforeEach
    void setUp() {
        teacher = new User();
        teacher.setId(1L);
        teacher.setUsername("teacher");
        teacher.setEmail("teacher@test.com");
        teacher.setFirstName("Prof");
        teacher.setLastName("Test");
        teacher.setRole(UserRole.TEACHER);

        user = new User();
        user.setId(2L);
        user.setUsername("student");
        user.setEmail("student@test.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(UserRole.STUDENT);
        user.setStudentNumber("STU001");
        user.setIndividualAchievements(new ArrayList<>());
        user.setTeamMemberships(new ArrayList<>());
        user.setEnrollments(new ArrayList<>());
    }

    // ===================== TOTAL POINTS TESTS =====================

    @Test
    void getTotalPoints_NoAchievements_ReturnsZero() {
        assertEquals(0, user.getTotalPoints());
    }

    @Test
    void getTotalPoints_WithAchievements_ReturnsSum() {
        Badge badge1 = createBadge("Badge 1", 50);
        Badge badge2 = createBadge("Badge 2", 30);

        user.getIndividualAchievements().add(createAchievement(badge1));
        user.getIndividualAchievements().add(createAchievement(badge2));

        assertEquals(80, user.getTotalPoints());
    }

    // ===================== EARNED BADGES TESTS =====================

    @Test
    void getEarnedBadges_NoAchievements_ReturnsEmptyList() {
        assertTrue(user.getEarnedBadges().isEmpty());
    }

    @Test
    void getEarnedBadges_WithAchievements_ReturnsBadges() {
        Badge badge1 = createBadge("Badge 1", 50);
        Badge badge2 = createBadge("Badge 2", 30);

        user.getIndividualAchievements().add(createAchievement(badge1));
        user.getIndividualAchievements().add(createAchievement(badge2));

        assertEquals(2, user.getEarnedBadges().size());
    }

    @Test
    void getEarnedBadges_DuplicateBadge_ReturnsDistinct() {
        Badge badge = createBadge("Same Badge", 50);

        user.getIndividualAchievements().add(createAchievement(badge));
        user.getIndividualAchievements().add(createAchievement(badge));

        assertEquals(1, user.getEarnedBadges().size());
    }

    // ===================== AVERAGE SCORE TESTS =====================

    @Test
    void getAverageScore_NoAchievements_ReturnsZero() {
        assertEquals(0.0, user.getAverageScore());
    }

    @Test
    void getAverageScore_WithAchievements_ReturnsAverage() {
        Badge badge1 = createBadge("Badge 1", 50);
        Badge badge2 = createBadge("Badge 2", 100);

        user.getIndividualAchievements().add(createAchievement(badge1));
        user.getIndividualAchievements().add(createAchievement(badge2));

        assertEquals(75.0, user.getAverageScore());
    }

    // ===================== ROLE CHECK TESTS =====================

    @Test
    void isTeacher_WhenTeacher_ReturnsTrue() {
        user.setRole(UserRole.TEACHER);
        assertTrue(user.isTeacher());
    }

    @Test
    void isTeacher_WhenStudent_ReturnsFalse() {
        user.setRole(UserRole.STUDENT);
        assertFalse(user.isTeacher());
    }

    @Test
    void isStudent_WhenStudent_ReturnsTrue() {
        user.setRole(UserRole.STUDENT);
        assertTrue(user.isStudent());
    }

    @Test
    void isStudent_WhenTeacher_ReturnsFalse() {
        user.setRole(UserRole.TEACHER);
        assertFalse(user.isStudent());
    }

    // ===================== FULL NAME TESTS =====================

    @Test
    void getFullName_ReturnsFirstAndLastName() {
        assertEquals("John Doe", user.getFullName());
    }

    @Test
    void getFullName_WithDifferentNames() {
        user.setFirstName("Jane");
        user.setLastName("Smith");
        assertEquals("Jane Smith", user.getFullName());
    }

    // ===================== POINTS IN COURSE/PROJECT TESTS =====================

    @Test
    void getPointsInProject_NoAchievements_ReturnsZero() {
        Project project = createProject("Test Project");
        assertEquals(0, user.getPointsInProject(project));
    }

    @Test
    void getPointsInProject_WithMatchingAchievement_ReturnsPoints() {
        Project project = createProject("Test Project");
        project.setId(1L);

        Badge badge = createBadge("Badge", 50);
        Achievement achievement = createAchievement(badge);
        achievement.setProject(project);

        user.getIndividualAchievements().add(achievement);

        assertEquals(50, user.getPointsInProject(project));
    }

    @Test
    void getPointsInProject_WithNonMatchingAchievement_ReturnsZero() {
        Project project1 = createProject("Project 1");
        project1.setId(1L);
        Project project2 = createProject("Project 2");
        project2.setId(2L);

        Badge badge = createBadge("Badge", 50);
        Achievement achievement = createAchievement(badge);
        achievement.setProject(project1);

        user.getIndividualAchievements().add(achievement);

        assertEquals(0, user.getPointsInProject(project2));
    }

    // ===================== CONSTRUCTOR TESTS =====================

    @Test
    void constructor_WithAllArgs_SetsFieldsCorrectly() {
        User newUser = new User("testuser", "test@test.com", "pass", UserRole.STUDENT, "First", "Last");

        assertEquals("testuser", newUser.getUsername());
        assertEquals("test@test.com", newUser.getEmail());
        assertEquals("pass", newUser.getPassword());
        assertEquals(UserRole.STUDENT, newUser.getRole());
        assertEquals("First", newUser.getFirstName());
        assertEquals("Last", newUser.getLastName());
        assertTrue(newUser.getIsActive());
    }

    @Test
    void constructor_WithStudentNumber_SetsStudentNumber() {
        User newUser = new User("testuser", "test@test.com", "pass", UserRole.STUDENT, "First", "Last", "STU123");

        assertEquals("STU123", newUser.getStudentNumber());
    }

    // ===================== EQUALS AND HASHCODE TESTS =====================

    @Test
    void equals_SameIdAndEmail_ReturnsTrue() {
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("student@test.com");

        assertEquals(user, user2);
    }

    @Test
    void equals_DifferentId_ReturnsFalse() {
        User user2 = new User();
        user2.setId(999L);
        user2.setEmail("student@test.com");

        assertNotEquals(user, user2);
    }

    @Test
    void equals_DifferentEmail_ReturnsFalse() {
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("different@test.com");

        assertNotEquals(user, user2);
    }

    @Test
    void hashCode_SameIdAndEmail_SameHash() {
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("student@test.com");

        assertEquals(user.hashCode(), user2.hashCode());
    }

    // ===================== TOSTRING TESTS =====================

    @Test
    void toString_ContainsKeyFields() {
        String str = user.toString();
        assertTrue(str.contains("student"));
        assertTrue(str.contains("student@test.com"));
        assertTrue(str.contains("John Doe"));
    }

    // ===================== HELPER METHODS =====================

    private Badge createBadge(String name, int points) {
        Badge badge = new Badge();
        badge.setId((long) name.hashCode());
        badge.setName(name);
        badge.setPoints(points);
        badge.setBadgeType(BadgeType.MANUAL);
        return badge;
    }

    private Achievement createAchievement(Badge badge) {
        Achievement achievement = new Achievement();
        achievement.setBadge(badge);
        achievement.setAwardedToUser(user);
        return achievement;
    }

    private Project createProject(String name) {
        Course course = new Course("Course", "C1", "Desc", Semester.FIRST, "2024", teacher);
        Project project = new Project(name, "Desc", LocalDate.now(), LocalDate.now().plusDays(30), course);
        return project;
    }
}
