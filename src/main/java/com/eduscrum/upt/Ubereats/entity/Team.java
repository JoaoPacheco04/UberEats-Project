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

/**
 * JPA entity representing a team in the EduScrum platform.
 * Contains members with Scrum roles, projects, analytics, and achievements.
 *
 * @author UberEats
 * @version 0.9.1
 */
@Entity
@Table(name = "teams")
public class Team {
    // === ATTRIBUTES ===
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    private String name;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // === RELATIONS ===
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "team_projects", joinColumns = @JoinColumn(name = "team_id"), inverseJoinColumns = @JoinColumn(name = "project_id"))
    private List<Project> projects = new ArrayList<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TeamMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Analytic> analytics = new ArrayList<>();

    @OneToMany(mappedBy = "awardedToTeam", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Achievement> teamAchievements = new ArrayList<>();

    // === CONSTRUCTORS ===
    public Team() {
    }

    public Team(String name) {
        this.name = name;
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

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public List<TeamMember> getMembers() {
        return members;
    }

    public void setMembers(List<TeamMember> members) {
        this.members = members;
    }

    public List<Analytic> getAnalytics() {
        return analytics;
    }

    public void setAnalytics(List<Analytic> analytics) {
        this.analytics = analytics;
    }

    public List<Achievement> getTeamAchievements() {
        return teamAchievements;
    }

    public void setTeamAchievements(List<Achievement> teamAchievements) {
        this.teamAchievements = teamAchievements;
    }

    // === BUSINESS METHODS ===

    /**
     * Adds a project to this team's project list.
     *
     * @param project The project to add
     */
    public void addProject(Project project) {
        if (!this.projects.contains(project)) {
            this.projects.add(project);
            project.getTeams().add(this);
        }
    }

    /**
     * Gets the number of active members in this team.
     *
     * @return The count of active team members
     */
    public Integer getMemberCount() {
        return getActiveMembers().size();
    }

    /**
     * Gets the Scrum Master of this team.
     *
     * @return The User assigned as Scrum Master, or null if not assigned
     */
    public User getScrumMaster() {
        return members.stream()
                .filter(member -> member.getRole() == ScrumRole.SCRUM_MASTER && member.getIsActive())
                .map(TeamMember::getUser)
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets the Product Owner of this team.
     *
     * @return The User assigned as Product Owner, or null if not assigned
     */
    public User getProductOwner() {
        return members.stream()
                .filter(member -> member.getRole() == ScrumRole.PRODUCT_OWNER && member.getIsActive())
                .map(TeamMember::getUser)
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets all developers in this team.
     *
     * @return List of Users with DEVELOPER role
     */
    public List<User> getDevelopers() {
        return members.stream()
                .filter(member -> member.getRole() == ScrumRole.DEVELOPER && member.getIsActive())
                .map(TeamMember::getUser)
                .collect(Collectors.toList());
    }

    /**
     * Calculates total points from team achievements.
     *
     * @return The sum of all badge points earned by the team
     */
    public Integer getTotalPoints() {
        return teamAchievements.stream()
                .mapToInt(achievement -> achievement.getBadge().getPoints())
                .sum();
    }

    /**
     * Calculates average velocity from progress metrics.
     *
     * @return The average velocity as BigDecimal
     */
    public BigDecimal getAverageVelocity() {
        if (analytics.isEmpty())
            return BigDecimal.ZERO;

        BigDecimal totalVelocity = analytics.stream()
                .map(metric -> metric.getVelocity() != null ? metric.getVelocity() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalVelocity.divide(
                new BigDecimal(analytics.size()),
                2,
                RoundingMode.HALF_UP);
    }

    /**
     * Calculates current progress percentage.
     *
     * @return The progress as a percentage (0-100)
     */
    public BigDecimal getCurrentProgress() {
        if (analytics.isEmpty())
            return BigDecimal.ZERO;
        Analytic latest = getLatestAnalytic();
        if (latest == null || latest.getTotalTasks() == 0)
            return BigDecimal.ZERO;

        return new BigDecimal(latest.getCompletedTasks())
                .divide(new BigDecimal(latest.getTotalTasks()), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100.0"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Gets all active team members.
     *
     * @return List of active TeamMembers
     */
    public List<TeamMember> getActiveMembers() {
        return members.stream()
                .filter(member -> member.getIsActive())
                .collect(Collectors.toList());
    }

    /**
     * Checks if a user is an active member of this team.
     *
     * @param user The user to check
     * @return true if the user is an active member, false otherwise
     */
    public boolean hasMember(User user) {
        return members.stream()
                .anyMatch(member -> member.getUser().equals(user) && member.getIsActive());
    }

    /**
     * Gets the most recent analytic record for this team.
     *
     * @return The latest Analytic, or null if none exist
     */
    public Analytic getLatestAnalytic() {
        return analytics.stream()
                .max(Comparator.comparing(Analytic::getRecordedDate))
                .orElse(null);
    }

    /**
     * Gets the team mood from the latest analytic.
     *
     * @return The current TeamMood, or null if no analytics exist
     */
    public TeamMood getCurrentTeamMood() {
        Analytic latest = getLatestAnalytic();
        return latest != null ? latest.getTeamMood() : null;
    }

    /**
     * Get team score (points per member)
     */
    public BigDecimal getTeamScore() {
        int totalPoints = getTotalPoints();
        int memberCount = getMemberCount();

        if (memberCount == 0)
            return BigDecimal.ZERO;

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
        if (this == o)
            return true;
        if (!(o instanceof Team))
            return false;
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
                ", projects=" + projects.stream().map(Project::getName).collect(Collectors.joining(", ")) +
                ", memberCount=" + getMemberCount() +
                ", progress=" + getCurrentProgress() + "%" +
                ", points=" + getTotalPoints() +
                '}';
    }
}
