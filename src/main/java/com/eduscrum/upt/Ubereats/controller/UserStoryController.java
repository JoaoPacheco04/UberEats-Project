package com.eduscrum.upt.Ubereats.controller;

import com.eduscrum.upt.Ubereats.dto.request.UserStoryRequestDTO;
import com.eduscrum.upt.Ubereats.dto.response.UserStoryResponseDTO;
import com.eduscrum.upt.Ubereats.service.UserStoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing user stories in the EduScrum platform.
 * Provides endpoints for user story CRUD operations and status transitions.
 *
 * @version 0.9.1 (2025-11-28)
 */
@RestController
@RequestMapping("/api/user-stories")
@CrossOrigin(origins = "*")
public class UserStoryController {

    private final UserStoryService userStoryService;

    /**
     * Constructs a new UserStoryController with required dependencies.
     *
     * @param userStoryService Service for user story operations
     */
    public UserStoryController(UserStoryService userStoryService) {
        this.userStoryService = userStoryService;
    }

    @PostMapping
    public ResponseEntity<UserStoryResponseDTO> createUserStory(@Valid @RequestBody UserStoryRequestDTO requestDTO) {
        UserStoryResponseDTO createdUserStory = userStoryService.createUserStory(requestDTO);
        return new ResponseEntity<>(createdUserStory, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserStoryResponseDTO>> getAllUserStories() {
        List<UserStoryResponseDTO> userStories = userStoryService.getAllUserStories();
        return ResponseEntity.ok(userStories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserStoryResponseDTO> getUserStoryById(@PathVariable Long id) {
        UserStoryResponseDTO userStory = userStoryService.getUserStoryById(id)
                .orElseThrow(() -> new IllegalArgumentException("User story not found with id: " + id));
        return ResponseEntity.ok(userStory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserStoryResponseDTO> updateUserStory(@PathVariable Long id,
            @Valid @RequestBody UserStoryRequestDTO requestDTO) {
        UserStoryResponseDTO updatedUserStory = userStoryService.updateUserStory(id, requestDTO);
        return ResponseEntity.ok(updatedUserStory);
    }

    @PutMapping("/{id}/assign/{assignedToId}")
    public ResponseEntity<UserStoryResponseDTO> assignUserStory(@PathVariable Long id,
            @PathVariable Long assignedToId) {
        UserStoryResponseDTO response = userStoryService.assignUserStory(id, assignedToId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/unassign")
    public ResponseEntity<UserStoryResponseDTO> unassignUserStory(@PathVariable Long id) {
        UserStoryResponseDTO response = userStoryService.unassignUserStory(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/next-status")
    public ResponseEntity<UserStoryResponseDTO> moveToNextStatus(@PathVariable Long id) {
        UserStoryResponseDTO response = userStoryService.moveToNextStatus(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/previous-status")
    public ResponseEntity<UserStoryResponseDTO> moveToPreviousStatus(@PathVariable Long id) {
        UserStoryResponseDTO response = userStoryService.moveToPreviousStatus(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserStory(@PathVariable Long id) {
        userStoryService.deleteUserStory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sprint/{sprintId}/stats")
    public ResponseEntity<Map<String, Object>> getSprintStats(@PathVariable Long sprintId) {
        Integer totalPoints = userStoryService.getTotalStoryPointsBySprint(sprintId);
        Integer completedPoints = userStoryService.getCompletedStoryPointsBySprint(sprintId);
        Double completionPercentage = userStoryService.getSprintCompletionPercentage(sprintId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStoryPoints", totalPoints != null ? totalPoints : 0);
        stats.put("completedStoryPoints", completedPoints != null ? completedPoints : 0);
        stats.put("completionPercentage", completionPercentage != null ? completionPercentage : 0.0);
        stats.put("remainingStoryPoints",
                (totalPoints != null ? totalPoints : 0) - (completedPoints != null ? completedPoints : 0));

        return ResponseEntity.ok(stats);
    }
}
