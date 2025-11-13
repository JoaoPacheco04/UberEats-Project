// AchievementController.java
package com.eduscrum.upt.Ubereats.controller;

import com.eduscrum.upt.Ubereats.dto.request.AchievementRequestDTO;
import com.eduscrum.upt.Ubereats.dto.response.AchievementResponseDTO;
import com.eduscrum.upt.Ubereats.service.AchievementService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/achievements")
@CrossOrigin(origins = "*")
public class AchievementController {

    private final AchievementService achievementService;

    public AchievementController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @PostMapping
    public ResponseEntity<?> createAchievement(@Valid @RequestBody AchievementRequestDTO requestDTO) {
        try {
            AchievementResponseDTO response = achievementService.createAchievement(requestDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to create achievement: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping
    public ResponseEntity<List<AchievementResponseDTO>> getAllAchievements() {
        List<AchievementResponseDTO> achievements = achievementService.getAllAchievements();
        return ResponseEntity.ok(achievements);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAchievementById(@PathVariable Long id) {
        try {
            AchievementResponseDTO achievement = achievementService.getAchievementById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Achievement not found"));
            return ResponseEntity.ok(achievement);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AchievementResponseDTO>> getUserAchievements(@PathVariable Long userId) {
        List<AchievementResponseDTO> achievements = achievementService.getUserAchievements(userId);
        return ResponseEntity.ok(achievements);
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<AchievementResponseDTO>> getTeamAchievements(@PathVariable Long teamId) {
        List<AchievementResponseDTO> achievements = achievementService.getTeamAchievements(teamId);
        return ResponseEntity.ok(achievements);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<AchievementResponseDTO>> getProjectAchievements(@PathVariable Long projectId) {
        List<AchievementResponseDTO> achievements = achievementService.getProjectAchievements(projectId);
        return ResponseEntity.ok(achievements);
    }

    @GetMapping("/user/{userId}/project/{projectId}/points")
    public ResponseEntity<Map<String, Integer>> getUserPointsInProject(
            @PathVariable Long userId, @PathVariable Long projectId) {
        Integer points = achievementService.getUserTotalPoints(userId, projectId);
        Map<String, Integer> response = new HashMap<>();
        response.put("points", points);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/team/{teamId}/points")
    public ResponseEntity<Map<String, Integer>> getTeamPoints(@PathVariable Long teamId) {
        Integer points = achievementService.getTeamTotalPoints(teamId);
        Map<String, Integer> response = new HashMap<>();
        response.put("points", points);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAchievement(@PathVariable Long id) {
        try {
            achievementService.deleteAchievement(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Achievement deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to delete achievement: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}