package com.eduscrum.upt.Ubereats.entity;

import com.eduscrum.upt.Ubereats.entity.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure unit tests for Sprint entity business logic methods.
 * Unit tests for Sprint entity.
 *
 * @author UberEats
 * @version 0.4.0
 */
class SprintEntityTest {

    private Sprint sprint;
    private Project project;

    @BeforeEach
    void setUp() {
        User teacher = new User();
        teacher.setId(1L);
        teacher.setRole(UserRole.TEACHER);

        Course course = new Course("Test Course", "TC101", "Desc", Semester.FIRST, "2024", teacher);
        project = new Project("Test Project", "Desc", LocalDate.now(), LocalDate.now().plusMonths(3), course);
        project.setId(1L);

        sprint = new Sprint();
        sprint.setId(1L);
        sprint.setSprintNumber(1);
        sprint.setName("Sprint 1");
        sprint.setStartDate(LocalDate.now());
        sprint.setEndDate(LocalDate.now().plusDays(14));
        sprint.setStatus(SprintStatus.IN_PROGRESS);
        sprint.setProject(project);
        sprint.setAnalytics(new ArrayList<>());
        sprint.setAchievements(new ArrayList<>());
    }

    // ===================== IS ACTIVE TESTS =====================

    @Test
    void isActive_WhenInProgress_ReturnsTrue() {
        sprint.setStatus(SprintStatus.IN_PROGRESS);
        assertTrue(sprint.isActive());
    }

    @Test
    void isActive_WhenPlanned_ReturnsFalse() {
        sprint.setStatus(SprintStatus.PLANNED);
        assertFalse(sprint.isActive());
    }

    @Test
    void isActive_WhenCompleted_ReturnsFalse() {
        sprint.setStatus(SprintStatus.COMPLETED);
        assertFalse(sprint.isActive());
    }

    // ===================== IS COMPLETED TESTS =====================

    @Test
    void isCompleted_WhenCompleted_ReturnsTrue() {
        sprint.setStatus(SprintStatus.COMPLETED);
        assertTrue(sprint.isCompleted());
    }

    @Test
    void isCompleted_WhenInProgress_ReturnsFalse() {
        sprint.setStatus(SprintStatus.IN_PROGRESS);
        assertFalse(sprint.isCompleted());
    }

    // ===================== DURATION TESTS =====================

    @Test
    void getDurationDays_ReturnsCorrectDays() {
        sprint.setStartDate(LocalDate.now());
        sprint.setEndDate(LocalDate.now().plusDays(14));

        assertEquals(14L, sprint.getDurationDays());
    }

    // ===================== OVERDUE TESTS =====================

    @Test
    void isOverdue_ActiveAndPastEndDate_ReturnsTrue() {
        sprint.setStatus(SprintStatus.IN_PROGRESS);
        sprint.setEndDate(LocalDate.now().minusDays(1));

        assertTrue(sprint.isOverdue());
    }

    @Test
    void isOverdue_CompletedSprint_ReturnsFalse() {
        sprint.setStatus(SprintStatus.COMPLETED);
        sprint.setEndDate(LocalDate.now().minusDays(1));

        assertFalse(sprint.isOverdue());
    }

    @Test
    void isOverdue_FutureEndDate_ReturnsFalse() {
        sprint.setStatus(SprintStatus.IN_PROGRESS);
        sprint.setEndDate(LocalDate.now().plusDays(10));

        assertFalse(sprint.isOverdue());
    }

    // ===================== DAYS REMAINING TESTS =====================

    @Test
    void getDaysRemaining_FutureEndDate_ReturnsPositive() {
        sprint.setEndDate(LocalDate.now().plusDays(7));

        assertTrue(sprint.getDaysRemaining() >= 0);
    }

    @Test
    void getDaysRemaining_PastEndDate_ReturnsZero() {
        sprint.setEndDate(LocalDate.now().minusDays(3));

        assertEquals(0L, sprint.getDaysRemaining());
    }

    // ===================== DISPLAY NAME TESTS =====================

    @Test
    void getDisplayName_ReturnsFormattedName() {
        sprint.setSprintNumber(3);
        sprint.setName("Feature Sprint");

        String displayName = sprint.getDisplayName();

        assertTrue(displayName.contains("3"));
        assertTrue(displayName.contains("Feature Sprint"));
    }

