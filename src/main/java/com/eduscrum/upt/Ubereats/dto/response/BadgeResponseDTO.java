package com.eduscrum.upt.Ubereats.dto.response;

import com.eduscrum.upt.Ubereats.entity.enums.BadgeType;
import com.eduscrum.upt.Ubereats.entity.enums.RecipientType;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for badge response data.
 * Contains badge details, status, and creation info.
 *
 * @author UberEats
 * @version 0.9.1
 */
public class BadgeResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Integer points;
    private BadgeType badgeType;
    private RecipientType recipientType;
    private String triggerCondition;
    private String icon;
    private String color;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer awardCount;

    // Related entity info
    private Long createdByUserId;
    private String createdByName;

<<<<<<< HEAD
    /** Default constructor. */
=======
    // Constructors
>>>>>>> Yesh_Branch
    public BadgeResponseDTO() {
    }

    public BadgeResponseDTO(Long id, String name, String description, Integer points,
            BadgeType badgeType, String triggerCondition, String icon,
            String color, Boolean isActive, LocalDateTime createdAt,
            LocalDateTime updatedAt, Integer awardCount, Long createdByUserId,
            String createdByName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.points = points;
        this.badgeType = badgeType;
        this.triggerCondition = triggerCondition;
        this.icon = icon;
        this.color = color;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.awardCount = awardCount;
        this.createdByUserId = createdByUserId;
        this.createdByName = createdByName;
    }

<<<<<<< HEAD
    /** @return The badge ID */
=======
    // Getters and Setters
>>>>>>> Yesh_Branch
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public BadgeType getBadgeType() {
        return badgeType;
    }

    public void setBadgeType(BadgeType badgeType) {
        this.badgeType = badgeType;
    }

<<<<<<< HEAD
    public String getTriggerCondition() {
        return triggerCondition;
    }

    public void setTriggerCondition(String triggerCondition) {
        this.triggerCondition = triggerCondition;
    }

    public String getIcon() {
        return icon;
    }

=======
    public RecipientType getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(RecipientType recipientType) {
        this.recipientType = recipientType;
    }

    public String getTriggerCondition() {
        return triggerCondition;
    }

    public void setTriggerCondition(String triggerCondition) {
        this.triggerCondition = triggerCondition;
    }

    public String getIcon() {
        return icon;
    }

>>>>>>> Yesh_Branch
    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getAwardCount() {
        return awardCount;
    }

    public void setAwardCount(Integer awardCount) {
        this.awardCount = awardCount;
    }

    public Long getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(Long createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }
}
