package com.eduscrum.upt.Ubereats.dto.request;

import com.eduscrum.upt.Ubereats.entity.enums.StoryPriority;
import com.eduscrum.upt.Ubereats.entity.enums.StoryStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for creating or updating a User Story.
 * Contains user story details and validation annotations.
 *
 * @version 1.0 (2025-12-10)
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

    // Constructors
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

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(Integer storyPoints) {
        this.storyPoints = storyPoints;
    }

    public StoryStatus getStatus() {
        return status;
    }

    public void setStatus(StoryStatus status) {
        this.status = status;
    }

    public StoryPriority getPriority() {
        return priority;
    }

    public void setPriority(StoryPriority priority) {
        this.priority = priority;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public Long getAssignedToUserId() {
        return assignedToUserId;
    }

    public void setAssignedToUserId(Long assignedToUserId) {
        this.assignedToUserId = assignedToUserId;
    }

    public Long getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(Long createdByUserId) {
        this.createdByUserId = createdByUserId;
    }
}