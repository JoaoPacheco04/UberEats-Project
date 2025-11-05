package com.eduscrum.upt.Ubereats.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.eduscrum.upt.Ubereats.entity.enums.TeamMood;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Entity
@Table(name = "progress_metrics")
public class ProgressMetric {
    // === ATTRIBUTES ===
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "completed_tasks")
    private Integer completedTasks = 0;

    @Column(name = "total_tasks")
    private Integer totalTasks = 0;

    @Column(name = "story_points_completed", precision = 5, scale = 2)
    private BigDecimal storyPointsCompleted = BigDecimal.ZERO;

    @Column(name = "total_story_points", precision = 5, scale = 2)
    private BigDecimal totalStoryPoints = BigDecimal.ZERO;

    @Column(precision = 5, scale = 2)
    private BigDecimal velocity = BigDecimal.ZERO;

    @Column(name = "burn_down_data", columnDefinition = "JSON")
    private String burnDownData;

    @Enumerated(EnumType.STRING)
    @Column(name = "team_mood", length = 20)
    private TeamMood teamMood;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "recorded_date", nullable = false)
    private LocalDate recordedDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // === RELATIONS ===
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sprint_id", nullable = false)
    private Sprint sprint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    // === CONSTRUCTORS ===
    public ProgressMetric() {}

    public ProgressMetric(Sprint sprint, Team team, LocalDate recordedDate) {
        this.sprint = sprint;
        this.team = team;
        this.recordedDate = recordedDate;
        this.completedTasks = 0;
        this.totalTasks = 0;
        this.storyPointsCompleted = BigDecimal.ZERO;
        this.totalStoryPoints = BigDecimal.ZERO;
        this.velocity = BigDecimal.ZERO;
    }

    // === GETTERS & SETTERS ===
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getCompletedTasks() { return completedTasks; }
    public void setCompletedTasks(Integer completedTasks) { this.completedTasks = completedTasks; }

    public Integer getTotalTasks() { return totalTasks; }
    public void setTotalTasks(Integer totalTasks) { this.totalTasks = totalTasks; }

    public BigDecimal getStoryPointsCompleted() { return storyPointsCompleted; }
    public void setStoryPointsCompleted(BigDecimal storyPointsCompleted) {
        this.storyPointsCompleted = storyPointsCompleted != null ? storyPointsCompleted : BigDecimal.ZERO;
    }

    public BigDecimal getTotalStoryPoints() { return totalStoryPoints; }
    public void setTotalStoryPoints(BigDecimal totalStoryPoints) {
        this.totalStoryPoints = totalStoryPoints != null ? totalStoryPoints : BigDecimal.ZERO;
    }

    public BigDecimal getVelocity() { return velocity; }
    public void setVelocity(BigDecimal velocity) {
        this.velocity = velocity != null ? velocity : BigDecimal.ZERO;
    }

    public String getBurnDownData() { return burnDownData; }
    public void setBurnDownData(String burnDownData) { this.burnDownData = burnDownData; }

    public TeamMood getTeamMood() { return teamMood; }
    public void setTeamMood(TeamMood teamMood) { this.teamMood = teamMood; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDate getRecordedDate() { return recordedDate; }
    public void setRecordedDate(LocalDate recordedDate) { this.recordedDate = recordedDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Sprint getSprint() { return sprint; }
    public void setSprint(Sprint sprint) { this.sprint = sprint; }

    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }

    // === BUSINESS METHODS ===

    /**
     * Calculate task completion percentage
     */
    public BigDecimal getCompletionPercentage() {
        if (totalTasks == 0) return BigDecimal.ZERO;
        return new BigDecimal(completedTasks)
                .divide(new BigDecimal(totalTasks), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100.0"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate story points completion percentage
     */
    public BigDecimal getStoryPointsCompletion() {
        if (totalStoryPoints.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return storyPointsCompleted
                .divide(totalStoryPoints, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100.0"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Check if team is on track (more than 50% completion when halfway through sprint)
     */
    public boolean isOnTrack() {
        BigDecimal sprintProgress = sprint.getTimeProgressPercentage();
        BigDecimal teamProgress = getCompletionPercentage();

        // If sprint is halfway done, team should be at least 50% complete
        return sprintProgress.compareTo(new BigDecimal("50.0")) <= 0 ||
                teamProgress.compareTo(new BigDecimal("50.0")) >= 0;
    }

    /**
     * Parse burn down data from JSON
     */
    public Map<String, Integer> getBurnDownDataMap() {
        if (burnDownData == null || burnDownData.trim().isEmpty()) {
            return new HashMap<>();
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(burnDownData, new TypeReference<Map<String, Integer>>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    /**
     * Calculate remaining tasks
     */
    public Integer getRemainingTasks() {
        return totalTasks - completedTasks;
    }

    /**
     * Calculate remaining story points
     */
    public BigDecimal getRemainingStoryPoints() {
        return totalStoryPoints.subtract(storyPointsCompleted);
    }

    /**
     * Check if all tasks are completed
     */
    public boolean isAllTasksCompleted() {
        return completedTasks >= totalTasks;
    }

    // === BUSINESS METHODS ===

    /**
     * Check if all story points are completed
     */
    public boolean isAllStoryPointsCompleted() {
        return storyPointsCompleted.compareTo(totalStoryPoints) >= 0;
    }


    /**
     * Update metrics with new task completion
     */
    public void updateTaskCompletion(Integer newCompletedTasks) {
        this.completedTasks = newCompletedTasks;
        recalculateVelocity();
    }

    /**
     * Update story points completion
     */
    public void updateStoryPointsCompletion(BigDecimal newStoryPointsCompleted) {
        this.storyPointsCompleted = newStoryPointsCompleted != null ? newStoryPointsCompleted : BigDecimal.ZERO;
        recalculateVelocity();
    }

    /**
     * Recalculate velocity based on completed work
     */
    private void recalculateVelocity() {
        if (sprint.getDurationDays() > 0) {
            long daysPassed = ChronoUnit.DAYS.between(sprint.getStartDate(), recordedDate);
            if (daysPassed > 0) {
                this.velocity = storyPointsCompleted
                        .divide(BigDecimal.valueOf(daysPassed), 2, RoundingMode.HALF_UP);
            }
        }
    }

    /**
     * Add completed story points
     */
    public void addStoryPoints(BigDecimal points) {
        if (points != null && points.compareTo(BigDecimal.ZERO) > 0) {
            this.storyPointsCompleted = this.storyPointsCompleted.add(points);
            recalculateVelocity();
        }
    }

    /**
     * Add completed tasks
     */
    public void addCompletedTasks(Integer tasks) {
        if (tasks != null && tasks > 0) {
            this.completedTasks += tasks;
            recalculateVelocity();
        }
    }

    /**
     * Get progress status based on completion percentage
     */
    public String getProgressStatus() {
        BigDecimal completion = getCompletionPercentage();
        if (completion.compareTo(new BigDecimal("100.0")) >= 0) {
            return "Completed";
        } else if (completion.compareTo(new BigDecimal("75.0")) >= 0) {
            return "Almost Done";
        } else if (completion.compareTo(new BigDecimal("50.0")) >= 0) {
            return "In Progress";
        } else if (completion.compareTo(new BigDecimal("25.0")) >= 0) {
            return "Getting Started";
        } else {
            return "Just Started";
        }
    }

    // === UTILITY METHODS ===
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProgressMetric)) return false;
        ProgressMetric that = (ProgressMetric) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(sprint, that.sprint) &&
                Objects.equals(team, that.team) &&
                Objects.equals(recordedDate, that.recordedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sprint, team, recordedDate);
    }

    @Override
    public String toString() {
        return "ProgressMetric{" +
                "id=" + id +
                ", sprint=" + (sprint != null ? sprint.getName() : "null") +
                ", team=" + (team != null ? team.getName() : "null") +
                ", completedTasks=" + completedTasks +
                "/" + totalTasks +
                ", completion=" + getCompletionPercentage() + "%" +
                ", storyPoints=" + storyPointsCompleted + "/" + totalStoryPoints +
                ", velocity=" + velocity +
                ", recordedDate=" + recordedDate +
                '}';
    }
}