package com.eduscrum.upt.Ubereats.entity;

import com.eduscrum.upt.Ubereats.entity.enums.ScrumRole;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "team_members")
public class TeamMember {
    // === ATTRIBUTES ===
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ScrumRole role;

    @CreationTimestamp
    @Column(name = "joined_at", updatable = false)
    private LocalDateTime joinedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // === RELATIONS ===
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // === CONSTRUCTORS ===
    public TeamMember() {
    }

    public TeamMember(Team team, User user, ScrumRole role) {
        this.team = team;
        this.user = user;
        this.role = role;
        this.isActive = true;
        this.joinedAt = LocalDateTime.now();
    }

    // === GETTERS & SETTERS ===
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ScrumRole getRole() {
        return role;
    }

    public void setRole(ScrumRole role) {
        this.role = role;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public LocalDateTime getLeftAt() {
        return leftAt;
    }

    public void setLeftAt(LocalDateTime leftAt) {
        this.leftAt = leftAt;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // === BUSINESS METHODS ===

    /**
     * Check if member is currently active
     */
    public boolean isActiveMember() {
        return Boolean.TRUE.equals(isActive) && leftAt == null;
    }

    /**
     * Calculate membership duration in days
     */
    public Long getDuration() {
        LocalDateTime endDate = leftAt != null ? leftAt : LocalDateTime.now();
        return Duration.between(joinedAt, endDate).toDays();
    }

    /**
     * Leave the team (set as inactive)
     */
    public void leaveTeam() {
        this.isActive = false;
        this.leftAt = LocalDateTime.now();
    }

    /**
     * Check if member has Scrum Master role
     */
    public boolean isScrumMaster() {
        return role == ScrumRole.SCRUM_MASTER;
    }

    /**
     * Check if member has Product Owner role
     */
    public boolean isProductOwner() {
        return role == ScrumRole.PRODUCT_OWNER;
    }

    /**
     * Check if member has Developer role
     */
    public boolean isDeveloper() {
        return role == ScrumRole.DEVELOPER;
    }

    // === UTILITY METHODS ===
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TeamMember))
            return false;
        TeamMember that = (TeamMember) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(team, that.team) &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, team, user);
    }

    @Override
    public String toString() {
        return "TeamMember{" +
                "id=" + id +
                ", user=" + (user != null ? user.getFullName() : "null") +
                ", role=" + role +
                ", team=" + (team != null ? team.getName() : "null") +
                ", isActive=" + isActive +
                '}';
    }
}