package com.eduscrum.upt.Ubereats.controller;

import com.eduscrum.upt.Ubereats.dto.request.UpdateProfileRequest;
import com.eduscrum.upt.Ubereats.dto.response.UserProfileResponse;
import com.eduscrum.upt.Ubereats.entity.User;
import com.eduscrum.upt.Ubereats.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for user profile operations.
 * Provides endpoints for viewing and updating user profiles.
 *
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Gets a user's profile by ID.
     *
     * @param id The user ID
     * @return ResponseEntity containing the user profile
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = userService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
            return ResponseEntity.ok(new UserProfileResponse(user));
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Updates a user's profile.
     * Note: studentNumber, username, and role cannot be changed.
     *
     * @param id      The user ID
     * @param request The update request containing profile changes
     * @return ResponseEntity containing the updated user profile
     */
    @PutMapping("/{id}/profile")
    public ResponseEntity<?> updateUserProfile(@PathVariable Long id,
            @RequestBody UpdateProfileRequest request) {
        try {
            User updatedUser = userService.updateUserProfile(
                    id,
                    request.getFirstName(),
                    request.getLastName(),
                    request.getEmail(),
                    request.getPassword());
            return ResponseEntity.ok(new UserProfileResponse(updatedUser));
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
