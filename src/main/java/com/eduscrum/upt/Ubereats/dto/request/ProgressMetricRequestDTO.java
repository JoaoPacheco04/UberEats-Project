// ProgressMetricRequestDTO.java
package com.eduscrum.upt.Ubereats.dto.request;

import com.eduscrum.upt.Ubereats.entity.enums.TeamMood;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ProgressMetricRequestDTO {

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

    // Constructors
    public ProgressMetricRequestDTO() {}

    public ProgressMetricRequestDTO(Long sprintId, Long teamId, LocalDate recordedDate,
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

    // Getters and Setters
    public Long getSprintId() { return sprintId; }
    public void setSprintId(Long sprintId) { this.sprintId = sprintId; }

    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }

    public LocalDate getRecordedDate() { return recordedDate; }
    public void setRecordedDate(LocalDate recordedDate) { this.recordedDate = recordedDate; }

    public Integer getCompletedTasks() { return completedTasks; }
    public void setCompletedTasks(Integer completedTasks) { this.completedTasks = completedTasks; }

    public Integer getTotalTasks() { return totalTasks; }
    public void setTotalTasks(Integer totalTasks) { this.totalTasks = totalTasks; }

    public BigDecimal getStoryPointsCompleted() { return storyPointsCompleted; }
    public void setStoryPointsCompleted(BigDecimal storyPointsCompleted) { this.storyPointsCompleted = storyPointsCompleted; }

    public BigDecimal getTotalStoryPoints() { return totalStoryPoints; }
    public void setTotalStoryPoints(BigDecimal totalStoryPoints) { this.totalStoryPoints = totalStoryPoints; }

    public BigDecimal getVelocity() { return velocity; }
    public void setVelocity(BigDecimal velocity) { this.velocity = velocity; }

    public String getBurnDownData() { return burnDownData; }
    public void setBurnDownData(String burnDownData) { this.burnDownData = burnDownData; }

    public TeamMood getTeamMood() { return teamMood; }
    public void setTeamMood(TeamMood teamMood) { this.teamMood = teamMood; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}