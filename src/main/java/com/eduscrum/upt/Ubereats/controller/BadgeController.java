// BadgeController.java
package com.eduscrum.upt.Ubereats.controller;

import com.eduscrum.upt.Ubereats.dto.request.BadgeRequestDTO;
import com.eduscrum.upt.Ubereats.dto.response.BadgeResponseDTO;
import com.eduscrum.upt.Ubereats.entity.enums.BadgeType;
import com.eduscrum.upt.Ubereats.service.BadgeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/badges")
@CrossOrigin(origins = "*")
public class BadgeController {

    private final BadgeService badgeService;

    public BadgeController(BadgeService badgeService) {
        this.badgeService = badgeService;
    }

    @PostMapping
    public ResponseEntity<?> createBadge(@Valid @RequestBody BadgeRequestDTO requestDTO) {
        try {
            BadgeResponseDTO response = badgeService.createBadge(requestDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to create badge: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping
    public ResponseEntity<List<BadgeResponseDTO>> getAllBadges() {
        List<BadgeResponseDTO> badges = badgeService.getAllBadges();
        return ResponseEntity.ok(badges);
    }

    @GetMapping("/active")
    public ResponseEntity<List<BadgeResponseDTO>> getActiveBadges() {
        List<BadgeResponseDTO> badges = badgeService.getActiveBadges();
        return ResponseEntity.ok(badges);
    }

    @GetMapping("/type/{badgeType}")
    public ResponseEntity<List<BadgeResponseDTO>> getBadgesByType(@PathVariable BadgeType badgeType) {
        List<BadgeResponseDTO> badges = badgeService.getBadgesByType(badgeType);
        return ResponseEntity.ok(badges);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBadgeById(@PathVariable Long id) {
        try {
            BadgeResponseDTO badge = badgeService.getBadgeById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Badge not found"));
            return ResponseEntity.ok(badge);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<?> getBadgeByName(@PathVariable String name) {
        try {
            BadgeResponseDTO badge = badgeService.getBadgeByName(name)
                    .orElseThrow(() -> new IllegalArgumentException("Badge not found"));
            return ResponseEntity.ok(badge);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBadge(@PathVariable Long id, @Valid @RequestBody BadgeRequestDTO requestDTO) {
        try {
            BadgeResponseDTO response = badgeService.updateBadge(id, requestDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to update badge: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> toggleBadgeStatus(@PathVariable Long id) {
        try {
            badgeService.toggleBadgeStatus(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Badge status toggled successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to toggle badge status: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBadge(@PathVariable Long id) {
        try {
            badgeService.deleteBadge(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Badge deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to delete badge: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}