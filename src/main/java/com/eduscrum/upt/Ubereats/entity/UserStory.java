package com.eduscrum.upt.Ubereats.entity;

import com.eduscrum.upt.Ubereats.entity.enums.StoryPriority;
import com.eduscrum.upt.Ubereats.entity.enums.StoryStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * JPA entity representing a user story in the EduScrum platform.
 * Contains task details, status, priority, and assignment info.
 *
 * @version 1.2.0 (2025-12-10)
 */
@Entity
@Table(name = "user_stories")
public class UserStory {
    // === ATTRIBUTES ===
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "story_points")
    private Integer storyPoints = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StoryStatus status = StoryStatus.TODO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StoryPriority priority = StoryPriority.MEDIUM;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // === RELATIONS ===
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sprint_id", nullable = false)
    private Sprint sprint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    // === CONSTRUCTORS ===
    public UserStory() {
    }

    public UserStory(String title, String description, Integer storyPoints,
            Sprint sprint, Team team, User createdBy) {
        this.title = title;
        this.description = description;
        this.storyPoints = storyPoints;
        this.sprint = sprint;
        this.team = team;
        this.createdBy = createdBy;
        this.status = StoryStatus.TODO;
        this.priority = StoryPriority.MEDIUM;
    }

    public UserStory(String title, String description, Integer storyPoints,
            StoryPriority priority, Sprint sprint, Team team, User createdBy) {
        this(title, description, storyPoints, sprint, team, createdBy);
        this.priority = priority;
    }

    // === GETTERS & SETTERS ===
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(Integer storyPoints) {
        this.storyPoints = storyPoints;
    }

    public StoryStatus getStatus() {
        return status;
    }

    public void setStatus(StoryStatus status) {
        this.status = status;
    }

    public StoryPriority getPriority() {
        return priority;
    }

    public void setPriority(StoryPriority priority) {
        this.priority = priority;
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

    public Sprint getSprint() {
        return sprint;
    }

    public void setSprint(Sprint sprint) {
        this.sprint = sprint;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(User assignedTo) {
        this.assignedTo = assignedTo;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    // === BUSINESS METHODS ===

    /**
     * Checks if user story is completed.
     *
     * @return true if status is DONE, false otherwise
     */
    public boolean isCompleted() {
        return status == StoryStatus.DONE;
    }

    /**
     * Checks if user story is in progress.
     *
     * @return true if status is IN_PROGRESS, false otherwise
     */
    public boolean isInProgress() {
        return status == StoryStatus.IN_PROGRESS;
    }

    /**
     * Checks if user story is in review.
     *
     * @return true if status is IN_REVIEW, false otherwise
     */
    public boolean isInReview() {
        return status == StoryStatus.IN_REVIEW;
    }

    /**
     * Checks if user story is pending (to do).
     *
     * @return true if status is TODO, false otherwise
     */
    public boolean isPending() {
        return status == StoryStatus.TODO;
    }

    /**
     * Gets status color for UI display.
     *
     * @return The CSS color name for the status
     */
    public String getStatusColor() {
        switch (status) {
            case DONE:
                return "green";
            case IN_REVIEW:
                return "blue";
            case IN_PROGRESS:
                return "orange";
            case TODO:
                return "gray";
            default:
                return "gray";
        }
    }

    /**
     * Gets priority color for UI display.
     *
     * @return The CSS color name for the priority
     */
    public String getPriorityColor() {
        switch (priority) {
            case CRITICAL:
                return "red";
            case HIGH:
                return "orange";
            case MEDIUM:
                return "yellow";
            case LOW:
                return "green";
            default:
                return "gray";
        }
    }

    /**
     * Gets priority icon for UI display.
     *
     * @return The emoji icon for the priority
     */
    public String getPriorityIcon() {
        switch (priority) {
            case CRITICAL:
                return "🔥";
            case HIGH:
                return "⚠️";
            case MEDIUM:
                return "📋";
            case LOW:
                return "📝";
            default:
                return "📋";
        }
    }

    /**
     * Checks if user story can be assigned to a user.
     *
     * @param user The user to check
     * @return true if user is a team member, false otherwise
     */
    public boolean canAssignTo(User user) {
        return team.hasMember(user);
    }

    /**
     * Assigns user story to a team member.
     *
     * @param user The user to assign to
     */
    public void assignTo(User user) {
        if (canAssignTo(user)) {
            this.assignedTo = user;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Unassigns user story from current assignee.
     */
    public void unassign() {
        this.assignedTo = null;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Moves user story to next workflow status.
     */
    public void moveToNextStatus() {
        switch (status) {
            case TODO:
                this.status = StoryStatus.IN_PROGRESS;
                break;
            case IN_PROGRESS:
                this.status = StoryStatus.IN_REVIEW;
                break;
            case IN_REVIEW:
                this.status = StoryStatus.DONE;
                break;
            case DONE:
                break;
        }
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Moves user story to previous workflow status.
     */
    public void moveToPreviousStatus() {
        switch (status) {
            case DONE:
                this.status = StoryStatus.IN_REVIEW;
                break;
            case IN_REVIEW:
                this.status = StoryStatus.IN_PROGRESS;
                break;
            case IN_PROGRESS:
                this.status = StoryStatus.TODO;
                break;
            case TODO:
                break;
        }
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Checks if user story is assigned to someone.
     *
     * @return true if assigned, false otherwise
     */
    public boolean isAssigned() {
        return assignedTo != null;
    }

    /**
     * Gets assigned user's name or "Unassigned".
     *
     * @return The assignee name or "Unassigned"
     */
    public String getAssignedUserName() {
        return isAssigned() ? assignedTo.getFullName() : "Unassigned";
    }

    /**
     * Checks if user story can be moved to next status.
     *
     * @return true if not DONE, false otherwise
     */
    public boolean canMoveToNextStatus() {
        return status != StoryStatus.DONE;
    }

    /**
     * Checks if user story can be moved to previous status.
     *
     * @return true if not TODO, false otherwise
     */
    public boolean canMoveToPreviousStatus() {
        return status != StoryStatus.TODO;
    }

    /**
     * Gets estimated effort level based on story points.
     *
     * @return The effort level description
     */
    public String getEffortLevel() {
        if (storyPoints == null || storyPoints == 0)
            return "Not estimated";
        if (storyPoints <= 3)
            return "Small";
        if (storyPoints <= 8)
            return "Medium";
        if (storyPoints <= 13)
            return "Large";
        return "Very Large";
    }

    /**
     * Checks if user story is blocked.
     *
     * @return true if blocked, false otherwise
     */
    public boolean isBlocked() {
        return false;
    }

    // === UTILITY METHODS ===
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof UserStory))
            return false;
        UserStory userStory = (UserStory) o;
        return Objects.equals(id, userStory.id) &&
                Objects.equals(title, userStory.title) &&
                Objects.equals(sprint, userStory.sprint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, sprint);
    }

    @Override
    public String toString() {
        return "UserStory{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", storyPoints=" + storyPoints +
                ", sprint=" + (sprint != null ? sprint.getName() : "null") +
                ", assignedTo=" + getAssignedUserName() +
                '}';
    }
}
