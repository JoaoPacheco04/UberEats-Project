package com.eduscrum.upt.Ubereats.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * JPA entity representing an achievement in the EduScrum platform.
 * Links a badge to a user or team with context (project, sprint).
 *
 * @version 1.1.0 (2025-12-08)
 */
@Entity
@Table(name = "achievements")
public class Achievement {
    // === ATTRIBUTES ===
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @CreationTimestamp
    @Column(name = "awarded_at", updatable = false)
    private LocalDateTime awardedAt;

    // === RELATIONS ===
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_id", nullable = false)
    private Badge badge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "awarded_to_user_id")
    private User awardedToUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "awarded_to_team_id")
    private Team awardedToTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sprint_id")
    private Sprint sprint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "awarded_by")
    private User awardedBy;

    // === CONSTRUCTORS ===
    public Achievement() {
    }

    // Individual achievement constructor
    public Achievement(Badge badge, User awardedToUser, Project project, User awardedBy, String reason) {
        this.badge = badge;
        this.awardedToUser = awardedToUser;
        this.project = project;
        this.awardedBy = awardedBy;
        this.reason = reason;
        this.awardedAt = LocalDateTime.now();
    }

    // Team achievement constructor
    public Achievement(Badge badge, Team awardedToTeam, Project project, User awardedBy, String reason) {
        this.badge = badge;
        this.awardedToTeam = awardedToTeam;
        this.project = project;
        this.awardedBy = awardedBy;
        this.reason = reason;
        this.awardedAt = LocalDateTime.now();
    }

    // === GETTERS & SETTERS ===
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getAwardedAt() {
        return awardedAt;
    }

    public void setAwardedAt(LocalDateTime awardedAt) {
        this.awardedAt = awardedAt;
    }

    public Badge getBadge() {
        return badge;
    }

    public void setBadge(Badge badge) {
        this.badge = badge;
    }

    public User getAwardedToUser() {
        return awardedToUser;
    }

    public void setAwardedToUser(User awardedToUser) {
        this.awardedToUser = awardedToUser;
    }

    public Team getAwardedToTeam() {
        return awardedToTeam;
    }

    public void setAwardedToTeam(Team awardedToTeam) {
        this.awardedToTeam = awardedToTeam;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Sprint getSprint() {
        return sprint;
    }

    public void setSprint(Sprint sprint) {
        this.sprint = sprint;
    }

    public User getAwardedBy() {
        return awardedBy;
    }

    public void setAwardedBy(User awardedBy) {
        this.awardedBy = awardedBy;
    }

    // === BUSINESS METHODS ===

    /**
     * Check if this is a team achievement
     */
    public boolean isTeamAchievement() {
        return awardedToTeam != null;
    }

    /**
     * Check if this is an individual achievement
     */
    public boolean isIndividualAchievement() {
        return awardedToUser != null;
    }

    /**
     * Get points value from the badge
     */
    public Integer getPoints() {
        return badge != null ? badge.getPoints() : 0;
    }

    /**
     * Get recipient name (user or team)
     */
    public String getRecipientName() {
        if (isTeamAchievement()) {
            return awardedToTeam.getName();
        } else if (isIndividualAchievement()) {
            return awardedToUser.getFullName();
        }
        return "Unknown";
    }

    /**
     * Check if this was an automatic award (no awardedBy user)
     */
    public boolean isAutomaticAward() {
        return awardedBy == null;
    }

    /**
     * Get awarded by name or "System" for automatic awards
     */
    public String getAwardedByName() {
        return isAutomaticAward() ? "System" : awardedBy.getFullName();
    }

    // === UTILITY METHODS ===
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Achievement))
            return false;
        Achievement that = (Achievement) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(badge, that.badge) &&
                Objects.equals(awardedAt, that.awardedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, badge, awardedAt);
    }

    @Override
    public String toString() {
        return "Achievement{" +
                "id=" + id +
                ", badge=" + (badge != null ? badge.getName() : "null") +
                ", recipient=" + getRecipientName() +
                ", points=" + getPoints() +
                ", awardedAt=" + awardedAt +
                '}';
    }
}
