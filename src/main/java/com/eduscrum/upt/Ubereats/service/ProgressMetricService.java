package com.eduscrum.upt.Ubereats.service;

import com.eduscrum.upt.Ubereats.dto.request.ProgressMetricRequestDTO;
import com.eduscrum.upt.Ubereats.dto.response.ProgressMetricResponseDTO;
import com.eduscrum.upt.Ubereats.entity.ProgressMetric;
import com.eduscrum.upt.Ubereats.entity.Sprint;
import com.eduscrum.upt.Ubereats.entity.Team;
import com.eduscrum.upt.Ubereats.repository.ProgressMetricRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProgressMetricService {

    private final ProgressMetricRepository progressMetricRepository;
    private final SprintService sprintService;
    private final TeamService teamService;

    public ProgressMetricService(ProgressMetricRepository progressMetricRepository, SprintService sprintService, TeamService teamService) {
        this.progressMetricRepository = progressMetricRepository;
        this.sprintService = sprintService;
        this.teamService = teamService;
    }

    public ProgressMetricResponseDTO createProgressMetric(ProgressMetricRequestDTO requestDTO) {
        Sprint sprint = sprintService.getSprintEntity(requestDTO.getSprintId());
        Team team = teamService.getTeamById(requestDTO.getTeamId());

        ProgressMetric progressMetric = new ProgressMetric(sprint, team, requestDTO.getRecordedDate());
        progressMetric.setCompletedTasks(requestDTO.getCompletedTasks());
        progressMetric.setTotalTasks(requestDTO.getTotalTasks());
        progressMetric.setStoryPointsCompleted(requestDTO.getStoryPointsCompleted());
        progressMetric.setTotalStoryPoints(requestDTO.getTotalStoryPoints());
        progressMetric.setTeamMood(requestDTO.getTeamMood());
        progressMetric.setNotes(requestDTO.getNotes());

        ProgressMetric savedMetric = progressMetricRepository.save(progressMetric);
        return convertToDTO(savedMetric);
    }

    @Transactional(readOnly = true)
    public List<ProgressMetricResponseDTO> getProgressMetricsBySprintAndTeam(Long sprintId, Long teamId) {
        List<ProgressMetric> metrics = progressMetricRepository.findLatestByTeamAndSprint(teamId, sprintId);
        return metrics.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ProgressMetricResponseDTO convertToDTO(ProgressMetric metric) {
        ProgressMetricResponseDTO dto = new ProgressMetricResponseDTO();
        dto.setId(metric.getId());
        dto.setCompletedTasks(metric.getCompletedTasks());
        dto.setTotalTasks(metric.getTotalTasks());
        dto.setStoryPointsCompleted(metric.getStoryPointsCompleted());
        dto.setTotalStoryPoints(metric.getTotalStoryPoints());
        dto.setVelocity(metric.getVelocity());
        dto.setTeamMood(metric.getTeamMood());
        dto.setNotes(metric.getNotes());
        dto.setRecordedDate(metric.getRecordedDate());
        dto.setCreatedAt(metric.getCreatedAt());

        if (metric.getSprint() != null) {
            dto.setSprintId(metric.getSprint().getId());
            dto.setSprintName(metric.getSprint().getName());
            if (metric.getSprint().getProject() != null) {
                dto.setProjectId(metric.getSprint().getProject().getId());
                dto.setProjectName(metric.getSprint().getProject().getName());
            }
        }

        if (metric.getTeam() != null) {
            dto.setTeamId(metric.getTeam().getId());
            dto.setTeamName(metric.getTeam().getName());
        }

        return dto;
    }
}