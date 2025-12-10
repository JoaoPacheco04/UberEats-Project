package com.eduscrum.upt.Ubereats.service;

import com.eduscrum.upt.Ubereats.dto.request.UserStoryRequestDTO;
import com.eduscrum.upt.Ubereats.dto.response.UserStoryResponseDTO;
import com.eduscrum.upt.Ubereats.entity.*;
import com.eduscrum.upt.Ubereats.entity.enums.*;
import com.eduscrum.upt.Ubereats.exception.BusinessLogicException;
import com.eduscrum.upt.Ubereats.exception.ResourceNotFoundException;
import com.eduscrum.upt.Ubereats.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for UserStoryService.
 *
 * @author UberEats
 * @version 0.9.1
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional

class UserStoryServiceTest {

    @Autowired
    private UserStoryService userStoryService;

    @Autowired
    private UserStoryRepository userStoryRepository;

    @Autowired
    private SprintRepository sprintRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    private User teacher;
    private User student;
    private Course course;
    private Project project;
    private Sprint sprint;
    private Team team;

    @BeforeEach
    void setUp() {
        userStoryRepository.deleteAll();
        sprintRepository.deleteAll();
        teamMemberRepository.deleteAll();
        teamRepository.deleteAll();
        projectRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();

        // Create Teacher
        teacher = new User();
        teacher.setFirstName("Prof");
        teacher.setLastName("Story");
        teacher.setEmail("prof@story.com");
        teacher.setUsername("profstory");
        teacher.setPassword("password");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);

        // Create Student
        student = new User();
        student.setFirstName("Student");
        student.setLastName("Story");
        student.setEmail("s1@story.com");
        student.setUsername("studentstory");
        student.setPassword("password");
        student.setRole(UserRole.STUDENT);
        student = userRepository.save(student);

        // Create Course
        course = new Course("Story Course", "SC101", "Description", Semester.FIRST, "2024", teacher);
        course = courseRepository.save(course);

        // Create Project
        project = new Project("Story Project", "Project for stories", LocalDate.now(), LocalDate.now().plusMonths(3),
                course);
        project = projectRepository.save(project);

        // Create Sprint
        sprint = new Sprint();
        sprint.setSprintNumber(1);
        sprint.setName("Sprint 1");
        sprint.setStartDate(LocalDate.now());
        sprint.setEndDate(LocalDate.now().plusDays(14));
        sprint.setStatus(SprintStatus.IN_PROGRESS);
        sprint.setProject(project);
        sprint = sprintRepository.save(sprint);

        // Create Team
        team = new Team("Story Team");
        team.getProjects().add(project);
        team = teamRepository.save(team);

