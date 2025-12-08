package com.eduscrum.upt.Ubereats.service;

import com.eduscrum.upt.Ubereats.dto.request.SprintRequestDTO;
import com.eduscrum.upt.Ubereats.dto.response.SprintResponseDTO;
import com.eduscrum.upt.Ubereats.entity.Sprint;
import com.eduscrum.upt.Ubereats.entity.Project;
import com.eduscrum.upt.Ubereats.entity.enums.SprintStatus;
import com.eduscrum.upt.Ubereats.repository.SprintRepository;
import com.eduscrum.upt.Ubereats.repository.ProjectRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class SprintService {

    private final SprintRepository sprintRepository;
    private final ProjectRepository projectRepository;
    private final AchievementService achievementService;

    public SprintService(SprintRepository sprintRepository, ProjectRepository projectRepository,
            @Lazy AchievementService achievementService) {
        this.sprintRepository = sprintRepository;
        this.projectRepository = projectRepository;
        this.achievementService = achievementService;
    }

    // === SPRINT CREATION ===
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
     * Validates all sprint input parameters
     */
    private void validateSprintInput(SprintRequestDTO requestDTO) {
        if (requestDTO.getSprintNumber() == null || requestDTO.getSprintNumber() <= 0) {
            throw new IllegalArgumentException("Sprint number must be positive");
        }

        if (requestDTO.getName() == null || requestDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Sprint name cannot be empty");
        }

        if (requestDTO.getStartDate() == null) {
            throw new IllegalArgumentException("Start date is required");
        }

        if (requestDTO.getEndDate() == null) {
            throw new IllegalArgumentException("End date is required");
        }

        if (requestDTO.getProjectId() == null) {
            throw new IllegalArgumentException("Project ID is required");
        }

        // Validate date range
        if (requestDTO.getEndDate().isBefore(requestDTO.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        // Validate name length
        if (requestDTO.getName().length() > 100) {
            throw new IllegalArgumentException("Sprint name cannot exceed 100 characters");
        }
    }

    /**
     * Checks if sprint number already exists in project
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
            throw new IllegalArgumentException("Sprint number " + sprintNumber + " already exists in this project");
        }
    }

    /**
     * Creates a new Sprint entity with the provided data
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

    // === SPRINT RETRIEVAL METHODS ===

    /**
     * Finds all sprints
     */
    @Transactional(readOnly = true)
    public List<SprintResponseDTO> getAllSprints() {
        return sprintRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Finds sprint by ID
     */
    @Transactional(readOnly = true)
    public Optional<SprintResponseDTO> getSprintById(Long id) {
        return sprintRepository.findById(id)
                .map(this::convertToDTO);
    }

    /**
     * Finds sprints by project
     */
    @Transactional(readOnly = true)
    public List<SprintResponseDTO> getSprintsByProject(Long projectId) {
        return sprintRepository.findByProjectId(projectId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Finds sprints by status
     */
    @Transactional(readOnly = true)
    public List<SprintResponseDTO> getSprintsByStatus(SprintStatus status) {
        return sprintRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Finds active sprints
     */
    @Transactional(readOnly = true)
    public List<SprintResponseDTO> getActiveSprints() {
        return sprintRepository.findActiveSprints().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Finds overdue sprints
     */
    @Transactional(readOnly = true)
    public List<SprintResponseDTO> getOverdueSprints() {
        return sprintRepository.findOverdueSprints(LocalDate.now()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Finds latest sprint for project
     */
    @Transactional(readOnly = true)
    public Optional<SprintResponseDTO> getLatestSprintByProject(Long projectId) {
        return sprintRepository.findLatestSprintByProject(projectId)
                .map(this::convertToDTO);
    }

    // === SPRINT EXISTENCE CHECKS ===

    /**
     * Checks if sprint exists by ID
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return sprintRepository.existsById(id);
    }

    /**
     * Checks if sprint number exists in project
     */
    @Transactional(readOnly = true)
    public boolean existsByProjectAndSprintNumber(Long projectId, Integer sprintNumber) {
        return sprintRepository.existsByProjectIdAndSprintNumber(projectId, sprintNumber);
    }

    // === SPRINT UPDATE OPERATIONS ===

    /**
     * Updates an existing sprint
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
     * Starts a sprint (changes status to IN_PROGRESS)
     */
    public SprintResponseDTO startSprint(Long id) {
        Sprint sprint = getSprintEntity(id);

        if (!sprint.canBeStarted()) {
            throw new IllegalStateException(
                    "Sprint cannot be started. It must be in PLANNED status and start date must be reached.");
        }

        sprint.setStatus(SprintStatus.IN_PROGRESS);
        Sprint updatedSprint = sprintRepository.save(sprint);
        return convertToDTO(updatedSprint);
    }

    /**
     * Completes a sprint (changes status to COMPLETED)
     */
    public SprintResponseDTO completeSprint(Long id, LocalDate completionDate) {
        Sprint sprint = getSprintEntity(id);

        if (!sprint.canBeCompleted()) {
            throw new IllegalStateException(
                    "Sprint cannot be completed. It must be in IN_PROGRESS status and end date must be reached.");
        }

        sprint.setStatus(SprintStatus.COMPLETED);
        if (completionDate != null) {
            sprint.setCompletedAt(completionDate);
        } else {
            sprint.setCompletedAt(LocalDate.now());
        }

        Sprint updatedSprint = sprintRepository.save(sprint);

        // Trigger automatic badge checks for sprint completion
        achievementService.checkAutomaticTeamBadgesOnSprintCompletion(id);

        return convertToDTO(updatedSprint);
    }

    /**
     * Cancels a sprint
     */
    public SprintResponseDTO cancelSprint(Long id) {
        Sprint sprint = getSprintEntity(id);
        sprint.setStatus(SprintStatus.CANCELLED);
        Sprint updatedSprint = sprintRepository.save(sprint);
        return convertToDTO(updatedSprint);
    }

    /**
     * Deletes a sprint
     */
    public void deleteSprint(Long id) {
        Sprint sprint = getSprintEntity(id);

        // Check if sprint has related data
        if (!sprint.getAchievements().isEmpty() || !sprint.getProgressMetrics().isEmpty()) {
            throw new IllegalStateException("Cannot delete sprint that has related data. Cancel it instead.");
        }

        sprintRepository.delete(sprint);
    }

    // === AUTOMATIC SPRINT MANAGEMENT ===

    /**
     * Automatically start sprints that are ready
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
     * Automatically complete sprints that are ready
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

    // === BUSINESS LOGIC FOR AWARDS ===

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

    // === INTERNAL ENTITY METHODS ===

    /**
     * Gets sprint entity by ID (for internal use)
     */
    public Sprint getSprintEntity(Long id) {
        return sprintRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sprint not found with id: " + id));
    }

    /**
     * Gets project entity by ID (for internal use)
     */
    public Project getProjectEntity(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + projectId));
    }

    // === CONVERSION METHODS ===

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

        return dto;
    }
}