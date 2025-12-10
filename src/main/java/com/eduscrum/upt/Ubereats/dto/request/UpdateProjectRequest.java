package com.eduscrum.upt.Ubereats.dto.request;

import java.time.LocalDate;

/**
 * Data Transfer Object for updating an existing Project.
 * Contains optional fields that can be updated.
 *
 * @version 0.2.1 (2025-10-22)
 */
public class UpdateProjectRequest {

    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;

    public UpdateProjectRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
