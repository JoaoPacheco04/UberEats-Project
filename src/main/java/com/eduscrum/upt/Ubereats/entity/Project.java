package com.eduscrum.upt.Ubereats.entity;

import com.eduscrum.upt.Ubereats.entity.enums.ProjectStatus;
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
import java.util.stream.Collectors;

@Entity
@Table(name = "projects")
public class Project {
    // === ATTRIBUTES ===
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "completed_at")
    private LocalDate completedAt;

    @Column(name = "max_score", precision = 5, scale = 2)
    private BigDecimal maxScore = new BigDecimal("100.00");

    @Column(name = "progress", nullable = false)
    private Double progress = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProjectStatus status = ProjectStatus.PLANNING;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // === RELATIONS ===
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Sprint> sprints = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Achievement> achievements = new ArrayList<>();

    // === CONSTRUCTORS ===
    public Project() {
    }

    public Project(String name, String description, LocalDate startDate,
            LocalDate endDate, Course course) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.course = course;
        this.status = ProjectStatus.PLANNING;
        this.maxScore = new BigDecimal("100.00");
    }

    public Project(String name, String description, LocalDate startDate,
            LocalDate endDate, Course course, BigDecimal maxScore) {
        this(name, description, startDate, endDate, course);
        this.maxScore = maxScore != null ? maxScore : new BigDecimal("100.00");
    }

    // === GETTERS & SETTERS ===
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public BigDecimal getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(BigDecimal maxScore) {
        this.maxScore = maxScore != null ? maxScore : new BigDecimal("100.00");
    }

    public Double getProgress() {
        return progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
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

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public List<Sprint> getSprints() {
        return sprints;
    }

    public void setSprints(List<Sprint> sprints) {
        this.sprints = sprints;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
    }

    // === BUSINESS METHODS ===

    /**
     * Check if project has a team assigned
     */
    public boolean hasTeam() {
        return team != null;
    }

    /**
     * Get number of completed sprints
     */
    public Integer getCompletedSprints() {
        return (int) sprints.stream()
                .filter(sprint -> sprint.getStatus() == SprintStatus.COMPLETED)
                .count();
    }

    /**
     * Get total number of sprints
     */
    public Integer getTotalSprints() {
        return sprints.size();
    }

    /**
     * Calculate progress percentage based on completed sprints
     */
    /**
     * Get progress percentage (stored field)
     */
    public BigDecimal getProgressPercentage() {
        return BigDecimal.valueOf(this.progress).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Check if project is active
     */
    public boolean isActive() {
        return status == ProjectStatus.ACTIVE;
    }

    /**
     * Check if project is completed
     */
    public boolean isCompleted() {
        return status == ProjectStatus.COMPLETED;
    }

    /**
     * Check if project is ongoing (between start and end dates)
     */
    public boolean isOngoing() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(startDate) && !today.isAfter(endDate);
    }

    /**
     * Get active sprints
     */
    public List<Sprint> getActiveSprints() {
        return sprints.stream()
                .filter(sprint -> sprint.getStatus() == SprintStatus.IN_PROGRESS)
                .collect(Collectors.toList());
    }

    /**
     * Get days remaining until project end
     */
    public Long getDaysRemaining() {
        LocalDate today = LocalDate.now();
        return ChronoUnit.DAYS.between(today, endDate);
    }

    /**
     * Get total students participating in this project
     */
    public Integer getTotalStudents() {
        if (team == null)
            return 0;
        return team.getActiveMembers().size();
    }

    /**
     * Get project duration in days
     */
    public Long getDurationDays() {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * Check if project is overdue
     */
    public boolean isOverdue() {
        return LocalDate.now().isAfter(endDate) && !isCompleted();
    }

    /**
     * Get days since project start
     */
    public Long getDaysSinceStart() {
        LocalDate today = LocalDate.now();
        return ChronoUnit.DAYS.between(startDate, today);
    }

    /**
     * Get team score for this project's team
     */
    public BigDecimal getTeamScore() {
        if (team == null)
            return BigDecimal.ZERO;

        // Calculate team score based on achievements
        int teamPoints = team.getTeamAchievements().stream()
                .mapToInt(achievement -> achievement.getBadge().getPoints())
                .sum();
        return new BigDecimal(teamPoints);
    }

    /**
     * Get project status description
     */
    public String getStatusDescription() {
        switch (status) {
            case PLANNING:
                return "Project is in planning phase";
            case ACTIVE:
                if (isOverdue()) {
                    return "Project is active but overdue";
                }
                return "Project is currently active";
            case COMPLETED:
                return "Project has been completed";
            case CANCELLED:
                return "Project was cancelled";
            default:
                return "Unknown status";
        }
    }

    // === UTILITY METHODS ===
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Project))
            return false;
        Project project = (Project) o;
        return Objects.equals(id, project.id) &&
                Objects.equals(name, project.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", progress=" + getProgressPercentage() + "%" +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}