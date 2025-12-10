package com.eduscrum.upt.Ubereats.dto.response;

import com.eduscrum.upt.Ubereats.entity.enums.TeamMood;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Data Transfer Object for analytics/progress metrics response.
 * Contains task progress, story points, velocity, and team data.
 *
 * @author UberEats
 * @version 0.8.0
 */
public class AnalyticsResponseDTO {
    private Long id;
    private Integer completedTasks;
    private Integer totalTasks;
    private BigDecimal storyPointsCompleted;
    private BigDecimal totalStoryPoints;
    private BigDecimal velocity;
    private String burnDownData;
    private TeamMood teamMood;
    private String notes;
    private LocalDate recordedDate;
    private LocalDateTime createdAt;

    // Calculated fields
    private BigDecimal completionPercentage;
    private BigDecimal storyPointsCompletion;
    private Integer remainingTasks;
    private BigDecimal remainingStoryPoints;
    private boolean onTrack;
    private boolean allTasksCompleted;
    private boolean allStoryPointsCompleted;
    private String progressStatus;
    private Map<String, Integer> burnDownDataMap;

    // Related entity info
    private Long sprintId;
    private String sprintName;
    private Long teamId;
    private String teamName;
    private Long projectId;
    private String projectName;

    /** Default constructor. */
    public AnalyticsResponseDTO() {
    }

    /** @return The analytics ID */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(Integer completedTasks) {
        this.completedTasks = completedTasks;
    }

    public Integer getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(Integer totalTasks) {
        this.totalTasks = totalTasks;
    }

    public BigDecimal getStoryPointsCompleted() {
        return storyPointsCompleted;
    }

    public void setStoryPointsCompleted(BigDecimal storyPointsCompleted) {
        this.storyPointsCompleted = storyPointsCompleted;
    }

    public BigDecimal getTotalStoryPoints() {
        return totalStoryPoints;
    }

    public void setTotalStoryPoints(BigDecimal totalStoryPoints) {
        this.totalStoryPoints = totalStoryPoints;
    }

    public BigDecimal getVelocity() {
        return velocity;
    }

    public void setVelocity(BigDecimal velocity) {
        this.velocity = velocity;
    }

    public String getBurnDownData() {
        return burnDownData;
    }

    public void setBurnDownData(String burnDownData) {
        this.burnDownData = burnDownData;
    }

    public TeamMood getTeamMood() {
        return teamMood;
    }

    public void setTeamMood(TeamMood teamMood) {
        this.teamMood = teamMood;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDate getRecordedDate() {
        return recordedDate;
    }

    public void setRecordedDate(LocalDate recordedDate) {
        this.recordedDate = recordedDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public BigDecimal getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(BigDecimal completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public BigDecimal getStoryPointsCompletion() {
        return storyPointsCompletion;
    }

    public void setStoryPointsCompletion(BigDecimal storyPointsCompletion) {
        this.storyPointsCompletion = storyPointsCompletion;
    }

    public Integer getRemainingTasks() {
        return remainingTasks;
    }

    public void setRemainingTasks(Integer remainingTasks) {
        this.remainingTasks = remainingTasks;
    }

    public BigDecimal getRemainingStoryPoints() {
        return remainingStoryPoints;
    }

    public void setRemainingStoryPoints(BigDecimal remainingStoryPoints) {
        this.remainingStoryPoints = remainingStoryPoints;
    }

    public boolean isOnTrack() {
        return onTrack;
    }

    public void setOnTrack(boolean onTrack) {
        this.onTrack = onTrack;
    }

    public boolean isAllTasksCompleted() {
        return allTasksCompleted;
    }

    public void setAllTasksCompleted(boolean allTasksCompleted) {
        this.allTasksCompleted = allTasksCompleted;
    }

    public boolean isAllStoryPointsCompleted() {
        return allStoryPointsCompleted;
    }

    public void setAllStoryPointsCompleted(boolean allStoryPointsCompleted) {
        this.allStoryPointsCompleted = allStoryPointsCompleted;
    }

    public String getProgressStatus() {
        return progressStatus;
    }

    public void setProgressStatus(String progressStatus) {
        this.progressStatus = progressStatus;
    }

    public Map<String, Integer> getBurnDownDataMap() {
        return burnDownDataMap;
    }

    public void setBurnDownDataMap(Map<String, Integer> burnDownDataMap) {
        this.burnDownDataMap = burnDownDataMap;
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
