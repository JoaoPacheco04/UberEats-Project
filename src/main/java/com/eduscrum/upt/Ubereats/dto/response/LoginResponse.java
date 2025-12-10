package com.eduscrum.upt.Ubereats.dto.response;

import com.eduscrum.upt.Ubereats.entity.enums.UserRole;

/**
 * Data Transfer Object for sending login response data to frontend.
 * Contains JWT token, user info, and role.
 *
 * @version 0.1.0 (2025-10-15)
 */
public class LoginResponse {
    private String token;
    private String type = "Bearer"; // Token type for frontend handling
    private Long id;
    private String username;
    private String email;
    private UserRole role;
    private String fullName;

    /** Default constructor. */
    public LoginResponse() {
    }

    public LoginResponse(String token, Long id, String username, String email,
            UserRole role, String fullName) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.fullName = fullName;
    }

    /** @return The JWT token */
    public String getToken() {
        return token;
    }

    /** @param token The JWT token */
    public void setToken(String token) {
        this.token = token;
    }

    /** @return The token type */
    public String getType() {
        return type;
    }

    /** @param type The token type */
    public void setType(String type) {
        this.type = type;
    }

    /** @return The user ID */
    public Long getId() {
        return id;
    }

    /** @param id The user ID */
    public void setId(Long id) {
        this.id = id;
    }

    /** @return The username */
    public String getUsername() {
        return username;
    }

    /** @param username The username */
    public void setUsername(String username) {
        this.username = username;
    }

    /** @return The email */
    public String getEmail() {
        return email;
    }

    /** @param email The email */
    public void setEmail(String email) {
        this.email = email;
    }

    /** @return The user role */
    public UserRole getRole() {
        return role;
    }

    /** @param role The user role */
    public void setRole(UserRole role) {
        this.role = role;
    }

    /** @return The full name */
    public String getFullName() {
        return fullName;
    }

    /** @param fullName The full name */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
