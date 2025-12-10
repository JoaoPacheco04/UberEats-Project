package com.eduscrum.upt.Ubereats.dto.response;

import com.eduscrum.upt.Ubereats.entity.enums.StoryPriority;
import com.eduscrum.upt.Ubereats.entity.enums.StoryStatus;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for user story response data.
 * Contains story details, status info, and related sprint/team data.
 *
 * @version 1.0.1 (2025-12-03)
 */
public class UserStoryResponseDTO {
    private Long id;
    private String title;
    private String description;
    private Integer storyPoints;
    private StoryStatus status;
    private StoryPriority priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean completed;
    private boolean inProgress;
    private boolean inReview;
    private boolean pending;
    private String statusColor;
    private String priorityColor;
    private String priorityIcon;
    private boolean assigned;
    private String assignedUserName;
    private String effortLevel;
    private boolean blocked;
    private boolean canMoveToNextStatus;
    private boolean canMoveToPreviousStatus;

    // Related entity info
    private Long sprintId;
    private String sprintName;
    private Long teamId;
    private String teamName;
    private Long assignedToUserId;
    private Long createdByUserId;
    private String createdByName;

    // Constructors
    public UserStoryResponseDTO() {
    }

    public UserStoryResponseDTO(Long id, String title, String description, Integer storyPoints,
            StoryStatus status, StoryPriority priority, LocalDateTime createdAt,
            LocalDateTime updatedAt, boolean completed, boolean inProgress,
            boolean inReview, boolean pending, String statusColor,
            String priorityColor, String priorityIcon, boolean assigned,
            String assignedUserName, String effortLevel, boolean blocked,
            boolean canMoveToNextStatus, boolean canMoveToPreviousStatus,
            Long sprintId, String sprintName, Long teamId, String teamName,
            Long assignedToUserId, Long createdByUserId, String createdByName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.storyPoints = storyPoints;
        this.status = status;
        this.priority = priority;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.completed = completed;
        this.inProgress = inProgress;
        this.inReview = inReview;
        this.pending = pending;
        this.statusColor = statusColor;
        this.priorityColor = priorityColor;
        this.priorityIcon = priorityIcon;
        this.assigned = assigned;
        this.assignedUserName = assignedUserName;
        this.effortLevel = effortLevel;
        this.blocked = blocked;
        this.canMoveToNextStatus = canMoveToNextStatus;
        this.canMoveToPreviousStatus = canMoveToPreviousStatus;
        this.sprintId = sprintId;
        this.sprintName = sprintName;
        this.teamId = teamId;
        this.teamName = teamName;
        this.assignedToUserId = assignedToUserId;
        this.createdByUserId = createdByUserId;
        this.createdByName = createdByName;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    public boolean isInReview() {
        return inReview;
    }

    public void setInReview(boolean inReview) {
        this.inReview = inReview;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }

    public String getStatusColor() {
        return statusColor;
    }

    public void setStatusColor(String statusColor) {
        this.statusColor = statusColor;
    }

    public String getPriorityColor() {
        return priorityColor;
    }

    public void setPriorityColor(String priorityColor) {
        this.priorityColor = priorityColor;
    }

    public String getPriorityIcon() {
        return priorityIcon;
    }

    public void setPriorityIcon(String priorityIcon) {
        this.priorityIcon = priorityIcon;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }

    public String getAssignedUserName() {
        return assignedUserName;
    }

    public void setAssignedUserName(String assignedUserName) {
        this.assignedUserName = assignedUserName;
    }

    public String getEffortLevel() {
        return effortLevel;
    }

    public void setEffortLevel(String effortLevel) {
        this.effortLevel = effortLevel;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isCanMoveToNextStatus() {
        return canMoveToNextStatus;
    }

    public void setCanMoveToNextStatus(boolean canMoveToNextStatus) {
        this.canMoveToNextStatus = canMoveToNextStatus;
    }

    public boolean isCanMoveToPreviousStatus() {
        return canMoveToPreviousStatus;
    }

    public void setCanMoveToPreviousStatus(boolean canMoveToPreviousStatus) {
        this.canMoveToPreviousStatus = canMoveToPreviousStatus;
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

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
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

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }
}
