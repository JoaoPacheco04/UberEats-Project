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

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByStudentNumber(String studentNumber);

    List<User> findByRole(UserRole role);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByStudentNumber(String studentNumber);

    List<User> findByIsActiveTrue();

    @org.springframework.data.jpa.repository.Query("SELECT s FROM User s JOIN s.enrollments e WHERE e.course.id = :courseId")
    List<User> findStudentsByCourseId(@org.springframework.data.repository.query.Param("courseId") Long courseId);
}
