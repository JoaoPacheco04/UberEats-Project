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

    // Constructors
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

    // Getters and Setters
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(Long badgeId) {
        this.badgeId = badgeId;
    }

    public Long getAwardedToUserId() {
        return awardedToUserId;
    }

    public void setAwardedToUserId(Long awardedToUserId) {
        this.awardedToUserId = awardedToUserId;
    }

    public Long getAwardedToTeamId() {
        return awardedToTeamId;
    }

    public void setAwardedToTeamId(Long awardedToTeamId) {
        this.awardedToTeamId = awardedToTeamId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public Long getAwardedByUserId() {
        return awardedByUserId;
    }

    public void setAwardedByUserId(Long awardedByUserId) {
        this.awardedByUserId = awardedByUserId;
    }
}
