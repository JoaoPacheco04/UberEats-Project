package com.eduscrum.upt.Ubereats.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.eduscrum.upt.Ubereats.entity.enums.BadgeType;
import com.eduscrum.upt.Ubereats.entity.enums.RecipientType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "badges")
public class Badge {
    // === ATTRIBUTES ===
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer points;

    @Enumerated(EnumType.STRING)
    @Column(name = "badge_type", nullable = false, length = 10)
    private BadgeType badgeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "recipient_type", nullable = false, length = 15)
    private RecipientType recipientType = RecipientType.BOTH;

    @Column(name = "trigger_condition", columnDefinition = "JSON")
    private String triggerCondition;

    @Column(length = 50)
    private String icon = "🏆";

    @Column(length = 7)
    private String color = "#FF5733";

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // === RELATIONS ===
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @OneToMany(mappedBy = "badge", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Achievement> achievements = new ArrayList<>();

    // === CONSTRUCTORS ===
    public Badge() {
    }

    public Badge(String name, String description, Integer points, BadgeType badgeType, User createdBy) {
        this.name = name;
        this.description = description;
        this.points = points;
        this.badgeType = badgeType;
        this.createdBy = createdBy;
        this.isActive = true;
    }

    public Badge(String name, String description, Integer points, BadgeType badgeType,
            String triggerCondition, User createdBy) {
        this(name, description, points, badgeType, createdBy);
        this.triggerCondition = triggerCondition;
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

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public BadgeType getBadgeType() {
        return badgeType;
    }

    public void setBadgeType(BadgeType badgeType) {
        this.badgeType = badgeType;
    }

    public RecipientType getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(RecipientType recipientType) {
        this.recipientType = recipientType;
    }

    public String getTriggerCondition() {
        return triggerCondition;
    }

    public void setTriggerCondition(String triggerCondition) {
        this.triggerCondition = triggerCondition;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
    }

    // === BUSINESS METHODS ===

    /**
     * Check if badge is automatic
     */
    public boolean isAutomatic() {
        return badgeType == BadgeType.AUTOMATIC;
    }

    /**
     * Check if badge is manual
     */
    public boolean isManual() {
        return badgeType == BadgeType.MANUAL;
    }

    /**
     * Parse trigger conditions from JSON
     */
    public Map<String, Object> getTriggerConditions() {
        if (triggerCondition == null || triggerCondition.trim().isEmpty()) {
            return new HashMap<>();
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(triggerCondition, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    /**
     * Check if user meets conditions for automatic award
     */
    public boolean meetsUserConditions(User user, Project project) {
        if (!isAutomatic())
            return false;
        Map<String, Object> conditions = getTriggerConditions();
        return checkUserConditions(user, project, conditions);
    }

    /**
     * Check if team meets conditions for automatic award
     */
    public boolean meetsTeamConditions(Team team) {
        if (!isAutomatic())
            return false;
        Map<String, Object> conditions = getTriggerConditions();
        return checkTeamConditions(team, conditions);
    }

    /**
     * Get number of times this badge has been awarded
     */
    public Integer getAwardCount() {
        return achievements.size();
    }

    /**
     * Check if badge can be awarded (active and meets criteria)
     */
    public boolean canBeAwarded() {
        return Boolean.TRUE.equals(isActive);
    }

    // === PRIVATE HELPER METHODS ===
    private boolean checkUserConditions(User user, Project project, Map<String, Object> conditions) {
        // Example conditions: {"min_tasks_completed": 10, "min_score": 80,
        // "required_badges": ["OnTime"]}
        // Implementation would depend on specific condition types
        if (conditions.isEmpty())
            return true;

        // Placeholder implementation - extend based on your specific conditions
        return conditions.entrySet().stream()
                .allMatch(
                        condition -> checkSingleUserCondition(user, project, condition.getKey(), condition.getValue()));
    }

    private boolean checkTeamConditions(Team team, Map<String, Object> conditions) {
        if (conditions.isEmpty())
            return true;

        // Placeholder implementation - extend based on your specific conditions
        return conditions.entrySet().stream()
                .allMatch(condition -> checkSingleTeamCondition(team, condition.getKey(), condition.getValue()));
    }

    private boolean checkSingleUserCondition(User user, Project project, String key, Object value) {
        // Implement specific condition checks
        switch (key) {
            case "min_tasks_completed":
                // Check if user completed minimum tasks
                return true; // Implement actual logic
            case "min_score":
                // Check if user has minimum score
                return true; // Implement actual logic
            default:
                return true;
        }
    }

    private boolean checkSingleTeamCondition(Team team, String key, Object value) {
        // Implement specific condition checks for teams
        switch (key) {
            case "min_velocity":
                // Check if team has minimum velocity
                return team.getAverageVelocity().compareTo(new BigDecimal(value.toString())) >= 0;
            case "all_tasks_completed":
                // Check if all tasks are completed
                Analytic latest = team.getLatestAnalytic();
                return latest != null && latest.getCompletedTasks() >= latest.getTotalTasks();
            default:
                return true;
        }
    }

    // === UTILITY METHODS ===
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Badge))
            return false;
        Badge badge = (Badge) o;
        return Objects.equals(id, badge.id) &&
                Objects.equals(name, badge.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Badge{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", points=" + points +
                ", badgeType=" + badgeType +
                ", isActive=" + isActive +
                '}';
    }
}