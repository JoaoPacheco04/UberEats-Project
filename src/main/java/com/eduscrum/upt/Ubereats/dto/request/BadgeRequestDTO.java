package com.eduscrum.upt.Ubereats.dto.request;

import com.eduscrum.upt.Ubereats.entity.enums.BadgeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Data Transfer Object for creating or updating a Badge.
 * Contains badge details and validation annotations.
 *
 * @author UberEats
 * @version 0.1.0
 */
public class BadgeRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Points are required")
    @PositiveOrZero(message = "Points must be zero or positive")
    private Integer points;

    @NotNull(message = "Badge type is required")
    private BadgeType badgeType;

    private String triggerCondition;
    private String icon = "🏆";
    private String color = "#FF5733";

    @NotNull(message = "Created by user ID is required")
    private Long createdByUserId;

    /** Default constructor. */
    public BadgeRequestDTO() {
    }

    public BadgeRequestDTO(String name, String description, Integer points, BadgeType badgeType,
            String triggerCondition, String icon, String color, Long createdByUserId) {
        this.name = name;
        this.description = description;
        this.points = points;
        this.badgeType = badgeType;
        this.triggerCondition = triggerCondition;
        this.icon = icon;
        this.color = color;
        this.createdByUserId = createdByUserId;
    }

    /** @return The badge name */
    public String getName() {
        return name;
    }

    /** @param name The badge name */
    public void setName(String name) {
        this.name = name;
    }

    /** @return The description */
    public String getDescription() {
        return description;
    }

    /** @param description The description */
    public void setDescription(String description) {
        this.description = description;
    }

    /** @return The points */
    public Integer getPoints() {
        return points;
    }

    /** @param points The points */
    public void setPoints(Integer points) {
        this.points = points;
    }

    /** @return The badge type */
    public BadgeType getBadgeType() {
        return badgeType;
    }

    /** @param badgeType The badge type */
    public void setBadgeType(BadgeType badgeType) {
        this.badgeType = badgeType;
    }

    /** @return The trigger condition */
    public String getTriggerCondition() {
        return triggerCondition;
    }

    /** @param triggerCondition The trigger condition */
    public void setTriggerCondition(String triggerCondition) {
        this.triggerCondition = triggerCondition;
    }

    /** @return The icon */
    public String getIcon() {
        return icon;
    }

    /** @param icon The icon */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /** @return The color */
    public String getColor() {
        return color;
    }

    /** @param color The color */
    public void setColor(String color) {
        this.color = color;
    }

    /** @return The creator user ID */
    public Long getCreatedByUserId() {
        return createdByUserId;
    }

    /** @param createdByUserId The creator user ID */
    public void setCreatedByUserId(Long createdByUserId) {
        this.createdByUserId = createdByUserId;
    }
}
