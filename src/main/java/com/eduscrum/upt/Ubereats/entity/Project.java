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

/**
 * JPA entity representing a project in the EduScrum platform.
 * Contains sprints, teams, and achievements. Belongs to a course.
 *
 * @author UberEats
 * @version 0.6.1
 */
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

    @ManyToMany(mappedBy = "projects", fetch = FetchType.LAZY)
    private List<Team> teams = new ArrayList<>();

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

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
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
     * Gets the number of teams in this project.
     *
     * @return The count of teams
     */
    public Integer getTeamCount() {
        return teams.size();
    }

    /**
     * Gets the number of completed sprints.
     *
     * @return The count of completed sprints
     */
    public Integer getCompletedSprints() {
        return (int) sprints.stream()
                .filter(sprint -> sprint.getStatus() == SprintStatus.COMPLETED)
                .count();
    }

    /**
     * Gets the total number of sprints.
     *
     * @return The total count of sprints
     */
    public Integer getTotalSprints() {
        return sprints.size();
    }

    /**
     * Calculate progress percentage based on completed sprints
     * Get progress percentage (stored field)
     */
    public BigDecimal getProgressPercentage() {
        return BigDecimal.valueOf(this.progress).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Checks if project has ACTIVE status.
     *
     * @return true if project is active, false otherwise
     */
    public boolean isActive() {
        return status == ProjectStatus.ACTIVE;
    }

    /**
     * Checks if project has COMPLETED status.
     *
     * @return true if project is completed, false otherwise
     */
    public boolean isCompleted() {
        return status == ProjectStatus.COMPLETED;
    }

    /**
     * Checks if project is ongoing (between start and end dates).
     *
     * @return true if currently within project dates, false otherwise
     */
    public boolean isOngoing() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(startDate) && !today.isAfter(endDate);
    }

    /**
     * Gets all active sprints (IN_PROGRESS status).
     *
     * @return List of active Sprint objects
     */
    public List<Sprint> getActiveSprints() {
        return sprints.stream()
                .filter(sprint -> sprint.getStatus() == SprintStatus.IN_PROGRESS)
                .collect(Collectors.toList());
    }

    /**
     * Calculates days remaining until project end.
     *
     * @return Number of days remaining (can be negative if past end date)
     */
    public Long getDaysRemaining() {
        LocalDate today = LocalDate.now();
        return ChronoUnit.DAYS.between(today, endDate);
    }

    /**
     * Gets total number of students participating in this project.
     *
     * @return The total count of active team members across all teams
     */
    public Integer getTotalStudents() {
        return teams.stream()
                .mapToInt(team -> team.getActiveMembers().size())
                .sum();
    }

    /**
     * Calculates project duration in days.
     *
     * @return Number of days between start and end dates
     */
    public Long getDurationDays() {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * Checks if project is overdue (past end date and not completed).
     *
     * @return true if overdue, false otherwise
     */
    public boolean isOverdue() {
        return LocalDate.now().isAfter(endDate) && !isCompleted();
    }

    /**
     * Calculates days since project started.
     *
     * @return Number of days since start date
     */
    public Long getDaysSinceStart() {
        LocalDate today = LocalDate.now();
        return ChronoUnit.DAYS.between(startDate, today);
    }

    /**
     * Calculates average team score across all teams.
     *
     * @return The average score as BigDecimal, or ZERO if no teams
     */
    public BigDecimal getAverageTeamScore() {
        if (teams.isEmpty())
            return BigDecimal.ZERO;

        BigDecimal totalScore = teams.stream()
                .map(team -> {
                    int teamPoints = team.getTeamAchievements().stream()
                            .mapToInt(achievement -> achievement.getBadge().getPoints())
                            .sum();
                    return new BigDecimal(teamPoints);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalScore.divide(new BigDecimal(teams.size()), 2, RoundingMode.HALF_UP);
    }

    /**
     * Gets a human-readable description of the project status.
     *
     * @return Status description string
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
