package com.eduscrum.upt.Ubereats.entity;

import com.eduscrum.upt.Ubereats.entity.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure unit tests for Project entity business logic methods.
 * Unit tests for Project entity.
 *
 * @author UberEats
 * @version 0.3.0
 */
class ProjectEntityTest {

    private Project project;
    private Course course;
    private User teacher;

    @BeforeEach
    void setUp() {
        teacher = new User();
        teacher.setId(1L);
        teacher.setUsername("teacher");
        teacher.setRole(UserRole.TEACHER);

        course = new Course("Test Course", "TC101", "Desc", Semester.FIRST, "2024", teacher);
        course.setId(1L);

        project = new Project("Test Project", "Description", LocalDate.now(), LocalDate.now().plusDays(30), course);
        project.setId(1L);
        project.setStatus(ProjectStatus.PLANNING);
        project.setSprints(new ArrayList<>());
        project.setTeams(new ArrayList<>());
        project.setAchievements(new ArrayList<>());
    }

    // ===================== STATUS CHECKS =====================

    @Test
    void isActive_WhenActive_ReturnsTrue() {
        project.setStatus(ProjectStatus.ACTIVE);
        assertTrue(project.isActive());
    }

    @Test
    void isActive_WhenNotActive_ReturnsFalse() {
        project.setStatus(ProjectStatus.PLANNING);
        assertFalse(project.isActive());
    }

    @Test
    void isCompleted_WhenCompleted_ReturnsTrue() {
        project.setStatus(ProjectStatus.COMPLETED);
        assertTrue(project.isCompleted());
    }

    @Test
    void isCompleted_WhenNotCompleted_ReturnsFalse() {
        project.setStatus(ProjectStatus.ACTIVE);
        assertFalse(project.isCompleted());
    }

    // ===================== SPRINT COUNTS =====================

    @Test
    void getTotalSprints_NoSprints_ReturnsZero() {
        assertEquals(0, project.getTotalSprints());
    }

    @Test
    void getTotalSprints_WithSprints_ReturnsCount() {
        project.getSprints().add(createSprint(1, SprintStatus.COMPLETED));
        project.getSprints().add(createSprint(2, SprintStatus.IN_PROGRESS));

        assertEquals(2, project.getTotalSprints());
    }

    @Test
    void getCompletedSprints_NoCompletedSprints_ReturnsZero() {
        project.getSprints().add(createSprint(1, SprintStatus.IN_PROGRESS));

        assertEquals(0, project.getCompletedSprints());
    }

    @Test
    void getCompletedSprints_WithCompletedSprints_ReturnsCount() {
        project.getSprints().add(createSprint(1, SprintStatus.COMPLETED));
        project.getSprints().add(createSprint(2, SprintStatus.COMPLETED));
        project.getSprints().add(createSprint(3, SprintStatus.IN_PROGRESS));

        assertEquals(2, project.getCompletedSprints());
    }

    // ===================== ACTIVE SPRINTS TESTS =====================

    @Test
    void getActiveSprints_NoActiveSprint_ReturnsEmpty() {
        project.getSprints().add(createSprint(1, SprintStatus.COMPLETED));

        assertTrue(project.getActiveSprints().isEmpty());
    }

    @Test
    void getActiveSprints_WithActiveSprint_ReturnsActiveSprints() {
        Sprint completedSprint = createSprint(1, SprintStatus.COMPLETED);
        Sprint activeSprint = createSprint(2, SprintStatus.IN_PROGRESS);

        project.getSprints().add(completedSprint);
        project.getSprints().add(activeSprint);

        assertEquals(1, project.getActiveSprints().size());
        assertEquals(activeSprint, project.getActiveSprints().get(0));
    }

    // ===================== TEAM COUNTS =====================

    @Test
    void getTeamCount_NoTeams_ReturnsZero() {
        assertEquals(0, project.getTeamCount());
    }

    @Test
    void getTeamCount_WithTeams_ReturnsCount() {
        project.getTeams().add(new Team("Team 1"));
        project.getTeams().add(new Team("Team 2"));

        assertEquals(2, project.getTeamCount());
    }

    // ===================== DURATION TESTS =====================

    @Test
    void getDurationDays_ReturnsCorrectDays() {
        project.setStartDate(LocalDate.now());
        project.setEndDate(LocalDate.now().plusDays(14));

        assertEquals(14L, project.getDurationDays());
    }

    // ===================== DAYS REMAINING TESTS =====================

    @Test
    void getDaysRemaining_FutureEndDate_ReturnsPositive() {
        project.setEndDate(LocalDate.now().plusDays(10));

        assertTrue(project.getDaysRemaining() >= 0);
    }

    @Test
    void getDaysRemaining_PastEndDate_ReturnsNegative() {
        project.setEndDate(LocalDate.now().minusDays(5));

        assertTrue(project.getDaysRemaining() < 0);
    }

    // ===================== OVERDUE TESTS =====================

    @Test
    void isOverdue_NotCompletedAndPastEndDate_ReturnsTrue() {
        project.setStatus(ProjectStatus.ACTIVE);
        project.setEndDate(LocalDate.now().minusDays(1));

        assertTrue(project.isOverdue());
    }

    @Test
    void isOverdue_CompletedProject_ReturnsFalse() {
        project.setStatus(ProjectStatus.COMPLETED);
        project.setEndDate(LocalDate.now().minusDays(1));

        assertFalse(project.isOverdue());
    }

    @Test
    void isOverdue_FutureEndDate_ReturnsFalse() {
        project.setStatus(ProjectStatus.ACTIVE);
        project.setEndDate(LocalDate.now().plusDays(10));

        assertFalse(project.isOverdue());
    }

    // ===================== IS ONGOING TESTS =====================

    @Test
    void isOngoing_BetweenDates_ReturnsTrue() {
        project.setStartDate(LocalDate.now().minusDays(5));
        project.setEndDate(LocalDate.now().plusDays(5));

        assertTrue(project.isOngoing());
    }

    @Test
    void isOngoing_BeforeStart_ReturnsFalse() {
        project.setStartDate(LocalDate.now().plusDays(5));
        project.setEndDate(LocalDate.now().plusDays(10));

        assertFalse(project.isOngoing());
    }

    // ===================== EQUALS AND HASHCODE TESTS =====================

    @Test
    void equals_SameIdAndName_ReturnsTrue() {
        Project project2 = new Project("Test Project", "Desc", LocalDate.now(), LocalDate.now().plusDays(30), course);
        project2.setId(1L);

        assertEquals(project, project2);
    }

    @Test
    void equals_DifferentId_ReturnsFalse() {
        Project project2 = new Project("Test Project", "Desc", LocalDate.now(), LocalDate.now().plusDays(30), course);
        project2.setId(2L);

        assertNotEquals(project, project2);
    }

    // ===================== TOSTRING TESTS =====================

    @Test
    void toString_ContainsName() {
        String str = project.toString();
        assertTrue(str.contains("Test Project"));
    }

    // ===================== HELPER METHODS =====================

    private Sprint createSprint(int number, SprintStatus status) {
        Sprint sprint = new Sprint();
        sprint.setId((long) number);
        sprint.setSprintNumber(number);
        sprint.setName("Sprint " + number);
        sprint.setStartDate(LocalDate.now());
        sprint.setEndDate(LocalDate.now().plusDays(14));
        sprint.setStatus(status);
        sprint.setProject(project);
        return sprint;
    }
}
