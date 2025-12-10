package com.eduscrum.upt.Ubereats.dto.request;

import com.eduscrum.upt.Ubereats.entity.enums.TeamMood;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object for creating Analytics/Progress metrics.
 * Contains sprint progress, velocity, and team mood data.
 *
 * @author UberEats
 * @version 1.2.0
 */
public class AnalyticsRequestDTO {

    @NotNull(message = "Sprint ID is required")
    private Long sprintId;

    @NotNull(message = "Team ID is required")
    private Long teamId;

    @NotNull(message = "Recorded date is required")
    private LocalDate recordedDate;

    private Integer completedTasks = 0;
    private Integer totalTasks = 0;
    private BigDecimal storyPointsCompleted = BigDecimal.ZERO;
    private BigDecimal totalStoryPoints = BigDecimal.ZERO;
    private BigDecimal velocity = BigDecimal.ZERO;
    private String burnDownData;
    private TeamMood teamMood;
    private String notes;

    /** Default constructor. */
    public AnalyticsRequestDTO() {
    }

    public AnalyticsRequestDTO(Long sprintId, Long teamId, LocalDate recordedDate,
            Integer completedTasks, Integer totalTasks,
            BigDecimal storyPointsCompleted, BigDecimal totalStoryPoints,
            BigDecimal velocity, String burnDownData, TeamMood teamMood, String notes) {
        this.sprintId = sprintId;
        this.teamId = teamId;
        this.recordedDate = recordedDate;
        this.completedTasks = completedTasks;
        this.totalTasks = totalTasks;
        this.storyPointsCompleted = storyPointsCompleted;
        this.totalStoryPoints = totalStoryPoints;
        this.velocity = velocity;
        this.burnDownData = burnDownData;
        this.teamMood = teamMood;
        this.notes = notes;
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

    /** @return The recorded date */
    public LocalDate getRecordedDate() {
        return recordedDate;
    }

    /** @param recordedDate The recorded date */
    public void setRecordedDate(LocalDate recordedDate) {
        this.recordedDate = recordedDate;
    }

    /** @return The completed tasks count */
    public Integer getCompletedTasks() {
        return completedTasks;
    }

    /** @param completedTasks The completed tasks count */
    public void setCompletedTasks(Integer completedTasks) {
        this.completedTasks = completedTasks;
    }

    /** @return The total tasks count */
    public Integer getTotalTasks() {
        return totalTasks;
    }

    /** @param totalTasks The total tasks count */
    public void setTotalTasks(Integer totalTasks) {
        this.totalTasks = totalTasks;
    }

    /** @return The story points completed */
    public BigDecimal getStoryPointsCompleted() {
        return storyPointsCompleted;
    }

    /** @param storyPointsCompleted The story points completed */
    public void setStoryPointsCompleted(BigDecimal storyPointsCompleted) {
        this.storyPointsCompleted = storyPointsCompleted;
    }

    /** @return The total story points */
    public BigDecimal getTotalStoryPoints() {
        return totalStoryPoints;
    }

    /** @param totalStoryPoints The total story points */
    public void setTotalStoryPoints(BigDecimal totalStoryPoints) {
        this.totalStoryPoints = totalStoryPoints;
    }

    /** @return The velocity */
    public BigDecimal getVelocity() {
        return velocity;
    }

    /** @param velocity The velocity */
    public void setVelocity(BigDecimal velocity) {
        this.velocity = velocity;
    }

    /** @return The burn down data */
    public String getBurnDownData() {
        return burnDownData;
    }

    /** @param burnDownData The burn down data */
    public void setBurnDownData(String burnDownData) {
        this.burnDownData = burnDownData;
    }

    /** @return The team mood */
    public TeamMood getTeamMood() {
        return teamMood;
    }

    /** @param teamMood The team mood */
    public void setTeamMood(TeamMood teamMood) {
        this.teamMood = teamMood;
    }

    /** @return The notes */
    public String getNotes() {
        return notes;
    }

    /** @param notes The notes */
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
