package com.eduscrum.upt.Ubereats.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for creating a new team.
 * Includes team name and optional project ID.
 *
 * @version 1.0 (2025-12-10)
 */
public class CreateTeamRequest {

    @NotBlank(message = "Team name is required")
    private String name;

    @NotNull(message = "Project ID is required")
    private Long projectId;

    // Constructors
    public CreateTeamRequest() {
    }

    public CreateTeamRequest(String name, Long projectId) {
        this.name = name;
        this.projectId = projectId;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}