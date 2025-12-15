package com.eduscrum.upt.Ubereats.dto.request;

import com.eduscrum.upt.Ubereats.entity.enums.Semester;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for creating a new Course.
 * Contains necessary fields and validation annotations.
 *
 * @author Joao
 * @author Ana
 * @version 0.5.0
 */
public class CreateCourseRequest {

    @NotBlank(message = "Course name is required")
    @Size(max = 200, message = "Course name must not exceed 200 characters")
    private String name;

    @NotBlank(message = "Course code is required")
    @Size(max = 50, message = "Course code must not exceed 50 characters")
    private String code;

    private String description;

    @NotNull(message = "Semester is required")
    private Semester semester;

    @NotBlank(message = "Academic year is required")
    @Size(max = 20, message = "Academic year must not exceed 20 characters")
    private String academicYear;

    /** Default constructor. */
    public CreateCourseRequest() {
    }

    public CreateCourseRequest(String name, String code, String description,
            Semester semester, String academicYear) {
        this.name = name;
        this.code = code;
        this.description = description;
        this.semester = semester;
        this.academicYear = academicYear;
    }

    /** @return The course name */
    public String getName() {
        return name;
    }

    /** @param name The course name */
    public void setName(String name) {
        this.name = name;
    }

    /** @return The course code */
    public String getCode() {
        return code;
    }

    /** @param code The course code */
    public void setCode(String code) {
        this.code = code;
    }

    /** @return The course description */
    public String getDescription() {
        return description;
    }

    /** @param description The course description */
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
}
