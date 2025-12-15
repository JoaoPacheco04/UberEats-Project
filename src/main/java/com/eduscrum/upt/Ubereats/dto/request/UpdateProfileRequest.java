package com.eduscrum.upt.Ubereats.dto.request;

/**
 * DTO for user profile update requests.
 * Note: studentNumber, username, and role cannot be changed via profile update.
 *
 * @author Joao Pacheco
 * @author Francisco
 * @version 1.0.0
 */
public class UpdateProfileRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password; // Optional - null means keep existing password

    // Default constructor
    public UpdateProfileRequest() {
    }

    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

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
