package com.eduscrum.upt.Ubereats.entity;

import com.eduscrum.upt.Ubereats.entity.enums.ScrumRole;
import com.eduscrum.upt.Ubereats.entity.enums.TeamMood;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Table(name = "teams")
public class Team {
    // === ATTRIBUTES ===
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

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

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TeamMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProgressMetric> progressMetrics = new ArrayList<>();

    @OneToMany(mappedBy = "awardedToTeam", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Achievement> teamAchievements = new ArrayList<>();

    // === CONSTRUCTORS ===
    public Team() {}

    public Team(String name, Project project) {
        this.name = name;
        this.project = project;
    }

    // === GETTERS & SETTERS ===
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public List<TeamMember> getMembers() { return members; }
    public void setMembers(List<TeamMember> members) { this.members = members; }

    public List<ProgressMetric> getProgressMetrics() { return progressMetrics; }
    public void setProgressMetrics(List<ProgressMetric> progressMetrics) { this.progressMetrics = progressMetrics; }

    public List<Achievement> getTeamAchievements() { return teamAchievements; }
    public void setTeamAchievements(List<Achievement> teamAchievements) { this.teamAchievements = teamAchievements; }

    // === BUSINESS METHODS ===

    /**
     * Get number of active members
     */
    public Integer getMemberCount() {
        return getActiveMembers().size();
    }

    /**
     * Get Scrum Master of the team
     */
    public User getScrumMaster() {
        return members.stream()
                .filter(member -> member.getRole() == ScrumRole.SCRUM_MASTER && member.isActive())
                .map(TeamMember::getUser)
                .findFirst()
                .orElse(null);
    }

    /**
     * Get Product Owner of the team
     */
    public User getProductOwner() {
        return members.stream()
                .filter(member -> member.getRole() == ScrumRole.PRODUCT_OWNER && member.isActive())
                .map(TeamMember::getUser)
                .findFirst()
                .orElse(null);
    }

    /**
     * Get all developers in the team
     */
    public List<User> getDevelopers() {
        return members.stream()
                .filter(member -> member.getRole() == ScrumRole.DEVELOPER && member.isActive())
                .map(TeamMember::getUser)
                .collect(Collectors.toList());
    }

    /**
     * Get total points from team achievements
     */
    public Integer getTotalPoints() {
        return teamAchievements.stream()
                .mapToInt(achievement -> achievement.getBadge().getPoints())
                .sum();
    }

    /**
     * Calculate average velocity from progress metrics
     */
    public BigDecimal getAverageVelocity() {
        if (progressMetrics.isEmpty()) return BigDecimal.ZERO;

        BigDecimal totalVelocity = progressMetrics.stream()
                .map(ProgressMetric::getVelocity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalVelocity.divide(
                new BigDecimal(progressMetrics.size()),
                2,
                RoundingMode.HALF_UP
        );
    }

    /**
     * Calculate current progress percentage
     */
    public BigDecimal getCurrentProgress() {
        if (progressMetrics.isEmpty()) return BigDecimal.ZERO;
        ProgressMetric latest = getLatestProgressMetric();
        if (latest == null || latest.getTotalTasks() == 0) return BigDecimal.ZERO;

        return new BigDecimal(latest.getCompletedTasks())
                .divide(new BigDecimal(latest.getTotalTasks()), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100.0"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Get active team members
     */
    public List<TeamMember> getActiveMembers() {
        return members.stream()
                .filter(TeamMember::isActive)
                .collect(Collectors.toList());
    }

    /**
     * Check if user is a member of this team
     */
    public boolean hasMember(User user) {
        return members.stream()
                .anyMatch(member -> member.getUser().equals(user) && member.isActive());
    }

    /**
     * Get latest progress metric
     */
    public ProgressMetric getLatestProgressMetric() {
        return progressMetrics.stream()
                .max(Comparator.comparing(ProgressMetric::getRecordedDate))
                .orElse(null);
    }

    /**
     * Get team mood from latest progress metric
     */
    public TeamMood getCurrentTeamMood() {
        ProgressMetric latest = getLatestProgressMetric();
        return latest != null ? latest.getTeamMood() : null;
    }

    /**
     * Get team score (points per member)
     */
    public BigDecimal getTeamScore() {
        int totalPoints = getTotalPoints();
        int memberCount = getMemberCount();

        if (memberCount == 0) return BigDecimal.ZERO;

        return new BigDecimal(totalPoints)
                .divide(new BigDecimal(memberCount), 2, RoundingMode.HALF_UP);
    }

    /**
     * Check if team has all required roles assigned
     */
    public boolean hasRequiredRoles() {
        return getScrumMaster() != null &&
                getProductOwner() != null &&
                !getDevelopers().isEmpty();
    }

    /**
     * Get team performance rating based on achievements and velocity
     */
    public BigDecimal getPerformanceRating() {
        BigDecimal velocityScore = getAverageVelocity();
        BigDecimal achievementScore = new BigDecimal(getTotalPoints());
        BigDecimal progressScore = getCurrentProgress();

        // Weighted calculation (adjust weights as needed)
        BigDecimal rating = velocityScore.multiply(new BigDecimal("0.4"))
                .add(achievementScore.multiply(new BigDecimal("0.3")))
                .add(progressScore.multiply(new BigDecimal("0.3")));

        return rating.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Get team status description
     */
    public String getTeamStatus() {
        BigDecimal progress = getCurrentProgress();

        if (progress.compareTo(new BigDecimal("100.0")) >= 0) {
            return "Completed";
        } else if (progress.compareTo(new BigDecimal("75.0")) >= 0) {
            return "Almost Done";
        } else if (progress.compareTo(new BigDecimal("50.0")) >= 0) {
            return "Good Progress";
        } else if (progress.compareTo(new BigDecimal("25.0")) >= 0) {
            return "In Progress";
        } else {
            return "Getting Started";
        }
    }

    /**
     * Get number of achievements in current project
     */
    public Integer getAchievementCount() {
        return teamAchievements.size();
    }

    // === UTILITY METHODS ===
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Team)) return false;
        Team team = (Team) o;
        return Objects.equals(id, team.id) &&
                Objects.equals(name, team.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", project=" + (project != null ? project.getName() : "null") +
                ", memberCount=" + getMemberCount() +
                ", progress=" + getCurrentProgress() + "%" +
                ", points=" + getTotalPoints() +
                '}';
    }
}