package com.eduscrum.upt.Ubereats.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Data Transfer Object for creating a new Project.
 * Contains project details and validation annotations.
 *
 * @author Joao
 * @author Ana
 * @version 0.6.1
 */
public class CreateProjectRequest {

    @NotBlank(message = "Project name is required")
    private String name;

    private String description;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Course id is required")
    private Long courseId;

    /** Default constructor. */
    public CreateProjectRequest() {
    }

    /** @return The project name */
    public String getName() {
        return name;
    }

    /** @param name The project name */
    public void setName(String name) {
        this.name = name;
    }

    /** @return The project description */
    public String getDescription() {
        return description;
    }

    /** @param description The project description */
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

    /** @return The course ID */
    public Long getCourseId() {
        return courseId;
    }

    /** @param courseId The course ID */
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}
