package com.eduscrum.upt.Ubereats.dto.request;

import com.eduscrum.upt.Ubereats.entity.enums.ScrumRole;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for updating a team member's role.
 * Contains the new Scrum role to assign.
 *
 * @author UberEats
 * @version 0.1.0
 */
public class UpdateMemberRoleRequest {

    @NotNull(message = "Role is required")
    private ScrumRole role;

    /** Default constructor. */
    public UpdateMemberRoleRequest() {
    }

    public UpdateMemberRoleRequest(ScrumRole role) {
        this.role = role;
    }

    /** @return The Scrum role */
    public ScrumRole getRole() {
        return role;
    }

    /** @param role The Scrum role */
    public void setRole(ScrumRole role) {
        this.role = role;
    }
}
