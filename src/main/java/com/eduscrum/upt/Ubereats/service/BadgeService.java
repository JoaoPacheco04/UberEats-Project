package com.eduscrum.upt.Ubereats.service;

import com.eduscrum.upt.Ubereats.dto.request.BadgeRequestDTO;
import com.eduscrum.upt.Ubereats.dto.response.BadgeResponseDTO;
import com.eduscrum.upt.Ubereats.entity.Badge;
import com.eduscrum.upt.Ubereats.entity.User;
import com.eduscrum.upt.Ubereats.entity.enums.BadgeType;
import com.eduscrum.upt.Ubereats.repository.BadgeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final UserService userService;

    public BadgeService(BadgeRepository badgeRepository, UserService userService) {
        this.badgeRepository = badgeRepository;
        this.userService = userService;
    }

    // === BADGE CREATION ===
    public BadgeResponseDTO createBadge(BadgeRequestDTO requestDTO) {
        // Validate input parameters
        validateBadgeInput(requestDTO);

        // Check for existing badge name
        validateBadgeUniqueness(requestDTO.getName(), null);

        // Create and save new badge
        Badge badge = createBadgeEntity(requestDTO);
        Badge savedBadge = badgeRepository.save(badge);
        return convertToDTO(savedBadge);
    }

    /**
     * Validates all badge input parameters
     */
    private void validateBadgeInput(BadgeRequestDTO requestDTO) {
        if (requestDTO.getName() == null || requestDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Badge name cannot be empty");
        }

        if (requestDTO.getDescription() == null || requestDTO.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Badge description cannot be empty");
        }

        if (requestDTO.getPoints() == null || requestDTO.getPoints() < 0) {
            throw new IllegalArgumentException("Points must be zero or positive");
        }

        if (requestDTO.getBadgeType() == null) {
            throw new IllegalArgumentException("Badge type cannot be null");
        }

        if (requestDTO.getCreatedByUserId() == null) {
            throw new IllegalArgumentException("Created by user ID cannot be null");
        }

        // Validate name length
        if (requestDTO.getName().length() > 100) {
            throw new IllegalArgumentException("Badge name cannot exceed 100 characters");
        }
    }

    /**
     * Checks if badge name already exists
     */
    private void validateBadgeUniqueness(String name, Long excludeId) {
        boolean nameExists;
        if (excludeId != null) {
            nameExists = badgeRepository.existsByNameAndIdNot(name, excludeId);
        } else {
            nameExists = badgeRepository.existsByName(name);
        }

        if (nameExists) {
            throw new IllegalArgumentException("Badge name '" + name + "' is already taken");
        }
    }

    /**
     * Creates a new Badge entity with the provided data
     */
    private Badge createBadgeEntity(BadgeRequestDTO requestDTO) {
        User createdBy = getUserEntity(requestDTO.getCreatedByUserId());

        Badge badge = new Badge(
                requestDTO.getName(),
                requestDTO.getDescription(),
                requestDTO.getPoints(),
                requestDTO.getBadgeType(),
                createdBy
        );

        // Set optional fields
        if (requestDTO.getTriggerCondition() != null) {
            badge.setTriggerCondition(requestDTO.getTriggerCondition());
        }

        if (requestDTO.getIcon() != null) {
            badge.setIcon(requestDTO.getIcon());
        }

        if (requestDTO.getColor() != null) {
            badge.setColor(requestDTO.getColor());
        }

        return badge;
    }

    // === BADGE RETRIEVAL METHODS ===

    /**
     * Finds all badges
     */
    @Transactional(readOnly = true)
    public List<BadgeResponseDTO> getAllBadges() {
        return badgeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Finds badge by ID
     */
    @Transactional(readOnly = true)
    public Optional<BadgeResponseDTO> getBadgeById(Long id) {
        return badgeRepository.findById(id)
                .map(this::convertToDTO);
    }

    /**
     * Finds badge by name
     */
    @Transactional(readOnly = true)
    public Optional<BadgeResponseDTO> getBadgeByName(String name) {
        return badgeRepository.findByName(name)
                .map(this::convertToDTO);
    }

    /**
     * Finds all active badges
     */
    @Transactional(readOnly = true)
    public List<BadgeResponseDTO> getActiveBadges() {
        return badgeRepository.findByIsActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Finds badges by type
     */
    @Transactional(readOnly = true)
    public List<BadgeResponseDTO> getBadgesByType(BadgeType badgeType) {
        return badgeRepository.findByBadgeType(badgeType).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Finds badges by creator
     */
    @Transactional(readOnly = true)
    public List<BadgeResponseDTO> getBadgesByCreator(Long createdById) {
        return badgeRepository.findByCreatedById(createdById).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // === BADGE EXISTENCE CHECKS ===

    /**
     * Checks if badge name already exists
     */
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return badgeRepository.existsByName(name);
    }

    /**
     * Checks if badge exists by ID
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return badgeRepository.existsById(id);
    }

    // === BADGE UPDATE OPERATIONS ===

    /**
     * Updates an existing badge
     */
    public BadgeResponseDTO updateBadge(Long id, BadgeRequestDTO requestDTO) {
        // Validate input
        validateBadgeInput(requestDTO);

        // Check for existing badge name (excluding current badge)
        validateBadgeUniqueness(requestDTO.getName(), id);

        // Get existing badge
        Badge badge = getBadgeEntity(id);

        // Update badge fields
        badge.setName(requestDTO.getName());
        badge.setDescription(requestDTO.getDescription());
        badge.setPoints(requestDTO.getPoints());
        badge.setBadgeType(requestDTO.getBadgeType());
        badge.setTriggerCondition(requestDTO.getTriggerCondition());
        badge.setIcon(requestDTO.getIcon());
        badge.setColor(requestDTO.getColor());

        // Update createdBy if different
        if (!badge.getCreatedBy().getId().equals(requestDTO.getCreatedByUserId())) {
            User newCreatedBy = getUserEntity(requestDTO.getCreatedByUserId());
            badge.setCreatedBy(newCreatedBy);
        }

        Badge updatedBadge = badgeRepository.save(badge);
        return convertToDTO(updatedBadge);
    }

    /**
     * Toggles badge active status
     */
    public BadgeResponseDTO toggleBadgeStatus(Long id) {
        Badge badge = getBadgeEntity(id);
        badge.setIsActive(!badge.getIsActive());
        Badge updatedBadge = badgeRepository.save(badge);
        return convertToDTO(updatedBadge);
    }

    /**
     * Deletes a badge (only if it has no achievements)
     */
    public void deleteBadge(Long id) {
        Badge badge = getBadgeEntity(id);

        // Check if badge has achievements
        if (!badge.getAchievements().isEmpty()) {
            throw new IllegalStateException("Cannot delete badge that has been awarded. Deactivate it instead.");
        }

        badgeRepository.delete(badge);
    }

    // === INTERNAL ENTITY METHODS ===

    /**
     * Gets badge entity by ID (for internal use)
     */
    public Badge getBadgeEntity(Long id) {
        return badgeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Badge not found with id: " + id));
    }

    /**
     * Gets user entity by ID (for internal use)
     */
    private User getUserEntity(Long userId) {
        return userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
    }

    // === AUTOMATIC BADGE METHODS ===

    /**
     * Gets all automatic badges for award checking
     */
    @Transactional(readOnly = true)
    public List<Badge> getAutomaticBadges() {
        return badgeRepository.findByBadgeTypeAndIsActiveTrue(BadgeType.AUTOMATIC);
    }

    /**
     * Gets badge award count
     */
    @Transactional(readOnly = true)
    public Integer getBadgeAwardCount(Long badgeId) {
        return badgeRepository.findById(badgeId)
                .map(badge -> badge.getAchievements().size())
                .orElse(0);
    }

    // === CONVERSION METHODS ===

    /**
     * Converts Badge entity to BadgeResponseDTO
     */
    private BadgeResponseDTO convertToDTO(Badge badge) {
        BadgeResponseDTO dto = new BadgeResponseDTO();
        dto.setId(badge.getId());
        dto.setName(badge.getName());
        dto.setDescription(badge.getDescription());
        dto.setPoints(badge.getPoints());
        dto.setBadgeType(badge.getBadgeType());
        dto.setTriggerCondition(badge.getTriggerCondition());
        dto.setIcon(badge.getIcon());
        dto.setColor(badge.getColor());
        dto.setIsActive(badge.getIsActive());
        dto.setCreatedAt(badge.getCreatedAt());
        dto.setUpdatedAt(badge.getUpdatedAt());
        dto.setAwardCount(badge.getAwardCount());

        if (badge.getCreatedBy() != null) {
            dto.setCreatedByUserId(badge.getCreatedBy().getId());
            dto.setCreatedByName(badge.getCreatedBy().getFullName());
        }

        return dto;
    }

    /**
     * Verifies badge can be awarded (active and meets criteria)
     */
    @Transactional(readOnly = true)
    public boolean canBeAwarded(Long badgeId) {
        return badgeRepository.findById(badgeId)
                .map(Badge::canBeAwarded)
                .orElse(false);
    }
}