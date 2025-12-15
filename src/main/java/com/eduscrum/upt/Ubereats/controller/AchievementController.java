package com.eduscrum.upt.Ubereats.controller;

import com.eduscrum.upt.Ubereats.dto.request.AchievementRequestDTO;
import com.eduscrum.upt.Ubereats.dto.response.AchievementResponseDTO;
import com.eduscrum.upt.Ubereats.service.AchievementService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing achievements in the EduScrum platform.
 * Provides endpoints for achievement CRUD operations and point calculations.
 *
 * @author Joao
 * @author Ana
 * @version 0.6.1
 */
@RestController
@RequestMapping("/api/achievements")
@CrossOrigin(origins = "*")
public class AchievementController {

    private final AchievementService achievementService;

    /**
     * Constructs a new AchievementController with required dependencies.
     *
     * @param achievementService Service for achievement operations
     */
    public AchievementController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    /**
     * Creates a new achievement (badge award).
     *
     * @param requestDTO The request containing achievement details
     * @return ResponseEntity containing the created achievement or error message
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
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

    /**
     * Retrieves all achievements in the system.
     *
     * @return ResponseEntity containing the list of all achievements
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public ResponseEntity<List<AchievementResponseDTO>> getAllAchievements() {
        List<AchievementResponseDTO> achievements = achievementService.getAllAchievements();
        return ResponseEntity.ok(achievements);
    }

    /**
     * Retrieves an achievement by its ID.
     *
     * @param id The ID of the achievement to retrieve
     * @return ResponseEntity containing the achievement or error message
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
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

    /**
     * Retrieves all achievements for a specific user.
     *
     * @param userId The ID of the user
     * @return ResponseEntity containing the list of user achievements
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('ROLE_TEACHER') or #userId.toString() == authentication.principal.id.toString()")
    public ResponseEntity<List<AchievementResponseDTO>> getUserAchievements(@PathVariable Long userId) {
        List<AchievementResponseDTO> achievements = achievementService.getUserAchievements(userId);
        return ResponseEntity.ok(achievements);
    }

    /**
     * Retrieves all achievements for a specific team.
     *
     * @param teamId The ID of the team
     * @return ResponseEntity containing the list of team achievements
     */
    @GetMapping("/team/{teamId}")
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public ResponseEntity<List<AchievementResponseDTO>> getTeamAchievements(@PathVariable Long teamId) {
        List<AchievementResponseDTO> achievements = achievementService.getTeamAchievements(teamId);
        return ResponseEntity.ok(achievements);
    }

    /**
     * Retrieves all achievements for a specific project.
     *
     * @param projectId The ID of the project
     * @return ResponseEntity containing the list of project achievements
     */
    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public ResponseEntity<List<AchievementResponseDTO>> getProjectAchievements(@PathVariable Long projectId) {
        List<AchievementResponseDTO> achievements = achievementService.getProjectAchievements(projectId);
        return ResponseEntity.ok(achievements);
    }

    /**
     * Gets the total points for a user in a specific project.
     *
     * @param userId    The ID of the user
     * @param projectId The ID of the project
     * @return ResponseEntity containing the points total
     */
    @GetMapping("/user/{userId}/project/{projectId}/points")
    @PreAuthorize("hasAuthority('ROLE_TEACHER') or #userId.toString() == authentication.principal.id.toString()")
    public ResponseEntity<Map<String, Integer>> getUserPointsInProject(
            @PathVariable Long userId, @PathVariable Long projectId) {
        Integer points = achievementService.getUserTotalPoints(userId, projectId);
        Map<String, Integer> response = new HashMap<>();
        response.put("points", points);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets the total points for a team.
     *
     * @param teamId The ID of the team
     * @return ResponseEntity containing the points total
     */
    @GetMapping("/team/{teamId}/points")
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public ResponseEntity<Map<String, Integer>> getTeamPoints(@PathVariable Long teamId) {
        Integer points = achievementService.getTeamTotalPoints(teamId);
        Map<String, Integer> response = new HashMap<>();
        response.put("points", points);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes an achievement by its ID.
     *
     * @param id The ID of the achievement to delete
     * @return ResponseEntity with success or error message
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
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
