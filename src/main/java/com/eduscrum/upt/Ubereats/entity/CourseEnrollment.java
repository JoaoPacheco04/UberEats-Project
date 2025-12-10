package com.eduscrum.upt.Ubereats.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * JPA entity representing a student's enrollment in a course.
 * Links a student to a course with enrollment date.
 *
 * @version 0.5.0 (2025-11-05)
 */
@Entity
@Table(name = "course_enrollments")
public class CourseEnrollment {
    // === ATTRIBUTES ===
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "enrolled_at", updatable = false)
    private LocalDateTime enrolledAt;

    // === RELATIONS ===
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    // === CONSTRUCTORS ===
    public CourseEnrollment() {
    }

    public CourseEnrollment(Course course, User student) {
        this.course = course;
        this.student = student;
        this.enrolledAt = LocalDateTime.now();
    }

    // === GETTERS & SETTERS ===
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

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    // === BUSINESS METHODS ===

    /**
     * Check if enrollment is active
     */
    public boolean isActiveEnrollment() {
        // Enrollment is active if the course is active
        return course != null && course.isActive();
    }

    /**
     * Get enrollment duration in days
     */
    public Long getEnrollmentDuration() {
        return Duration.between(enrolledAt, LocalDateTime.now()).toDays();
    }

    /**
     * Check if student can be enrolled (not already enrolled)
     */
    public boolean canEnroll() {
        return course != null && student != null &&
                !course.getEnrolledStudents().contains(student);
    }

    // === UTILITY METHODS ===
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CourseEnrollment))
            return false;
        CourseEnrollment that = (CourseEnrollment) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(course, that.course) &&
                Objects.equals(student, that.student);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, course, student);
    }

    @Override
    public String toString() {
        return "CourseEnrollment{" +
                "id=" + id +
                ", course=" + (course != null ? course.getCode() : "null") +
                ", student=" + (student != null ? student.getFullName() : "null") +
                ", enrolledAt=" + enrolledAt +
                '}';
    }
}
