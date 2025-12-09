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

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final SprintService sprintService;

    public AnalyticsController(AnalyticsService analyticsService, SprintService sprintService) {
        this.analyticsService = analyticsService;
        this.sprintService = sprintService;
    }

    @PostMapping
    public ResponseEntity<AnalyticsResponseDTO> createAnalytic(
            @Valid @RequestBody AnalyticsRequestDTO requestDTO) {
        AnalyticsResponseDTO createdAnalytic = analyticsService.createAnalytic(requestDTO);
        return new ResponseEntity<>(createdAnalytic, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AnalyticsResponseDTO>> getAnalytics(
            @RequestParam Long sprintId,
            @RequestParam Long teamId) {
        List<AnalyticsResponseDTO> analytics = analyticsService.getAnalyticsBySprintAndTeam(sprintId, teamId);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/project/{projectId}/burndown")
    public ResponseEntity<Object> getProjectBurndown(@PathVariable Long projectId) {
        // Simple implementation: Map of Sprint Name -> Remaining Points
        // Note: For a real daily burndown, we would need a history table or calculate
        // from history
        // Here we just return the current state of each sprint in the project
        return ResponseEntity.ok(sprintService.getProjectBurndown(projectId));
    }
}