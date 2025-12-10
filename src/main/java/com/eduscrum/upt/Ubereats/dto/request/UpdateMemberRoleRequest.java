package com.eduscrum.upt.Ubereats.dto.request;

import com.eduscrum.upt.Ubereats.entity.enums.ScrumRole;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for updating a team member's role.
 * Contains the new Scrum role to assign.
 *
 * @version 1.0 (2025-12-10)
 */
public class UpdateMemberRoleRequest {

    @NotNull(message = "Role is required")
    private ScrumRole role;

    // Constructors
    public UpdateMemberRoleRequest() {
    }

    public UpdateMemberRoleRequest(ScrumRole role) {
        this.role = role;
    }

    // Getters and Setters
    public ScrumRole getRole() {
        return role;
    }

    public void setRole(ScrumRole role) {
        this.role = role;
    }
}
