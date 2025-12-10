package com.eduscrum.upt.Ubereats.entity;

import com.eduscrum.upt.Ubereats.entity.enums.Semester;
import com.eduscrum.upt.Ubereats.entity.enums.ProjectStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * JPA entity representing a course in the EduScrum platform.
 * Contains projects, enrollments, and is taught by a teacher.
 *
 * @author UberEatss
 * @version 0.2.1
 */
@Entity
@Table(name = "courses")
public class Course {
    // === ATTRIBUTES ===
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Semester semester;

    @Column(name = "academic_year", nullable = false, length = 20)
    private String academicYear;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // === RELATIONS ===
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Project> projects = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CourseEnrollment> enrollments = new ArrayList<>();

    // === CONSTRUCTORS ===
    public Course() {
    }

    public Course(String name, String code, String description, Semester semester,
            String academicYear, User teacher) {
        this.name = name;
        this.code = code;
        this.description = description;
        this.semester = semester;
        this.academicYear = academicYear;
        this.teacher = teacher;
        this.isActive = true;
    }

    // === GETTERS & SETTERS ===
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public List<CourseEnrollment> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(List<CourseEnrollment> enrollments) {
        this.enrollments = enrollments;
    }

    // === BUSINESS METHODS ===

    /**
     * Gets the number of enrolled students.
     *
     * @return The count of active enrollments
     */
    public Integer getStudentCount() {
        return (int) enrollments.stream()
                .filter(CourseEnrollment::isActiveEnrollment)
                .count();
    }

    /**
     * Gets the number of projects in this course.
     *
     * @return The project count
     */
    public Integer getProjectCount() {
        return projects.size();
    }

    /**
     * Gets list of enrolled students.
     *
     * @return List of User objects for active enrollments
     */
    public List<User> getEnrolledStudents() {
        return enrollments.stream()
                .filter(CourseEnrollment::isActiveEnrollment)
                .map(CourseEnrollment::getStudent)
                .collect(Collectors.toList());
    }

    /**
     * Calculate average team score across all projects
     */
    public Double getAverageTeamScore() {
        if (projects.isEmpty())
            return 0.0;

        double totalScore = projects.stream()
                .flatMap(project -> project.getTeams().stream())
                .mapToDouble(team -> {
                    // Calculate team score based on achievements and performance
                    int teamPoints = team.getTeamAchievements().stream()
                            .mapToInt(achievement -> achievement.getBadge().getPoints())
                            .sum();
                    int memberCount = team.getActiveMembers().size();
                    return memberCount > 0 ? (double) teamPoints / memberCount : 0.0;
                })
                .sum();

        long teamCount = projects.stream()
                .flatMap(project -> project.getTeams().stream())
                .count();

        return teamCount > 0 ? totalScore / teamCount : 0.0;
    }

    /**
     * Checks if course is active.
     *
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return Boolean.TRUE.equals(isActive);
    }

    /**
     * Gets active projects only.
     *
     * @return List of projects with ACTIVE status
     */
    public List<Project> getActiveProjects() {
        return projects.stream()
                .filter(project -> project.getStatus() == ProjectStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    /**
     * Gets completed projects.
     *
     * @return List of projects with COMPLETED status
     */
    public List<Project> getCompletedProjects() {
        return projects.stream()
                .filter(project -> project.getStatus() == ProjectStatus.COMPLETED)
                .collect(Collectors.toList());
    }

    /**
     * Checks if student is enrolled in this course.
     *
     * @param student The student to check
     * @return true if student has active enrollment, false otherwise
     */
    public boolean isStudentEnrolled(User student) {
        return enrollments.stream()
                .anyMatch(enrollment -> enrollment.getStudent().equals(student) &&
                        enrollment.isActiveEnrollment());
    }

    /**
     * Gets course display name with code.
     *
     * @return Formatted display name (e.g., "CS101 - Computer Science")
     */
    public String getDisplayName() {
        return code + " - " + name;
    }

    // === UTILITY METHODS ===
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Course))
            return false;
        Course course = (Course) o;
        return Objects.equals(id, course.id) &&
                Objects.equals(code, course.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code);
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", academicYear='" + academicYear + '\'' +
                ", semester=" + semester +
                '}';
    }
}
