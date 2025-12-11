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
    private Long teamId;
    private String teamName;

<<<<<<< HEAD
    /** Default constructor. */
=======
>>>>>>> Yesh_Branch
    public ProjectResponse() {
    }

    public ProjectResponse(Long id, String name, String description,
            LocalDate startDate, LocalDate endDate,
            ProjectStatus status,
            LocalDateTime createdAt, LocalDateTime updatedAt,
<<<<<<< HEAD
            Long courseId, String courseName) {
=======
            Long courseId, String courseName,
            Long teamId, String teamName) {
>>>>>>> Yesh_Branch
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
        this.teamId = teamId;
        this.teamName = teamName;
    }

<<<<<<< HEAD
    /** @return The project ID */
=======
    // === GETTERS & SETTERS ===
>>>>>>> Yesh_Branch
    public Long getId() {
        return id;
    }

<<<<<<< HEAD
    /** @return The project name */
=======
>>>>>>> Yesh_Branch
    public String getName() {
        return name;
    }

<<<<<<< HEAD
    /** @return The description */
=======
>>>>>>> Yesh_Branch
    public String getDescription() {
        return description;
    }

<<<<<<< HEAD
    /** @return The start date */
=======
>>>>>>> Yesh_Branch
    public LocalDate getStartDate() {
        return startDate;
    }

<<<<<<< HEAD
    /** @return The end date */
=======
>>>>>>> Yesh_Branch
    public LocalDate getEndDate() {
        return endDate;
    }

<<<<<<< HEAD
    /** @return The project status */
=======
>>>>>>> Yesh_Branch
    public ProjectStatus getStatus() {
        return status;
    }

<<<<<<< HEAD
    /** @return The creation timestamp */
=======
>>>>>>> Yesh_Branch
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

<<<<<<< HEAD
    /** @return The update timestamp */
=======
>>>>>>> Yesh_Branch
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

<<<<<<< HEAD
    /** @return The course ID */
=======
>>>>>>> Yesh_Branch
    public Long getCourseId() {
        return courseId;
    }

<<<<<<< HEAD
    /** @return The course name */
=======
>>>>>>> Yesh_Branch
    public String getCourseName() {
        return courseName;
    }

<<<<<<< HEAD
    /** @param id The project ID */
=======
    public Long getTeamId() {
        return teamId;
    }

    public String getTeamName() {
        return teamName;
    }

>>>>>>> Yesh_Branch
    public void setId(Long id) {
        this.id = id;
    }

<<<<<<< HEAD
    /** @param name The project name */
=======
>>>>>>> Yesh_Branch
    public void setName(String name) {
        this.name = name;
    }

<<<<<<< HEAD
    /** @param description The description */
=======
>>>>>>> Yesh_Branch
    public void setDescription(String description) {
        this.description = description;
    }

<<<<<<< HEAD
    /** @param startDate The start date */
=======
>>>>>>> Yesh_Branch
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

<<<<<<< HEAD
    /** @param endDate The end date */
=======
>>>>>>> Yesh_Branch
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

<<<<<<< HEAD
    /** @param status The project status */
=======
>>>>>>> Yesh_Branch
    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

<<<<<<< HEAD
    /** @param createdAt The creation timestamp */
=======
>>>>>>> Yesh_Branch
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

<<<<<<< HEAD
    /** @param updatedAt The update timestamp */
=======
>>>>>>> Yesh_Branch
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

<<<<<<< HEAD
    /** @param courseId The course ID */
=======
>>>>>>> Yesh_Branch
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

<<<<<<< HEAD
    /** @param courseName The course name */
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}
=======
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
}
>>>>>>> Yesh_Branch
