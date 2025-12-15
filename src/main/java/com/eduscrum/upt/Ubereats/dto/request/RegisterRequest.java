package com.eduscrum.upt.Ubereats.dto.request;

import com.eduscrum.upt.Ubereats.entity.enums.UserRole;

/**
 * Data Transfer Object for receiving user registration data.
 * Contains all fields needed to create a new user account.
 *
 * @author Joao
 * @author Ana
 * @version 1.0.1
 */
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private UserRole role;
    private String studentNumber; // Only required for STUDENT role

    /** Default constructor. */
    public RegisterRequest() {
    }

    public RegisterRequest(String username, String email, String password,
            String firstName, String lastName, UserRole role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
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

    /** @return The password */
    public String getPassword() {
        return password;
    }

    /** @param password The password */
    public void setPassword(String password) {
        this.password = password;
    }

    /** @return The first name */
    public String getFirstName() {
        return firstName;
    }

    /** @param firstName The first name */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /** @return The last name */
    public String getLastName() {
        return lastName;
    }

    /** @param lastName The last name */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /** @return The user role */
    public UserRole getRole() {
        return role;
    }

    /** @param role The user role */
    public void setRole(UserRole role) {
        this.role = role;
    }

    /** @return The student number */
    public String getStudentNumber() {
        return studentNumber;
    }

    /** @param studentNumber The student number */
    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }
}
