// SprintController.java
package com.eduscrum.upt.Ubereats.controller;

import com.eduscrum.upt.Ubereats.dto.request.SprintRequestDTO;
import com.eduscrum.upt.Ubereats.dto.response.SprintResponseDTO;
import com.eduscrum.upt.Ubereats.entity.enums.SprintStatus;
import com.eduscrum.upt.Ubereats.service.SprintService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sprints")
@CrossOrigin(origins = "*")
public class SprintController {

    private final SprintService sprintService;

    public SprintController(SprintService sprintService) {
        this.sprintService = sprintService;
    }

    @PostMapping
    public ResponseEntity<?> createSprint(@Valid @RequestBody SprintRequestDTO requestDTO) {
        try {
            SprintResponseDTO response = sprintService.createSprint(requestDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to create sprint: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping
    public ResponseEntity<List<SprintResponseDTO>> getAllSprints() {
        List<SprintResponseDTO> sprints = sprintService.getAllSprints();
        return ResponseEntity.ok(sprints);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSprintById(@PathVariable Long id) {
        try {
            SprintResponseDTO sprint = sprintService.getSprintById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Sprint not found"));
            return ResponseEntity.ok(sprint);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
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

    @GetMapping("/active")
    public ResponseEntity<List<SprintResponseDTO>> getActiveSprints() {
        List<SprintResponseDTO> sprints = sprintService.getActiveSprints();
        return ResponseEntity.ok(sprints);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<SprintResponseDTO>> getOverdueSprints() {
        List<SprintResponseDTO> sprints = sprintService.getOverdueSprints();
        return ResponseEntity.ok(sprints);
    }

    @GetMapping("/project/{projectId}/latest")
    public ResponseEntity<?> getLatestSprintByProject(@PathVariable Long projectId) {
        try {
            SprintResponseDTO sprint = sprintService.getLatestSprintByProject(projectId)
                    .orElseThrow(() -> new IllegalArgumentException("No sprints found for project"));
            return ResponseEntity.ok(sprint);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSprint(@PathVariable Long id, @Valid @RequestBody SprintRequestDTO requestDTO) {
        try {
            SprintResponseDTO response = sprintService.updateSprint(id, requestDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to update sprint: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PatchMapping("/{id}/start")
    public ResponseEntity<?> startSprint(@PathVariable Long id) {
        try {
            SprintResponseDTO response = sprintService.startSprint(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to start sprint: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<?> completeSprint(@PathVariable Long id) {
        try {
            SprintResponseDTO response = sprintService.completeSprint(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to complete sprint: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancelSprint(@PathVariable Long id) {
        try {
            SprintResponseDTO response = sprintService.cancelSprint(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to cancel sprint: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/auto-start")
    public ResponseEntity<List<SprintResponseDTO>> autoStartReadySprints() {
        List<SprintResponseDTO> startedSprints = sprintService.startReadySprints();
        return ResponseEntity.ok(startedSprints);
    }

    @PostMapping("/auto-complete")
    public ResponseEntity<List<SprintResponseDTO>> autoCompleteReadySprints() {
        List<SprintResponseDTO> completedSprints = sprintService.completeReadySprints();
        return ResponseEntity.ok(completedSprints);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSprint(@PathVariable Long id) {
        try {
            sprintService.deleteSprint(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Sprint deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to delete sprint: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}