package com.eduscrum.upt.Ubereats.service;

import com.eduscrum.upt.Ubereats.config.AppConstants;

import com.eduscrum.upt.Ubereats.dto.request.AchievementRequestDTO;
import com.eduscrum.upt.Ubereats.dto.response.AchievementResponseDTO;
import com.eduscrum.upt.Ubereats.entity.*;
import com.eduscrum.upt.Ubereats.repository.AchievementRepository;
import com.eduscrum.upt.Ubereats.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

/**
 * Service class for managing achievements in the EduScrum platform.
 * Handles achievement creation, retrieval, and automatic badge awarding logic.
 *
 * @version 0.2.1 (2025-10-22)
 */
@Service
@Transactional
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final BadgeService badgeService;
    private final UserService userService;
    private final TeamService teamService;
    private final ProjectRepository projectRepository;
    private final SprintService sprintService;
    private final UserStoryService userStoryService;

    /**
     * Constructs a new AchievementService with required dependencies.
     *
     * @param achievementRepository Repository for achievement data access
     * @param badgeService          Service for badge operations
     * @param userService           Service for user operations
     * @param teamService           Service for team operations
     * @param projectRepository     Repository for project data access
     * @param sprintService         Service for sprint operations
     * @param userStoryService      Service for user story operations
     */
    public AchievementService(AchievementRepository achievementRepository,
            BadgeService badgeService,
            UserService userService,
            TeamService teamService,
            ProjectRepository projectRepository,
            SprintService sprintService,
            UserStoryService userStoryService) {
        this.achievementRepository = achievementRepository;
        this.badgeService = badgeService;
        this.userService = userService;
        this.teamService = teamService;
        this.projectRepository = projectRepository;
        this.sprintService = sprintService;
        this.userStoryService = userStoryService;
    }

    /**
     * Creates a new achievement for a user or team.
     *
     * @param requestDTO The request containing achievement details
     * @return The created achievement as a response DTO
     * @throws IllegalArgumentException if validation fails
     */
    public AchievementResponseDTO createAchievement(AchievementRequestDTO requestDTO) {
        validateAchievementRequest(requestDTO);

        Badge badge = badgeService.getBadgeEntity(requestDTO.getBadgeId());
        Project project = getProjectEntity(requestDTO.getProjectId());

        // LÓGICA DE AWARDS AUTOMÁTICOS: Handle awardedBy = null
        User awardedBy = null;
        if (requestDTO.getAwardedByUserId() != null) {
            awardedBy = getUserEntity(requestDTO.getAwardedByUserId());
        } else if (badge.isManual()) {
            throw new IllegalArgumentException("Manual badges must be awarded by a user (Teacher).");
        }

        Achievement achievement;

        if (requestDTO.getAwardedToUserId() != null) {
            User awardedToUser = getUserEntity(requestDTO.getAwardedToUserId());
            achievement = new Achievement(badge, awardedToUser, project, awardedBy, requestDTO.getReason());
        } else if (requestDTO.getAwardedToTeamId() != null) {
            Team awardedToTeam = teamService.getTeamById(requestDTO.getAwardedToTeamId());
            achievement = new Achievement(badge, awardedToTeam, project, awardedBy, requestDTO.getReason());
        } else {
            throw new IllegalArgumentException("Either awardedToUserId or awardedToTeamId must be provided");
        }

        if (requestDTO.getSprintId() != null) {
            Sprint sprint = sprintService.getSprintEntity(requestDTO.getSprintId());
            achievement.setSprint(sprint);
        }

        Achievement savedAchievement = achievementRepository.save(achievement);
        return convertToDTO(savedAchievement);
    }

    /**
     * Retrieves all achievements in the system.
     *
     * @return List of all achievements as response DTOs
     */
    @Transactional(readOnly = true)
    public List<AchievementResponseDTO> getAllAchievements() {
        return achievementRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves an achievement by its ID.
     *
     * @param id The ID of the achievement to retrieve
     * @return Optional containing the achievement if found
     */
    @Transactional(readOnly = true)
    public Optional<AchievementResponseDTO> getAchievementById(Long id) {
        return achievementRepository.findById(id)
                .map(this::convertToDTO);
    }

    /**
     * Retrieves all achievements for a specific user.
     *
     * @param userId The ID of the user
     * @return List of achievements awarded to the user
     */
    @Transactional(readOnly = true)
    public List<AchievementResponseDTO> getUserAchievements(Long userId) {
        return achievementRepository.findByAwardedToUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all achievements for a specific team.
     *
     * @param teamId The ID of the team
     * @return List of achievements awarded to the team
     */
    @Transactional(readOnly = true)
    public List<AchievementResponseDTO> getTeamAchievements(Long teamId) {
        return achievementRepository.findByAwardedToTeamId(teamId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all achievements for a specific project.
     *
     * @param projectId The ID of the project
     * @return List of achievements within the project
     */
    @Transactional(readOnly = true)
    public List<AchievementResponseDTO> getProjectAchievements(Long projectId) {
        return achievementRepository.findByProjectId(projectId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all achievements for a specific sprint.
     *
     * @param sprintId The ID of the sprint
     * @return List of achievements within the sprint
     */
    @Transactional(readOnly = true)
    public List<AchievementResponseDTO> getSprintAchievements(Long sprintId) {
        return achievementRepository.findBySprintId(sprintId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Calculates the total points earned by a user in a specific project.
     *
     * @param userId    The ID of the user
     * @param projectId The ID of the project
     * @return Total points earned, defaults to 0 if none
     */
    @Transactional(readOnly = true)
    public Integer getUserTotalPoints(Long userId, Long projectId) {
        Integer points = achievementRepository.sumUserPointsInProject(userId, projectId);
        return points != null ? points : 0;
    }

    /**
     * Calculates the total points earned by a team.
     *
     * @param teamId The ID of the team
     * @return Total points earned, defaults to 0 if none
     */
    @Transactional(readOnly = true)
    public Integer getTeamTotalPoints(Long teamId) {
        Integer points = achievementRepository.sumTeamPoints(teamId);
        return points != null ? points : 0;
    }

    /**
     * Calculates the total points earned by a user across all projects.
     *
     * @param userId The ID of the user
     * @return Total points earned across all projects
     */
    @Transactional(readOnly = true)
    public Integer getUserTotalPointsAllProjects(Long userId) {
        List<Achievement> userAchievements = achievementRepository.findByAwardedToUserId(userId);
        return userAchievements.stream()
                .mapToInt(Achievement::getPoints)
                .sum();
    }

    /**
     * Calculates the total points earned by a team across all projects.
     *
     * @param teamId The ID of the team
     * @return Total points earned across all projects
     */
    @Transactional(readOnly = true)
    public Integer getTeamTotalPointsAllProjects(Long teamId) {
        List<Achievement> teamAchievements = achievementRepository.findByAwardedToTeamId(teamId);
        return teamAchievements.stream()
                .mapToInt(Achievement::getPoints)
                .sum();
    }

    /**
     * Deletes an achievement by its ID.
     *
     * @param id The ID of the achievement to delete
     * @throws IllegalArgumentException if achievement not found
     */
    public void deleteAchievement(Long id) {
        if (!achievementRepository.existsById(id)) {
            throw new IllegalArgumentException("Achievement not found with id: " + id);
        }
        achievementRepository.deleteById(id);
    }

    // region ACHIEVEMENT CHECK METHODS

    /**
     * Checks if a user already has a specific badge in a project.
     *
     * @param userId    The ID of the user
     * @param badgeId   The ID of the badge
     * @param projectId The ID of the project
     * @return true if the user has the badge in the project, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean userHasBadgeInProject(Long userId, Long badgeId, Long projectId) {
        return achievementRepository.existsByUserIdAndBadgeIdAndProjectId(userId, badgeId, projectId);
    }

    /**
     * Checks if a team already has a specific badge.
     *
     * @param teamId  The ID of the team
     * @param badgeId The ID of the badge
     * @return true if the team has the badge, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean teamHasBadge(Long teamId, Long badgeId) {
        return achievementRepository.existsByTeamIdAndBadgeId(teamId, badgeId);
    }

    /**
     * Retrieves the most recent achievements up to a specified limit.
     *
     * @param limit The maximum number of achievements to return
     * @return List of recent achievements
     */
    @Transactional(readOnly = true)
    public List<AchievementResponseDTO> getRecentAchievements(int limit) {
        return achievementRepository.findLatestAchievements().stream()
                .limit(limit)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // region VALIDATION
    private void validateAchievementRequest(AchievementRequestDTO requestDTO) {
        if (requestDTO.getAwardedToUserId() != null && requestDTO.getAwardedToTeamId() != null) {
            throw new IllegalArgumentException("Cannot award to both user and team");
        }

        if (requestDTO.getAwardedToUserId() == null && requestDTO.getAwardedToTeamId() == null) {
            throw new IllegalArgumentException("Must award to either user or team");
        }

        // Check if user already has this badge in this project (for individual awards)
        if (requestDTO.getAwardedToUserId() != null) {
            boolean alreadyHasBadge = achievementRepository.existsByUserIdAndBadgeIdAndProjectId(
                    requestDTO.getAwardedToUserId(), requestDTO.getBadgeId(), requestDTO.getProjectId());
            if (alreadyHasBadge) {
                throw new IllegalArgumentException("User already has this badge in the specified project");
            }
        }

        // Check if team already has this badge
        if (requestDTO.getAwardedToTeamId() != null) {
            boolean alreadyHasBadge = achievementRepository.existsByTeamIdAndBadgeId(
                    requestDTO.getAwardedToTeamId(), requestDTO.getBadgeId());
            if (alreadyHasBadge) {
                throw new IllegalArgumentException("Team already has this badge");
            }
        }
    }

    // region INTERNAL ENTITY METHODS

    /**
     * Gets user entity by ID (for internal use)
     */
    private User getUserEntity(Long userId) {
        return userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
    }

    /**
     * Gets project entity by ID (for internal use)
     * Uses ProjectRepository directly since ProjectService returns DTOs
     */
    private Project getProjectEntity(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + projectId));
    }

    // region CONVERSION METHODS
    private AchievementResponseDTO convertToDTO(Achievement achievement) {
        AchievementResponseDTO dto = new AchievementResponseDTO();
        dto.setId(achievement.getId());
        dto.setReason(achievement.getReason());
        dto.setAwardedAt(achievement.getAwardedAt());
        dto.setPoints(achievement.getPoints());
        dto.setRecipientName(achievement.getRecipientName());
        dto.setAwardedByName(achievement.getAwardedByName());
        dto.setTeamAchievement(achievement.isTeamAchievement());
        dto.setIndividualAchievement(achievement.isIndividualAchievement());
        dto.setAutomaticAward(achievement.isAutomaticAward());

        // Set related entity IDs and names
        if (achievement.getBadge() != null) {
            dto.setBadgeId(achievement.getBadge().getId());
            dto.setBadgeName(achievement.getBadge().getName());
            dto.setBadgeIcon(achievement.getBadge().getIcon());
        }

        if (achievement.getAwardedToUser() != null) {
            dto.setAwardedToUserId(achievement.getAwardedToUser().getId());
        }

        if (achievement.getAwardedToTeam() != null) {
            dto.setAwardedToTeamId(achievement.getAwardedToTeam().getId());
        }

        if (achievement.getProject() != null) {
            dto.setProjectId(achievement.getProject().getId());
            dto.setProjectName(achievement.getProject().getName());
        }

        if (achievement.getSprint() != null) {
            dto.setSprintId(achievement.getSprint().getId());
            dto.setSprintName(achievement.getSprint().getName());
        }

        if (achievement.getAwardedBy() != null) {
            dto.setAwardedById(achievement.getAwardedBy().getId());
        }

        return dto;
    }

    /** Internal method for entity operations. */
    public Achievement getAchievementEntity(Long id) {
        return achievementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Achievement not found with id: " + id));
    }

    // region AUTOMATIC AWARDS IMPLEMENTATION

    // region Awards Sprint Level

    /**
     * Check for and awards automatic team badges when a sprint is completed.
     * (Sprint Master)
     */
    public void checkAutomaticTeamBadgesOnSprintCompletion(Long sprintId) {
        Sprint sprint = sprintService.getSprintEntity(sprintId);
        Long projectId = sprint.getProject().getId();
        List<Team> teams = sprint.getProject().getTeams();

        Badge sprintMasterBadge = badgeService.getBadgeByName("Sprint Master")
                .map(dto -> badgeService.getBadgeEntity(dto.getId()))
                .orElse(null);

        if (sprintMasterBadge == null || !sprintMasterBadge.isAutomatic()) {
            return;
        }

        for (Team team : teams) {
            Long teamId = team.getId();
            Integer totalPoints = userStoryService.getTotalStoryPointsBySprint(sprintId);
            Integer completedPoints = userStoryService.getCompletedStoryPointsBySprint(sprintId);

            if (totalPoints > 0 && completedPoints.equals(totalPoints)) {
                AchievementRequestDTO request = new AchievementRequestDTO(
                        "Concluded 100% of planned Story Points for Sprint " + sprint.getSprintNumber(),
                        sprintMasterBadge.getId(), null, teamId, projectId, sprintId, null);

                try {
                    createAchievement(request);
                } catch (IllegalArgumentException e) {
                    if (!e.getMessage().contains("already has this badge")) {
                        System.err.println(
                                "Error awarding Sprint Master to Team " + team.getName() + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    // region Awards Project Level - Entry Point

    /**
     * Entry point to check for and award all automatic badges related to project
     * conclusion.
     * This method is meant to be triggered when a project changes status to
     * COMPLETED.
     */
    public void checkAutomaticBadgesOnProjectCompletion(Long projectId) {
        // 1. Check Individual Awards
        checkHighImpactDev(projectId);
        checkConsistentContributor(projectId);

        // 2. Check Team Awards
        checkOnTimeLegend(projectId);
        checkProjectMultiplier(projectId);
    }

    // region Awards Project Level - Detailed Logic

    /**
     * Checks if any student qualifies for the "High-Impact Dev" badge in a project.
     * (Individual)
     */
    private void checkHighImpactDev(Long projectId) {
        Project project = getProjectEntity(projectId);

        Badge highImpactDevBadge = badgeService.getBadgeByName("High-Impact Dev")
                .map(dto -> badgeService.getBadgeEntity(dto.getId()))
                .orElse(null);

        if (highImpactDevBadge == null || !highImpactDevBadge.isAutomatic())
            return;

        List<Team> teams = project.getTeams();
        if (teams.isEmpty())
            return;

        Map<Long, Integer> studentImpactScores = new HashMap<>();
        Integer maxScore = 0;

        for (Team team : teams) {
            for (TeamMember member : team.getActiveMembers()) {
                Long userId = member.getUser().getId();
                // Usa a query implementada no UserStoryRepository
                Integer score = userStoryService.sumHighPriorityCompletedStoryPointsByProject(userId, projectId);

                studentImpactScores.put(userId, score);
                if (score > maxScore) {
                    maxScore = score;
                }
            }
        }

        if (maxScore == 0)
            return;

        for (Map.Entry<Long, Integer> entry : studentImpactScores.entrySet()) {
            Long userId = entry.getKey();
            Integer score = entry.getValue();

            if (score.equals(maxScore)) {
                if (achievementRepository.existsByUserIdAndBadgeIdAndProjectId(userId, highImpactDevBadge.getId(),
                        projectId)) {
                    continue;
                }

                AchievementRequestDTO request = new AchievementRequestDTO(
                        "Achieved max High/Critical Story Points (" + maxScore + ") in the project.",
                        highImpactDevBadge.getId(), userId, null, projectId, null, null);

                try {
                    createAchievement(request);
                } catch (IllegalArgumentException e) {
                    if (!e.getMessage().contains("already has this badge")) {
                        System.err.println("Error awarding High-Impact Dev to User " + userId + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * Checks if any student qualifies for the "Consistent Contributor" badge in a
     * project. (Individual)
     * Criteria: Completed >= 8 Story Points in >= 75% of sprints participated in.
     */
    private void checkConsistentContributor(Long projectId) {
        Project project = getProjectEntity(projectId);
        final double MIN_CONSISTENCY_PERCENTAGE = AppConstants.CONSISTENCY_RATIO;
        // MIN_STORY_POINTS is now in AppConstants

        // 1. Get the target Badge entity
        Badge consistentContributorBadge = badgeService.getBadgeByName("Consistent Contributor")
                .map(dto -> badgeService.getBadgeEntity(dto.getId()))
                .orElse(null);

        if (consistentContributorBadge == null || !consistentContributorBadge.isAutomatic())
            return;

        List<Team> teams = project.getTeams();
        if (teams.isEmpty())
            return;

        // Collect all unique active students in the project
        List<User> students = teams.stream()
                .flatMap(team -> team.getActiveMembers().stream())
                .map(TeamMember::getUser)
                .filter(Objects::nonNull) // Ensure user is not null
                .distinct()
                .collect(Collectors.toList());

        for (User student : students) {
            Long userId = student.getId();

            // 2. Retrieve completed Story Points per sprint for this user/project
            List<Object[]> completedSprintsData = userStoryService.getCompletedStoryPointsPerSprintInProject(userId,
                    projectId);

            // Total Sprints in which the user contributed something (total sprints
            // participated)
            int totalSprintsParticipated = completedSprintsData.size();
            int successfulSprints = 0;

            if (totalSprintsParticipated == 0)
                continue; // Skip if user contributed zero completed items

            // 3. Count successful sprints (where SPs >= 8)
            for (Object[] data : completedSprintsData) {
                // data[0] is Sprint ID (Long), data[1] is Total SPs Completed (Long)
                Long storyPointsCompletedLong = (Long) data[1];
                int storyPointsCompleted = storyPointsCompletedLong != null ? storyPointsCompletedLong.intValue() : 0;

                if (storyPointsCompleted >= AppConstants.MIN_STORY_POINTS_FOR_CONSISTENCY) {
                    successfulSprints++;
                }
            }

            // 4. Calculate consistency ratio
            double consistencyRatio = (double) successfulSprints / totalSprintsParticipated;

            // 5. Check Criterion (Ratio >= 75%)
            if (consistencyRatio >= MIN_CONSISTENCY_PERCENTAGE) {

                if (achievementRepository.existsByUserIdAndBadgeIdAndProjectId(userId,
                        consistentContributorBadge.getId(), projectId)) {
                    continue;
                }

                AchievementRequestDTO request = new AchievementRequestDTO(
                        "Achieved " + (int) (consistencyRatio * 100) + "% consistency (Completed >= 8 SPs in "
                                + successfulSprints + " of " + totalSprintsParticipated + " sprints).",
                        consistentContributorBadge.getId(), userId, null, projectId, null, null);

                try {
                    createAchievement(request);
                } catch (IllegalArgumentException e) {
                    if (!e.getMessage().contains("already has this badge")) {
                        System.err.println("Error awarding Consistent Contributor to User " + student.getFullName()
                                + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * Checks if all teams in a project qualify for the "On-Time Legend" badge.
     * (Team)
     */
    private void checkOnTimeLegend(Long projectId) {
        Project project = getProjectEntity(projectId);

        Badge onTimeLegendBadge = badgeService.getBadgeByName("On-Time Legend")
                .map(dto -> badgeService.getBadgeEntity(dto.getId()))
                .orElse(null);

        if (onTimeLegendBadge == null || !onTimeLegendBadge.isAutomatic()) {
            return;
        }

        // Usa o método implementado no SprintService
        boolean allSprintsOnTime = sprintService.checkIfAllSprintsInProjectCompletedOnTime(projectId);

        if (allSprintsOnTime) {
            for (Team team : project.getTeams()) {
                if (achievementRepository.existsByTeamIdAndBadgeId(team.getId(), onTimeLegendBadge.getId())) {
                    continue;
                }

                AchievementRequestDTO request = new AchievementRequestDTO(
                        "All project sprints were completed on or before the deadline.",
                        onTimeLegendBadge.getId(), null, team.getId(), projectId, null, null);

                try {
                    createAchievement(request);
                } catch (IllegalArgumentException e) {
                    if (!e.getMessage().contains("already has this badge")) {
                        System.err.println(
                                "Error awarding On-Time Legend to Team " + team.getName() + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * Checks if a team qualifies for the "Project Multiplier" badge. (Team)
     */
    private void checkProjectMultiplier(Long projectId) {
        Project project = getProjectEntity(projectId);
        Long courseId = project.getCourse().getId();
        final int MIN_PROJECTS_REQUIRED = 3;

        Badge projectMultiplierBadge = badgeService.getBadgeByName("Project Multiplier")
                .map(dto -> badgeService.getBadgeEntity(dto.getId()))
                .orElse(null);

        if (projectMultiplierBadge == null || !projectMultiplierBadge.isAutomatic()) {
            return;
        }

        for (Team team : project.getTeams()) {
            Long teamId = team.getId();

            if (achievementRepository.existsByTeamIdAndBadgeId(teamId, projectMultiplierBadge.getId())) {
                continue;
            }

            // Usa o método implementado no TeamService
            Long completedProjectsCount = teamService.countCompletedProjectsByTeamInCourse(teamId, courseId);

            if (completedProjectsCount >= MIN_PROJECTS_REQUIRED) {

                AchievementRequestDTO request = new AchievementRequestDTO(
                        "Team successfully completed " + completedProjectsCount + " projects in the course.",
                        projectMultiplierBadge.getId(), null, teamId, projectId, null, null);

                try {
                    createAchievement(request);
                } catch (IllegalArgumentException e) {
                    if (!e.getMessage().contains("already has this badge")) {
                        System.err.println(
                                "Error awarding Project Multiplier to Team " + team.getName() + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * Check if user qualifies for automatic achievements based on progress metrics
     */
    @Transactional(readOnly = true)
    public boolean userQualifiesForAutomaticBadge(Long userId, Long projectId, Long badgeId) {
        return false;
    }

    /**
     * Check if team qualifies for automatic achievements based on progress metrics
     */
    @Transactional(readOnly = true)
    public boolean teamQualifiesForAutomaticBadge(Long teamId, Long badgeId) {
        return false;
    }

    /**
     * Get user's achievement statistics
     */
    @Transactional(readOnly = true)
    public AchievementStatsDTO getUserAchievementStats(Long userId) {
        List<Achievement> userAchievements = achievementRepository.findByAwardedToUserId(userId);

        int totalPoints = userAchievements.stream()
                .mapToInt(Achievement::getPoints)
                .sum();

        long individualAchievements = userAchievements.stream()
                .filter(Achievement::isIndividualAchievement)
                .count();

        long teamAchievements = userAchievements.stream()
                .filter(Achievement::isTeamAchievement)
                .count();

        return new AchievementStatsDTO(totalPoints, individualAchievements, teamAchievements, userAchievements.size());
    }

    // region INNER CLASS FOR STATS DTO
    public static class AchievementStatsDTO {
        private final int totalPoints;
        private final long individualAchievements;
        private final long teamAchievements;
        private final long totalAchievements;

        public AchievementStatsDTO(int totalPoints, long individualAchievements, long teamAchievements,
                long totalAchievements) {
            this.totalPoints = totalPoints;
            this.individualAchievements = individualAchievements;
            this.teamAchievements = teamAchievements;
            this.totalAchievements = totalAchievements;
        }

        // Getters
        public int getTotalPoints() {
            return totalPoints;
        }

        public long getIndividualAchievements() {
            return individualAchievements;
        }

        public long getTeamAchievements() {
            return teamAchievements;
        }

        public long getTotalAchievements() {
            return totalAchievements;
        }
    }
}
