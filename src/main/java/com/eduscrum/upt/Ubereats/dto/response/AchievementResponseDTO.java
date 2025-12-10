package com.eduscrum.upt.Ubereats.dto.response;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for achievement response data.
 * Contains award details, badge info, and related entities.
 *
 * @author UberEats
 * @version 0.6.1
 */
public class AchievementResponseDTO {
    private Long id;
    private String reason;
    private LocalDateTime awardedAt;
    private Integer points;
    private String recipientName;
    private String awardedByName;
    private boolean teamAchievement;
    private boolean individualAchievement;
    private boolean automaticAward;

    // Related entity IDs and basic info
    private Long badgeId;
    private String badgeName;
    private String badgeIcon;
    private Long awardedToUserId;
    private Long awardedToTeamId;
    private Long projectId;
    private String projectName;
    private Long sprintId;
    private String sprintName;
    private Long awardedById;

    /** Default constructor. */
    public AchievementResponseDTO() {
    }

    public AchievementResponseDTO(Long id, String reason, LocalDateTime awardedAt, Integer points,
            String recipientName, String awardedByName, boolean teamAchievement,
            boolean individualAchievement, boolean automaticAward, Long badgeId,
            String badgeName, String badgeIcon, Long awardedToUserId,
            Long awardedToTeamId, Long projectId, String projectName,
            Long sprintId, String sprintName, Long awardedById) {
        this.id = id;
        this.reason = reason;
        this.awardedAt = awardedAt;
        this.points = points;
        this.recipientName = recipientName;
        this.awardedByName = awardedByName;
        this.teamAchievement = teamAchievement;
        this.individualAchievement = individualAchievement;
        this.automaticAward = automaticAward;
        this.badgeId = badgeId;
        this.badgeName = badgeName;
        this.badgeIcon = badgeIcon;
        this.awardedToUserId = awardedToUserId;
        this.awardedToTeamId = awardedToTeamId;
        this.projectId = projectId;
        this.projectName = projectName;
        this.sprintId = sprintId;
        this.sprintName = sprintName;
        this.awardedById = awardedById;
    }

    /** @return The achievement ID */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getAwardedAt() {
        return awardedAt;
    }

    public void setAwardedAt(LocalDateTime awardedAt) {
        this.awardedAt = awardedAt;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getAwardedByName() {
        return awardedByName;
    }

    public void setAwardedByName(String awardedByName) {
        this.awardedByName = awardedByName;
    }

    public boolean isTeamAchievement() {
        return teamAchievement;
    }

    public void setTeamAchievement(boolean teamAchievement) {
        this.teamAchievement = teamAchievement;
    }

    public boolean isIndividualAchievement() {
        return individualAchievement;
    }

    public void setIndividualAchievement(boolean individualAchievement) {
        this.individualAchievement = individualAchievement;
    }

    public boolean isAutomaticAward() {
        return automaticAward;
    }

    public void setAutomaticAward(boolean automaticAward) {
        this.automaticAward = automaticAward;
    }

    public Long getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(Long badgeId) {
        this.badgeId = badgeId;
    }

    public String getBadgeName() {
        return badgeName;
    }

    public void setBadgeName(String badgeName) {
        this.badgeName = badgeName;
    }

    public String getBadgeIcon() {
        return badgeIcon;
    }

    public void setBadgeIcon(String badgeIcon) {
        this.badgeIcon = badgeIcon;
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

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public String getSprintName() {
        return sprintName;
    }

    public void setSprintName(String sprintName) {
        this.sprintName = sprintName;
    }

    public Long getAwardedById() {
        return awardedById;
    }

    public void setAwardedById(Long awardedById) {
        this.awardedById = awardedById;
    }
}
