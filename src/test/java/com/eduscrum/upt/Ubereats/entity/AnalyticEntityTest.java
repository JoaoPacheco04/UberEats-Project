package com.eduscrum.upt.Ubereats.entity;

import com.eduscrum.upt.Ubereats.entity.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure unit tests for Analytic entity business logic methods.
 * No Spring context needed - tests entity methods directly.
 */
class AnalyticEntityTest {

    private Analytic analytic;
    private Sprint sprint;
    private Team team;
    private Project project;
    private Course course;
    private User teacher;

    @BeforeEach
    void setUp() {
        // Create teacher
        teacher = new User();
        teacher.setId(1L);
        teacher.setUsername("teacher");
        teacher.setEmail("teacher@test.com");
        teacher.setFirstName("Prof");
        teacher.setLastName("Test");
        teacher.setRole(UserRole.TEACHER);

        // Create course
        course = new Course("Test Course", "TC001", "Description", Semester.FIRST, "2024", teacher);
        course.setId(1L);

        // Create project
        project = new Project("Test Project", "Project Desc", LocalDate.now().minusDays(30),
                LocalDate.now().plusDays(30), course);
        project.setId(1L);

        // Create team
        team = new Team();
        team.setId(1L);
        team.setName("Test Team");
        team.addProject(project);

        // Create sprint
        sprint = new Sprint();
        sprint.setId(1L);
        sprint.setName("Sprint 1");
        sprint.setStartDate(LocalDate.now().minusDays(7));
        sprint.setEndDate(LocalDate.now().plusDays(7));
        sprint.setStatus(SprintStatus.IN_PROGRESS);
        sprint.setProject(project);

        // Create analytic
        analytic = new Analytic(sprint, team, LocalDate.now());
    }

    // ===================== CONSTRUCTOR TESTS =====================

    @Test
    void constructor_WithArgs_SetsDefaultValues() {
        Analytic newAnalytic = new Analytic(sprint, team, LocalDate.now());

        assertEquals(sprint, newAnalytic.getSprint());
        assertEquals(team, newAnalytic.getTeam());
        assertEquals(0, newAnalytic.getCompletedTasks());
        assertEquals(0, newAnalytic.getTotalTasks());
        assertEquals(BigDecimal.ZERO, newAnalytic.getStoryPointsCompleted());
        assertEquals(BigDecimal.ZERO, newAnalytic.getTotalStoryPoints());
        assertEquals(BigDecimal.ZERO, newAnalytic.getVelocity());
    }

    @Test
    void defaultConstructor_CreatesEmptyAnalytic() {
        Analytic emptyAnalytic = new Analytic();
        assertNull(emptyAnalytic.getId());
        assertNull(emptyAnalytic.getSprint());
        assertNull(emptyAnalytic.getTeam());
    }

    // ===================== COMPLETION PERCENTAGE TESTS =====================

    @Test
    void getCompletionPercentage_NoTasks_ReturnsZero() {
        analytic.setTotalTasks(0);
        analytic.setCompletedTasks(0);

        assertEquals(BigDecimal.ZERO, analytic.getCompletionPercentage());
    }

    @Test
    void getCompletionPercentage_HalfComplete_Returns50() {
        analytic.setTotalTasks(10);
        analytic.setCompletedTasks(5);

        assertEquals(new BigDecimal("50.00"), analytic.getCompletionPercentage());
    }

    @Test
    void getCompletionPercentage_AllComplete_Returns100() {
        analytic.setTotalTasks(10);
        analytic.setCompletedTasks(10);

        assertEquals(new BigDecimal("100.00"), analytic.getCompletionPercentage());
    }

    // ===================== STORY POINTS COMPLETION TESTS =====================

    @Test
    void getStoryPointsCompletion_NoStoryPoints_ReturnsZero() {
        analytic.setTotalStoryPoints(BigDecimal.ZERO);
        analytic.setStoryPointsCompleted(BigDecimal.ZERO);

        assertEquals(BigDecimal.ZERO, analytic.getStoryPointsCompletion());
    }

    @Test
    void getStoryPointsCompletion_HalfComplete_Returns50() {
        analytic.setTotalStoryPoints(new BigDecimal("20"));
        analytic.setStoryPointsCompleted(new BigDecimal("10"));

        assertEquals(new BigDecimal("50.00"), analytic.getStoryPointsCompletion());
    }

    @Test
    void getStoryPointsCompletion_AllComplete_Returns100() {
        analytic.setTotalStoryPoints(new BigDecimal("20"));
        analytic.setStoryPointsCompleted(new BigDecimal("20"));

        assertEquals(new BigDecimal("100.00"), analytic.getStoryPointsCompletion());
    }

    // ===================== REMAINING TASKS TESTS =====================

    @Test
    void getRemainingTasks_WithTasks_ReturnsCorrectValue() {
        analytic.setTotalTasks(10);
        analytic.setCompletedTasks(4);

        assertEquals(6, analytic.getRemainingTasks());
    }

