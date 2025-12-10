package com.eduscrum.upt.Ubereats.service;

import com.eduscrum.upt.Ubereats.dto.request.AnalyticsRequestDTO;
import com.eduscrum.upt.Ubereats.dto.response.AnalyticsResponseDTO;
import com.eduscrum.upt.Ubereats.entity.Analytic;
import com.eduscrum.upt.Ubereats.entity.Sprint;
import com.eduscrum.upt.Ubereats.entity.Team;
import com.eduscrum.upt.Ubereats.entity.enums.TeamMood;
import com.eduscrum.upt.Ubereats.repository.AnalyticRepository;
import com.eduscrum.upt.Ubereats.repository.UserStoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing analytics data in the EduScrum platform.
 * Handles creation and retrieval of sprint/team progress metrics.
 *
 * @version 0.5.0 (2025-11-05)
 */
@Service
@Transactional
public class AnalyticsService {

    private final AnalyticRepository analyticRepository;
    private final SprintService sprintService;
    private final TeamService teamService;
    private final UserStoryRepository userStoryRepository;

    /**
     * Constructs a new AnalyticsService with required dependencies.
     *
     * @param analyticRepository  Repository for analytic data access
     * @param sprintService       Service for sprint operations
     * @param teamService         Service for team operations
     * @param userStoryRepository Repository for user story data access
     */
    public AnalyticsService(AnalyticRepository analyticRepository,
            SprintService sprintService,
            TeamService teamService,
            UserStoryRepository userStoryRepository) {
        this.analyticRepository = analyticRepository;
        this.sprintService = sprintService;
        this.teamService = teamService;
        this.userStoryRepository = userStoryRepository;
    }

    /**
     * Automatically updates daily analytics when a user story changes.
     * Recalculates stats from database and upserts the daily record.
     *
     * @param sprintId The ID of the sprint
     * @param teamId   The ID of the team
     */
    public void updateDailyAnalytic(Long sprintId, Long teamId) {
        // Fetch fresh counts from DB
        Integer totalTasks = userStoryRepository.countBySprintIdAndTeamId(sprintId, teamId);
        Integer completedTasks = userStoryRepository.countCompletedBySprintIdAndTeamId(sprintId, teamId);
        Integer totalPoints = userStoryRepository.sumStoryPointsBySprintIdAndTeamId(sprintId, teamId);
        Integer completedPoints = userStoryRepository.sumCompletedStoryPointsBySprintIdAndTeamId(sprintId, teamId);

        // Update the analytic record (passing null for manual fields to preserve
        // existing values)
        upsertAnalytic(sprintId, teamId, totalTasks, completedTasks,
                new BigDecimal(totalPoints), new BigDecimal(completedPoints),
                null, null);
    }

    /**
     * Manual Endpoint: Called by Controller (e.g., to set Mood).
     * Re-fetches counts from DB to ensure data consistency (prevents overwriting
     * with 0).
     */
    public AnalyticsResponseDTO createAnalytic(AnalyticsRequestDTO requestDTO) {
        // 1. IGNORE counts from requestDTO.
        // 2. FORCE recalculation from the database.
        Integer totalTasks = userStoryRepository.countBySprintIdAndTeamId(requestDTO.getSprintId(),
                requestDTO.getTeamId());
        Integer completedTasks = userStoryRepository.countCompletedBySprintIdAndTeamId(requestDTO.getSprintId(),
                requestDTO.getTeamId());
        Integer totalPoints = userStoryRepository.sumStoryPointsBySprintIdAndTeamId(requestDTO.getSprintId(),
                requestDTO.getTeamId());
        Integer completedPoints = userStoryRepository
                .sumCompletedStoryPointsBySprintIdAndTeamId(requestDTO.getSprintId(), requestDTO.getTeamId());

        // 3. Pass fresh DB values + Manual Mood/Notes
        Analytic saved = upsertAnalytic(
                requestDTO.getSprintId(),
                requestDTO.getTeamId(),
                totalTasks,
                completedTasks,
                new BigDecimal(totalPoints),
                new BigDecimal(completedPoints),
                requestDTO.getTeamMood(),
                requestDTO.getNotes());
        return convertToDTO(saved);
    }

