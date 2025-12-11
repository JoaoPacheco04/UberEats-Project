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

/**
 * Service class for managing user stories in the EduScrum platform.
 * Handles creation, updates, assignment, status transitions, and analytics for
 * user stories.
 *
 * @version 0.8.0 (2025-11-20)
 */
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

    /**
     * Constructs a new UserStoryService with required dependencies.
     *
     * @param userStoryRepository  Repository for user story data access
     * @param sprintService        Service for sprint operations
     * @param teamService          Service for team operations
     * @param userService          Service for user operations
     * @param teamMemberRepository Repository for team member data access
     * @param achievementService   Service for achievement operations
     * @param projectRepository    Repository for project data access
     * @param analyticsService     Service for analytics operations
     */
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

    /**
     * Creates a new user story with validation and uniqueness checks.
     *
     * @param requestDTO The request containing user story details
     * @return The created user story as a response DTO
     * @throws BusinessLogicException if validation fails
     */
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
     * Validates all user story input parameters.
     *
     * @param requestDTO The request to validate
     * @throws BusinessLogicException if any validation fails
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
     * Checks if user story title already exists in sprint.
     *
     * @param sprintId  The sprint ID to check in
     * @param title     The title to check
     * @param excludeId The story ID to exclude (null for new stories)
     * @throws BusinessLogicException if title already exists
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
     * Creates a new UserStory entity with the provided data.
     *
     * @param requestDTO The request containing user story details
     * @return The new UserStory entity (not yet persisted)
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

    // region USER STORY RETRIEVAL METHODS

    /**
     * Finds all user stories.
     *
     * @return List of all user stories as DTOs
     */
    @Transactional(readOnly = true)
    public List<UserStoryResponseDTO> getAllUserStories() {
        return userStoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Finds user story by ID.
     *
     * @param id The user story ID
     * @return Optional containing the user story if found
     */
    @Transactional(readOnly = true)
    public Optional<UserStoryResponseDTO> getUserStoryById(Long id) {
        return userStoryRepository.findById(id)
                .map(this::convertToDTO);
    }

    /**
     * Finds user stories by sprint.
     *
     * @param sprintId The sprint ID to filter by
     * @return List of user stories in the sprint
     */
    @Transactional(readOnly = true)
    public List<UserStoryResponseDTO> getUserStoriesBySprint(Long sprintId) {
        return userStoryRepository.findBySprintId(sprintId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Finds user stories by team.
     *
     * @param teamId The team ID to filter by
     * @return List of user stories for the team
     */
    @Transactional(readOnly = true)
    public List<UserStoryResponseDTO> getUserStoriesByTeam(Long teamId) {
        return userStoryRepository.findByTeamId(teamId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Finds user stories by assigned user.
     *
     * @param assignedToId The user ID to filter by
     * @return List of user stories assigned to the user
     */
    @Transactional(readOnly = true)
    public List<UserStoryResponseDTO> getUserStoriesByAssignedUser(Long assignedToId) {
        return userStoryRepository.findByAssignedToId(assignedToId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Finds user stories by status.
     *
     * @param status The status to filter by
     * @return List of user stories with the given status
     */
    @Transactional(readOnly = true)
    public List<UserStoryResponseDTO> getUserStoriesByStatus(StoryStatus status) {
        return userStoryRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Finds user stories by multiple criteria.
     *
     * @param sprintId     The sprint ID filter (optional)
     * @param teamId       The team ID filter (optional)
     * @param status       The status filter (optional)
     * @param assignedToId The assigned user ID filter (optional)
     * @return List of user stories matching the criteria
     */
    @Transactional(readOnly = true)
    public List<UserStoryResponseDTO> getUserStoriesByCriteria(Long sprintId, Long teamId, StoryStatus status,
            Long assignedToId) {
        return userStoryRepository.findByCriteria(sprintId, teamId, status, assignedToId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // region USER STORY EXISTENCE CHECKS

    /**
     * Checks if user story exists by ID.
     *
     * @param id The user story ID
     * @return true if the user story exists
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return userStoryRepository.existsById(id);
    }

    // region USER STORY UPDATE OPERATIONS

    /**
     * Updates an existing user story.
     *
     * @param id         The user story ID to update
     * @param requestDTO The request containing updated details
     * @return The updated user story as a DTO
     * @throws BusinessLogicException if validation fails
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
     * Assigns user story to a team member.
     *
     * @param id               The user story ID
     * @param assignedToUserId The user ID to assign to
     * @return The updated user story as a DTO
     * @throws BusinessLogicException if user is not a team member
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
     * Unassigns user story.
     *
     * @param id The user story ID
     * @return The updated user story as a DTO
     */
    public UserStoryResponseDTO unassignUserStory(Long id) {
        UserStory userStory = getUserStoryEntity(id);
        userStory.unassign();
        UserStory updatedUserStory = userStoryRepository.save(userStory);
        return convertToDTO(updatedUserStory);
    }

    /**
     * Moves user story to next status.
     *
     * @param id The user story ID
     * @return The updated user story as a DTO
     * @throws BusinessLogicException if story cannot be moved
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
     * Moves user story to previous status.
     *
     * @param id The user story ID
     * @return The updated user story as a DTO
     * @throws BusinessLogicException if story cannot be moved
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
     * Deletes a user story.
     *
     * @param id The user story ID to delete
     * @throws ResourceNotFoundException if story not found
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

    // region STATISTICS AND ANALYTICS

    /**
     * Gets total story points in sprint.
     *
     * @param sprintId The sprint ID
     * @return Total story points in the sprint
     */
    @Transactional(readOnly = true)
    public Integer getTotalStoryPointsBySprint(Long sprintId) {
        return userStoryRepository.sumStoryPointsBySprint(sprintId);
    }

    /**
     * Gets completed story points in sprint.
     *
     * @param sprintId The sprint ID
     * @return Completed story points in the sprint
     */
    @Transactional(readOnly = true)
    public Integer getCompletedStoryPointsBySprint(Long sprintId) {
        return userStoryRepository.sumCompletedStoryPointsBySprint(sprintId);
    }

    /**
     * Gets sprint completion percentage.
     *
     * @param sprintId The sprint ID
     * @return Completion percentage (0-100)
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
     * Used by AchievementService for consistency badge calculation.
     *
     * @param userId    The user ID
     * @param projectId The project ID
     * @return List of [Sprint ID, Total SPs Completed] arrays
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
     *
     * @param userId    The user ID
     * @param projectId The project ID
     * @return Total high-priority story points completed
     */
    @Transactional(readOnly = true)
    public Integer sumHighPriorityCompletedStoryPointsByProject(Long userId, Long projectId) {
        return userStoryRepository.sumHighPriorityCompletedStoryPointsByProject(userId, projectId);
    }

<<<<<<< HEAD
    // region UTILITY METHODS
=======
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
>>>>>>> Yesh_Branch

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
     * Checks if user is a member of the team.
     *
     * @param userId The user ID
     * @param teamId The team ID
     * @return true if user is an active team member
     */
    private boolean isUserMemberOfTeam(Long userId, Long teamId) {
        return teamMemberRepository.findByUserIdAndTeamId(userId, teamId)
                .map(teamMember -> teamMember.getIsActive())
                .orElse(false);
    }

    // region INTERNAL ENTITY METHODS

    /**
     * Gets user story entity by ID (for internal use).
     *
     * @param id The user story ID
     * @return The UserStory entity
     * @throws ResourceNotFoundException if not found
     */
    public UserStory getUserStoryEntity(Long id) {
        return userStoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User story not found with id: " + id));
    }

    /**
     * Gets sprint entity by ID (for internal use).
     *
     * @param sprintId The sprint ID
     * @return The Sprint entity
     */
    public Sprint getSprintEntity(Long sprintId) {
        return sprintService.getSprintEntity(sprintId);
    }

    /**
     * Gets team entity by ID (for internal use).
     *
     * @param teamId The team ID
     * @return The Team entity
     */
    public Team getTeamEntity(Long teamId) {
        return teamService.getTeamById(teamId);
    }

    /**
     * Gets user entity by ID (for internal use).
     *
     * @param userId The user ID
     * @return The User entity
     * @throws ResourceNotFoundException if not found
     */
    public User getUserEntity(Long userId) {
        return userService.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    // region CONVERSION METHODS

    /**
     * Converts UserStory entity to UserStoryResponseDTO.
     *
     * @param userStory The UserStory entity
     * @return The response DTO
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
