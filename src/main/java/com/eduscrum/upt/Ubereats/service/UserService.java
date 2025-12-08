package com.eduscrum.upt.Ubereats.service;

import com.eduscrum.upt.Ubereats.entity.User;
import com.eduscrum.upt.Ubereats.entity.enums.UserRole;
import com.eduscrum.upt.Ubereats.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import com.eduscrum.upt.Ubereats.entity.Achievement;
import com.eduscrum.upt.Ubereats.entity.Team;
import com.eduscrum.upt.Ubereats.entity.TeamMember;
import java.util.Optional;

/**
 * Service class for user management operations
 * Handles business logic for user registration, retrieval, and management
 * Uses constructor injection for better testability and safety
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // === USER REGISTRATION ===
    public User registerUser(String username, String email, String password, String firstName,
            String lastName, UserRole role, String studentNumber) {

        // Validate input parameters
        validateRegistrationInput(username, email, password, firstName, lastName, role, studentNumber);

        // Check for existing users
        validateUserUniqueness(username, email, studentNumber, role);

        // Create and save new user
        User user = createUserEntity(username, email, password, firstName, lastName, role, studentNumber);
        return userRepository.save(user);
    }

    // Validates all registration input parameter
    private void validateRegistrationInput(String username, String email, String password,
            String firstName, String lastName, UserRole role,
            String studentNumber) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }

        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }

        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }

        // Validate student number for students
        if (role == UserRole.STUDENT && (studentNumber == null || studentNumber.trim().isEmpty())) {
            throw new IllegalArgumentException("Student number is required for students");
        }

        // Validate email format (basic validation)
        if (!email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        // Validate password strength (basic validation)
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
    }

    // Checks if username, email, or student number already exist
    private void validateUserUniqueness(String username, String email, String studentNumber, UserRole role) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username '" + username + "' is already taken");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email '" + email + "' is already registered");
        }

        // Validate student number uniqueness for students
        if (role == UserRole.STUDENT && userRepository.existsByStudentNumber(studentNumber)) {
            throw new IllegalArgumentException("Student number '" + studentNumber + "' is already registered");
        }
    }

    // Creates a new User entity with the provided data
    private User createUserEntity(String username, String email, String password,
            String firstName, String lastName, UserRole role,
            String studentNumber) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // Encrypt password
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role);
        user.setStudentNumber(studentNumber);
        user.setIsActive(true); // Activate user by default

        return user;
    }

    // === USER RETRIEVAL METHODS ===

    // Finds user by email address
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Finds user by username
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Finds user by database ID
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    // Finds all users by specific role
    @Transactional(readOnly = true)
    public List<User> findAllByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    // === USER EXISTENCE CHECKS ===

    // Checks if email already exists in database
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // Checks if username already exists in database
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    // Verifies user credentials (email and password)

    @Transactional(readOnly = true)
    public boolean verifyCredentials(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        return passwordEncoder.matches(password, user.getPassword());
    }

    // === GLOBAL SCORE CALCULATION ===

    /**
     * Calculate global score for a user.
     * Logic: Sum(Individual_Badge_Points) + Sum(Team_Badge_Points / Team_Size)
     */
    @Transactional(readOnly = true)
    public Integer calculateGlobalScore(Long userId) {
        User user = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // 1. Sum Individual Achievements
        int individualScore = user.getIndividualAchievements().stream()
                .mapToInt(Achievement::getPoints)
                .sum();

        // 2. Sum Team Achievements (divided by team size)
        int teamScore = 0;
        for (TeamMember membership : user.getTeamMemberships()) {
            if (Boolean.TRUE.equals(membership.getIsActive())) {
                Team team = membership.getTeam();
                List<Achievement> teamAchievements = team.getTeamAchievements();

                if (teamAchievements.isEmpty())
                    continue;

                int teamTotalPoints = teamAchievements.stream()
                        .mapToInt(Achievement::getPoints)
                        .sum();

                long activeMembersCount = team.getMembers().stream()
                        .filter(m -> Boolean.TRUE.equals(m.getIsActive()))
                        .count();

                if (activeMembersCount > 0) {
                    teamScore += teamTotalPoints / activeMembersCount;
                }
            }
        }

        return individualScore + teamScore;
    }
}
