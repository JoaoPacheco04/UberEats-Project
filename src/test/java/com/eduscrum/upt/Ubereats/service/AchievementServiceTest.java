package com.eduscrum.upt.Ubereats.service;

import com.eduscrum.upt.Ubereats.dto.request.AchievementRequestDTO;
import com.eduscrum.upt.Ubereats.dto.request.BadgeRequestDTO;
import com.eduscrum.upt.Ubereats.dto.response.AchievementResponseDTO;
import com.eduscrum.upt.Ubereats.dto.response.BadgeResponseDTO;
import com.eduscrum.upt.Ubereats.entity.*;
import com.eduscrum.upt.Ubereats.entity.enums.BadgeType;
import com.eduscrum.upt.Ubereats.entity.enums.Semester;
import com.eduscrum.upt.Ubereats.entity.enums.UserRole;
import com.eduscrum.upt.Ubereats.repository.*;
import com.eduscrum.upt.Ubereats.entity.enums.SprintStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for AchievementService.
 *
 * @version 1.0.1 (2025-12-03)
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
/**
 * Integration tests for AchievementService.
 *
 * @version 1.0.1 (2025-12-03)
 */
class AchievementServiceTest {

    @Autowired
    private AchievementService achievementService;

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private BadgeService badgeService;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SprintRepository sprintRepository;

    private User teacher;
    private User student;
    private Team team;
    private Project project;
    private BadgeResponseDTO manualBadge;

    @BeforeEach
    void setUp() {
        achievementRepository.deleteAll();
        badgeRepository.deleteAll();
        teamRepository.deleteAll();
        projectRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();

        // Create Teacher
        teacher = new User();
        teacher.setFirstName("Prof");
        teacher.setLastName("Award");
        teacher.setEmail("prof@award.com");
        teacher.setUsername("profaward");
        teacher.setPassword("password");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);

        // Create Student
        student = new User();
        student.setFirstName("Student");
        student.setLastName("Award");
        student.setEmail("s1@award.com");
        student.setUsername("studentaward");
        student.setPassword("password");
        student.setRole(UserRole.STUDENT);
        student = userRepository.save(student);

        // Create Course
        Course course = new Course("Award Course", "AC101", "Description", Semester.FIRST, "2024", teacher);
        course = courseRepository.save(course);

        // Create Project
        project = new Project("Award Project", "Project for achievements", LocalDate.now(),
                LocalDate.now().plusMonths(3), course);
        project = projectRepository.save(project);

        // Create Team
        team = new Team("Award Team");
        team = teamRepository.save(team);

