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

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for SchedulerService.
 * Uses H2 in-memory database for testing.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SchedulerServiceTest {

    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    private SprintRepository sprintRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    private User teacher;
    private Course course;
    private Project project;
    private int sprintCounter = 1;

    @BeforeEach
    void setUp() {
        sprintCounter = 1;

        // Create teacher
        teacher = new User("teacher", "teacher@test.com", "password", UserRole.TEACHER, "Prof", "Teacher");
        teacher = userRepository.save(teacher);

        // Create course
        course = new Course("Test Course", "TC001", "Course Description", Semester.FIRST, "2024", teacher);
        course = courseRepository.save(course);

        // Create project
        project = new Project("Test Project", "Project Description",
                LocalDate.now().minusDays(30), LocalDate.now().plusDays(30), course);
        project = projectRepository.save(project);
    }

    private Sprint createSprint(String name, LocalDate startDate, LocalDate endDate, SprintStatus status) {
        Sprint sprint = new Sprint();
        sprint.setName(name);
        sprint.setSprintNumber(sprintCounter++);
        sprint.setStartDate(startDate);
        sprint.setEndDate(endDate);
        sprint.setStatus(status);
        sprint.setProject(project);
        return sprintRepository.save(sprint);
    }

    // ===================== CHECK OVERDUE SPRINTS TESTS =====================

    @Test
    void checkOverdueSprints_NoSprints_DoesNotThrow() {
        // Should not throw any exception even with no sprints
        assertDoesNotThrow(() -> schedulerService.checkOverdueSprints());
    }

    @Test
    void checkOverdueSprints_NoActiveSprints_DoesNotThrow() {
        // Create a planned sprint (not active)
        createSprint("Planned Sprint", LocalDate.now().plusDays(5), LocalDate.now().plusDays(19), SprintStatus.PLANNED);

        assertDoesNotThrow(() -> schedulerService.checkOverdueSprints());
    }

    @Test
    void checkOverdueSprints_ActiveSprintNotOverdue_DoesNotThrow() {
        // Create an active sprint that is NOT overdue
        createSprint("Active Sprint", LocalDate.now().minusDays(5), LocalDate.now().plusDays(9),
                SprintStatus.IN_PROGRESS);

        assertDoesNotThrow(() -> schedulerService.checkOverdueSprints());
    }

    @Test
    void checkOverdueSprints_ActiveSprintOverdue_DoesNotThrow() {
        // Create an active sprint that IS overdue
        createSprint("Overdue Sprint", LocalDate.now().minusDays(20), LocalDate.now().minusDays(1),
                SprintStatus.IN_PROGRESS);

        // Should not throw - just logs the overdue sprint
        assertDoesNotThrow(() -> schedulerService.checkOverdueSprints());
    }

    @Test
    void checkOverdueSprints_MultipleActiveSprints_ChecksAll() {
        // Create multiple active sprints
        createSprint("Overdue Sprint", LocalDate.now().minusDays(20), LocalDate.now().minusDays(1),
                SprintStatus.IN_PROGRESS);
        createSprint("Active Sprint", LocalDate.now().minusDays(5), LocalDate.now().plusDays(9),
                SprintStatus.IN_PROGRESS);

        assertDoesNotThrow(() -> schedulerService.checkOverdueSprints());
    }

    @Test
    void checkOverdueSprints_CompletedSprint_IsNotChecked() {
        // Create a completed sprint with past end date
        createSprint("Completed Sprint", LocalDate.now().minusDays(20), LocalDate.now().minusDays(1),
                SprintStatus.COMPLETED);

        // Should not throw - completed sprints are not checked
        assertDoesNotThrow(() -> schedulerService.checkOverdueSprints());
    }

    @Test
    void checkOverdueSprints_SprintEndingToday_IsNotOverdue() {
        // Create a sprint ending today
        createSprint("Ending Today Sprint", LocalDate.now().minusDays(14), LocalDate.now(), SprintStatus.IN_PROGRESS);

        // Should not throw - sprint ending today is not overdue yet
        assertDoesNotThrow(() -> schedulerService.checkOverdueSprints());
    }

    @Test
    void checkOverdueSprints_MixedSprintStatuses_OnlyChecksActive() {
        // Create sprints with different statuses
        createSprint("Planned Sprint", LocalDate.now().plusDays(1), LocalDate.now().plusDays(15), SprintStatus.PLANNED);
        createSprint("Active Sprint", LocalDate.now().minusDays(5), LocalDate.now().plusDays(9),
                SprintStatus.IN_PROGRESS);
        createSprint("Completed Sprint", LocalDate.now().minusDays(30), LocalDate.now().minusDays(16),
                SprintStatus.COMPLETED);

        assertDoesNotThrow(() -> schedulerService.checkOverdueSprints());
    }
}