    /**
     * Helper Method: Updates existing record for today OR creates a new one.
     * This avoids duplicate keys for the same day.
     */
    private Analytic upsertAnalytic(Long sprintId, Long teamId,
            Integer totalTasks, Integer completedTasks,
            BigDecimal totalPoints, BigDecimal completedPoints,
            TeamMood mood, String notes) {
        LocalDate today = LocalDate.now();

        // Check if an entry already exists for Today + Team + Sprint
        Optional<Analytic> existing = analyticRepository.findByTeamIdAndSprintIdAndRecordedDate(teamId, sprintId,
                today);

        Analytic analytic;
        if (existing.isPresent()) {
            analytic = existing.get();
        } else {
            // Create new if not found
            Sprint sprint = sprintService.getSprintEntity(sprintId);
            Team team = teamService.getTeamById(teamId);
            analytic = new Analytic(sprint, team, today);
        }

        // Update Calculated Stats
        analytic.setTotalTasks(totalTasks);
        analytic.setCompletedTasks(completedTasks);
        analytic.setTotalStoryPoints(totalPoints);
        analytic.setStoryPointsCompleted(completedPoints);

        // Update Manual Fields ONLY if provided (not null)
        if (mood != null)
            analytic.setTeamMood(mood);
        if (notes != null)
            analytic.setNotes(notes);

        // Trigger Velocity Calculation (internal entity logic)
        analytic.updateStoryPointsCompletion(completedPoints);

        return analyticRepository.save(analytic);
    }

    @Transactional(readOnly = true)
    public List<AnalyticsResponseDTO> getAnalyticsBySprintAndTeam(Long sprintId, Long teamId) {
        List<Analytic> metrics = analyticRepository.findLatestByTeamAndSprint(teamId, sprintId);
        return metrics.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private AnalyticsResponseDTO convertToDTO(Analytic analytic) {
        AnalyticsResponseDTO dto = new AnalyticsResponseDTO();
        dto.setId(analytic.getId());
        dto.setCompletedTasks(analytic.getCompletedTasks());
        dto.setTotalTasks(analytic.getTotalTasks());
        dto.setStoryPointsCompleted(analytic.getStoryPointsCompleted());
        dto.setTotalStoryPoints(analytic.getTotalStoryPoints());
        dto.setVelocity(analytic.getVelocity());
        dto.setTeamMood(analytic.getTeamMood());
        dto.setNotes(analytic.getNotes());
        dto.setRecordedDate(analytic.getRecordedDate());
        dto.setCreatedAt(analytic.getCreatedAt());
        dto.setId(analytic.getId());
        dto.setCompletedTasks(analytic.getCompletedTasks());

        // Mapped calculated fields
        dto.setCompletionPercentage(analytic.getCompletionPercentage());
        dto.setRemainingTasks(analytic.getRemainingTasks());
        dto.setStoryPointsCompletion(analytic.getStoryPointsCompletion());
        dto.setRemainingStoryPoints(analytic.getRemainingStoryPoints());
        dto.setOnTrack(analytic.isOnTrack());
        dto.setBurnDownDataMap(analytic.getBurnDownDataMap());
        dto.setAllTasksCompleted(analytic.isAllTasksCompleted());
        dto.setAllStoryPointsCompleted(analytic.isAllStoryPointsCompleted());
        dto.setCompletionPercentage(analytic.getCompletionPercentage());
        dto.setProgressStatus(analytic.getProgressStatus());

        if (analytic.getSprint() != null) {
            dto.setSprintId(analytic.getSprint().getId());
            dto.setSprintName(analytic.getSprint().getName());
            if (analytic.getSprint().getProject() != null) {
                dto.setProjectId(analytic.getSprint().getProject().getId());
                dto.setProjectName(analytic.getSprint().getProject().getName());
            }
        }

        if (analytic.getTeam() != null) {
            dto.setTeamId(analytic.getTeam().getId());
            dto.setTeamName(analytic.getTeam().getName());
        }

        return dto;
    }
}
