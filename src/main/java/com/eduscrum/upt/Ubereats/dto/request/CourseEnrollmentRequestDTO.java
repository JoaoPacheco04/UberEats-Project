package com.eduscrum.upt.Ubereats.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for enrolling a student in a course.
 * Contains course and student identifiers.
 *
 * @version 0.2.1 (2025-10-22)
 */
public class CourseEnrollmentRequestDTO {

    @NotNull(message = "Course ID is required")
    private Long courseId;

    @NotNull(message = "Student ID is required")
    private Long studentId;

    /** Default constructor. */
    public CourseEnrollmentRequestDTO() {
    }

    public CourseEnrollmentRequestDTO(Long courseId, Long studentId) {
        this.courseId = courseId;
        this.studentId = studentId;
    }

    /** @return The course ID */
    public Long getCourseId() {
        return courseId;
    }

    /** @param courseId The course ID */
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    /** @return The student ID */
    public Long getStudentId() {
        return studentId;
    }

    /** @param studentId The student ID */
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
}
