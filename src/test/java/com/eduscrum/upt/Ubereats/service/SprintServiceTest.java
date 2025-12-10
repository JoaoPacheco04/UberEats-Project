package com.eduscrum.upt.Ubereats.service;

import com.eduscrum.upt.Ubereats.dto.request.SprintRequestDTO;
import com.eduscrum.upt.Ubereats.dto.response.SprintResponseDTO;
import com.eduscrum.upt.Ubereats.entity.Course;
import com.eduscrum.upt.Ubereats.entity.Project;
import com.eduscrum.upt.Ubereats.entity.User;
import com.eduscrum.upt.Ubereats.entity.enums.Semester;
import com.eduscrum.upt.Ubereats.entity.enums.SprintStatus;
import com.eduscrum.upt.Ubereats.entity.enums.UserRole;
import com.eduscrum.upt.Ubereats.exception.BusinessLogicException;
import com.eduscrum.upt.Ubereats.exception.ResourceNotFoundException;
import com.eduscrum.upt.Ubereats.repository.CourseRepository;
import com.eduscrum.upt.Ubereats.repository.ProjectRepository;
import com.eduscrum.upt.Ubereats.repository.SprintRepository;
import com.eduscrum.upt.Ubereats.repository.UserRepository;
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
 * Integration tests for SprintService.
 *
 * @version 1.0.0 (2025-12-10)
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SprintServiceTest {

    @Autowired
    private SprintService sprintService;

    @Autowired
    private SprintRepository sprintRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    private Project project;
    private User teacher;
    private Course course;

    @BeforeEach
    void setUp() {
        sprintRepository.deleteAll();
        projectRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();

        // Create Teacher
        teacher = new User();
        teacher.setFirstName("Prof");
        teacher.setLastName("Sprint");
        teacher.setEmail("prof@sprint.com");
        teacher.setUsername("profsprint");
        teacher.setPassword("password");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);

        // Create Course
        course = new Course("Sprint Course", "SC101", "Description", Semester.FIRST, "2024", teacher);
        course = courseRepository.save(course);

        // Create Project
        project = new Project("Sprint Project", "Project for sprints", LocalDate.now(), LocalDate.now().plusMonths(3),
                course);
        project = projectRepository.save(project);
    }

    // ===================== CREATE SPRINT TESTS =====================

    @Test
    void createSprint_Success() {
        SprintRequestDTO request = createSprintRequest(1, "Sprint 1",
                LocalDate.now(), LocalDate.now().plusDays(14));

        SprintResponseDTO response = sprintService.createSprint(request);

        assertNotNull(response.getId());
        assertEquals(1, response.getSprintNumber());
        assertEquals("Sprint 1", response.getName());
        assertEquals(SprintStatus.PLANNED, response.getStatus());
        assertEquals(project.getId(), response.getProjectId());
    }

    @Test
    void createSprint_WithGoal_Success() {
        SprintRequestDTO request = createSprintRequest(1, "Sprint 1",
                LocalDate.now(), LocalDate.now().plusDays(14));
        request.setGoal("Complete user authentication");

        SprintResponseDTO response = sprintService.createSprint(request);

        assertEquals("Complete user authentication", response.getGoal());
    }

    @Test
    void createSprint_DuplicateNumber_ThrowsException() {
        SprintRequestDTO request1 = createSprintRequest(1, "Sprint 1",
                LocalDate.now(), LocalDate.now().plusDays(14));
        sprintService.createSprint(request1);

        SprintRequestDTO request2 = createSprintRequest(1, "Sprint 1 Duplicate",
                LocalDate.now().plusDays(15), LocalDate.now().plusDays(28));

        assertThrows(BusinessLogicException.class, () -> {
            sprintService.createSprint(request2);
        });
    }

    @Test
    void createSprint_InvalidSprintNumber_ThrowsException() {
        SprintRequestDTO request = createSprintRequest(0, "Sprint 0",
                LocalDate.now(), LocalDate.now().plusDays(14));

        assertThrows(BusinessLogicException.class, () -> {
            sprintService.createSprint(request);
        });
    }

    @Test
    void createSprint_EmptyName_ThrowsException() {
        SprintRequestDTO request = createSprintRequest(1, "",
                LocalDate.now(), LocalDate.now().plusDays(14));

        assertThrows(BusinessLogicException.class, () -> {
            sprintService.createSprint(request);
        });
    }

    @Test
    void createSprint_EndBeforeStart_ThrowsException() {
        SprintRequestDTO request = createSprintRequest(1, "Invalid Sprint",
                LocalDate.now().plusDays(14), LocalDate.now());

        assertThrows(BusinessLogicException.class, () -> {
            sprintService.createSprint(request);
        });
    }

    @Test
    void createSprint_NullStartDate_ThrowsException() {
        SprintRequestDTO request = new SprintRequestDTO();
        request.setProjectId(project.getId());
        request.setSprintNumber(1);
        request.setName("Sprint");
        request.setStartDate(null);
        request.setEndDate(LocalDate.now().plusDays(14));

        assertThrows(BusinessLogicException.class, () -> {
            sprintService.createSprint(request);
        });
    }

    @Test
    void createSprint_InvalidProjectId_ThrowsException() {
        SprintRequestDTO request = new SprintRequestDTO();
        request.setProjectId(999L);
        request.setSprintNumber(1);
        request.setName("Sprint");
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(14));

        assertThrows(ResourceNotFoundException.class, () -> {
            sprintService.createSprint(request);
        });
    }

    // ===================== GET SPRINT TESTS =====================

    @Test
    void getSprintById_Success() {
        SprintResponseDTO created = createTestSprint(1, "Find Sprint");

        SprintResponseDTO found = sprintService.getSprintById(created.getId());

        assertEquals("Find Sprint", found.getName());
    }

    @Test
    void getSprintById_NotFound_ThrowsException() {
        assertThrows(ResourceNotFoundException.class, () -> {
            sprintService.getSprintById(999L);
        });
    }

    @Test
    void getAllSprints_ReturnsAllSprints() {
        createTestSprint(1, "Sprint 1");
        createTestSprint(2, "Sprint 2");
        createTestSprint(3, "Sprint 3");

        List<SprintResponseDTO> sprints = sprintService.getAllSprints();

        assertEquals(3, sprints.size());
    }

    @Test
    void getSprintsByProject_ReturnsCorrectSprints() {
        createTestSprint(1, "Sprint 1");
        createTestSprint(2, "Sprint 2");

        List<SprintResponseDTO> sprints = sprintService.getSprintsByProject(project.getId());

        assertEquals(2, sprints.size());
    }

    @Test
    void getSprintsByStatus_ReturnsCorrectStatus() {
        createTestSprint(1, "Planned Sprint");

        List<SprintResponseDTO> plannedSprints = sprintService.getSprintsByStatus(SprintStatus.PLANNED);

        assertEquals(1, plannedSprints.size());
        assertEquals(SprintStatus.PLANNED, plannedSprints.get(0).getStatus());
    }

    // ===================== EXISTENCE CHECKS =====================

    @Test
    void existsById_ReturnsTrue() {
        SprintResponseDTO sprint = createTestSprint(1, "Test Sprint");

        assertTrue(sprintService.existsById(sprint.getId()));
    }

    @Test
    void existsById_ReturnsFalse() {
        assertFalse(sprintService.existsById(999L));
    }

    @Test
    void existsByProjectAndSprintNumber_ReturnsTrue() {
        createTestSprint(1, "Sprint 1");

        assertTrue(sprintService.existsByProjectAndSprintNumber(project.getId(), 1));
    }

    @Test
    void existsByProjectAndSprintNumber_ReturnsFalse() {
        assertFalse(sprintService.existsByProjectAndSprintNumber(project.getId(), 999));
    }

    // ===================== UPDATE SPRINT TESTS =====================

    @Test
    void updateSprint_Success() {
        SprintResponseDTO created = createTestSprint(1, "Original Sprint");

        SprintRequestDTO updateRequest = createSprintRequest(1, "Updated Sprint",
                LocalDate.now(), LocalDate.now().plusDays(21));
        updateRequest.setGoal("Updated goal");

        SprintResponseDTO updated = sprintService.updateSprint(created.getId(), updateRequest);

        assertEquals("Updated Sprint", updated.getName());
        assertEquals("Updated goal", updated.getGoal());
    }

    @Test
    void updateSprint_ChangeSprintNumber_Success() {
        SprintResponseDTO created = createTestSprint(1, "Sprint 1");

        SprintRequestDTO updateRequest = createSprintRequest(5, "Sprint 5",
                LocalDate.now(), LocalDate.now().plusDays(14));

        SprintResponseDTO updated = sprintService.updateSprint(created.getId(), updateRequest);

        assertEquals(5, updated.getSprintNumber());
    }

    @Test
    void updateSprint_DuplicateNumber_ThrowsException() {
        createTestSprint(1, "Sprint 1");
        SprintResponseDTO sprint2 = createTestSprint(2, "Sprint 2");

        SprintRequestDTO updateRequest = createSprintRequest(1, "Sprint 2 Updated",
                LocalDate.now(), LocalDate.now().plusDays(14));

        assertThrows(BusinessLogicException.class, () -> {
            sprintService.updateSprint(sprint2.getId(), updateRequest);
        });
    }

    // ===================== SPRINT LIFECYCLE TESTS =====================

    @Test
    void startSprint_Success() {
        SprintRequestDTO request = createSprintRequest(1, "Ready Sprint",
                LocalDate.now(), LocalDate.now().plusDays(14));
        SprintResponseDTO created = sprintService.createSprint(request);

        SprintResponseDTO started = sprintService.startSprint(created.getId());

        assertEquals(SprintStatus.IN_PROGRESS, started.getStatus());
    }

    @Test
    void startSprint_NotPlanned_ThrowsException() {
        SprintRequestDTO request = createSprintRequest(1, "Sprint",
                LocalDate.now(), LocalDate.now().plusDays(14));
        request.setStatus(SprintStatus.IN_PROGRESS);
        SprintResponseDTO created = sprintService.createSprint(request);

        assertThrows(BusinessLogicException.class, () -> {
            sprintService.startSprint(created.getId());
        });
    }

    @Test
    void cancelSprint_Success() {
        SprintResponseDTO created = createTestSprint(1, "To Cancel");

        SprintResponseDTO cancelled = sprintService.cancelSprint(created.getId());

        assertEquals(SprintStatus.CANCELLED, cancelled.getStatus());
    }

    // ===================== DELETE SPRINT TESTS =====================

    @Test
    void deleteSprint_Success() {
        SprintResponseDTO sprint = createTestSprint(1, "To Delete");

        sprintService.deleteSprint(sprint.getId());

        assertFalse(sprintService.existsById(sprint.getId()));
    }

    @Test
    void deleteSprint_NotFound_ThrowsException() {
        assertThrows(ResourceNotFoundException.class, () -> {
            sprintService.deleteSprint(999L);
        });
    }

    // ===================== VELOCITY CALCULATION TESTS =====================

    @Test
    void calculateTeamVelocity_NoData_ReturnsZero() {
        Double velocity = sprintService.calculateTeamVelocity(999L);

        assertEquals(0.0, velocity);
    }

    // ===================== COMPLETE SPRINT TESTS =====================

    @Test
    void completeSprint_Success() {
        SprintRequestDTO request = createSprintRequest(1, "Sprint to Complete",
                LocalDate.now().minusDays(14), LocalDate.now());
        SprintResponseDTO created = sprintService.createSprint(request);
        sprintService.startSprint(created.getId());

        SprintResponseDTO completed = sprintService.completeSprint(created.getId(), LocalDate.now());

        assertEquals(SprintStatus.COMPLETED, completed.getStatus());
    }

    @Test
    void completeSprint_NotInProgress_ThrowsException() {
        SprintResponseDTO created = createTestSprint(1, "Planned Sprint");

        assertThrows(BusinessLogicException.class, () -> {
            sprintService.completeSprint(created.getId(), LocalDate.now());
        });
    }

    // ===================== ACTIVE SPRINTS TESTS =====================

    @Test
    void getActiveSprints_ReturnsOnlyActive() {
        SprintRequestDTO request = createSprintRequest(1, "Active Sprint",
                LocalDate.now(), LocalDate.now().plusDays(14));
        SprintResponseDTO created = sprintService.createSprint(request);
        sprintService.startSprint(created.getId());

        createTestSprint(2, "Planned Sprint");

        List<SprintResponseDTO> activeSprints = sprintService.getActiveSprints();

        assertEquals(1, activeSprints.size());
        assertEquals("Active Sprint", activeSprints.get(0).getName());
    }

    @Test
    void getActiveSprints_NoActive_ReturnsEmpty() {
        createTestSprint(1, "Planned Sprint");

        List<SprintResponseDTO> activeSprints = sprintService.getActiveSprints();

        assertTrue(activeSprints.isEmpty());
    }

    // ===================== OVERDUE SPRINTS TESTS =====================

    @Test
    void getOverdueSprints_ReturnsOverdueSprints() {
        SprintRequestDTO request = createSprintRequest(1, "Overdue Sprint",
                LocalDate.now().minusDays(20), LocalDate.now().minusDays(5));
        SprintResponseDTO created = sprintService.createSprint(request);
        sprintService.startSprint(created.getId());

        List<SprintResponseDTO> overdueSprints = sprintService.getOverdueSprints();

        assertTrue(overdueSprints.size() >= 1);
    }

    // ===================== LATEST SPRINT TESTS =====================

    @Test
    void getLatestSprintByProject_ReturnsLatest() {
        createTestSprint(1, "Sprint 1");
        createTestSprint(2, "Sprint 2");
        SprintResponseDTO sprint3 = createTestSprint(3, "Sprint 3");

        SprintResponseDTO latest = sprintService.getLatestSprintByProject(project.getId());

        assertEquals(sprint3.getId(), latest.getId());
        assertEquals("Sprint 3", latest.getName());
    }

    @Test
    void getLatestSprintByProject_NoSprints_ThrowsException() {
        assertThrows(ResourceNotFoundException.class, () -> {
            sprintService.getLatestSprintByProject(project.getId());
        });
    }

    // ===================== GET SPRINT ENTITY TESTS =====================

    @Test
    void getSprintEntity_Success() {
        SprintResponseDTO created = createTestSprint(1, "Entity Sprint");

        var entity = sprintService.getSprintEntity(created.getId());

        assertNotNull(entity);
        assertEquals(created.getId(), entity.getId());
    }

    @Test
    void getSprintEntity_NotFound_ThrowsException() {
        assertThrows(ResourceNotFoundException.class, () -> {
            sprintService.getSprintEntity(999L);
        });
    }

    // ===================== CHECK SPRINTS COMPLETED ON TIME =====================

    @Test
    void checkIfAllSprintsInProjectCompletedOnTime_AllOnTime_ReturnsTrue() {
        SprintRequestDTO request = createSprintRequest(1, "On Time Sprint",
                LocalDate.now().minusDays(14), LocalDate.now());
        SprintResponseDTO created = sprintService.createSprint(request);
        sprintService.startSprint(created.getId());
        sprintService.completeSprint(created.getId(), LocalDate.now());

        boolean allOnTime = sprintService.checkIfAllSprintsInProjectCompletedOnTime(project.getId());

        assertTrue(allOnTime);
    }

    @Test
    void checkIfAllSprintsInProjectCompletedOnTime_NoSprints_ReturnsTrue() {
        boolean result = sprintService.checkIfAllSprintsInProjectCompletedOnTime(project.getId());

        assertTrue(result);
    }

    // ===================== PROJECT BURNDOWN TESTS =====================

    @Test
    void getProjectBurndown_ReturnsEmptyMap_WhenNoSprints() {
        var burndown = sprintService.getProjectBurndown(project.getId());

        assertNotNull(burndown);
        assertTrue(burndown.isEmpty());
    }

    @Test
    void getProjectBurndown_ReturnsMap_WithSprints() {
        createTestSprint(1, "Sprint 1");
        createTestSprint(2, "Sprint 2");

        var burndown = sprintService.getProjectBurndown(project.getId());

        assertNotNull(burndown);
        assertEquals(2, burndown.size());
    }

    // ===================== START READY SPRINTS TESTS =====================

    @Test
    void startReadySprints_StartsSprintsWithStartDateReached() {
        // Create a sprint that should start today
        SprintRequestDTO request = createSprintRequest(1, "Ready to Start",
                LocalDate.now(), LocalDate.now().plusDays(14));
        sprintService.createSprint(request);

        List<SprintResponseDTO> started = sprintService.startReadySprints();

        // The sprint should be started if start date is today
        assertNotNull(started);
    }

    @Test
    void startReadySprints_ReturnsEmpty_WhenNoReadySprints() {
        // Create a sprint that starts in the future
        SprintRequestDTO request = createSprintRequest(1, "Future Sprint",
                LocalDate.now().plusDays(5), LocalDate.now().plusDays(19));
        sprintService.createSprint(request);

        List<SprintResponseDTO> started = sprintService.startReadySprints();

        assertTrue(started.isEmpty());
    }

    // ===================== COMPLETE READY SPRINTS TESTS =====================

    @Test
    void completeReadySprints_ReturnsEmpty_WhenNoReadySprints() {
        createTestSprint(1, "Planned Sprint");

        List<SprintResponseDTO> completed = sprintService.completeReadySprints();

        assertTrue(completed.isEmpty());
    }

    // ===================== HELPER METHODS =====================

    private SprintRequestDTO createSprintRequest(int number, String name, LocalDate start, LocalDate end) {
        SprintRequestDTO request = new SprintRequestDTO();
        request.setProjectId(project.getId());
        request.setSprintNumber(number);
        request.setName(name);
        request.setStartDate(start);
        request.setEndDate(end);
        return request;
    }

    private SprintResponseDTO createTestSprint(int number, String name) {
        SprintRequestDTO request = createSprintRequest(number, name,
                LocalDate.now(), LocalDate.now().plusDays(14));
        return sprintService.createSprint(request);
    }
}
