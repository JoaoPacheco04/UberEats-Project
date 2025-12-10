package com.eduscrum.upt.Ubereats.dto.request;

/**
 * Data Transfer Object for receiving login credentials from frontend.
 * Contains only the fields needed for user authentication.
 *
 * @version 0.9.1 (2025-11-28)
 */
public class LoginRequest {
    private String email;
    private String password;

    // === CONSTRUCTORS ===

    public LoginRequest() {
    }

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // === GETTERS & SETTERS ===

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
