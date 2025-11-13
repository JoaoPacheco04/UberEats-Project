// BadgeRequestDTO.java
package com.eduscrum.upt.Ubereats.dto.request;

import com.eduscrum.upt.Ubereats.entity.enums.BadgeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

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

    // Constructors
    public BadgeRequestDTO() {}

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

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }

    public BadgeType getBadgeType() { return badgeType; }
    public void setBadgeType(BadgeType badgeType) { this.badgeType = badgeType; }

    public String getTriggerCondition() { return triggerCondition; }
    public void setTriggerCondition(String triggerCondition) { this.triggerCondition = triggerCondition; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Long getCreatedByUserId() { return createdByUserId; }
    public void setCreatedByUserId(Long createdByUserId) { this.createdByUserId = createdByUserId; }
}