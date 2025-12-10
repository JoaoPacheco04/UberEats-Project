package com.eduscrum.upt.Ubereats.repository;

import com.eduscrum.upt.Ubereats.entity.User;
import com.eduscrum.upt.Ubereats.entity.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity.
 * Extends JpaRepository to inherit basic CRUD operations.
 *
 * @version 0.6.1 (2025-11-12)
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by email.
     *
     * @param email The email address
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by username.
     *
     * @param username The username
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by student number.
     *
     * @param studentNumber The student number
     * @return Optional containing the user if found
     */
    Optional<User> findByStudentNumber(String studentNumber);

    /**
     * Finds users by role.
     *
     * @param role The user role
     * @return List of users with the given role
     */
    List<User> findByRole(UserRole role);

    /**
     * Checks if email exists.
     *
     * @param email The email to check
     * @return true if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Checks if username exists.
     *
     * @param username The username to check
     * @return true if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Checks if student number exists.
     *
     * @param studentNumber The student number to check
     * @return true if student number exists
     */
    boolean existsByStudentNumber(String studentNumber);

    /**
     * Finds all active users.
     *
     * @return List of active users
     */
    List<User> findByIsActiveTrue();

    /**
     * Finds students enrolled in a course.
     *
     * @param courseId The course ID
     * @return List of students in the course
     */
    @org.springframework.data.jpa.repository.Query("SELECT s FROM User s JOIN s.enrollments e WHERE e.course.id = :courseId")
    List<User> findStudentsByCourseId(@org.springframework.data.repository.query.Param("courseId") Long courseId);
}
