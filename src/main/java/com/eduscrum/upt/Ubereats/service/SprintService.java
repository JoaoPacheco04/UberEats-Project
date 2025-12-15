package com.eduscrum.upt.Ubereats.service;

import com.eduscrum.upt.Ubereats.dto.request.SprintRequestDTO;
import com.eduscrum.upt.Ubereats.dto.response.SprintResponseDTO;
import com.eduscrum.upt.Ubereats.entity.Sprint;
import com.eduscrum.upt.Ubereats.entity.Project;
import com.eduscrum.upt.Ubereats.entity.enums.SprintStatus;
import com.eduscrum.upt.Ubereats.repository.SprintRepository;
import com.eduscrum.upt.Ubereats.repository.ProjectRepository;
import com.eduscrum.upt.Ubereats.exception.ResourceNotFoundException;
import com.eduscrum.upt.Ubereats.exception.BusinessLogicException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing sprints in the EduScrum platform.
 * Handles sprint creation, updates, status transitions, and velocity
 * calculations.
 *
 * @author Francisco
 * @author Yeswanth Kumar
 * @version 0.2.1 (2025-10-22)
 */
@Service
@Transactional
public class SprintService {

    private final SprintRepository sprintRepository;
    private final ProjectRepository projectRepository;
    private final AchievementService achievementService;
    private final com.eduscrum.upt.Ubereats.repository.UserStoryRepository userStoryRepository;

    /**
     * Constructs a new SprintService with required dependencies.
     *
     * @param sprintRepository    Repository for sprint data access
     * @param projectRepository   Repository for project data access
     * @param achievementService  Service for achievement operations
     * @param userStoryRepository Repository for user story data access
     */
    public SprintService(SprintRepository sprintRepository, ProjectRepository projectRepository,
            @Lazy AchievementService achievementService,
            com.eduscrum.upt.Ubereats.repository.UserStoryRepository userStoryRepository) {
        this.sprintRepository = sprintRepository;
        this.projectRepository = projectRepository;
        this.achievementService = achievementService;
        this.userStoryRepository = userStoryRepository;
    }

    /**
     * Creates a new sprint with validation and uniqueness checks.
     *
     * @param requestDTO The sprint request containing sprint details
     * @return The created sprint as a response DTO
     * @throws BusinessLogicException if validation fails or sprint number exists
     */
    public SprintResponseDTO createSprint(SprintRequestDTO requestDTO) {
        // Validate input parameters
        validateSprintInput(requestDTO);

        // Check for existing sprint number in project
        validateSprintUniqueness(requestDTO.getProjectId(), requestDTO.getSprintNumber(), null);

        // Create and save new sprint
        Sprint sprint = createSprintEntity(requestDTO);
        Sprint savedSprint = sprintRepository.save(sprint);
        return convertToDTO(savedSprint);
    }

    /**
     * Validates all sprint input parameters.
     *
     * @param requestDTO The sprint request to validate
     * @throws BusinessLogicException if any validation fails
     */
    private void validateSprintInput(SprintRequestDTO requestDTO) {
        if (requestDTO.getSprintNumber() == null || requestDTO.getSprintNumber() <= 0) {
            throw new BusinessLogicException("Sprint number must be positive");
        }

        if (requestDTO.getName() == null || requestDTO.getName().trim().isEmpty()) {
            throw new BusinessLogicException("Sprint name cannot be empty");
        }

        if (requestDTO.getStartDate() == null) {
            throw new BusinessLogicException("Start date is required");
        }

        if (requestDTO.getEndDate() == null) {
            throw new BusinessLogicException("End date is required");
        }

        if (requestDTO.getProjectId() == null) {
            throw new BusinessLogicException("Project ID is required");
        }

        // Validate date range
        if (requestDTO.getEndDate().isBefore(requestDTO.getStartDate())) {
            throw new BusinessLogicException("End date cannot be before start date");
        }

        // Validate name length
        if (requestDTO.getName().length() > 100) {
            throw new BusinessLogicException("Sprint name cannot exceed 100 characters");
        }
    }

    /**
     * Checks if sprint number already exists in project.
     *
     * @param projectId    The ID of the project
     * @param sprintNumber The sprint number to check
     * @param excludeId    The ID to exclude from the check (for updates)
     * @throws BusinessLogicException if sprint number already exists
     */
    private void validateSprintUniqueness(Long projectId, Integer sprintNumber, Long excludeId) {
        boolean sprintNumberExists;
        if (excludeId != null) {
            sprintNumberExists = sprintRepository.existsByProjectIdAndSprintNumberAndIdNot(projectId, sprintNumber,
                    excludeId);
        } else {
            sprintNumberExists = sprintRepository.existsByProjectIdAndSprintNumber(projectId, sprintNumber);
        }

        if (sprintNumberExists) {
            throw new BusinessLogicException("Sprint number " + sprintNumber + " already exists in this project");
        }
    }

