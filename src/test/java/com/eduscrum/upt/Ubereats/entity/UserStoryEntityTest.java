package com.eduscrum.upt.Ubereats.entity;

import com.eduscrum.upt.Ubereats.entity.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure unit tests for UserStory entity.
 */
/**
 * Unit tests for UserStory entity.
 *
 * @version 0.5.1 (2025-11-08)
 */
class UserStoryEntityTest {

    private UserStory userStory;
    private Sprint sprint;
    private Team team;
    private User assignedUser;

    @BeforeEach
    void setUp() {
        User teacher = new User();
        teacher.setId(1L);
        teacher.setRole(UserRole.TEACHER);

        Course course = new Course("Test Course", "TC101", "Desc", Semester.FIRST, "2024", teacher);
        Project project = new Project("Test Project", "Desc", LocalDate.now(), LocalDate.now().plusMonths(3), course);

        sprint = new Sprint();
        sprint.setId(1L);
        sprint.setSprintNumber(1);
        sprint.setName("Sprint 1");
        sprint.setStartDate(LocalDate.now());
        sprint.setEndDate(LocalDate.now().plusDays(14));
        sprint.setProject(project);

        team = new Team("Test Team");
        team.setId(1L);

        assignedUser = new User();
        assignedUser.setId(2L);
        assignedUser.setUsername("developer");
        assignedUser.setRole(UserRole.STUDENT);

        userStory = new UserStory();
        userStory.setId(1L);
        userStory.setTitle("Test Story");
        userStory.setDescription("Description");
        userStory.setStoryPoints(5);
        userStory.setStatus(StoryStatus.TODO);
        userStory.setPriority(StoryPriority.MEDIUM);
        userStory.setSprint(sprint);
        userStory.setTeam(team);
    }

    // ===================== STATUS CHECKS =====================

    @Test
    void isCompleted_WhenDone_ReturnsTrue() {
        userStory.setStatus(StoryStatus.DONE);
        assertTrue(userStory.isCompleted());
    }

    @Test
    void isCompleted_WhenNotDone_ReturnsFalse() {
        userStory.setStatus(StoryStatus.IN_PROGRESS);
        assertFalse(userStory.isCompleted());
    }

    @Test
    void isInProgress_WhenInProgress_ReturnsTrue() {
        userStory.setStatus(StoryStatus.IN_PROGRESS);
        assertTrue(userStory.isInProgress());
    }

    @Test
    void isInProgress_WhenTodo_ReturnsFalse() {
        userStory.setStatus(StoryStatus.TODO);
        assertFalse(userStory.isInProgress());
    }

    // ===================== ASSIGNMENT CHECKS =====================

    @Test
    void isAssigned_WhenUserAssigned_ReturnsTrue() {
        userStory.setAssignedTo(assignedUser);
        assertTrue(userStory.isAssigned());
    }

    @Test
    void isAssigned_WhenNoUserAssigned_ReturnsFalse() {
        userStory.setAssignedTo(null);
        assertFalse(userStory.isAssigned());
    }

    // ===================== GETTERS AND SETTERS =====================

    @Test
    void getTitle_ReturnsTitle() {
        assertEquals("Test Story", userStory.getTitle());
    }

    @Test
    void getStoryPoints_ReturnsStoryPoints() {
        assertEquals(5, userStory.getStoryPoints());
    }

    @Test
    void getSprint_ReturnsSprint() {
        assertEquals(sprint, userStory.getSprint());
    }

    @Test
    void getTeam_ReturnsTeam() {
        assertEquals(team, userStory.getTeam());
    }

    @Test
    void getPriority_ReturnsPriority() {
        assertEquals(StoryPriority.MEDIUM, userStory.getPriority());
    }

    // ===================== SETTERS =====================

    @Test
    void setStatus_ChangesStatus() {
        userStory.setStatus(StoryStatus.IN_REVIEW);
        assertEquals(StoryStatus.IN_REVIEW, userStory.getStatus());
    }

    @Test
    void setStoryPoints_ChangesPoints() {
        userStory.setStoryPoints(8);
        assertEquals(8, userStory.getStoryPoints());
    }

    @Test
    void setPriority_ChangesPriority() {
        userStory.setPriority(StoryPriority.HIGH);
        assertEquals(StoryPriority.HIGH, userStory.getPriority());
    }

    // ===================== EQUALS AND HASHCODE =====================

    @Test
    void equals_DifferentId_ReturnsFalse() {
        UserStory story2 = new UserStory();
        story2.setId(2L);

        assertNotEquals(userStory, story2);
    }
}
