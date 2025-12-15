package com.eduscrum.upt.Ubereats.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for creating a new team.
 * Includes team name and optional project ID.
 *
 * @author Joao
 * @author Ana
 * @version 0.8.0
 */
public class CreateTeamRequest {

    @NotBlank(message = "Team name is required")
    private String name;

    @NotNull(message = "Project ID is required")
    private Long projectId;

    /** Default constructor. */
    public CreateTeamRequest() {
    }

    public CreateTeamRequest(String name, Long projectId) {
        this.name = name;
        this.projectId = projectId;
    }

    /** @return The team name */
    public String getName() {
        return name;
    }

    /** @param name The team name */
    public void setName(String name) {
        this.name = name;
    }

    /** @return The project ID */
    public Long getProjectId() {
        return projectId;
    }

    /** @param projectId The project ID */
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
