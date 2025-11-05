package com.eduscrum.upt.Ubereats.repository;

import com.eduscrum.upt.Ubereats.entity.User;
import com.eduscrum.upt.Ubereats.entity.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity
 * Extends JpaRepository to inherit basic CRUD operations
 * Spring Data JPA automatically implements these methods
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
}
