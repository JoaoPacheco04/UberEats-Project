package com.eduscrum.upt.Ubereats.dto.request;

import com.eduscrum.upt.Ubereats.entity.enums.Semester;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for updating an existing Course.
 * Contains optional fields that can be updated with validation.
 *
 * @author UberEats
 * @version 1.2.0
 */
public class UpdateCourseRequest {

    @Size(max = 200, message = "Course name must not exceed 200 characters")
    private String name;

    private String description;

    private Semester semester;

    @Size(max = 20, message = "Academic year must not exceed 20 characters")
    private String academicYear;

    private Boolean isActive;

    /** Default constructor. */
    public UpdateCourseRequest() {
    }

    public UpdateCourseRequest(String name, String description, Semester semester, String academicYear,
            Boolean isActive) {
        this.name = name;
        this.description = description;
        this.semester = semester;
        this.academicYear = academicYear;
        this.isActive = isActive;
    }

    /** @return The course name */
    public String getName() {
        return name;
    }

    /** @param name The course name */
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

    /** @return The semester */
    public Semester getSemester() {
        return semester;
    }

    /** @param semester The semester */
    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    /** @return The academic year */
    public String getAcademicYear() {
        return academicYear;
    }

    /** @param academicYear The academic year */
    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    /** @return Whether active */
    public Boolean getActive() {
        return isActive;
    }

    /** @param active Whether active */
    public void setActive(Boolean active) {
        isActive = active;
    }
}