        // Create a Manual Badge
        BadgeRequestDTO badgeRequest = new BadgeRequestDTO();
        badgeRequest.setName("Gold Star");
        badgeRequest.setDescription("Excellence Award");
        badgeRequest.setPoints(50);
        badgeRequest.setBadgeType(BadgeType.MANUAL);
        badgeRequest.setCreatedByUserId(teacher.getId());
        manualBadge = badgeService.createBadge(badgeRequest);
    }

    // ===================== CREATE ACHIEVEMENT (INDIVIDUAL) TESTS
    // =====================

    @Test
    void createAchievement_ToIndividual_Success() {
        AchievementRequestDTO request = new AchievementRequestDTO(
                "Outstanding performance",
                manualBadge.getId(),
                student.getId(),
                null,
                project.getId(),
                null,
                teacher.getId());

        AchievementResponseDTO response = achievementService.createAchievement(request);

        assertNotNull(response.getId());
        assertEquals(student.getId(), response.getAwardedToUserId());
        assertEquals(50, response.getPoints());
        assertEquals("Gold Star", response.getBadgeName());
        assertTrue(response.isIndividualAchievement());
        assertFalse(response.isTeamAchievement());
    }

    @Test
    void createAchievement_ToTeam_Success() {
        AchievementRequestDTO request = new AchievementRequestDTO(
                "Great team collaboration",
                manualBadge.getId(),
                null,
                team.getId(),
                project.getId(),
                null,
                teacher.getId());

        AchievementResponseDTO response = achievementService.createAchievement(request);

        assertNotNull(response.getId());
        assertEquals(team.getId(), response.getAwardedToTeamId());
        assertTrue(response.isTeamAchievement());
        assertFalse(response.isIndividualAchievement());
    }

    @Test
    void createAchievement_ManualBadgeWithoutAwarder_ThrowsException() {
        AchievementRequestDTO request = new AchievementRequestDTO(
                "No awarder",
                manualBadge.getId(),
                student.getId(),
                null,
                project.getId(),
                null,
                null // No awarder for manual badge
        );

        assertThrows(IllegalArgumentException.class, () -> {
            achievementService.createAchievement(request);
        });
    }

    @Test
    void createAchievement_BothUserAndTeam_ThrowsException() {
        AchievementRequestDTO request = new AchievementRequestDTO(
                "Both recipients",
                manualBadge.getId(),
                student.getId(),
                team.getId(), // Both set
                project.getId(),
                null,
                teacher.getId());

        assertThrows(IllegalArgumentException.class, () -> {
            achievementService.createAchievement(request);
        });
    }

    @Test
    void createAchievement_NeitherUserNorTeam_ThrowsException() {
        AchievementRequestDTO request = new AchievementRequestDTO(
                "No recipients",
                manualBadge.getId(),
                null,
                null, // Neither set
                project.getId(),
                null,
                teacher.getId());

        assertThrows(IllegalArgumentException.class, () -> {
            achievementService.createAchievement(request);
        });
    }

    @Test
    void createAchievement_DuplicateUserBadgeInProject_ThrowsException() {
        // Create first achievement
        AchievementRequestDTO request1 = new AchievementRequestDTO(
                "First award",
                manualBadge.getId(),
                student.getId(),
                null,
                project.getId(),
                null,
                teacher.getId());
        achievementService.createAchievement(request1);

        // Try to create duplicate
        AchievementRequestDTO request2 = new AchievementRequestDTO(
                "Duplicate award",
                manualBadge.getId(),
                student.getId(),
                null,
                project.getId(),
                null,
                teacher.getId());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            achievementService.createAchievement(request2);
        });

        assertTrue(ex.getMessage().contains("already has this badge"));
    }

    @Test
    void createAchievement_DuplicateTeamBadge_ThrowsException() {
        // Create first achievement
        AchievementRequestDTO request1 = new AchievementRequestDTO(
                "First team award",
                manualBadge.getId(),
                null,
                team.getId(),
                project.getId(),
                null,
                teacher.getId());
        achievementService.createAchievement(request1);

        // Try to create duplicate
        AchievementRequestDTO request2 = new AchievementRequestDTO(
                "Duplicate team award",
                manualBadge.getId(),
                null,
                team.getId(),
                project.getId(),
                null,
                teacher.getId());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            achievementService.createAchievement(request2);
        });

        assertTrue(ex.getMessage().contains("already has this badge"));
    }

    // ===================== GET ACHIEVEMENTS TESTS =====================

    @Test
    void getAllAchievements_ReturnsAll() {
        createTestUserAchievement();
        createTestTeamAchievement();

        List<AchievementResponseDTO> achievements = achievementService.getAllAchievements();

        assertEquals(2, achievements.size());
    }

    @Test
    void getAchievementById_Success() {
        AchievementResponseDTO created = createTestUserAchievement();

        Optional<AchievementResponseDTO> found = achievementService.getAchievementById(created.getId());

        assertTrue(found.isPresent());
        assertEquals(created.getId(), found.get().getId());
    }

    @Test
    void getAchievementById_NotFound() {
        Optional<AchievementResponseDTO> found = achievementService.getAchievementById(999L);

        assertTrue(found.isEmpty());
    }

    @Test
    void getUserAchievements_ReturnsUserAchievements() {
        createTestUserAchievement();
        createTestTeamAchievement();

        List<AchievementResponseDTO> userAchievements = achievementService.getUserAchievements(student.getId());

        assertEquals(1, userAchievements.size());
        assertEquals(student.getId(), userAchievements.get(0).getAwardedToUserId());
    }

    @Test
    void getTeamAchievements_ReturnsTeamAchievements() {
        createTestUserAchievement();
        createTestTeamAchievement();

        List<AchievementResponseDTO> teamAchievements = achievementService.getTeamAchievements(team.getId());

        assertEquals(1, teamAchievements.size());
        assertEquals(team.getId(), teamAchievements.get(0).getAwardedToTeamId());
    }

    @Test
    void getProjectAchievements_ReturnsProjectAchievements() {
        createTestUserAchievement();
        createTestTeamAchievement();

        List<AchievementResponseDTO> projectAchievements = achievementService.getProjectAchievements(project.getId());

        assertEquals(2, projectAchievements.size());
    }

    // ===================== POINTS CALCULATION TESTS =====================

    @Test
    void getUserTotalPoints_Success() {
        createTestUserAchievement();

        Integer points = achievementService.getUserTotalPoints(student.getId(), project.getId());

        assertEquals(50, points);
    }

    @Test
    void getUserTotalPoints_NoAchievements_ReturnsZero() {
        Integer points = achievementService.getUserTotalPoints(999L, project.getId());

        assertEquals(0, points);
    }

    @Test
    void getTeamTotalPoints_Success() {
        createTestTeamAchievement();

        Integer points = achievementService.getTeamTotalPoints(team.getId());

        assertEquals(50, points);
    }

    @Test
    void getUserTotalPointsAllProjects_Success() {
        createTestUserAchievement();

        Integer points = achievementService.getUserTotalPointsAllProjects(student.getId());

        assertEquals(50, points);
    }

    @Test
    void getTeamTotalPointsAllProjects_Success() {
        createTestTeamAchievement();

        Integer points = achievementService.getTeamTotalPointsAllProjects(team.getId());

        assertEquals(50, points);
    }

    // ===================== DELETE ACHIEVEMENT TESTS =====================

    @Test
    void deleteAchievement_Success() {
        AchievementResponseDTO created = createTestUserAchievement();

        achievementService.deleteAchievement(created.getId());

        assertTrue(achievementService.getAchievementById(created.getId()).isEmpty());
    }

    @Test
    void deleteAchievement_NotFound_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            achievementService.deleteAchievement(999L);
        });
    }

    // ===================== EXISTENCE CHECKS =====================

    @Test
    void userHasBadgeInProject_ReturnsTrue() {
        createTestUserAchievement();

        assertTrue(achievementService.userHasBadgeInProject(student.getId(), manualBadge.getId(), project.getId()));
    }

    @Test
    void userHasBadgeInProject_ReturnsFalse() {
        assertFalse(achievementService.userHasBadgeInProject(student.getId(), manualBadge.getId(), project.getId()));
    }

    @Test
    void teamHasBadge_ReturnsTrue() {
        // Award manualBadge directly to team
        AchievementRequestDTO request = new AchievementRequestDTO(
                "Team achievement",
                manualBadge.getId(),
                null,
                team.getId(),
                project.getId(),
                null,
                teacher.getId());
        achievementService.createAchievement(request);

        assertTrue(achievementService.teamHasBadge(team.getId(), manualBadge.getId()));
    }

    @Test
    void teamHasBadge_ReturnsFalse() {
        assertFalse(achievementService.teamHasBadge(team.getId(), manualBadge.getId()));
    }

    // ===================== USER ACHIEVEMENT STATS TESTS =====================

    @Test
    void getUserAchievementStats_Success() {
        createTestUserAchievement();

        AchievementService.AchievementStatsDTO stats = achievementService.getUserAchievementStats(student.getId());

        assertEquals(50, stats.getTotalPoints());
        assertEquals(1, stats.getIndividualAchievements());
        assertEquals(0, stats.getTeamAchievements());
        assertEquals(1, stats.getTotalAchievements());
    }

    @Test
    void getUserAchievementStats_NoAchievements() {
        AchievementService.AchievementStatsDTO stats = achievementService.getUserAchievementStats(student.getId());

        assertEquals(0, stats.getTotalPoints());
        assertEquals(0, stats.getTotalAchievements());
    }

    // ===================== SPRINT ACHIEVEMENTS TESTS =====================

    @Test
    void getSprintAchievements_ReturnsAchievementsForSprint() {
        // Create a sprint
        Sprint sprint = new Sprint();
        sprint.setName("Sprint 1");
        sprint.setSprintNumber(1);
        sprint.setStartDate(LocalDate.now());
        sprint.setEndDate(LocalDate.now().plusDays(14));
        sprint.setStatus(SprintStatus.IN_PROGRESS);
        sprint.setProject(project);
        sprint = sprintRepository.save(sprint);

        // Create achievement with sprint
        AchievementRequestDTO request = new AchievementRequestDTO(
                "Sprint achievement",
                manualBadge.getId(),
                student.getId(),
                null,
                project.getId(),
                sprint.getId(),
                teacher.getId());
        achievementService.createAchievement(request);

        // Act
        List<AchievementResponseDTO> sprintAchievements = achievementService.getSprintAchievements(sprint.getId());

        // Assert
        assertEquals(1, sprintAchievements.size());
        assertEquals(sprint.getId(), sprintAchievements.get(0).getSprintId());
    }

    @Test
    void getSprintAchievements_EmptySprint_ReturnsEmptyList() {
        // Create a sprint with no achievements
        Sprint sprint = new Sprint();
        sprint.setName("Empty Sprint");
        sprint.setSprintNumber(2);
        sprint.setStartDate(LocalDate.now());
        sprint.setEndDate(LocalDate.now().plusDays(14));
        sprint.setStatus(SprintStatus.PLANNED);
        sprint.setProject(project);
        sprint = sprintRepository.save(sprint);

        // Act
        List<AchievementResponseDTO> sprintAchievements = achievementService.getSprintAchievements(sprint.getId());

        // Assert
        assertTrue(sprintAchievements.isEmpty());
    }

    // ===================== RECENT ACHIEVEMENTS TESTS =====================

    @Test
    void getRecentAchievements_ReturnsLimitedList() {
        // Create 5 achievements
        for (int i = 1; i <= 5; i++) {
            BadgeRequestDTO badgeRequest = new BadgeRequestDTO();
            badgeRequest.setName("Badge " + i);
            badgeRequest.setDescription("Desc " + i);
            badgeRequest.setPoints(i * 10);
            badgeRequest.setBadgeType(BadgeType.MANUAL);
            badgeRequest.setCreatedByUserId(teacher.getId());
            BadgeResponseDTO badge = badgeService.createBadge(badgeRequest);

            AchievementRequestDTO request = new AchievementRequestDTO(
                    "Achievement " + i,
                    badge.getId(),
                    student.getId(),
                    null,
                    project.getId(),
                    null,
                    teacher.getId());
            achievementService.createAchievement(request);
        }

        // Act
        List<AchievementResponseDTO> recent = achievementService.getRecentAchievements(3);

        // Assert
        assertEquals(3, recent.size());
    }

    @Test
    void getRecentAchievements_NoAchievements_ReturnsEmptyList() {
        List<AchievementResponseDTO> recent = achievementService.getRecentAchievements(5);

        assertTrue(recent.isEmpty());
    }

    // ===================== GET ACHIEVEMENT ENTITY TESTS =====================

    @Test
    void getAchievementEntity_Success() {
        AchievementResponseDTO created = createTestUserAchievement();

        Achievement entity = achievementService.getAchievementEntity(created.getId());

        assertNotNull(entity);
        assertEquals(created.getId(), entity.getId());
    }

    @Test
    void getAchievementEntity_NotFound_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            achievementService.getAchievementEntity(999L);
        });
    }

    // ===================== CREATE WITH SPRINT TESTS =====================

    @Test
    void createAchievement_WithSprint_Success() {
        // Create a sprint
        Sprint sprint = new Sprint();
        sprint.setName("Test Sprint");
        sprint.setSprintNumber(3);
        sprint.setStartDate(LocalDate.now());
        sprint.setEndDate(LocalDate.now().plusDays(14));
        sprint.setStatus(SprintStatus.IN_PROGRESS);
        sprint.setProject(project);
        sprint = sprintRepository.save(sprint);

        // Create a new badge for this test
        BadgeRequestDTO badgeRequest = new BadgeRequestDTO();
        badgeRequest.setName("Sprint Badge");
        badgeRequest.setDescription("Sprint Desc");
        badgeRequest.setPoints(75);
        badgeRequest.setBadgeType(BadgeType.MANUAL);
        badgeRequest.setCreatedByUserId(teacher.getId());
        BadgeResponseDTO sprintBadge = badgeService.createBadge(badgeRequest);

        AchievementRequestDTO request = new AchievementRequestDTO(
                "Sprint-related achievement",
                sprintBadge.getId(),
                student.getId(),
                null,
                project.getId(),
                sprint.getId(),
                teacher.getId());

        AchievementResponseDTO response = achievementService.createAchievement(request);

        assertNotNull(response.getId());
        assertEquals(sprint.getId(), response.getSprintId());
        assertEquals("Test Sprint", response.getSprintName());
    }

    // ===================== AUTOMATIC BADGE QUALIFICATION TESTS
    // =====================

    @Test
    void userQualifiesForAutomaticBadge_ReturnsFalse() {
        // This method currently returns false as a placeholder
        boolean result = achievementService.userQualifiesForAutomaticBadge(
                student.getId(), project.getId(), manualBadge.getId());

        assertFalse(result);
    }

    @Test
    void teamQualifiesForAutomaticBadge_ReturnsFalse() {
        // This method currently returns false as a placeholder
        boolean result = achievementService.teamQualifiesForAutomaticBadge(
                team.getId(), manualBadge.getId());

        assertFalse(result);
    }

    // ===================== HELPER METHODS =====================

    private AchievementResponseDTO createTestUserAchievement() {
        AchievementRequestDTO request = new AchievementRequestDTO(
                "Test user achievement",
                manualBadge.getId(),
                student.getId(),
                null,
                project.getId(),
                null,
                teacher.getId());
        return achievementService.createAchievement(request);
    }

    private AchievementResponseDTO createTestTeamAchievement() {
        // Create a second badge for team to avoid duplicate
        BadgeRequestDTO badge2Request = new BadgeRequestDTO();
        badge2Request.setName("Team Star");
        badge2Request.setDescription("Team Excellence");
        badge2Request.setPoints(50);
        badge2Request.setBadgeType(BadgeType.MANUAL);
        badge2Request.setCreatedByUserId(teacher.getId());
        BadgeResponseDTO teamBadge = badgeService.createBadge(badge2Request);

        AchievementRequestDTO request = new AchievementRequestDTO(
                "Test team achievement",
                teamBadge.getId(),
                null,
                team.getId(),
                project.getId(),
                null,
                teacher.getId());
        return achievementService.createAchievement(request);
    }
}
