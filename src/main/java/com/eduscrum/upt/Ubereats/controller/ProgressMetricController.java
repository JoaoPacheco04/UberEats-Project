package com.eduscrum.upt.Ubereats.controller;

import com.eduscrum.upt.Ubereats.dto.request.ProgressMetricRequestDTO;
import com.eduscrum.upt.Ubereats.dto.response.ProgressMetricResponseDTO;
import com.eduscrum.upt.Ubereats.service.ProgressMetricService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progress-metrics")
@CrossOrigin(origins = "*")
public class ProgressMetricController {

    private final ProgressMetricService progressMetricService;

    public ProgressMetricController(ProgressMetricService progressMetricService) {
        this.progressMetricService = progressMetricService;
    }

    @PostMapping
    public ResponseEntity<ProgressMetricResponseDTO> createProgressMetric(@Valid @RequestBody ProgressMetricRequestDTO requestDTO) {
        ProgressMetricResponseDTO createdMetric = progressMetricService.createProgressMetric(requestDTO);
        return new ResponseEntity<>(createdMetric, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProgressMetricResponseDTO>> getProgressMetrics(
            @RequestParam Long sprintId,
            @RequestParam Long teamId) {
        List<ProgressMetricResponseDTO> metrics = progressMetricService.getProgressMetricsBySprintAndTeam(sprintId, teamId);
        return ResponseEntity.ok(metrics);
    }
}