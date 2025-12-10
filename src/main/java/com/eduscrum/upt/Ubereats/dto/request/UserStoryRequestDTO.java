package com.eduscrum.upt.Ubereats.dto.request;

import com.eduscrum.upt.Ubereats.entity.enums.StoryPriority;
import com.eduscrum.upt.Ubereats.entity.enums.StoryStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for creating or updating a User Story.
 * Contains user story details and validation annotations.
 *
 * @author UberEats
 * @version 0.5.0
 */
public class UserStoryRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private Integer storyPoints = 0;

    private StoryStatus status = StoryStatus.TODO;

    private StoryPriority priority = StoryPriority.MEDIUM;

    @NotNull(message = "Sprint ID is required")
    private Long sprintId;

    @NotNull(message = "Team ID is required")
    private Long teamId;

    private Long assignedToUserId;

    @NotNull(message = "Created by user ID is required")
    private Long createdByUserId;

    /** Default constructor. */
    public UserStoryRequestDTO() {
    }

    public UserStoryRequestDTO(String title, String description, Integer storyPoints,
            StoryStatus status, StoryPriority priority, Long sprintId,
            Long teamId, Long assignedToUserId, Long createdByUserId) {
        this.title = title;
        this.description = description;
        this.storyPoints = storyPoints;
        this.status = status;
        this.priority = priority;
        this.sprintId = sprintId;
        this.teamId = teamId;
        this.assignedToUserId = assignedToUserId;
        this.createdByUserId = createdByUserId;
    }

    /** @return The title */
    public String getTitle() {
        return title;
    }

    /** @param title The title */
    public void setTitle(String title) {
        this.title = title;
    }

    /** @return The description */
    public String getDescription() {
        return description;
    }

    /** @param description The description */
    public void setDescription(String description) {
        this.description = description;
    }

    /** @return The story points */
    public Integer getStoryPoints() {
        return storyPoints;
    }

    /** @param storyPoints The story points */
    public void setStoryPoints(Integer storyPoints) {
        this.storyPoints = storyPoints;
    }

    /** @return The status */
    public StoryStatus getStatus() {
        return status;
    }

    /** @param status The status */
    public void setStatus(StoryStatus status) {
        this.status = status;
    }

    /** @return The priority */
    public StoryPriority getPriority() {
        return priority;
    }

    /** @param priority The priority */
    public void setPriority(StoryPriority priority) {
        this.priority = priority;
    }

    /** @return The sprint ID */
    public Long getSprintId() {
        return sprintId;
    }

    /** @param sprintId The sprint ID */
    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    /** @return The team ID */
    public Long getTeamId() {
        return teamId;
    }

    /** @param teamId The team ID */
    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    /** @return The assigned user ID */
    public Long getAssignedToUserId() {
        return assignedToUserId;
    }

    /** @param assignedToUserId The assigned user ID */
    public void setAssignedToUserId(Long assignedToUserId) {
        this.assignedToUserId = assignedToUserId;
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
