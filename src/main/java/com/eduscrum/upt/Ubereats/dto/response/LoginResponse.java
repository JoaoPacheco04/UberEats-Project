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

    // === CONSTRUCTORS ===
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

    // === GETTERS & SETTERS ===

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
