// UserStoryController.java
package com.eduscrum.upt.Ubereats.controller;

import com.eduscrum.upt.Ubereats.dto.request.UserStoryRequestDTO;
import com.eduscrum.upt.Ubereats.dto.response.UserStoryResponseDTO;
import com.eduscrum.upt.Ubereats.entity.enums.StoryStatus;
import com.eduscrum.upt.Ubereats.service.UserStoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user-stories")
@CrossOrigin(origins = "*")
public class UserStoryController {

    private final UserStoryService userStoryService;

    public UserStoryController(UserStoryService userStoryService) {
        this.userStoryService = userStoryService;
    }

    @PostMapping
    public ResponseEntity<?> createUserStory(@Valid @RequestBody UserStoryRequestDTO requestDTO) {
        try {
            UserStoryResponseDTO response = userStoryService.createUserStory(requestDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to create user story: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping
    public ResponseEntity<List<UserStoryResponseDTO>> getAllUserStories() {
        List<UserStoryResponseDTO> userStories = userStoryService.getAllUserStories();
        return ResponseEntity.ok(userStories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserStoryById(@PathVariable Long id) {
        try {
            UserStoryResponseDTO userStory = userStoryService.getUserStoryById(id)
                    .orElseThrow(() -> new IllegalArgumentException("User story not found"));
            return ResponseEntity.ok(userStory);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/sprint/{sprintId}")
    public ResponseEntity<List<UserStoryResponseDTO>> getUserStoriesBySprint(@PathVariable Long sprintId) {
        List<UserStoryResponseDTO> userStories = userStoryService.getUserStoriesBySprint(sprintId);
        return ResponseEntity.ok(userStories);
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<UserStoryResponseDTO>> getUserStoriesByTeam(@PathVariable Long teamId) {
        List<UserStoryResponseDTO> userStories = userStoryService.getUserStoriesByTeam(teamId);
        return ResponseEntity.ok(userStories);
    }

    @GetMapping("/assigned/{assignedToId}")
    public ResponseEntity<List<UserStoryResponseDTO>> getUserStoriesByAssignedUser(@PathVariable Long assignedToId) {
        List<UserStoryResponseDTO> userStories = userStoryService.getUserStoriesByAssignedUser(assignedToId);
        return ResponseEntity.ok(userStories);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<UserStoryResponseDTO>> getUserStoriesByStatus(@PathVariable StoryStatus status) {
        List<UserStoryResponseDTO> userStories = userStoryService.getUserStoriesByStatus(status);
        return ResponseEntity.ok(userStories);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserStoryResponseDTO>> getUserStoriesByCriteria(
            @RequestParam(required = false) Long sprintId,
            @RequestParam(required = false) Long teamId,
            @RequestParam(required = false) StoryStatus status,
            @RequestParam(required = false) Long assignedToId) {
        List<UserStoryResponseDTO> userStories = userStoryService.getUserStoriesByCriteria(sprintId, teamId, status, assignedToId);
        return ResponseEntity.ok(userStories);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserStory(@PathVariable Long id, @Valid @RequestBody UserStoryRequestDTO requestDTO) {
        try {
            UserStoryResponseDTO response = userStoryService.updateUserStory(id, requestDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to update user story: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PatchMapping("/{id}/assign/{assignedToId}")
    public ResponseEntity<?> assignUserStory(@PathVariable Long id, @PathVariable Long assignedToId) {
        try {
            UserStoryResponseDTO response = userStoryService.assignUserStory(id, assignedToId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to assign user story: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PatchMapping("/{id}/unassign")
    public ResponseEntity<?> unassignUserStory(@PathVariable Long id) {
        try {
            UserStoryResponseDTO response = userStoryService.unassignUserStory(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to unassign user story: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PatchMapping("/{id}/next-status")
    public ResponseEntity<?> moveToNextStatus(@PathVariable Long id) {
        try {
            UserStoryResponseDTO response = userStoryService.moveToNextStatus(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to move user story to next status: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PatchMapping("/{id}/previous-status")
    public ResponseEntity<?> moveToPreviousStatus(@PathVariable Long id) {
        try {
            UserStoryResponseDTO response = userStoryService.moveToPreviousStatus(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to move user story to previous status: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/sprint/{sprintId}/stats")
    public ResponseEntity<Map<String, Object>> getSprintStats(@PathVariable Long sprintId) {
        try {
            Integer totalPoints = userStoryService.getTotalStoryPointsBySprint(sprintId);
            Integer completedPoints = userStoryService.getCompletedStoryPointsBySprint(sprintId);
            Double completionPercentage = userStoryService.getSprintCompletionPercentage(sprintId);

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalStoryPoints", totalPoints);
            stats.put("completedStoryPoints", completedPoints);
            stats.put("completionPercentage", completionPercentage);
            stats.put("remainingStoryPoints", totalPoints - completedPoints);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>(); // Change to Map<String, Object>
            response.put("message", "Failed to get sprint stats: " + e.getMessage());
            response.put("error", true);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserStory(@PathVariable Long id) {
        try {
            userStoryService.deleteUserStory(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User story deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to delete user story: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