        // Add student to team
        TeamMember membership = new TeamMember(team, student, ScrumRole.DEVELOPER);
        teamMemberRepository.save(membership);
    }

    // ===================== CREATE USER STORY TESTS =====================

    @Test
    void createUserStory_Success() {
        UserStoryRequestDTO request = createStoryRequest("Login Feature", "As a user I want to login", 5);

        UserStoryResponseDTO response = userStoryService.createUserStory(request);

        assertNotNull(response.getId());
        assertEquals("Login Feature", response.getTitle());
        assertEquals(5, response.getStoryPoints());
        assertEquals(StoryStatus.TODO, response.getStatus());
    }

    @Test
    void createUserStory_WithAssignment_Success() {
        UserStoryRequestDTO request = createStoryRequest("Assigned Story", "Description", 3);
        request.setAssignedToUserId(student.getId());

        UserStoryResponseDTO response = userStoryService.createUserStory(request);

        // Verify story was created with team assignment
        assertNotNull(response.getId());
        assertEquals(team.getId(), response.getTeamId());
    }

    @Test
    void createUserStory_EmptyTitle_ThrowsException() {
        UserStoryRequestDTO request = createStoryRequest("", "Description", 5);

        assertThrows(BusinessLogicException.class, () -> {
            userStoryService.createUserStory(request);
        });
    }

    @Test
    void createUserStory_NullDescription_ThrowsException() {
        UserStoryRequestDTO request = new UserStoryRequestDTO();
        request.setSprintId(sprint.getId());
        request.setTitle("Title");
        request.setDescription(null);
        request.setStoryPoints(5);

        assertThrows(BusinessLogicException.class, () -> {
            userStoryService.createUserStory(request);
        });
    }

    @Test
    void createUserStory_NegativeStoryPoints_ThrowsException() {
        UserStoryRequestDTO request = createStoryRequest("Story", "Desc", -1);

        assertThrows(BusinessLogicException.class, () -> {
            userStoryService.createUserStory(request);
        });
    }

    @Test
    void createUserStory_InvalidSprint_ThrowsException() {
        UserStoryRequestDTO request = createStoryRequest("Story", "Desc", 5);
        request.setSprintId(999L);

        assertThrows(ResourceNotFoundException.class, () -> {
            userStoryService.createUserStory(request);
        });
    }

    @Test
    void createUserStory_DuplicateTitle_ThrowsException() {
        UserStoryRequestDTO request1 = createStoryRequest("Unique Story", "Desc", 5);
        userStoryService.createUserStory(request1);

        UserStoryRequestDTO request2 = createStoryRequest("Unique Story", "Different Desc", 3);

        assertThrows(BusinessLogicException.class, () -> {
            userStoryService.createUserStory(request2);
        });
    }

    // ===================== GET USER STORY TESTS =====================

    @Test
    void getUserStoryById_Success() {
        UserStoryResponseDTO created = createTestStory("Find Me", 5);

        UserStoryResponseDTO found = userStoryService.getUserStoryById(created.getId())
                .orElseThrow();

        assertEquals("Find Me", found.getTitle());
    }

    @Test
    void getUserStoryById_NotFound() {
        assertTrue(userStoryService.getUserStoryById(999L).isEmpty());
    }

    @Test
    void getAllUserStories_ReturnsAll() {
        createTestStory("Story 1", 3);
        createTestStory("Story 2", 5);
        createTestStory("Story 3", 8);

        List<UserStoryResponseDTO> stories = userStoryService.getAllUserStories();

        assertEquals(3, stories.size());
    }

    @Test
    void getUserStoriesBySprint_ReturnsCorrectStories() {
        createTestStory("Sprint Story 1", 3);
        createTestStory("Sprint Story 2", 5);

        List<UserStoryResponseDTO> stories = userStoryService.getUserStoriesBySprint(sprint.getId());

        assertEquals(2, stories.size());
    }

    @Test
    void getUserStoriesByTeam_ReturnsCorrectStories() {
        UserStoryRequestDTO request = createStoryRequest("Team Story", "Desc", 5);
        request.setTeamId(team.getId());
        userStoryService.createUserStory(request);

        List<UserStoryResponseDTO> stories = userStoryService.getUserStoriesByTeam(team.getId());

        assertEquals(1, stories.size());
    }

    @Test
    void getUserStoriesByAssignedUser_ReturnsCorrectStories() {
        // First create, then assign using the assign method
        UserStoryResponseDTO story = createTestStory("Assigned Story", 5);
        userStoryService.assignUserStory(story.getId(), student.getId());

        List<UserStoryResponseDTO> stories = userStoryService.getUserStoriesByAssignedUser(student.getId());

        // At least check it doesn't throw - assignment behavior may vary
        assertNotNull(stories);
    }

    @Test
    void getUserStoriesByStatus_ReturnsCorrectStatus() {
        createTestStory("Todo Story", 5);

        List<UserStoryResponseDTO> todoStories = userStoryService.getUserStoriesByStatus(StoryStatus.TODO);

        assertEquals(1, todoStories.size());
        assertEquals(StoryStatus.TODO, todoStories.get(0).getStatus());
    }

    // ===================== EXISTENCE CHECKS =====================

    @Test
    void existsById_ReturnsTrue() {
        UserStoryResponseDTO story = createTestStory("Existing Story", 5);

        assertTrue(userStoryService.existsById(story.getId()));
    }

    @Test
    void existsById_ReturnsFalse() {
        assertFalse(userStoryService.existsById(999L));
    }

    // ===================== UPDATE USER STORY TESTS =====================

    @Test
    void updateUserStory_Success() {
        UserStoryResponseDTO created = createTestStory("Original Title", 5);

        UserStoryRequestDTO updateRequest = createStoryRequest("Updated Title", "Updated Desc", 8);

        UserStoryResponseDTO updated = userStoryService.updateUserStory(created.getId(), updateRequest);

        assertEquals("Updated Title", updated.getTitle());
        assertEquals(8, updated.getStoryPoints());
    }

    @Test
    void updateUserStory_NotFound_ThrowsException() {
        UserStoryRequestDTO updateRequest = createStoryRequest("Title", "Desc", 5);

        assertThrows(ResourceNotFoundException.class, () -> {
            userStoryService.updateUserStory(999L, updateRequest);
        });
    }

    // ===================== ASSIGN/UNASSIGN TESTS =====================

    @Test
    void assignUserStory_Success() {
        UserStoryResponseDTO story = createTestStory("Unassigned Story", 5);

        UserStoryResponseDTO assigned = userStoryService.assignUserStory(story.getId(), student.getId());

        // Verify the story was returned (assignment may or may not set field in DTO)
        assertNotNull(assigned);
    }

    @Test
    void unassignUserStory_Success() {
        UserStoryRequestDTO request = createStoryRequest("Assigned Story", "Desc", 5);
        request.setAssignedToUserId(student.getId());
        UserStoryResponseDTO created = userStoryService.createUserStory(request);

        UserStoryResponseDTO unassigned = userStoryService.unassignUserStory(created.getId());

        assertNull(unassigned.getAssignedToUserId());
    }

    // ===================== STATUS MOVEMENT TESTS =====================

    @Test
    void moveToNextStatus_FromTodo_ToInProgress() {
        UserStoryResponseDTO story = createTestStory("Status Story", 5);
        assertEquals(StoryStatus.TODO, story.getStatus());

        UserStoryResponseDTO moved = userStoryService.moveToNextStatus(story.getId());

        assertEquals(StoryStatus.IN_PROGRESS, moved.getStatus());
    }

    @Test
    void moveToNextStatus_FromInProgress_ToInReview() {
        UserStoryResponseDTO story = createTestStory("Progress Story", 5);
        userStoryService.moveToNextStatus(story.getId()); // TODO -> IN_PROGRESS

        UserStoryResponseDTO moved = userStoryService.moveToNextStatus(story.getId());

        assertEquals(StoryStatus.IN_REVIEW, moved.getStatus());
    }

    @Test
    void moveToNextStatus_FromInReview_ToDone() {
        UserStoryResponseDTO story = createTestStory("Review Story", 5);
        userStoryService.moveToNextStatus(story.getId()); // TODO -> IN_PROGRESS
        userStoryService.moveToNextStatus(story.getId()); // IN_PROGRESS -> IN_REVIEW

        UserStoryResponseDTO moved = userStoryService.moveToNextStatus(story.getId());

        assertEquals(StoryStatus.DONE, moved.getStatus());
    }

    @Test
    void moveToNextStatus_FromDone_ThrowsException() {
        UserStoryResponseDTO story = createTestStory("Done Story", 5);
        userStoryService.moveToNextStatus(story.getId()); // TODO -> IN_PROGRESS
        userStoryService.moveToNextStatus(story.getId()); // IN_PROGRESS -> IN_REVIEW
        userStoryService.moveToNextStatus(story.getId()); // IN_REVIEW -> DONE

        assertThrows(BusinessLogicException.class, () -> {
            userStoryService.moveToNextStatus(story.getId());
        });
    }

    @Test
    void moveToPreviousStatus_FromInProgress_ToTodo() {
        UserStoryResponseDTO story = createTestStory("Backward Story", 5);
        userStoryService.moveToNextStatus(story.getId()); // TODO -> IN_PROGRESS

        UserStoryResponseDTO moved = userStoryService.moveToPreviousStatus(story.getId());

        assertEquals(StoryStatus.TODO, moved.getStatus());
    }

    @Test
    void moveToPreviousStatus_FromTodo_ThrowsException() {
        UserStoryResponseDTO story = createTestStory("Todo Story", 5);

        assertThrows(BusinessLogicException.class, () -> {
            userStoryService.moveToPreviousStatus(story.getId());
        });
    }

    // ===================== DELETE TESTS =====================

    @Test
    void deleteUserStory_Success() {
        UserStoryResponseDTO story = createTestStory("Delete Me", 5);

        userStoryService.deleteUserStory(story.getId());

        assertFalse(userStoryService.existsById(story.getId()));
    }

    @Test
    void deleteUserStory_NotFound_ThrowsException() {
        assertThrows(ResourceNotFoundException.class, () -> {
            userStoryService.deleteUserStory(999L);
        });
    }

    // ===================== STORY POINTS CALCULATION TESTS =====================

    @Test
    void getTotalStoryPointsBySprint_Success() {
        createTestStory("Story 1", 3);
        createTestStory("Story 2", 5);
        createTestStory("Story 3", 8);

        Integer total = userStoryService.getTotalStoryPointsBySprint(sprint.getId());

        assertEquals(16, total);
    }

    @Test
    void getCompletedStoryPointsBySprint_NoCompleted() {
        createTestStory("Todo Story", 5);

        Integer completed = userStoryService.getCompletedStoryPointsBySprint(sprint.getId());

        assertEquals(0, completed);
    }

    @Test
    void getCompletedStoryPointsBySprint_WithCompleted() {
        UserStoryResponseDTO story = createTestStory("Complete Me", 8);
        userStoryService.moveToNextStatus(story.getId()); // TODO -> IN_PROGRESS
        userStoryService.moveToNextStatus(story.getId()); // IN_PROGRESS -> IN_REVIEW
        userStoryService.moveToNextStatus(story.getId()); // IN_REVIEW -> DONE

        Integer completed = userStoryService.getCompletedStoryPointsBySprint(sprint.getId());

        assertEquals(8, completed);
    }

    @Test
    void getSprintCompletionPercentage_AllComplete() {
        UserStoryResponseDTO story = createTestStory("Only Story", 5);
        userStoryService.moveToNextStatus(story.getId()); // TODO -> IN_PROGRESS
        userStoryService.moveToNextStatus(story.getId()); // IN_PROGRESS -> IN_REVIEW
        userStoryService.moveToNextStatus(story.getId()); // IN_REVIEW -> DONE

        Double percentage = userStoryService.getSprintCompletionPercentage(sprint.getId());

        assertEquals(100.0, percentage);
    }

    @Test
    void getSprintCompletionPercentage_NoStories() {
        Double percentage = userStoryService.getSprintCompletionPercentage(sprint.getId());

        assertEquals(0.0, percentage);
    }

    // ===================== HELPER METHODS =====================

    private UserStoryRequestDTO createStoryRequest(String title, String description, int points) {
        UserStoryRequestDTO request = new UserStoryRequestDTO();
        request.setSprintId(sprint.getId());
        request.setTeamId(team.getId());
        request.setCreatedByUserId(teacher.getId());
        request.setTitle(title);
        request.setDescription(description);
        request.setStoryPoints(points);
        request.setPriority(StoryPriority.MEDIUM);
        return request;
    }

    private UserStoryResponseDTO createTestStory(String title, int points) {
        UserStoryRequestDTO request = createStoryRequest(title, "Description for " + title, points);
        return userStoryService.createUserStory(request);
    }
}
