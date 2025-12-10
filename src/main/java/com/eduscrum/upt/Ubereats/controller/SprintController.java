package com.eduscrum.upt.Ubereats.controller;

import com.eduscrum.upt.Ubereats.dto.request.SprintRequestDTO;
import com.eduscrum.upt.Ubereats.dto.response.SprintResponseDTO;
import com.eduscrum.upt.Ubereats.entity.enums.SprintStatus;
import com.eduscrum.upt.Ubereats.service.SprintService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for managing sprints in the EduScrum platform.
 * Provides endpoints for sprint CRUD operations and status transitions.
 *
 * @version 0.6.1 (2025-11-12)
 */
@RestController
@RequestMapping("/api/sprints")
@CrossOrigin(origins = "*")
public class SprintController {

    private final SprintService sprintService;

    /**
     * Constructs a new SprintController with required dependencies.
     *
     * @param sprintService Service for sprint operations
     */
    public SprintController(SprintService sprintService) {
        this.sprintService = sprintService;
    }

    /**
     * Creates a new sprint.
     *
     * @param requestDTO The request containing sprint details
     * @return ResponseEntity containing the created sprint
     */
    @PostMapping
    public ResponseEntity<SprintResponseDTO> createSprint(@Valid @RequestBody SprintRequestDTO requestDTO) {
        SprintResponseDTO createdSprint = sprintService.createSprint(requestDTO);
        return new ResponseEntity<>(createdSprint, HttpStatus.CREATED);
    }

    /**
     * Retrieves all sprints.
     *
     * @return ResponseEntity containing the list of all sprints
     */
    @GetMapping
    public ResponseEntity<List<SprintResponseDTO>> getAllSprints() {
        List<SprintResponseDTO> sprints = sprintService.getAllSprints();
        return ResponseEntity.ok(sprints);
    }

    /**
     * Retrieves a sprint by its ID.
     *
     * @param id The ID of the sprint
     * @return ResponseEntity containing the sprint
     */
    @GetMapping("/{id}")
    public ResponseEntity<SprintResponseDTO> getSprintById(@PathVariable Long id) {
        SprintResponseDTO sprint = sprintService.getSprintById(id);
        return ResponseEntity.ok(sprint);
    }

    /**
     * Retrieves all sprints for a specific project.
     *
     * @param projectId The ID of the project
     * @return ResponseEntity containing the list of sprints
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<SprintResponseDTO>> getSprintsByProject(@PathVariable Long projectId) {
        List<SprintResponseDTO> sprints = sprintService.getSprintsByProject(projectId);
        return ResponseEntity.ok(sprints);
    }

    /**
     * Retrieves all sprints by their status.
     *
     * @param status The sprint status to filter by
     * @return ResponseEntity containing the list of sprints
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<SprintResponseDTO>> getSprintsByStatus(@PathVariable SprintStatus status) {
        List<SprintResponseDTO> sprints = sprintService.getSprintsByStatus(status);
        return ResponseEntity.ok(sprints);
    }

    /**
     * Updates an existing sprint.
     *
     * @param id         The ID of the sprint to update
     * @param requestDTO The request containing updated sprint details
     * @return ResponseEntity containing the updated sprint
     */
    @PutMapping("/{id}")
    public ResponseEntity<SprintResponseDTO> updateSprint(@PathVariable Long id,
            @Valid @RequestBody SprintRequestDTO requestDTO) {
        SprintResponseDTO updatedSprint = sprintService.updateSprint(id, requestDTO);
        return ResponseEntity.ok(updatedSprint);
    }

    /**
     * Starts a sprint by changing its status to IN_PROGRESS.
     *
     * @param id The ID of the sprint to start
     * @return ResponseEntity containing the started sprint
     */
    @PutMapping("/{id}/start")
    public ResponseEntity<SprintResponseDTO> startSprint(@PathVariable Long id) {
        SprintResponseDTO response = sprintService.startSprint(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Completes a sprint by changing its status to COMPLETED.
     *
     * @param id             The ID of the sprint to complete
     * @param completionDate Optional completion date
     * @return ResponseEntity containing the completed sprint
     */
    @PatchMapping("/{id}/complete")
    public ResponseEntity<SprintResponseDTO> completeSprint(
            @PathVariable Long id,
            @RequestParam(required = false) LocalDate completionDate) {
        SprintResponseDTO response = sprintService.completeSprint(id, completionDate);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancels a sprint by changing its status to CANCELLED.
     *
     * @param id The ID of the sprint to cancel
     * @return ResponseEntity containing the cancelled sprint
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<SprintResponseDTO> cancelSprint(@PathVariable Long id) {
        SprintResponseDTO response = sprintService.cancelSprint(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a sprint.
     *
     * @param id The ID of the sprint to delete
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSprint(@PathVariable Long id) {
        sprintService.deleteSprint(id);
        return ResponseEntity.noContent().build();
    }
}