    @Test
    void getRemainingTasks_AllComplete_ReturnsZero() {
        analytic.setTotalTasks(10);
        analytic.setCompletedTasks(10);

        assertEquals(0, analytic.getRemainingTasks());
    }

    // ===================== REMAINING STORY POINTS TESTS =====================

    @Test
    void getRemainingStoryPoints_WithPoints_ReturnsCorrectValue() {
        analytic.setTotalStoryPoints(new BigDecimal("20"));
        analytic.setStoryPointsCompleted(new BigDecimal("8"));

        assertEquals(new BigDecimal("12"), analytic.getRemainingStoryPoints());
    }

    @Test
    void getRemainingStoryPoints_AllComplete_ReturnsZero() {
        analytic.setTotalStoryPoints(new BigDecimal("20"));
        analytic.setStoryPointsCompleted(new BigDecimal("20"));

        assertEquals(new BigDecimal("0"), analytic.getRemainingStoryPoints());
    }

    // ===================== ALL TASKS COMPLETED TESTS =====================

    @Test
    void isAllTasksCompleted_NotComplete_ReturnsFalse() {
        analytic.setTotalTasks(10);
        analytic.setCompletedTasks(5);

        assertFalse(analytic.isAllTasksCompleted());
    }

    @Test
    void isAllTasksCompleted_Complete_ReturnsTrue() {
        analytic.setTotalTasks(10);
        analytic.setCompletedTasks(10);

        assertTrue(analytic.isAllTasksCompleted());
    }

    @Test
    void isAllTasksCompleted_OverComplete_ReturnsTrue() {
        analytic.setTotalTasks(10);
        analytic.setCompletedTasks(12);

        assertTrue(analytic.isAllTasksCompleted());
    }

    // ===================== ALL STORY POINTS COMPLETED TESTS =====================

    @Test
    void isAllStoryPointsCompleted_NotComplete_ReturnsFalse() {
        analytic.setTotalStoryPoints(new BigDecimal("20"));
        analytic.setStoryPointsCompleted(new BigDecimal("15"));

        assertFalse(analytic.isAllStoryPointsCompleted());
    }

    @Test
    void isAllStoryPointsCompleted_Complete_ReturnsTrue() {
        analytic.setTotalStoryPoints(new BigDecimal("20"));
        analytic.setStoryPointsCompleted(new BigDecimal("20"));

        assertTrue(analytic.isAllStoryPointsCompleted());
    }

    // ===================== ADD STORY POINTS TESTS =====================

    @Test
    void addStoryPoints_ValidPoints_AddsToTotal() {
        analytic.setStoryPointsCompleted(new BigDecimal("10"));
        analytic.addStoryPoints(new BigDecimal("5"));

        assertEquals(new BigDecimal("15"), analytic.getStoryPointsCompleted());
    }

    @Test
    void addStoryPoints_Null_DoesNotChange() {
        analytic.setStoryPointsCompleted(new BigDecimal("10"));
        analytic.addStoryPoints(null);

        assertEquals(new BigDecimal("10"), analytic.getStoryPointsCompleted());
    }

    @Test
    void addStoryPoints_Zero_DoesNotChange() {
        analytic.setStoryPointsCompleted(new BigDecimal("10"));
        analytic.addStoryPoints(BigDecimal.ZERO);

        assertEquals(new BigDecimal("10"), analytic.getStoryPointsCompleted());
    }

    @Test
    void addStoryPoints_Negative_DoesNotChange() {
        analytic.setStoryPointsCompleted(new BigDecimal("10"));
        analytic.addStoryPoints(new BigDecimal("-5"));

        assertEquals(new BigDecimal("10"), analytic.getStoryPointsCompleted());
    }

    // ===================== ADD COMPLETED TASKS TESTS =====================

    @Test
    void addCompletedTasks_ValidTasks_AddsToTotal() {
        analytic.setCompletedTasks(5);
        analytic.addCompletedTasks(3);

        assertEquals(8, analytic.getCompletedTasks());
    }

    @Test
    void addCompletedTasks_Null_DoesNotChange() {
        analytic.setCompletedTasks(5);
        analytic.addCompletedTasks(null);

        assertEquals(5, analytic.getCompletedTasks());
    }

    @Test
    void addCompletedTasks_Zero_DoesNotChange() {
        analytic.setCompletedTasks(5);
        analytic.addCompletedTasks(0);

        assertEquals(5, analytic.getCompletedTasks());
    }

    @Test
    void addCompletedTasks_Negative_DoesNotChange() {
        analytic.setCompletedTasks(5);
        analytic.addCompletedTasks(-3);

        assertEquals(5, analytic.getCompletedTasks());
    }

    // ===================== PROGRESS STATUS TESTS =====================

    @Test
    void getProgressStatus_Completed_ReturnsCompleted() {
        analytic.setTotalTasks(10);
        analytic.setCompletedTasks(10);

        assertEquals("Completed", analytic.getProgressStatus());
    }

