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
import com.eduscrum.upt.Ubereats.entity.UserStory;
import com.eduscrum.upt.Ubereats.entity.enums.StoryStatus;
import com.eduscrum.upt.Ubereats.repository.UserStoryRepository;
import java.util.Optional;

/**
 * Service class for user management operations in the EduScrum platform.
 * Handles user registration, authentication, retrieval, and global score
 * calculation.
 *
 * @version 0.6.1 (2025-11-12)
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserStoryRepository userStoryRepository;

    /**
     * Constructs a new UserService with required dependencies.
     *
     * @param userRepository      Repository for user data access
     * @param passwordEncoder     Encoder for password hashing
     * @param userStoryRepository Repository for user story data access
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            UserStoryRepository userStoryRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userStoryRepository = userStoryRepository;
    }

    /**
     * Registers a new user in the system.
     *
     * @param username      The username for the new user
     * @param email         The email address for the new user
     * @param password      The password for the new user
     * @param firstName     The first name of the user
     * @param lastName      The last name of the user
     * @param role          The role of the user (STUDENT or TEACHER)
     * @param studentNumber The student number (required for students)
     * @return The created User entity
     * @throws IllegalArgumentException if validation fails
     */
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

    /**
     * Validates all registration input parameters.
     *
     * @param username      The username to validate
     * @param email         The email to validate
     * @param password      The password to validate
     * @param firstName     The first name to validate
     * @param lastName      The last name to validate
     * @param role          The role to validate
     * @param studentNumber The student number to validate
     * @throws IllegalArgumentException if any validation fails
     */
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

    /**
     * Checks if username, email, or student number already exist.
     *
     * @param username      The username to check
     * @param email         The email to check
     * @param studentNumber The student number to check
     * @param role          The role of the user
     * @throws IllegalArgumentException if any value already exists
     */
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

    /**
     * Creates a new User entity with the provided data.
     *
     * @param username      The username for the user
     * @param email         The email for the user
     * @param password      The raw password (will be encoded)
     * @param firstName     The first name of the user
     * @param lastName      The last name of the user
     * @param role          The role of the user
     * @param studentNumber The student number (if applicable)
     * @return The new User entity (not yet persisted)
     */
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

    /**
     * Finds a user by their email address.
     *
     * @param email The email address to search for
     * @return Optional containing the user if found
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Finds a user by their username.
     *
     * @param username The username to search for
     * @return Optional containing the user if found
     */
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Finds a user by their database ID.
     *
     * @param id The ID of the user to find
     * @return Optional containing the user if found
     */
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Finds all users with a specific role.
     *
     * @param role The role to filter by
     * @return List of users with the specified role
     */
    @Transactional(readOnly = true)
    public List<User> findAllByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    /**
     * Checks if an email already exists in the database.
     *
     * @param email The email to check
     * @return true if email exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Checks if a username already exists in the database.
     *
     * @param username The username to check
     * @return true if username exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Verifies user credentials (email and password).
     *
     * @param email    The email to verify
     * @param password The raw password to verify
     * @return true if credentials are valid, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean verifyCredentials(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        return passwordEncoder.matches(password, user.getPassword());
    }

    /**
     * Calculates the global score for a user.
     * Formula:
     * - Individual Badge Points
     * - Team Badge Points / Team Size
     * - 10 points per completed story (base reward for completion)
     * - 2× Story Points from completed stories (effort-based reward)
     *
     * @param userId The ID of the user
     * @return The calculated global score
     * @throws IllegalArgumentException if user not found
     */
    @Transactional(readOnly = true)
    public Integer calculateGlobalScore(Long userId) {
        User user = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // 1. Sum Individual Achievements (badge points)
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

        // 3. Calculate Story-based Score from completed user stories
        // Use repository to fetch stories assigned to this user
        List<UserStory> userStories = userStoryRepository.findByAssignedToId(userId);
        int storyScore = 0;
        for (UserStory story : userStories) {
            if (story.getStatus() == StoryStatus.DONE) {
                // 25 base points per completed story (reward for completion)
                storyScore += 25;
                // Plus 5× the story points (rewarding harder stories more significantly)
                if (story.getStoryPoints() != null) {
                    storyScore += story.getStoryPoints() * 5;
                }
            }
        }

        return individualScore + teamScore + storyScore;
    }

}
