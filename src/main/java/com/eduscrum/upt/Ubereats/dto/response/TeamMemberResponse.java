package com.eduscrum.upt.Ubereats.dto.response;

import com.eduscrum.upt.Ubereats.entity.TeamMember;
import com.eduscrum.upt.Ubereats.entity.enums.ScrumRole;

import java.time.LocalDateTime;

/*
    * DTO for team member response.
    * Includes member details and status.
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

    // Constructor from TeamMember entity
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

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public ScrumRole getRole() {
        return role;
    }

    public Boolean getActive() {
        return active;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public LocalDateTime getLeftAt() {
        return leftAt;
    }

    public Long getDuration() {
        return duration;
    }
}