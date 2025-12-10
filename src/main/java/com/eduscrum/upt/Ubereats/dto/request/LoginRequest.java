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

    /** Default constructor. */
    public LoginRequest() {
    }

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /** @return The email */
    public String getEmail() {
        return email;
    }

    /** @param email The email */
    public void setEmail(String email) {
        this.email = email;
    }

    /** @return The password */
    public String getPassword() {
        return password;
    }

    /** @param password The password */
    public void setPassword(String password) {
        this.password = password;
    }
}
