package com.eduscrum.upt.Ubereats.dto.request;

import com.eduscrum.upt.Ubereats.entity.enums.BadgeType;
import com.eduscrum.upt.Ubereats.entity.enums.RecipientType;
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

    private RecipientType recipientType = RecipientType.BOTH;

    private String triggerCondition;
    private String icon = "🏆";
    private String color = "#FF5733";

    @NotNull(message = "Created by user ID is required")
    private Long createdByUserId;

<<<<<<< HEAD
    /** Default constructor. */
=======
    // Constructors
>>>>>>> Yesh_Branch
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

<<<<<<< HEAD
    /** @return The badge name */
=======
    // Getters and Setters
>>>>>>> Yesh_Branch
    public String getName() {
        return name;
    }

<<<<<<< HEAD
    /** @param name The badge name */
=======
>>>>>>> Yesh_Branch
    public void setName(String name) {
        this.name = name;
    }

<<<<<<< HEAD
    /** @return The description */
=======
>>>>>>> Yesh_Branch
    public String getDescription() {
        return description;
    }

<<<<<<< HEAD
    /** @param description The description */
=======
>>>>>>> Yesh_Branch
    public void setDescription(String description) {
        this.description = description;
    }

<<<<<<< HEAD
    /** @return The points */
=======
>>>>>>> Yesh_Branch
    public Integer getPoints() {
        return points;
    }

<<<<<<< HEAD
    /** @param points The points */
=======
>>>>>>> Yesh_Branch
    public void setPoints(Integer points) {
        this.points = points;
    }

<<<<<<< HEAD
    /** @return The badge type */
=======
>>>>>>> Yesh_Branch
    public BadgeType getBadgeType() {
        return badgeType;
    }

<<<<<<< HEAD
    /** @param badgeType The badge type */
=======
>>>>>>> Yesh_Branch
    public void setBadgeType(BadgeType badgeType) {
        this.badgeType = badgeType;
    }

<<<<<<< HEAD
    /** @return The trigger condition */
=======
    public RecipientType getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(RecipientType recipientType) {
        this.recipientType = recipientType;
    }

>>>>>>> Yesh_Branch
    public String getTriggerCondition() {
        return triggerCondition;
    }

<<<<<<< HEAD
    /** @param triggerCondition The trigger condition */
=======
>>>>>>> Yesh_Branch
    public void setTriggerCondition(String triggerCondition) {
        this.triggerCondition = triggerCondition;
    }

<<<<<<< HEAD
    /** @return The icon */
=======
>>>>>>> Yesh_Branch
    public String getIcon() {
        return icon;
    }

<<<<<<< HEAD
    /** @param icon The icon */
=======
>>>>>>> Yesh_Branch
    public void setIcon(String icon) {
        this.icon = icon;
    }

<<<<<<< HEAD
    /** @return The color */
=======
>>>>>>> Yesh_Branch
    public String getColor() {
        return color;
    }

<<<<<<< HEAD
    /** @param color The color */
=======
>>>>>>> Yesh_Branch
    public void setColor(String color) {
        this.color = color;
    }

<<<<<<< HEAD
    /** @return The creator user ID */
=======
>>>>>>> Yesh_Branch
    public Long getCreatedByUserId() {
        return createdByUserId;
    }

<<<<<<< HEAD
    /** @param createdByUserId The creator user ID */
    public void setCreatedByUserId(Long createdByUserId) {
        this.createdByUserId = createdByUserId;
    }
}
=======
    public void setCreatedByUserId(Long createdByUserId) {
        this.createdByUserId = createdByUserId;
    }
}
>>>>>>> Yesh_Branch
