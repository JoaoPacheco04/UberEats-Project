package com.eduscrum.upt.Ubereats.dto.request;

import com.eduscrum.upt.Ubereats.entity.enums.SprintStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Data Transfer Object for creating or updating a Sprint.
 * Contains sprint details and validation annotations.
 *
 * @version 1.1.0 (2025-12-08)
 */
public class SprintRequestDTO {

    @NotNull(message = "Sprint number is required")
    private Integer sprintNumber;

    @NotBlank(message = "Name is required")
    private String name;

    private String goal;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private SprintStatus status = SprintStatus.PLANNED;

    @NotNull(message = "Project ID is required")
    private Long projectId;

    /** Default constructor. */
    public SprintRequestDTO() {
    }

    public SprintRequestDTO(Integer sprintNumber, String name, String goal,
            LocalDate startDate, LocalDate endDate, SprintStatus status, Long projectId) {
        this.sprintNumber = sprintNumber;
        this.name = name;
        this.goal = goal;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.projectId = projectId;
    }

    /** @return The sprint number */
    public Integer getSprintNumber() {
        return sprintNumber;
    }

    /** @param sprintNumber The sprint number */
    public void setSprintNumber(Integer sprintNumber) {
        this.sprintNumber = sprintNumber;
    }

    /** @return The sprint name */
    public String getName() {
        return name;
    }

    /** @param name The sprint name */
    public void setName(String name) {
        this.name = name;
    }

    /** @return The sprint goal */
    public String getGoal() {
        return goal;
    }

    /** @param goal The sprint goal */
    public void setGoal(String goal) {
        this.goal = goal;
    }

    /** @return The start date */
    public LocalDate getStartDate() {
        return startDate;
    }

    /** @param startDate The start date */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /** @return The end date */
    public LocalDate getEndDate() {
        return endDate;
    }

    /** @param endDate The end date */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /** @return The sprint status */
    public SprintStatus getStatus() {
        return status;
    }

    /** @param status The sprint status */
    public void setStatus(SprintStatus status) {
        this.status = status;
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
