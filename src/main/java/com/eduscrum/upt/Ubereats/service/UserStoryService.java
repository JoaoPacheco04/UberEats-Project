package com.eduscrum.upt.Ubereats.service;

import com.eduscrum.upt.Ubereats.dto.request.UserStoryRequestDTO;
import com.eduscrum.upt.Ubereats.dto.response.UserStoryResponseDTO;
import com.eduscrum.upt.Ubereats.entity.UserStory;
import com.eduscrum.upt.Ubereats.entity.Sprint;
import com.eduscrum.upt.Ubereats.entity.Team;
import com.eduscrum.upt.Ubereats.entity.User;
import com.eduscrum.upt.Ubereats.entity.enums.StoryStatus;
import com.eduscrum.upt.Ubereats.repository.UserStoryRepository;
import com.eduscrum.upt.Ubereats.repository.TeamMemberRepository;
import com.eduscrum.upt.Ubereats.repository.ProjectRepository;
import com.eduscrum.upt.Ubereats.exception.ResourceNotFoundException;
import com.eduscrum.upt.Ubereats.exception.BusinessLogicException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserStoryService {

    private final UserStoryRepository userStoryRepository;
    private final SprintService sprintService;
    private final TeamService teamService;
    private final UserService userService;
    private final TeamMemberRepository teamMemberRepository;
    private final AchievementService achievementService;
    private final ProjectRepository projectRepository;
    private final AnalyticsService analyticsService;

    public UserStoryService(UserStoryRepository userStoryRepository,
            SprintService sprintService,
            TeamService teamService,
            UserService userService,
            TeamMemberRepository teamMemberRepository,
            @Lazy AchievementService achievementService,
            ProjectRepository projectRepository,
            @Lazy AnalyticsService analyticsService) {
        this.userStoryRepository = userStoryRepository;
        this.sprintService = sprintService;
        this.teamService = teamService;
        this.userService = userService;
        this.teamMemberRepository = teamMemberRepository;
        this.achievementService = achievementService;
        this.projectRepository = projectRepository;
        this.analyticsService = analyticsService;
    }

    // === USER STORY CREATION ===
    public UserStoryResponseDTO createUserStory(UserStoryRequestDTO requestDTO) {
        // Validate input parameters
        validateUserStoryInput(requestDTO);

        // Check for existing title in sprint
        validateUserStoryUniqueness(requestDTO.getSprintId(), requestDTO.getTitle(), null);

        // Create and save new user story
        UserStory userStory = createUserStoryEntity(requestDTO);
        UserStory savedUserStory = userStoryRepository.save(userStory);
        updateProjectProgress(savedUserStory.getSprint().getProject().getId());

        // Trigger daily analytics update
        analyticsService.updateDailyAnalytic(savedUserStory.getSprint().getId(), savedUserStory.getTeam().getId());

        return convertToDTO(savedUserStory);
    }

    /**
     * Validates all user story input parameters
     */
    private void validateUserStoryInput(UserStoryRequestDTO requestDTO) {
        if (requestDTO.getTitle() == null || requestDTO.getTitle().trim().isEmpty()) {
            throw new BusinessLogicException("User story title cannot be empty");
        }

        if (requestDTO.getSprintId() == null) {
            throw new BusinessLogicException("Sprint ID is required");
        }

        if (requestDTO.getTeamId() == null) {
            throw new BusinessLogicException("Team ID is required");
        }

        if (requestDTO.getCreatedByUserId() == null) {
            throw new BusinessLogicException("Created by user ID is required");
        }

        if (requestDTO.getStoryPoints() != null && requestDTO.getStoryPoints() < 0) {
            throw new BusinessLogicException("Story points cannot be negative");
        }

        // Validate title length
        if (requestDTO.getTitle().length() > 200) {
            throw new BusinessLogicException("User story title cannot exceed 200 characters");
        }
    }

    /**
     * Checks if user story title already exists in sprint
     */
    private void validateUserStoryUniqueness(Long sprintId, String title, Long excludeId) {
        boolean titleExists;
        if (excludeId != null) {
            titleExists = userStoryRepository.existsBySprintIdAndTitleAndIdNot(sprintId, title, excludeId);
        } else {
            titleExists = userStoryRepository.existsBySprintIdAndTitle(sprintId, title);
        }

        if (titleExists) {
            throw new BusinessLogicException("User story title '" + title + "' already exists in this sprint");
        }
    }

    /**
     * Creates a new UserStory entity with the provided data
     */
    private UserStory createUserStoryEntity(UserStoryRequestDTO requestDTO) {
        Sprint sprint = getSprintEntity(requestDTO.getSprintId());
        Team team = getTeamEntity(requestDTO.getTeamId());
        User createdBy = getUserEntity(requestDTO.getCreatedByUserId());

        UserStory userStory = new UserStory(
                requestDTO.getTitle(),
                requestDTO.getDescription(),
                requestDTO.getStoryPoints(),
                sprint,
                team,
                createdBy);

        // Set optional fields
        if (requestDTO.getStatus() != null) {
            userStory.setStatus(requestDTO.getStatus());
        }

        if (requestDTO.getPriority() != null) {
            userStory.setPriority(requestDTO.getPriority());
        }

        // Assign to user if provided
        if (requestDTO.getAssignedToUserId() != null) {
            User assignedTo = getUserEntity(requestDTO.getAssignedToUserId());
            if (isUserMemberOfTeam(assignedTo.getId(), team.getId())) {
                userStory.assignTo(assignedTo);
            } else {
                throw new BusinessLogicException("User is not a member of the assigned team");
            }
        }

        return userStory;
    }

    // === USER STORY RETRIEVAL METHODS ===

    /**
     * Finds all user stories
     */
    @Transactional(readOnly = true)
    public List<UserStoryResponseDTO> getAllUserStories() {
        return userStoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Finds user story by ID
     */
    @Transactional(readOnly = true)
    public Optional<UserStoryResponseDTO> getUserStoryById(Long id) {
        return userStoryRepository.findById(id)
                .map(this::convertToDTO);
    }

    /**
     * Finds user stories by sprint
     */
    @Transactional(readOnly = true)
    public List<UserStoryResponseDTO> getUserStoriesBySprint(Long sprintId) {
        return userStoryRepository.findBySprintId(sprintId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Finds user stories by team
     */
    @Transactional(readOnly = true)
    public List<UserStoryResponseDTO> getUserStoriesByTeam(Long teamId) {
        return userStoryRepository.findByTeamId(teamId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Finds user stories by assigned user
     */
    @Transactional(readOnly = true)
    public List<UserStoryResponseDTO> getUserStoriesByAssignedUser(Long assignedToId) {
        return userStoryRepository.findByAssignedToId(assignedToId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Finds user stories by status
     */
    @Transactional(readOnly = true)
    public List<UserStoryResponseDTO> getUserStoriesByStatus(StoryStatus status) {
        return userStoryRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Finds user stories by multiple criteria
     */
    @Transactional(readOnly = true)
    public List<UserStoryResponseDTO> getUserStoriesByCriteria(Long sprintId, Long teamId, StoryStatus status,
            Long assignedToId) {
        return userStoryRepository.findByCriteria(sprintId, teamId, status, assignedToId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // === USER STORY EXISTENCE CHECKS ===

    /**
     * Checks if user story exists by ID
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return userStoryRepository.existsById(id);
    }

    // === USER STORY UPDATE OPERATIONS ===

    /**
     * Updates an existing user story
     */
    public UserStoryResponseDTO updateUserStory(Long id, UserStoryRequestDTO requestDTO) {
        // Validate input
        validateUserStoryInput(requestDTO);

        // Check for existing title in sprint (excluding current story)
        validateUserStoryUniqueness(requestDTO.getSprintId(), requestDTO.getTitle(), id);

        // Get existing user story
        UserStory userStory = getUserStoryEntity(id);

        // Update user story fields
        userStory.setTitle(requestDTO.getTitle());
        userStory.setDescription(requestDTO.getDescription());
        userStory.setStoryPoints(requestDTO.getStoryPoints());
        userStory.setStatus(requestDTO.getStatus());
        userStory.setPriority(requestDTO.getPriority());

        // Update sprint if different
        if (!userStory.getSprint().getId().equals(requestDTO.getSprintId())) {
            Sprint newSprint = getSprintEntity(requestDTO.getSprintId());
            userStory.setSprint(newSprint);
        }

        // Update team if different
        if (!userStory.getTeam().getId().equals(requestDTO.getTeamId())) {
            Team newTeam = getTeamEntity(requestDTO.getTeamId());
            userStory.setTeam(newTeam);
        }

        // Update createdBy if different
        if (!userStory.getCreatedBy().getId().equals(requestDTO.getCreatedByUserId())) {
            User newCreatedBy = getUserEntity(requestDTO.getCreatedByUserId());
            userStory.setCreatedBy(newCreatedBy);
        }

        // Update assigned user
        if (requestDTO.getAssignedToUserId() != null) {
            User assignedTo = getUserEntity(requestDTO.getAssignedToUserId());
            Team team = getTeamEntity(requestDTO.getTeamId());
            if (isUserMemberOfTeam(assignedTo.getId(), team.getId())) {
                userStory.assignTo(assignedTo);
            } else {
                throw new BusinessLogicException("User is not a member of the assigned team");
            }
        } else {
            userStory.unassign();
        }

        UserStory updatedUserStory = userStoryRepository.save(userStory);
        updateProjectProgress(updatedUserStory.getSprint().getProject().getId());

        // Trigger daily analytics update
        analyticsService.updateDailyAnalytic(updatedUserStory.getSprint().getId(), updatedUserStory.getTeam().getId());

        return convertToDTO(updatedUserStory);
    }

    /**
     * Assigns user story to a team member
     */
    public UserStoryResponseDTO assignUserStory(Long id, Long assignedToUserId) {
        UserStory userStory = getUserStoryEntity(id);
        User assignedTo = getUserEntity(assignedToUserId);

        // Check if user is member of the team using repository
        if (!isUserMemberOfTeam(assignedTo.getId(), userStory.getTeam().getId())) {
            throw new BusinessLogicException("User is not a member of the team");
        }

        userStory.assignTo(assignedTo);
        UserStory updatedUserStory = userStoryRepository.save(userStory);
        return convertToDTO(updatedUserStory);
    }

    /**
     * Unassigns user story
     */
    public UserStoryResponseDTO unassignUserStory(Long id) {
        UserStory userStory = getUserStoryEntity(id);
        userStory.unassign();
        UserStory updatedUserStory = userStoryRepository.save(userStory);
        return convertToDTO(updatedUserStory);
    }

    /**
     * Moves user story to next status
     */
    public UserStoryResponseDTO moveToNextStatus(Long id) {
        UserStory userStory = getUserStoryEntity(id);

        if (!userStory.canMoveToNextStatus()) {
            throw new BusinessLogicException("User story cannot be moved to next status");
        }

        userStory.moveToNextStatus();
        UserStory updatedUserStory = userStoryRepository.save(userStory);

        // Check for automatic achievements when a story is completed
        if (updatedUserStory.getStatus() == StoryStatus.DONE) {
            achievementService.checkAutomaticTeamBadgesOnSprintCompletion(updatedUserStory.getSprint().getId());
        }

        updateProjectProgress(updatedUserStory.getSprint().getProject().getId());

        // Trigger daily analytics update
        analyticsService.updateDailyAnalytic(updatedUserStory.getSprint().getId(), updatedUserStory.getTeam().getId());

        return convertToDTO(updatedUserStory);
    }

    /**
     * Moves user story to previous status
     */
    public UserStoryResponseDTO moveToPreviousStatus(Long id) {
        UserStory userStory = getUserStoryEntity(id);

        if (!userStory.canMoveToPreviousStatus()) {
            throw new BusinessLogicException("User story cannot be moved to previous status");
        }

        userStory.moveToPreviousStatus();
        UserStory updatedUserStory = userStoryRepository.save(userStory);
        updateProjectProgress(updatedUserStory.getSprint().getProject().getId());

        // Trigger daily analytics update
        analyticsService.updateDailyAnalytic(updatedUserStory.getSprint().getId(), updatedUserStory.getTeam().getId());

        return convertToDTO(updatedUserStory);
    }

    /**
     * Deletes a user story
     */
    public void deleteUserStory(Long id) {
        UserStory userStory = getUserStoryEntity(id);
        Long projectId = userStory.getSprint().getProject().getId();
        Long sprintId = userStory.getSprint().getId();
        Long teamId = userStory.getTeam().getId();
        userStoryRepository.deleteById(id);
        updateProjectProgress(projectId);

        // Trigger daily analytics update
        analyticsService.updateDailyAnalytic(sprintId, teamId);
    }

    // === STATISTICS AND ANALYTICS ===

    /**
     * Gets total story points in sprint
     */
    @Transactional(readOnly = true)
    public Integer getTotalStoryPointsBySprint(Long sprintId) {
        return userStoryRepository.sumStoryPointsBySprint(sprintId);
    }

    /**
     * Gets completed story points in sprint
     */
    @Transactional(readOnly = true)
    public Integer getCompletedStoryPointsBySprint(Long sprintId) {
        return userStoryRepository.sumCompletedStoryPointsBySprint(sprintId);
    }

    /**
     * Gets sprint completion percentage
     */
    @Transactional(readOnly = true)
    public Double getSprintCompletionPercentage(Long sprintId) {
        Integer totalPoints = getTotalStoryPointsBySprint(sprintId);
        Integer completedPoints = getCompletedStoryPointsBySprint(sprintId);

        if (totalPoints == 0)
            return 0.0;
        return (completedPoints.doubleValue() / totalPoints.doubleValue()) * 100.0;
    }

    /**
     * Retrieves completed Story Points per sprint for a specific user within a
     * project.
     * This method is used by AchievementService to calculate consistency
     * (Consistent Contributor badge).
     * Returns a list of arrays: [Sprint ID (Long), Total SPs Completed (Long)]
     */
    @Transactional(readOnly = true)
    public List<Object[]> getCompletedStoryPointsPerSprintInProject(Long userId, Long projectId) {
        // Assume Long instead of Integer for sum, as per repository definition
        // convention
        return userStoryRepository.sumCompletedStoryPointsPerSprintInProject(userId, projectId);
    }

    /**
     * Retrieves the sum of HIGH/CRITICAL Story Points completed by a user in a
     * project.
     */
    @Transactional(readOnly = true)
    public Integer sumHighPriorityCompletedStoryPointsByProject(Long userId, Long projectId) {
        return userStoryRepository.sumHighPriorityCompletedStoryPointsByProject(userId, projectId);
    }

    /**
     * Gets total story points for a specific team in a sprint
     */
    @Transactional(readOnly = true)
    public Integer getTotalStoryPointsBySprintAndTeam(Long sprintId, Long teamId) {
        return userStoryRepository.sumStoryPointsBySprintIdAndTeamId(sprintId, teamId);
    }

    /**
     * Gets completed story points for a specific team in a sprint
     */
    @Transactional(readOnly = true)
    public Integer getCompletedStoryPointsBySprintAndTeam(Long sprintId, Long teamId) {
        return userStoryRepository.sumCompletedStoryPointsBySprintIdAndTeamId(sprintId, teamId);
    }

    // === UTILITY METHODS ===

    private void updateProjectProgress(Long projectId) {
        com.eduscrum.upt.Ubereats.entity.Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        List<Sprint> sprints = sprintService.getSprintsByProject(projectId).stream()
                .map(dto -> sprintService.getSprintEntity(dto.getId()))
                .collect(Collectors.toList());

        double totalPoints = 0;
        double donePoints = 0;

        for (Sprint sprint : sprints) {
            Integer sPoints = getTotalStoryPointsBySprint(sprint.getId());
            Integer dPoints = getCompletedStoryPointsBySprint(sprint.getId());
            if (sPoints != null)
                totalPoints += sPoints;
            if (dPoints != null)
                donePoints += dPoints;
        }

        double progress = (totalPoints > 0) ? (donePoints / totalPoints) * 100.0 : 0.0;
        project.setProgress(progress);
        projectRepository.save(project);
    }

    /**
     * Checks if user is a member of the team
     */
    private boolean isUserMemberOfTeam(Long userId, Long teamId) {
        return teamMemberRepository.findByUserIdAndTeamId(userId, teamId)
                .map(teamMember -> teamMember.getIsActive())
                .orElse(false);
    }

    // === INTERNAL ENTITY METHODS ===

    /**
     * Gets user story entity by ID (for internal use)
     */
    public UserStory getUserStoryEntity(Long id) {
        return userStoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User story not found with id: " + id));
    }

    /**
     * Gets sprint entity by ID (for internal use)
     */
    public Sprint getSprintEntity(Long sprintId) {
        return sprintService.getSprintEntity(sprintId);
    }

    /**
     * Gets team entity by ID (for internal use)
     */
    public Team getTeamEntity(Long teamId) {
        return teamService.getTeamById(teamId);
    }

    /**
     * Gets user entity by ID (for internal use)
     */
    public User getUserEntity(Long userId) {
        return userService.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    // === CONVERSION METHODS ===

    /**
     * Converts UserStory entity to UserStoryResponseDTO
     */
    private UserStoryResponseDTO convertToDTO(UserStory userStory) {
        UserStoryResponseDTO dto = new UserStoryResponseDTO();
        dto.setId(userStory.getId());
        dto.setTitle(userStory.getTitle());
        dto.setDescription(userStory.getDescription());
        dto.setStoryPoints(userStory.getStoryPoints());
        dto.setStatus(userStory.getStatus());
        dto.setPriority(userStory.getPriority());
        dto.setCreatedAt(userStory.getCreatedAt());
        dto.setUpdatedAt(userStory.getUpdatedAt());

        // Calculate dynamic properties
        dto.setCompleted(userStory.isCompleted());
        dto.setInProgress(userStory.isInProgress());
        dto.setInReview(userStory.isInReview());
        dto.setPending(userStory.isPending());
        dto.setStatusColor(userStory.getStatusColor());
        dto.setPriorityColor(userStory.getPriorityColor());
        dto.setPriorityIcon(userStory.getPriorityIcon());
        dto.setAssigned(userStory.isAssigned());
        dto.setAssignedUserName(userStory.getAssignedUserName());
        dto.setEffortLevel(userStory.getEffortLevel());
        dto.setBlocked(userStory.isBlocked());
        dto.setCanMoveToNextStatus(userStory.canMoveToNextStatus());
        dto.setCanMoveToPreviousStatus(userStory.canMoveToPreviousStatus());

        // Set related entity info
        if (userStory.getSprint() != null) {
            dto.setSprintId(userStory.getSprint().getId());
            dto.setSprintName(userStory.getSprint().getName());
        }

        if (userStory.getTeam() != null) {
            dto.setTeamId(userStory.getTeam().getId());
            dto.setTeamName(userStory.getTeam().getName());
        }

        if (userStory.getAssignedTo() != null) {
            dto.setAssignedToUserId(userStory.getAssignedTo().getId());
        }

        if (userStory.getCreatedBy() != null) {
            dto.setCreatedByUserId(userStory.getCreatedBy().getId());
            dto.setCreatedByName(userStory.getCreatedBy().getFullName());
        }

        return dto;
    }
}