    /**
     * Creates a new Sprint entity with the provided data.
     *
     * @param requestDTO The sprint request containing sprint details
     * @return The new Sprint entity (not yet persisted)
     */
    private Sprint createSprintEntity(SprintRequestDTO requestDTO) {
        Project project = getProjectEntity(requestDTO.getProjectId());

        Sprint sprint = new Sprint(
                requestDTO.getSprintNumber(),
                requestDTO.getName(),
                requestDTO.getGoal(),
                requestDTO.getStartDate(),
                requestDTO.getEndDate(),
                project);

        // Set status if provided
        if (requestDTO.getStatus() != null) {
            sprint.setStatus(requestDTO.getStatus());
        }

        return sprint;
    }

    /**
     * Finds all sprints in the system.
     *
     * @return List of all sprints as response DTOs
     */
    @Transactional(readOnly = true)
    public List<SprintResponseDTO> getAllSprints() {
        return sprintRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Finds a sprint by its ID.
     *
     * @param id The ID of the sprint to find
     * @return The sprint as a response DTO
     * @throws ResourceNotFoundException if sprint not found
     */
    @Transactional(readOnly = true)
    public SprintResponseDTO getSprintById(Long id) {
        return sprintRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found with id: " + id));
    }

    /**
     * Finds all sprints for a specific project.
     *
     * @param projectId The ID of the project
     * @return List of sprints in the project
     */
    @Transactional(readOnly = true)
    public List<SprintResponseDTO> getSprintsByProject(Long projectId) {
        return sprintRepository.findByProjectId(projectId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Finds all sprints with a specific status.
     *
     * @param status The status to filter by
     * @return List of sprints with the specified status
     */
    @Transactional(readOnly = true)
    public List<SprintResponseDTO> getSprintsByStatus(SprintStatus status) {
        return sprintRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Finds all active sprints in the system.
     *
     * @return List of active sprints
     */
    @Transactional(readOnly = true)
    public List<SprintResponseDTO> getActiveSprints() {
        return sprintRepository.findActiveSprints().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Finds all sprints that are past their end date but not yet completed.
     *
     * @return List of overdue sprints as response DTOs
     */
    @Transactional(readOnly = true)
    public List<SprintResponseDTO> getOverdueSprints() {
        return sprintRepository.findOverdueSprints(LocalDate.now()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the most recent sprint for a specific project.
     *
     * @param projectId The ID of the project
     * @return The latest sprint as a response DTO
     * @throws ResourceNotFoundException if no sprints found for the project
     */
    @Transactional(readOnly = true)
    public SprintResponseDTO getLatestSprintByProject(Long projectId) {
        return sprintRepository.findLatestSprintByProject(projectId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("No sprints found for project id: " + projectId));
    }

    // region SPRINT EXISTENCE CHECKS

    /**
     * Checks if a sprint exists by its ID.
     *
     * @param id The ID of the sprint to check
     * @return true if sprint exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return sprintRepository.existsById(id);
    }

    /**
     * Checks if a sprint number already exists within a project.
     *
     * @param projectId    The ID of the project
     * @param sprintNumber The sprint number to check
     * @return true if sprint number exists in the project, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsByProjectAndSprintNumber(Long projectId, Integer sprintNumber) {
        return sprintRepository.existsByProjectIdAndSprintNumber(projectId, sprintNumber);
    }

    // region SPRINT UPDATE OPERATIONS

    /**
     * Updates an existing sprint with the provided details.
     *
     * @param id         The ID of the sprint to update
     * @param requestDTO The request containing updated sprint details
     * @return The updated sprint as a response DTO
     * @throws BusinessLogicException if validation fails
     */
    public SprintResponseDTO updateSprint(Long id, SprintRequestDTO requestDTO) {
        // Validate input
        validateSprintInput(requestDTO);

        // Check for existing sprint number (excluding current sprint)
        validateSprintUniqueness(requestDTO.getProjectId(), requestDTO.getSprintNumber(), id);

        // Get existing sprint
        Sprint sprint = getSprintEntity(id);

        // Update sprint fields
        sprint.setSprintNumber(requestDTO.getSprintNumber());
        sprint.setName(requestDTO.getName());
        sprint.setGoal(requestDTO.getGoal());
        sprint.setStartDate(requestDTO.getStartDate());
        sprint.setEndDate(requestDTO.getEndDate());
        sprint.setStatus(requestDTO.getStatus());

        // Update project if different
        if (!sprint.getProject().getId().equals(requestDTO.getProjectId())) {
            Project newProject = getProjectEntity(requestDTO.getProjectId());
            sprint.setProject(newProject);
        }

        Sprint updatedSprint = sprintRepository.save(sprint);
        return convertToDTO(updatedSprint);
    }

    /**
     * Starts a sprint by changing its status to IN_PROGRESS.
     *
     * @param id The ID of the sprint to start
     * @return The started sprint as a response DTO
     * @throws BusinessLogicException if sprint cannot be started
     */
    public SprintResponseDTO startSprint(Long id) {
        Sprint sprint = getSprintEntity(id);

        if (!sprint.canBeStarted()) {
            throw new BusinessLogicException(
                    "Sprint cannot be started. It must be in PLANNED status.");
        }

        sprint.setStatus(SprintStatus.IN_PROGRESS);
        Sprint updatedSprint = sprintRepository.save(sprint);
        return convertToDTO(updatedSprint);
    }

    /**
     * Completes a sprint by changing its status to COMPLETED.
     *
     * @param id             The ID of the sprint to complete
     * @param completionDate The date of completion (uses current date if null)
     * @return The completed sprint as a response DTO
     * @throws BusinessLogicException if sprint cannot be completed
     */
    public SprintResponseDTO completeSprint(Long id, LocalDate completionDate, Integer teamMood) {
        Sprint sprint = getSprintEntity(id);

        if (!sprint.canBeCompleted()) {
            throw new BusinessLogicException(
                    "Sprint cannot be completed. It must be in IN_PROGRESS status.");
        }

        sprint.setStatus(SprintStatus.COMPLETED);
        if (completionDate != null) {
            sprint.setCompletedAt(completionDate);
        } else {
            sprint.setCompletedAt(LocalDate.now());
        }

        if (teamMood != null) {
            sprint.setTeamMood(teamMood);
        }

        Sprint updatedSprint = sprintRepository.save(sprint);

        // Trigger automatic badge checks for sprint completion
        achievementService.checkAutomaticTeamBadgesOnSprintCompletion(id);

        return convertToDTO(updatedSprint);
    }

    /**
     * Cancels a sprint by changing its status to CANCELLED.
     * Completed sprints cannot be cancelled (they are locked).
     *
     * @param id The ID of the sprint to cancel
     * @return The cancelled sprint as a response DTO
     * @throws BusinessLogicException if sprint cannot be cancelled
     */
    public SprintResponseDTO cancelSprint(Long id) {
        Sprint sprint = getSprintEntity(id);

        if (!sprint.canBeCancelled()) {
            throw new BusinessLogicException(
                    "Sprint cannot be cancelled. Completed and cancelled sprints are locked.");
        }

        sprint.setStatus(SprintStatus.CANCELLED);
        Sprint updatedSprint = sprintRepository.save(sprint);
        return convertToDTO(updatedSprint);
    }

    /**
     * Deletes a sprint from the system.
     *
     * @param id The ID of the sprint to delete
     * @throws BusinessLogicException if sprint has related data
     */
    public void deleteSprint(Long id) {
        Sprint sprint = getSprintEntity(id);

        // Check if sprint has related data
        if (!sprint.getAchievements().isEmpty() || !sprint.getAnalytics().isEmpty()) {
            throw new BusinessLogicException("Cannot delete sprint that has related data. Cancel it instead.");
        }

        sprintRepository.delete(sprint);
    }

    // region AUTOMATIC SPRINT MANAGEMENT

    /**
     * Automatically starts all sprints that are ready to begin.
     * A sprint is ready when its start date has been reached and status is PLANNED.
     *
     * @return List of sprints that were started
     */
    public List<SprintResponseDTO> startReadySprints() {
        List<Sprint> readySprints = sprintRepository.findSprintsReadyToStart(LocalDate.now());
        readySprints.forEach(sprint -> sprint.setStatus(SprintStatus.IN_PROGRESS));
        List<Sprint> updatedSprints = sprintRepository.saveAll(readySprints);
        return updatedSprints.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Automatically completes all sprints that are ready to finish.
     * A sprint is ready when its end date has been reached and status is
     * IN_PROGRESS.
     *
     * @return List of sprints that were completed
     */
    public List<SprintResponseDTO> completeReadySprints() {
        List<Sprint> readySprints = sprintRepository.findSprintsReadyToComplete(LocalDate.now());
        readySprints.forEach(sprint -> {
            sprint.setStatus(SprintStatus.COMPLETED);
            achievementService.checkAutomaticTeamBadgesOnSprintCompletion(sprint.getId());
        });
        List<Sprint> updatedSprints = sprintRepository.saveAll(readySprints);
        return updatedSprints.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // region BUSINESS LOGIC FOR AWARDS

    /**
     * Checks if all sprints belonging to a given project were completed on or
     * before their planned end date.
     * It relies on the 'updatedAt' timestamp as a proxy for the completion date
     * when status is COMPLETED.
     */
    @Transactional(readOnly = true)
    public boolean checkIfAllSprintsInProjectCompletedOnTime(Long projectId) {
        List<Sprint> sprints = sprintRepository.findByProjectId(projectId);

        if (sprints.isEmpty()) {
            // If there are no sprints, the criterion is trivially met.
            return true;
        }

        for (Sprint sprint : sprints) {
            // 1. Check if the sprint was actually completed
            if (sprint.getStatus() != SprintStatus.COMPLETED) {
                return false; // Fail: A sprint was not completed
            }

            // 2. Check if the completion date was after the planned deadline
            if (sprint.getCompletedAt() != null) {
                if (sprint.getCompletedAt().isAfter(sprint.getEndDate())) {
                    return false; // Fail: Completed late
                }
            } else if (sprint.getUpdatedAt() != null) {
                // Fallback to updatedAt if completedAt is missing (legacy)
                if (sprint.getUpdatedAt().toLocalDate().isAfter(sprint.getEndDate())) {
                    return false; // Fail: Completed late
                }
            } else {
                // If the status is COMPLETED, but dates are null, it indicates an error or
                // inconsistency.
                // We treat it as a failure for the award criteria.
                return false;
            }
        }

        return true; // All sprints were completed and on time.
    }

    // region VELOCITY CALCULATION

    /**
     * Calculates team velocity based on COMPLETED sprints only.
     * Returns the average of completed story points per completed sprint.
     */
    @Transactional(readOnly = true)
    public Double calculateTeamVelocity(Long teamId) {
        List<Object[]> sprintPoints = userStoryRepository.findCompletedPointsByTeamAndCompletedSprints(teamId);

        if (sprintPoints.isEmpty()) {
            return 0.0;
        }

        double totalPoints = 0;
        for (Object[] record : sprintPoints) {
            Long points = (Long) record[1];
            if (points != null) {
                totalPoints += points.doubleValue();
            }
        }

        return totalPoints / sprintPoints.size();
    }

    // region ANALYTICS

    public java.util.Map<String, Integer> getProjectBurndown(Long projectId) {
        List<Sprint> sprints = sprintRepository.findByProjectId(projectId);
        java.util.Map<String, Integer> burndown = new java.util.LinkedHashMap<>();

        for (Sprint sprint : sprints) {
            Integer total = userStoryRepository.sumStoryPointsBySprint(sprint.getId());
            Integer completed = userStoryRepository.sumCompletedStoryPointsBySprint(sprint.getId());
            int remaining = (total != null ? total : 0) - (completed != null ? completed : 0);
            burndown.put(sprint.getName(), remaining);
        }
        return burndown;
    }

    // region INTERNAL ENTITY METHODS

    /**
     * Gets sprint entity by ID (for internal use)
     */
    public Sprint getSprintEntity(Long id) {
        return sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found with id: " + id));
    }

    /**
     * Gets project entity by ID (for internal use)
     */
    public Project getProjectEntity(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
    }

    // region CONVERSION METHODS

    /**
     * Converts Sprint entity to SprintResponseDTO
     */
    private SprintResponseDTO convertToDTO(Sprint sprint) {
        SprintResponseDTO dto = new SprintResponseDTO();
        dto.setId(sprint.getId());
        dto.setSprintNumber(sprint.getSprintNumber());
        dto.setName(sprint.getName());
        dto.setGoal(sprint.getGoal());
        dto.setStartDate(sprint.getStartDate());
        dto.setEndDate(sprint.getEndDate());
        dto.setStatus(sprint.getStatus());
        dto.setCreatedAt(sprint.getCreatedAt());
        dto.setUpdatedAt(sprint.getUpdatedAt());

        // Calculate dynamic properties
        dto.setDurationDays(sprint.getDurationDays());
        dto.setDaysRemaining(sprint.getDaysRemaining());
        dto.setTimeProgressPercentage(sprint.getTimeProgressPercentage());
        dto.setOverdue(sprint.isOverdue());
        dto.setActive(sprint.isActive());
        dto.setCompleted(sprint.isCompleted());
        dto.setStatusDescription(sprint.getStatusDescription());
        dto.setDisplayName(sprint.getDisplayName());

        // Set related entity info
        if (sprint.getProject() != null) {
            dto.setProjectId(sprint.getProject().getId());
            dto.setProjectName(sprint.getProject().getName());
        }

        dto.setTeamMood(sprint.getTeamMood());

        return dto;
    }
}
