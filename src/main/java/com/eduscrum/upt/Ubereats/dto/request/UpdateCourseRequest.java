package com.eduscrum.upt.Ubereats.dto.request;

import com.eduscrum.upt.Ubereats.entity.enums.Semester;
import jakarta.validation.constraints.Size;
/*
    * DTO for updating an existing Course.
    * Contains fields that can be updated with validation annotations.
 */
public class UpdateCourseRequest {

    @Size(max = 200, message = "Course name must not exceed 200 characters")
    private String name;

    private String description;

    private Semester semester;

    @Size(max = 20, message = "Academic year must not exceed 20 characters")
    private String academicYear;

    private Boolean isActive;

    // Constructors, Getters and Setters
    public UpdateCourseRequest() {}

    public UpdateCourseRequest(String name, String description, Semester semester, String academicYear, Boolean isActive) {
        this.name = name;
        this.description = description;
        this.semester = semester;
        this.academicYear = academicYear;
        this.isActive = isActive;
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

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}