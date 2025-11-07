package com.eduscrum.upt.Ubereats.dto.request;

import com.eduscrum.upt.Ubereats.entity.enums.ScrumRole;
import jakarta.validation.constraints.NotNull;

/*
    * DTO for adding a member to a team.
    * Includes user ID and role.
 */
public class AddMemberRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Role is required")
    private ScrumRole role;

    // Constructors
    public AddMemberRequest() {}

    public AddMemberRequest(Long userId, ScrumRole role) {
        this.userId = userId;
        this.role = role;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public ScrumRole getRole() { return role; }
    public void setRole(ScrumRole role) { this.role = role; }
}