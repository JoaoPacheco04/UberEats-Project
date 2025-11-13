package com.eduscrum.upt.Ubereats.service;

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

@Service
@Transactional
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final BadgeService badgeService;
    private final UserService userService;
    private final TeamService teamService;
    private final ProjectRepository projectRepository; // Added to access Project entities directly
    private final SprintService sprintService;

    public AchievementService(AchievementRepository achievementRepository,
                              BadgeService badgeService,
                              UserService userService,
                              TeamService teamService,
                              ProjectRepository projectRepository, // Added
                              SprintService sprintService) {
        this.achievementRepository = achievementRepository;
        this.badgeService = badgeService;
        this.userService = userService;
        this.teamService = teamService;
        this.projectRepository = projectRepository;
        this.sprintService = sprintService;
    }

    // === CRUD OPERATIONS ===

    public AchievementResponseDTO createAchievement(AchievementRequestDTO requestDTO) {
        validateAchievementRequest(requestDTO);

        Badge badge = badgeService.getBadgeEntity(requestDTO.getBadgeId());
        Project project = getProjectEntity(requestDTO.getProjectId());
        User awardedBy = getUserEntity(requestDTO.getAwardedByUserId());

        Achievement achievement;

        if (requestDTO.getAwardedToUserId() != null) {
            // Individual achievement
            User awardedToUser = getUserEntity(requestDTO.getAwardedToUserId());
            achievement = new Achievement(badge, awardedToUser, project, awardedBy, requestDTO.getReason());
        } else if (requestDTO.getAwardedToTeamId() != null) {
            // Team achievement
            Team awardedToTeam = teamService.getTeamById(requestDTO.getAwardedToTeamId());
            achievement = new Achievement(badge, awardedToTeam, project, awardedBy, requestDTO.getReason());
        } else {
            throw new IllegalArgumentException("Either awardedToUserId or awardedToTeamId must be provided");
        }

        // Set sprint if provided
        if (requestDTO.getSprintId() != null) {
            Sprint sprint = sprintService.getSprintEntity(requestDTO.getSprintId());
            achievement.setSprint(sprint);
        }

        Achievement savedAchievement = achievementRepository.save(achievement);
        return convertToDTO(savedAchievement);
    }

    @Transactional(readOnly = true)
    public List<AchievementResponseDTO> getAllAchievements() {
        return achievementRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<AchievementResponseDTO> getAchievementById(Long id) {
        return achievementRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public List<AchievementResponseDTO> getUserAchievements(Long userId) {
        return achievementRepository.findByAwardedToUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AchievementResponseDTO> getTeamAchievements(Long teamId) {
        return achievementRepository.findByAwardedToTeamId(teamId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AchievementResponseDTO> getProjectAchievements(Long projectId) {
        return achievementRepository.findByProjectId(projectId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AchievementResponseDTO> getSprintAchievements(Long sprintId) {
        return achievementRepository.findBySprintId(sprintId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Integer getUserTotalPoints(Long userId, Long projectId) {
        Integer points = achievementRepository.sumUserPointsInProject(userId, projectId);
        return points != null ? points : 0;
    }

    @Transactional(readOnly = true)
    public Integer getTeamTotalPoints(Long teamId) {
        Integer points = achievementRepository.sumTeamPoints(teamId);
        return points != null ? points : 0;
    }

    @Transactional(readOnly = true)
    public Integer getUserTotalPointsAllProjects(Long userId) {
        List<Achievement> userAchievements = achievementRepository.findByAwardedToUserId(userId);
        return userAchievements.stream()
                .mapToInt(Achievement::getPoints)
                .sum();
    }

    @Transactional(readOnly = true)
    public Integer getTeamTotalPointsAllProjects(Long teamId) {
        List<Achievement> teamAchievements = achievementRepository.findByAwardedToTeamId(teamId);
        return teamAchievements.stream()
                .mapToInt(Achievement::getPoints)
                .sum();
    }

    public void deleteAchievement(Long id) {
        if (!achievementRepository.existsById(id)) {
            throw new IllegalArgumentException("Achievement not found with id: " + id);
        }
        achievementRepository.deleteById(id);
    }

    // === ACHIEVEMENT CHECK METHODS ===

    @Transactional(readOnly = true)
    public boolean userHasBadgeInProject(Long userId, Long badgeId, Long projectId) {
        return achievementRepository.existsByUserIdAndBadgeIdAndProjectId(userId, badgeId, projectId);
    }

    @Transactional(readOnly = true)
    public boolean teamHasBadge(Long teamId, Long badgeId) {
        return achievementRepository.existsByTeamIdAndBadgeId(teamId, badgeId);
    }

    @Transactional(readOnly = true)
    public List<AchievementResponseDTO> getRecentAchievements(int limit) {
        return achievementRepository.findLatestAchievements().stream()
                .limit(limit)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // === VALIDATION ===
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

    // === INTERNAL ENTITY METHODS ===

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

    // === CONVERSION METHODS ===
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

    // Internal method for entity operations
    public Achievement getAchievementEntity(Long id) {
        return achievementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Achievement not found with id: " + id));
    }

    // === AUTOMATIC ACHIEVEMENT METHODS ===

    /**
     * Check if user qualifies for automatic achievements based on progress metrics
     */
    @Transactional(readOnly = true)
    public boolean userQualifiesForAutomaticBadge(Long userId, Long projectId, Long badgeId) {
        // Implementation would depend on specific badge conditions
        // This is a placeholder - you would integrate with ProgressMetricService
        // to check if user meets the criteria for automatic badge awarding

        // Example: Check if user has completed all tasks in sprint
        // Example: Check if user has maintained high velocity
        // Example: Check if user has perfect attendance, etc.

        return false; // Implement based on your specific business rules
    }

    /**
     * Check if team qualifies for automatic achievements based on progress metrics
     */
    @Transactional(readOnly = true)
    public boolean teamQualifiesForAutomaticBadge(Long teamId, Long badgeId) {
        // Implementation would depend on specific badge conditions
        // This is a placeholder - you would integrate with ProgressMetricService
        // to check if team meets the criteria for automatic badge awarding

        // Example: Check if team completed sprint early
        // Example: Check if team maintained high morale
        // Example: Check if team exceeded velocity targets

        return false; // Implement based on your specific business rules
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

    // === INNER CLASS FOR STATS DTO ===
    public static class AchievementStatsDTO {
        private final int totalPoints;
        private final long individualAchievements;
        private final long teamAchievements;
        private final long totalAchievements;

        public AchievementStatsDTO(int totalPoints, long individualAchievements, long teamAchievements, long totalAchievements) {
            this.totalPoints = totalPoints;
            this.individualAchievements = individualAchievements;
            this.teamAchievements = teamAchievements;
            this.totalAchievements = totalAchievements;
        }

        // Getters
        public int getTotalPoints() { return totalPoints; }
        public long getIndividualAchievements() { return individualAchievements; }
        public long getTeamAchievements() { return teamAchievements; }
        public long getTotalAchievements() { return totalAchievements; }
    }
}