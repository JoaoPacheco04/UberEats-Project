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

@RestController
@RequestMapping("/api/sprints")
@CrossOrigin(origins = "*")
public class SprintController {

    private final SprintService sprintService;

    public SprintController(SprintService sprintService) {
        this.sprintService = sprintService;
    }

    @PostMapping
    public ResponseEntity<SprintResponseDTO> createSprint(@Valid @RequestBody SprintRequestDTO requestDTO) {
        SprintResponseDTO createdSprint = sprintService.createSprint(requestDTO);
        return new ResponseEntity<>(createdSprint, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SprintResponseDTO>> getAllSprints() {
        List<SprintResponseDTO> sprints = sprintService.getAllSprints();
        return ResponseEntity.ok(sprints);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SprintResponseDTO> getSprintById(@PathVariable Long id) {
        SprintResponseDTO sprint = sprintService.getSprintById(id);
        return ResponseEntity.ok(sprint);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<SprintResponseDTO>> getSprintsByProject(@PathVariable Long projectId) {
        List<SprintResponseDTO> sprints = sprintService.getSprintsByProject(projectId);
        return ResponseEntity.ok(sprints);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<SprintResponseDTO>> getSprintsByStatus(@PathVariable SprintStatus status) {
        List<SprintResponseDTO> sprints = sprintService.getSprintsByStatus(status);
        return ResponseEntity.ok(sprints);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SprintResponseDTO> updateSprint(@PathVariable Long id,
            @Valid @RequestBody SprintRequestDTO requestDTO) {
        SprintResponseDTO updatedSprint = sprintService.updateSprint(id, requestDTO);
        return ResponseEntity.ok(updatedSprint);
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<SprintResponseDTO> startSprint(@PathVariable Long id) {
        SprintResponseDTO response = sprintService.startSprint(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<SprintResponseDTO> completeSprint(
            @PathVariable Long id,
            @RequestParam(required = false) LocalDate completionDate) {
        SprintResponseDTO response = sprintService.completeSprint(id, completionDate);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<SprintResponseDTO> cancelSprint(@PathVariable Long id) {
        SprintResponseDTO response = sprintService.cancelSprint(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSprint(@PathVariable Long id) {
        sprintService.deleteSprint(id);
        return ResponseEntity.noContent().build();
    }
}