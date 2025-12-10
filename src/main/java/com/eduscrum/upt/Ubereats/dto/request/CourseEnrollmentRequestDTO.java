package com.eduscrum.upt.Ubereats.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for enrolling a student in a course.
 * Contains course and student identifiers.
 *
 * @version 1.0 (2025-12-10)
 */
public class CourseEnrollmentRequestDTO {

    @NotNull(message = "Course ID is required")
    private Long courseId;

    @NotNull(message = "Student ID is required")
    private Long studentId;

    public CourseEnrollmentRequestDTO() {
    }

    public CourseEnrollmentRequestDTO(Long courseId, Long studentId) {
        this.courseId = courseId;
        this.studentId = studentId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
}