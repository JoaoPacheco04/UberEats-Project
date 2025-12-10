package com.eduscrum.upt.Ubereats.dto.response;

import com.eduscrum.upt.Ubereats.entity.enums.Semester;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for course response data.
 * Contains course details, teacher info, and statistics.
 *
 * @author UberEats
 * @version 1.1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseResponse {

    private Long id;
    private String name;
    private String code;
    private String description;
    private Semester semester;
    private String academicYear;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Teacher info
    private String teacherName;
    private String teacherEmail;

    // Statistics
    private Integer studentCount;
    private Integer projectCount;
    private Double averageTeamScore;

    /** Default constructor. */
    public CourseResponse() {
    }

    public CourseResponse(Long id, String name, String code, String description,
            Semester semester, String academicYear, Boolean isActive,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
        this.semester = semester;
        this.academicYear = academicYear;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** @return The course ID */
    public Long getId() {
        return id;
    }

    /** @param id The course ID */
    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherEmail() {
        return teacherEmail;
    }

    public void setTeacherEmail(String teacherEmail) {
        this.teacherEmail = teacherEmail;
    }

    public Integer getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
    }

    public Integer getProjectCount() {
        return projectCount;
    }

    public void setProjectCount(Integer projectCount) {
        this.projectCount = projectCount;
    }

    public Double getAverageTeamScore() {
        return averageTeamScore;
    }

    public void setAverageTeamScore(Double averageTeamScore) {
        this.averageTeamScore = averageTeamScore;
    }
}
