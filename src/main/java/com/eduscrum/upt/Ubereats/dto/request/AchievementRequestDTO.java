package com.eduscrum.upt.Ubereats.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for creating an Achievement.
 * Contains achievement details and validation annotations.
 *
 * @version 1.0.1 (2025-12-03)
 */
public class AchievementRequestDTO {

    @NotBlank(message = "Reason is required")
    private String reason;

    @NotNull(message = "Badge ID is required")
    private Long badgeId;

    private Long awardedToUserId;
    private Long awardedToTeamId;

    @NotNull(message = "Project ID is required")
    private Long projectId;

    private Long sprintId;

    private Long awardedByUserId;

    /** Default constructor. */
    public AchievementRequestDTO() {
    }

    public AchievementRequestDTO(String reason, Long badgeId, Long awardedToUserId,
            Long awardedToTeamId, Long projectId, Long sprintId, Long awardedByUserId) {
        this.reason = reason;
        this.badgeId = badgeId;
        this.awardedToUserId = awardedToUserId;
        this.awardedToTeamId = awardedToTeamId;
        this.projectId = projectId;
        this.sprintId = sprintId;
        this.awardedByUserId = awardedByUserId;
    }

    /**
     * Gets the reason for the achievement.
     *
     * @return The achievement reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * Sets the reason for the achievement.
     *
     * @param reason The achievement reason
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * Gets the badge ID.
     *
     * @return The badge ID
     */
    public Long getBadgeId() {
        return badgeId;
    }

    /**
     * Sets the badge ID.
     *
     * @param badgeId The badge ID
     */
    public void setBadgeId(Long badgeId) {
        this.badgeId = badgeId;
    }

    /**
     * Gets the awarded user ID.
     *
     * @return The awarded user ID
     */
    public Long getAwardedToUserId() {
        return awardedToUserId;
    }

    /**
     * Sets the awarded user ID.
     *
     * @param awardedToUserId The awarded user ID
     */
    public void setAwardedToUserId(Long awardedToUserId) {
        this.awardedToUserId = awardedToUserId;
    }

    /**
     * Gets the awarded team ID.
     *
     * @return The awarded team ID
     */
    public Long getAwardedToTeamId() {
        return awardedToTeamId;
    }

    /**
     * Sets the awarded team ID.
     *
     * @param awardedToTeamId The awarded team ID
     */
    public void setAwardedToTeamId(Long awardedToTeamId) {
        this.awardedToTeamId = awardedToTeamId;
    }

    /**
     * Gets the project ID.
     *
     * @return The project ID
     */
    public Long getProjectId() {
        return projectId;
    }

    /**
     * Sets the project ID.
     *
     * @param projectId The project ID
     */
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    /**
     * Gets the sprint ID.
     *
     * @return The sprint ID
     */
    public Long getSprintId() {
        return sprintId;
    }

    /**
     * Sets the sprint ID.
     *
     * @param sprintId The sprint ID
     */
    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    /**
     * Gets the awarder user ID.
     *
     * @return The awarder user ID
     */
    public Long getAwardedByUserId() {
        return awardedByUserId;
    }

    /**
     * Sets the awarder user ID.
     *
     * @param awardedByUserId The awarder user ID
     */
    public void setAwardedByUserId(Long awardedByUserId) {
        this.awardedByUserId = awardedByUserId;
    }
}
