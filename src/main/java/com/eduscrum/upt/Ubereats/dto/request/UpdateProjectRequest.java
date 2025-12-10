package com.eduscrum.upt.Ubereats.dto.request;

import java.time.LocalDate;

/**
 * Data Transfer Object for updating an existing Project.
 * Contains optional fields that can be updated.
 *
 * @author UberEats
 * @version 0.2.1
 */
public class UpdateProjectRequest {

    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;

    /** Default constructor. */
    public UpdateProjectRequest() {
    }

    /** @return The project name */
    public String getName() {
        return name;
    }

    /** @param name The project name */
    public void setName(String name) {
        this.name = name;
    }

    /** @return The description */
    public String getDescription() {
        return description;
    }

    /** @param description The description */
    public void setDescription(String description) {
        this.description = description;
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
}