    @Test
    void getProgressStatus_AlmostDone_ReturnsAlmostDone() {
        analytic.setTotalTasks(10);
        analytic.setCompletedTasks(8);

        assertEquals("Almost Done", analytic.getProgressStatus());
    }

    @Test
    void getProgressStatus_InProgress_ReturnsInProgress() {
        analytic.setTotalTasks(10);
        analytic.setCompletedTasks(6);

        assertEquals("In Progress", analytic.getProgressStatus());
    }

    @Test
    void getProgressStatus_GettingStarted_ReturnsGettingStarted() {
        analytic.setTotalTasks(10);
        analytic.setCompletedTasks(3);

        assertEquals("Getting Started", analytic.getProgressStatus());
    }

    @Test
    void getProgressStatus_JustStarted_ReturnsJustStarted() {
        analytic.setTotalTasks(10);
        analytic.setCompletedTasks(1);

        assertEquals("Just Started", analytic.getProgressStatus());
    }

    // ===================== BURN DOWN DATA TESTS =====================

    @Test
    void getBurnDownDataMap_NullData_ReturnsEmptyMap() {
        analytic.setBurnDownData(null);

        Map<String, Integer> result = analytic.getBurnDownDataMap();
        assertTrue(result.isEmpty());
    }

    @Test
    void getBurnDownDataMap_EmptyData_ReturnsEmptyMap() {
        analytic.setBurnDownData("");

        Map<String, Integer> result = analytic.getBurnDownDataMap();
        assertTrue(result.isEmpty());
    }

    @Test
    void getBurnDownDataMap_ValidJson_ReturnsMap() {
        analytic.setBurnDownData("{\"day1\": 10, \"day2\": 8}");

        Map<String, Integer> result = analytic.getBurnDownDataMap();
        assertEquals(2, result.size());
        assertEquals(10, result.get("day1"));
        assertEquals(8, result.get("day2"));
    }

    @Test
    void getBurnDownDataMap_InvalidJson_ReturnsEmptyMap() {
        analytic.setBurnDownData("invalid json");

        Map<String, Integer> result = analytic.getBurnDownDataMap();
        assertTrue(result.isEmpty());
    }

    // ===================== SETTERS NULL HANDLING TESTS =====================

    @Test
    void setStoryPointsCompleted_Null_SetsToZero() {
        analytic.setStoryPointsCompleted(null);
        assertEquals(BigDecimal.ZERO, analytic.getStoryPointsCompleted());
    }

    @Test
    void setTotalStoryPoints_Null_SetsToZero() {
        analytic.setTotalStoryPoints(null);
        assertEquals(BigDecimal.ZERO, analytic.getTotalStoryPoints());
    }

    @Test
    void setVelocity_Null_SetsToZero() {
        analytic.setVelocity(null);
        assertEquals(BigDecimal.ZERO, analytic.getVelocity());
    }

    // ===================== GETTER/SETTER TESTS =====================

    @Test
    void setAndGetId() {
        analytic.setId(100L);
        assertEquals(100L, analytic.getId());
    }

    @Test
    void setAndGetTeamMood() {
        analytic.setTeamMood(TeamMood.HAPPY);
        assertEquals(TeamMood.HAPPY, analytic.getTeamMood());
    }

    @Test
    void setAndGetNotes() {
        analytic.setNotes("Test notes");
        assertEquals("Test notes", analytic.getNotes());
    }

    // ===================== EQUALS AND HASHCODE TESTS =====================

    @Test
    void equals_SameIdSprintTeamDate_ReturnsTrue() {
        Analytic analytic2 = new Analytic(sprint, team, analytic.getRecordedDate());
        analytic.setId(1L);
        analytic2.setId(1L);

        assertEquals(analytic, analytic2);
    }

    @Test
    void equals_DifferentId_ReturnsFalse() {
        Analytic analytic2 = new Analytic(sprint, team, analytic.getRecordedDate());
        analytic.setId(1L);
        analytic2.setId(2L);

        assertNotEquals(analytic, analytic2);
    }

    @Test
    void equals_SameObject_ReturnsTrue() {
        assertEquals(analytic, analytic);
    }

    @Test
    void equals_Null_ReturnsFalse() {
        assertNotEquals(null, analytic);
    }

    @Test
    void hashCode_SameFields_SameHash() {
        Analytic analytic2 = new Analytic(sprint, team, analytic.getRecordedDate());
        analytic.setId(1L);
        analytic2.setId(1L);

        assertEquals(analytic.hashCode(), analytic2.hashCode());
    }

    // ===================== TOSTRING TESTS =====================

    @Test
    void toString_ContainsKeyFields() {
        analytic.setCompletedTasks(5);
        analytic.setTotalTasks(10);

        String str = analytic.toString();
        assertTrue(str.contains("Sprint 1"));
        assertTrue(str.contains("Test Team"));
        assertTrue(str.contains("5"));
        assertTrue(str.contains("10"));
    }
}
