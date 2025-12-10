package com.eduscrum.upt.Ubereats.dto.response;

import com.eduscrum.upt.Ubereats.entity.TeamMember;
import com.eduscrum.upt.Ubereats.entity.enums.ScrumRole;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for team member response data.
 * Includes member details, role, and status information.
 *
 * @version 0.8.0 (2025-11-20)
 */
public class TeamMemberResponse {
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private ScrumRole role;
    private Boolean active;
    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;
    private Long duration;

    /**
     * Constructs a TeamMemberResponse from a TeamMember entity.
     *
     * @param teamMember The team member entity
     */
    public TeamMemberResponse(TeamMember teamMember) {
        this.id = teamMember.getId();
        this.userId = teamMember.getUser().getId();
        this.userName = teamMember.getUser().getFullName();
        this.userEmail = teamMember.getUser().getEmail();
        this.role = teamMember.getRole();
        this.active = teamMember.getIsActive();
        this.joinedAt = teamMember.getJoinedAt();
        this.leftAt = teamMember.getLeftAt();
        this.duration = teamMember.getDuration();
    }

    /** @return The team member ID */
    public Long getId() {
        return id;
    }

    /** @return The user ID */
    public Long getUserId() {
        return userId;
    }

    /** @return The user name */
    public String getUserName() {
        return userName;
    }

    /** @return The user email */
    public String getUserEmail() {
        return userEmail;
    }

    /** @return The Scrum role */
    public ScrumRole getRole() {
        return role;
    }

    /** @return Whether active */
    public Boolean getActive() {
        return active;
    }

    /** @return The join timestamp */
    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    /** @return The leave timestamp */
    public LocalDateTime getLeftAt() {
        return leftAt;
    }

    /** @return The membership duration */
    public Long getDuration() {
        return duration;
    }
}
