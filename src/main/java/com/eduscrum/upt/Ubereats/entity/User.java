package com.eduscrum.upt.Ubereats.entity;

import com.eduscrum.upt.Ubereats.entity.enums.UserRole;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
public class User {
    // === ATTRIBUTES ===
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private UserRole role;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "student_number", unique = true, length = 20)
    private String studentNumber;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // === RELATIONS ===
    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Course> taughtCourses = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TeamMember> teamMemberships = new ArrayList<>();

    @OneToMany(mappedBy = "awardedToUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Achievement> individualAchievements = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CourseEnrollment> enrollments = new ArrayList<>();

    // === CONSTRUCTORS ===
    public User() {}

    public User(String username, String email, String password, UserRole role,
                String firstName, String lastName) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = true;
    }

    public User(String username, String email, String password, UserRole role,
                String firstName, String lastName, String studentNumber) {
        this(username, email, password, role, firstName, lastName);
        this.studentNumber = studentNumber;
    }

    // === GETTERS & SETTERS ===
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<Course> getTaughtCourses() { return taughtCourses; }
    public void setTaughtCourses(List<Course> taughtCourses) { this.taughtCourses = taughtCourses; }

    public List<TeamMember> getTeamMemberships() { return teamMemberships; }
    public void setTeamMemberships(List<TeamMember> teamMemberships) { this.teamMemberships = teamMemberships; }

    public List<Achievement> getIndividualAchievements() { return individualAchievements; }
    public void setIndividualAchievements(List<Achievement> individualAchievements) { this.individualAchievements = individualAchievements; }

    public List<CourseEnrollment> getEnrollments() { return enrollments; }
    public void setEnrollments(List<CourseEnrollment> enrollments) { this.enrollments = enrollments; }

    // === BUSINESS METHODS ===

    /**
     * Total points from all individual achievements across all courses
     */
    public Integer getTotalPoints() {
        return individualAchievements.stream()
                .mapToInt(achievement -> achievement.getBadge().getPoints())
                .sum();
    }

    /**
     * Get all badges earned by this user
     */
    public List<Badge> getEarnedBadges() {
        return individualAchievements.stream()
                .map(Achievement::getBadge)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Calculate average score across all achievements
     */
    public Double getAverageScore() {
        if (individualAchievements.isEmpty()) return 0.0;
        return individualAchievements.stream()
                .mapToInt(achievement -> achievement.getBadge().getPoints())
                .average()
                .orElse(0.0);
    }

    /**
     * Check if user is a teacher
     */
    public boolean isTeacher() {
        return role == UserRole.TEACHER;
    }

    /**
     * Check if user is a student
     */
    public boolean isStudent() {
        return role == UserRole.STUDENT;
    }

    /**
     * Get user's full name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Get points in a specific course
     */
    public Integer getPointsInCourse(Course course) {
        return individualAchievements.stream()
                .filter(achievement -> achievement.getProject() != null &&
                        achievement.getProject().getCourse().equals(course))
                .mapToInt(achievement -> achievement.getBadge().getPoints())
                .sum();
    }

    /**
     * Get points in a specific project
     */
    public Integer getPointsInProject(Project project) {
        return individualAchievements.stream()
                .filter(achievement -> achievement.getProject() != null &&
                        achievement.getProject().equals(project))
                .mapToInt(achievement -> achievement.getBadge().getPoints())
                .sum();
    }

    /**
     * Get team points share from a specific project
     */
    public Integer getTeamPointsInProject(Project project) {
        Optional<TeamMember> teamMember = teamMemberships.stream()
                .filter(member -> member.isActive() &&
                        member.getTeam().getProject().equals(project))
                .findFirst();

        if (teamMember.isPresent()) {
            Team team = teamMember.get().getTeam();
            int teamAchievementPoints = team.getTeamAchievements().stream()
                    .mapToInt(achievement -> achievement.getBadge().getPoints())
                    .sum();
            int teamSize = team.getMembers().stream()
                    .filter(TeamMember::isActive)
                    .toList().size();
            return teamSize > 0 ? teamAchievementPoints / teamSize : 0;
        }
        return 0;
    }

    /**
     * Get combined points (individual + team share) for a project
     */
    public Integer getCombinedPointsInProject(Project project) {
        return getPointsInProject(project) + getTeamPointsInProject(project);
    }

    // === UTILITY METHODS ===
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", fullName='" + getFullName() + '\'' +
                '}';
    }
}