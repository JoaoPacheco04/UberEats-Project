package com.eduscrum.upt.Ubereats.entity;

import com.eduscrum.upt.Ubereats.entity.enums.SprintStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * JPA entity representing a sprint in the EduScrum platform.
 * Contains user stories, analytics, and achievements. Belongs to a project.
 *
 * @version 0.8.0 (2025-11-20)
 */
@Entity
@Table(name = "sprints")
public class Sprint {
    // === ATTRIBUTES ===
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sprint_number", nullable = false)
    private Integer sprintNumber;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String goal;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "completed_at")
    private LocalDate completedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SprintStatus status = SprintStatus.PLANNED;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // === RELATIONS ===
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @OneToMany(mappedBy = "sprint", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Analytic> analytics = new ArrayList<>();

    @OneToMany(mappedBy = "sprint", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Achievement> achievements = new ArrayList<>();

    // === CONSTRUCTORS ===
    public Sprint() {
    }

    public Sprint(Integer sprintNumber, String name, String goal, LocalDate startDate,
            LocalDate endDate, Project project) {
        this.sprintNumber = sprintNumber;
        this.name = name;
        this.goal = goal;
        this.startDate = startDate;
        this.endDate = endDate;
        this.project = project;
        this.status = SprintStatus.PLANNED;
    }

    // === GETTERS & SETTERS ===
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

    public LocalDate getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDate completedAt) {
        this.completedAt = completedAt;
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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<Analytic> getAnalytics() {
        return analytics;
    }

    public void setAnalytics(List<Analytic> analytics) {
        this.analytics = analytics;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
    }

    // === BUSINESS METHODS ===

    /**
     * Checks if sprint has IN_PROGRESS status.
     *
     * @return true if sprint is active, false otherwise
     */
    public boolean isActive() {
        return status == SprintStatus.IN_PROGRESS;
    }

    /**
     * Checks if sprint has COMPLETED status.
     *
     * @return true if sprint is completed, false otherwise
     */
    public boolean isCompleted() {
        return status == SprintStatus.COMPLETED;
    }

    /**
     * Calculates sprint duration in days.
     *
     * @return Number of days between start and end dates
     */
    public Long getDurationDays() {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * Checks if sprint is overdue (past end date and not completed).
     *
     * @return true if overdue, false otherwise
     */
    public boolean isOverdue() {
        return LocalDate.now().isAfter(endDate) && !isCompleted();
    }

    /**
     * Calculates days remaining in sprint.
     *
     * @return Number of days remaining, or 0 if past end date
     */
    public Long getDaysRemaining() {
        LocalDate today = LocalDate.now();
        if (today.isAfter(endDate))
            return 0L;
        return ChronoUnit.DAYS.between(today, endDate);
    }

    /**
     * Calculates team velocity for this sprint.
     *
     * @param team The team to calculate velocity for
     * @return The average velocity as BigDecimal, or ZERO if no analytics
     */
    public BigDecimal getTeamVelocity(Team team) {
        if (analytics.isEmpty())
            return BigDecimal.ZERO;

        BigDecimal totalVelocity = analytics.stream()
                .filter(metric -> metric.getTeam().equals(team))
                .map(Analytic::getVelocity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long metricCount = analytics.stream()
                .filter(metric -> metric.getTeam().equals(team))
                .count();

        return metricCount > 0 ? totalVelocity.divide(BigDecimal.valueOf(metricCount), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
    }

    /**
     * Get progress percentage based on time
     */
    public BigDecimal getTimeProgressPercentage() {
        LocalDate today = LocalDate.now();
        if (today.isBefore(startDate))
            return BigDecimal.ZERO;
        if (today.isAfter(endDate))
            return new BigDecimal("100.00");

        long totalDays = getDurationDays();
        long daysPassed = ChronoUnit.DAYS.between(startDate, today);

        if (totalDays == 0)
            return BigDecimal.ZERO;

        return new BigDecimal(daysPassed)
                .divide(new BigDecimal(totalDays), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100.0"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Checks if sprint can be started (PLANNED status and not before start date).
     *
     * @return true if sprint can be started, false otherwise
     */
    public boolean canBeStarted() {
        return status == SprintStatus.PLANNED &&
                !LocalDate.now().isBefore(startDate);
    }

    /**
     * Checks if sprint can be completed (IN_PROGRESS and not before end date).
     *
     * @return true if sprint can be completed, false otherwise
     */
    public boolean canBeCompleted() {
        return status == SprintStatus.IN_PROGRESS &&
                !LocalDate.now().isBefore(endDate);
    }

    /**
     * Gets display name with sprint number.
     *
     * @return Formatted display name (e.g., "Sprint 1: Name")
     */
    public String getDisplayName() {
        return "Sprint " + sprintNumber + ": " + name;
    }

    /**
     * Calculates days since sprint started.
     *
     * @return Number of days since start date
     */
    public Long getDaysSinceStart() {
        LocalDate today = LocalDate.now();
        return ChronoUnit.DAYS.between(startDate, today);
    }

    /**
     * Checks if sprint is in planning phase (PLANNED status).
     *
     * @return true if in planning, false otherwise
     */
    public boolean isInPlanning() {
        return status == SprintStatus.PLANNED;
    }

    /**
     * Gets a human-readable description of the sprint status.
     *
     * @return Status description string
     */
    public String getStatusDescription() {
        switch (status) {
            case PLANNED:
                return "Sprint is planned and waiting to start";
            case IN_PROGRESS:
                if (isOverdue()) {
                    return "Sprint is in progress but overdue";
                }
                return "Sprint is currently in progress";
            case COMPLETED:
                return "Sprint has been completed";
            case CANCELLED:
                return "Sprint was cancelled";
            default:
                return "Unknown status";
        }
    }

    // === UTILITY METHODS ===
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Sprint))
            return false;
        Sprint sprint = (Sprint) o;
        return Objects.equals(id, sprint.id) &&
                Objects.equals(sprintNumber, sprint.sprintNumber) &&
                Objects.equals(project, sprint.project);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sprintNumber, project);
    }

    @Override
    public String toString() {
        return "Sprint{" +
                "id=" + id +
                ", sprintNumber=" + sprintNumber +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", progress=" + getTimeProgressPercentage() + "%" +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
