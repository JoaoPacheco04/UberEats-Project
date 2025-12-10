package com.eduscrum.upt.Ubereats.controller;

import com.eduscrum.upt.Ubereats.dto.request.AnalyticsRequestDTO;
import com.eduscrum.upt.Ubereats.dto.response.AnalyticsResponseDTO;
import com.eduscrum.upt.Ubereats.service.AnalyticsService;
import com.eduscrum.upt.Ubereats.service.SprintService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing analytics in the EduScrum platform.
 * Provides endpoints for analytics creation and retrieval.
 *
 * @author UberEats
 * @version 0.8.0
 */
@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final SprintService sprintService;

    /**
     * Constructs a new AnalyticsController with required dependencies.
     *
     * @param analyticsService Service for analytics operations
     * @param sprintService    Service for sprint operations
     */
    public AnalyticsController(AnalyticsService analyticsService, SprintService sprintService) {
        this.analyticsService = analyticsService;
        this.sprintService = sprintService;
    }

    /**
     * Creates a new analytics record.
     *
     * @param requestDTO The request containing analytics details
     * @return ResponseEntity containing the created analytics
     */
    @PostMapping
    public ResponseEntity<AnalyticsResponseDTO> createAnalytic(
            @Valid @RequestBody AnalyticsRequestDTO requestDTO) {
        AnalyticsResponseDTO createdAnalytic = analyticsService.createAnalytic(requestDTO);
        return new ResponseEntity<>(createdAnalytic, HttpStatus.CREATED);
    }

    /**
     * Gets analytics by sprint and team.
     *
     * @param sprintId The ID of the sprint
     * @param teamId   The ID of the team
     * @return ResponseEntity containing the list of analytics
     */
    @GetMapping
    public ResponseEntity<List<AnalyticsResponseDTO>> getAnalytics(
            @RequestParam Long sprintId,
            @RequestParam Long teamId) {
        List<AnalyticsResponseDTO> analytics = analyticsService.getAnalyticsBySprintAndTeam(sprintId, teamId);
        return ResponseEntity.ok(analytics);
    }

    /**
     * Gets project burndown data.
     *
     * @param projectId The ID of the project
     * @return ResponseEntity containing burndown data
     */
    @GetMapping("/project/{projectId}/burndown")
    public ResponseEntity<Object> getProjectBurndown(@PathVariable Long projectId) {
        return ResponseEntity.ok(sprintService.getProjectBurndown(projectId));
    }
}
