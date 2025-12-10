package com.eduscrum.upt.Ubereats.dto.response;

import com.eduscrum.upt.Ubereats.entity.enums.ProjectStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for project response data.
 * Contains project details and associated course info.
 *
 * @author UberEats
 * @version 0.2.1
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectResponse {

    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private ProjectStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long courseId;
    private String courseName;

    /** Default constructor. */
    public ProjectResponse() {
    }

    public ProjectResponse(Long id, String name, String description,
            LocalDate startDate, LocalDate endDate,
            ProjectStatus status,
            LocalDateTime createdAt, LocalDateTime updatedAt,
            Long courseId, String courseName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.courseId = courseId;
        this.courseName = courseName;
    }

    /** @return The project ID */
    public Long getId() {
        return id;
    }

    /** @return The project name */
    public String getName() {
        return name;
    }

    /** @return The description */
    public String getDescription() {
        return description;
    }

    /** @return The start date */
    public LocalDate getStartDate() {
        return startDate;
    }

    /** @return The end date */
    public LocalDate getEndDate() {
        return endDate;
    }

    /** @return The project status */
    public ProjectStatus getStatus() {
        return status;
    }

    /** @return The creation timestamp */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /** @return The update timestamp */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /** @return The course ID */
    public Long getCourseId() {
        return courseId;
    }

    /** @return The course name */
    public String getCourseName() {
        return courseName;
    }

    /** @param id The project ID */
    public void setId(Long id) {
        this.id = id;
    }

    /** @param name The project name */
    public void setName(String name) {
        this.name = name;
    }

    /** @param description The description */
    public void setDescription(String description) {
        this.description = description;
    }

    /** @param startDate The start date */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /** @param endDate The end date */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /** @param status The project status */
    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    /** @param createdAt The creation timestamp */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /** @param updatedAt The update timestamp */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /** @param courseId The course ID */
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    /** @param courseName The course name */
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}
