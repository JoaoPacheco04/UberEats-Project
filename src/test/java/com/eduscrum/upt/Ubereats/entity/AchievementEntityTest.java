package com.eduscrum.upt.Ubereats.entity;

import com.eduscrum.upt.Ubereats.entity.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure unit tests for Achievement entity.
 */
/**
 * Unit tests for Achievement entity.
 *
 * @version 0.6.1 (2025-11-12)
 */
class AchievementEntityTest {

    private Achievement achievement;
    private Badge badge;
    private User user;
    private Team team;
    private Project project;

    @BeforeEach
    void setUp() {
        User teacher = new User();
        teacher.setId(1L);
        teacher.setUsername("teacher");
        teacher.setRole(UserRole.TEACHER);

        badge = new Badge("Test Badge", "Description", 50, BadgeType.MANUAL, teacher);
        badge.setId(1L);

        user = new User();
        user.setId(2L);
        user.setUsername("student");
        user.setEmail("student@test.com");
        user.setFirstName("Test");
        user.setLastName("Student");
        user.setRole(UserRole.STUDENT);

        team = new Team("Test Team");
        team.setId(1L);

        Course course = new Course("Test Course", "TC101", "Desc", Semester.FIRST, "2024", teacher);
        project = new Project("Test Project", "Desc", LocalDate.now(), LocalDate.now().plusMonths(3), course);
        project.setId(1L);

        achievement = new Achievement();
        achievement.setId(1L);
        achievement.setBadge(badge);
        achievement.setAwardedToUser(user);
        achievement.setProject(project);
    }

    // ===================== INDIVIDUAL VS TEAM ACHIEVEMENT =====================

    @Test
    void isIndividualAchievement_WhenUserIsSet_ReturnsTrue() {
        achievement.setAwardedToUser(user);
        achievement.setAwardedToTeam(null);

        assertTrue(achievement.isIndividualAchievement());
    }

    @Test
    void isIndividualAchievement_WhenTeamIsSet_ReturnsFalse() {
        achievement.setAwardedToUser(null);
        achievement.setAwardedToTeam(team);

        assertFalse(achievement.isIndividualAchievement());
    }

    @Test
    void isTeamAchievement_WhenTeamIsSet_ReturnsTrue() {
        achievement.setAwardedToUser(null);
        achievement.setAwardedToTeam(team);

        assertTrue(achievement.isTeamAchievement());
    }

    @Test
    void isTeamAchievement_WhenUserIsSet_ReturnsFalse() {
        achievement.setAwardedToUser(user);
        achievement.setAwardedToTeam(null);

        assertFalse(achievement.isTeamAchievement());
    }

    // ===================== GETTERS TESTS =====================

    @Test
    void getBadge_ReturnsBadge() {
        assertEquals(badge, achievement.getBadge());
    }

    @Test
    void getProject_ReturnsProject() {
        assertEquals(project, achievement.getProject());
    }

    @Test
    void getAwardedToUser_ReturnsUser() {
        assertEquals(user, achievement.getAwardedToUser());
    }

    // ===================== SETTERS TESTS =====================

    @Test
    void setAwardedToTeam_SetsTeamCorrectly() {
        achievement.setAwardedToTeam(team);
        assertEquals(team, achievement.getAwardedToTeam());
    }

    // ===================== EQUALS AND HASHCODE =====================

    @Test
    void equals_SameId_ReturnsTrue() {
        Achievement ach2 = new Achievement();
        ach2.setId(1L);
        ach2.setBadge(badge);

        assertEquals(achievement, ach2);
    }

    @Test
    void equals_DifferentId_ReturnsFalse() {
        Achievement ach2 = new Achievement();
        ach2.setId(2L);

        assertNotEquals(achievement, ach2);
    }
}
