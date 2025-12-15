package com.eduscrum.upt.Ubereats.dto.request;

import com.eduscrum.upt.Ubereats.entity.enums.ScrumRole;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for adding a member to a team.
 * Includes user ID and role assignment.
 *
 * @author Joao
 * @author Ana
 * @version 1.1.0
 */
public class AddMemberRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Role is required")
    private ScrumRole role;

    /** Default constructor. */
    public AddMemberRequest() {
    }

    public AddMemberRequest(Long userId, ScrumRole role) {
        this.userId = userId;
        this.role = role;
    }

    /**
     * Gets the user ID.
     *
     * @return The user ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Sets the user ID.
     *
     * @param userId The user ID
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * Gets the Scrum role.
     *
     * @return The Scrum role
     */
    public ScrumRole getRole() {
        return role;
    }

    /**
     * Sets the Scrum role.
     *
     * @param role The Scrum role
     */
    public void setRole(ScrumRole role) {
        this.role = role;
    }
}
