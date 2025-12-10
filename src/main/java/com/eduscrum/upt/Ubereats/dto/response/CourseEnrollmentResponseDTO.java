package com.eduscrum.upt.Ubereats.dto.response;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for course enrollment response data.
 * Contains enrollment details, course and student info.
 *
 * @author UberEats
 * @version 1.0.1
 */
public class CourseEnrollmentResponseDTO {

    private Long id;
    private LocalDateTime enrolledAt;
    private Long courseId;
    private String courseName;
    private Long studentId;
    private String studentName;

    /** Default constructor. */
    public CourseEnrollmentResponseDTO() {
    }

    public CourseEnrollmentResponseDTO(Long id, LocalDateTime enrolledAt, Long courseId, String courseName,
            Long studentId, String studentName) {
        this.id = id;
        this.enrolledAt = enrolledAt;
        this.courseId = courseId;
        this.courseName = courseName;
        this.studentId = studentId;
        this.studentName = studentName;
    }

    /** @return The enrollment ID */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(LocalDateTime enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}