    // ===================== IS IN PLANNING TESTS =====================

    @Test
    void isInPlanning_WhenPlanned_ReturnsTrue() {
        sprint.setStatus(SprintStatus.PLANNED);
        assertTrue(sprint.isInPlanning());
    }

    @Test
    void isInPlanning_WhenInProgress_ReturnsFalse() {
        sprint.setStatus(SprintStatus.IN_PROGRESS);
        assertFalse(sprint.isInPlanning());
    }

    // ===================== CAN BE STARTED TESTS =====================

    @Test
    void canBeStarted_PlannedAndAfterStartDate_ReturnsTrue() {
        sprint.setStatus(SprintStatus.PLANNED);
        sprint.setStartDate(LocalDate.now().minusDays(1));

        assertTrue(sprint.canBeStarted());
    }

    @Test
    void canBeStarted_InProgress_ReturnsFalse() {
        sprint.setStatus(SprintStatus.IN_PROGRESS);
        sprint.setStartDate(LocalDate.now().minusDays(1));

        assertFalse(sprint.canBeStarted());
    }

    // ===================== EQUALS AND HASHCODE TESTS =====================

    @Test
    void equals_SameId_ReturnsTrue() {
        Sprint sprint2 = new Sprint();
        sprint2.setId(1L);
        sprint2.setSprintNumber(1);
        sprint2.setProject(project);

        assertEquals(sprint, sprint2);
    }

    @Test
    void equals_DifferentId_ReturnsFalse() {
        Sprint sprint2 = new Sprint();
        sprint2.setId(2L);
        sprint2.setSprintNumber(1);
        sprint2.setProject(project);

        assertNotEquals(sprint, sprint2);
    }

    // ===================== TOSTRING TESTS =====================

    @Test
    void toString_ContainsName() {
        String str = sprint.toString();
        assertTrue(str.contains("Sprint 1"));
    }

    // ===================== CONSTRUCTOR TESTS =====================

    @Test
    void settersWork() {
        Sprint newSprint = new Sprint();
        newSprint.setSprintNumber(1);
        newSprint.setName("Test Sprint");
        newSprint.setProject(project);

        assertEquals(1, newSprint.getSprintNumber());
        assertEquals("Test Sprint", newSprint.getName());
        assertEquals(project, newSprint.getProject());
    }

    // ===================== DAYS SINCE START TESTS =====================

    @Test
    void getDaysSinceStart_Today_ReturnsZero() {
        sprint.setStartDate(LocalDate.now());
        assertEquals(0L, sprint.getDaysSinceStart());
    }

    @Test
    void getDaysSinceStart_FiveDaysAgo_ReturnsFive() {
        sprint.setStartDate(LocalDate.now().minusDays(5));
        assertEquals(5L, sprint.getDaysSinceStart());
    }

    @Test
    void getDaysSinceStart_FutureSprint_ReturnsNegative() {
        sprint.setStartDate(LocalDate.now().plusDays(3));
        assertEquals(-3L, sprint.getDaysSinceStart());
    }

    // ===================== STATUS DESCRIPTION TESTS =====================

    @Test
    void getStatusDescription_Planned_ReturnsPlannedMessage() {
        sprint.setStatus(SprintStatus.PLANNED);
        assertTrue(sprint.getStatusDescription().contains("planned"));
    }

    @Test
    void getStatusDescription_InProgress_ReturnsInProgressMessage() {
        sprint.setStatus(SprintStatus.IN_PROGRESS);
        sprint.setEndDate(LocalDate.now().plusDays(7));
        assertTrue(sprint.getStatusDescription().contains("in progress"));
    }

    @Test
    void getStatusDescription_InProgressOverdue_ReturnsOverdueMessage() {
        sprint.setStatus(SprintStatus.IN_PROGRESS);
        sprint.setEndDate(LocalDate.now().minusDays(1));
        assertTrue(sprint.getStatusDescription().contains("overdue"));
    }

    @Test
    void getStatusDescription_Completed_ReturnsCompletedMessage() {
        sprint.setStatus(SprintStatus.COMPLETED);
        assertTrue(sprint.getStatusDescription().contains("completed"));
    }

    @Test
    void getStatusDescription_Cancelled_ReturnsCancelledMessage() {
        sprint.setStatus(SprintStatus.CANCELLED);
        assertTrue(sprint.getStatusDescription().contains("cancelled"));
    }
}
