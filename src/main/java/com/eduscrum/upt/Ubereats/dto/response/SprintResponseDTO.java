package com.eduscrum.upt.Ubereats.dto.response;

import com.eduscrum.upt.Ubereats.entity.enums.SprintStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * Data Transfer Object for sprint response data.
 * Contains sprint details, progress info, and related project data.
 *
 * @version 0.6.1 (2025-11-12)
 */
public class SprintResponseDTO {
    private Long id;
    private Integer sprintNumber;
    private String name;
    private String goal;
    private LocalDate startDate;
    private LocalDate endDate;
    private SprintStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long durationDays;
    private Long daysRemaining;
    private BigDecimal timeProgressPercentage;
    private boolean overdue;
    private boolean active;
    private boolean completed;
    private String statusDescription;
    private String displayName;

    // Related entity info
    private Long projectId;
    private String projectName;

    /** Default constructor. */
    public SprintResponseDTO() {
    }

    public SprintResponseDTO(Long id, Integer sprintNumber, String name, String goal,
            LocalDate startDate, LocalDate endDate, SprintStatus status,
            LocalDateTime createdAt, LocalDateTime updatedAt, Long durationDays,
            Long daysRemaining, BigDecimal timeProgressPercentage, boolean overdue,
            boolean active, boolean completed, String statusDescription,
            String displayName, Long projectId, String projectName) {
        this.id = id;
        this.sprintNumber = sprintNumber;
        this.name = name;
        this.goal = goal;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.durationDays = durationDays;
        this.daysRemaining = daysRemaining;
        this.timeProgressPercentage = timeProgressPercentage;
        this.overdue = overdue;
        this.active = active;
        this.completed = completed;
        this.statusDescription = statusDescription;
        this.displayName = displayName;
        this.projectId = projectId;
        this.projectName = projectName;
    }

    /** @return The sprint ID */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSprintNumber() {
        return sprintNumber;
    }

    public void setSprintNumber(Integer sprintNumber) {
        this.sprintNumber = sprintNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public SprintStatus getStatus() {
        return status;
    }

    public void setStatus(SprintStatus status) {
        this.status = status;
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

    public Long getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Long durationDays) {
        this.durationDays = durationDays;
    }

    public Long getDaysRemaining() {
        return daysRemaining;
    }

    public void setDaysRemaining(Long daysRemaining) {
        this.daysRemaining = daysRemaining;
    }

    public BigDecimal getTimeProgressPercentage() {
        return timeProgressPercentage;
    }

    public void setTimeProgressPercentage(BigDecimal timeProgressPercentage) {
        this.timeProgressPercentage = timeProgressPercentage;
    }

    public boolean isOverdue() {
        return overdue;
    }

    public void setOverdue(boolean overdue) {
        this.overdue = overdue;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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
}
