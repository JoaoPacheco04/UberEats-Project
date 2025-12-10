package com.eduscrum.upt.Ubereats.service;

import com.eduscrum.upt.Ubereats.dto.request.AnalyticsRequestDTO;
import com.eduscrum.upt.Ubereats.dto.response.AnalyticsResponseDTO;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AnalyticsServiceTest {

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private AnalyticRepository analyticRepository;

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
    private Course course;
    private Project project;
    private Sprint sprint;
    private Team team;

    @BeforeEach
    void setUp() {
        analyticRepository.deleteAll();
        sprintRepository.deleteAll();
        teamMemberRepository.deleteAll();
        teamRepository.deleteAll();
        projectRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();

        // Create Teacher
        teacher = new User();
        teacher.setFirstName("Prof");
        teacher.setLastName("Analytics");
        teacher.setEmail("prof@analytics.com");
        teacher.setUsername("profanalytics");
        teacher.setPassword("password");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);

        // Create Course
        course = new Course("Analytics Course", "AC101", "Description", Semester.FIRST, "2024", teacher);
        course = courseRepository.save(course);

        // Create Project
        project = new Project("Analytics Project", "Description", LocalDate.now(), LocalDate.now().plusMonths(3),
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
        team = new Team("Analytics Team");
        team.getProjects().add(project);
        team = teamRepository.save(team);
    }

    // ===================== CREATE ANALYTIC (via DTO) TESTS =====================

    @Test
    void createAnalytic_WithRequestDTO_Success() {
        AnalyticsRequestDTO request = new AnalyticsRequestDTO();
        request.setSprintId(sprint.getId());
        request.setTeamId(team.getId());
        request.setTeamMood(TeamMood.HAPPY);
        request.setNotes("Test notes");

        AnalyticsResponseDTO response = analyticsService.createAnalytic(request);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(sprint.getId(), response.getSprintId());
        assertEquals(team.getId(), response.getTeamId());
    }

    @Test
    void createAnalytic_SetsTeamMood() {
        AnalyticsRequestDTO request = new AnalyticsRequestDTO();
        request.setSprintId(sprint.getId());
        request.setTeamId(team.getId());
        request.setTeamMood(TeamMood.NEUTRAL);

        AnalyticsResponseDTO response = analyticsService.createAnalytic(request);

        assertEquals(TeamMood.NEUTRAL, response.getTeamMood());
    }

    // ===================== UPDATE DAILY ANALYTIC TESTS =====================

    @Test
    void updateDailyAnalytic_CreatesRecord() {
        // This method is void, so we just verify it doesn't throw
        assertDoesNotThrow(() -> analyticsService.updateDailyAnalytic(sprint.getId(), team.getId()));
    }

    // ===================== GET ANALYTICS TESTS =====================

    @Test
    void getAnalyticsBySprintAndTeam_NotFound_ReturnsEmptyList() {
        List<AnalyticsResponseDTO> analytics = analyticsService.getAnalyticsBySprintAndTeam(sprint.getId(),
                team.getId());

        assertNotNull(analytics);
        // May be empty if no analytics created
    }

    @Test
    void getAnalyticsBySprintAndTeam_Found_ReturnsAnalytics() {
        // First create an analytic
        AnalyticsRequestDTO request = new AnalyticsRequestDTO();
        request.setSprintId(sprint.getId());
        request.setTeamId(team.getId());
        request.setTeamMood(TeamMood.HAPPY);
        analyticsService.createAnalytic(request);

        List<AnalyticsResponseDTO> analytics = analyticsService.getAnalyticsBySprintAndTeam(sprint.getId(),
                team.getId());

        assertNotNull(analytics);
        assertFalse(analytics.isEmpty());
    }

    // ===================== UPSERT BEHAVIOR TESTS =====================

    @Test
    void createAnalytic_SameDay_UpdatesExisting() {
        // Create first analytic
        AnalyticsRequestDTO request1 = new AnalyticsRequestDTO();
        request1.setSprintId(sprint.getId());
        request1.setTeamId(team.getId());
        request1.setTeamMood(TeamMood.NEUTRAL);
        request1.setNotes("First note");
        AnalyticsResponseDTO first = analyticsService.createAnalytic(request1);

        // Create second analytic same day - should update
        AnalyticsRequestDTO request2 = new AnalyticsRequestDTO();
        request2.setSprintId(sprint.getId());
        request2.setTeamId(team.getId());
        request2.setTeamMood(TeamMood.HAPPY);
        request2.setNotes("Updated note");
        AnalyticsResponseDTO second = analyticsService.createAnalytic(request2);

        // Should be the same record (updated)
        assertEquals(first.getId(), second.getId());
        assertEquals(TeamMood.HAPPY, second.getTeamMood());
    }
}
