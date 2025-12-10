package com.eduscrum.upt.Ubereats.controller;

import com.eduscrum.upt.Ubereats.dto.request.LoginRequest;
import com.eduscrum.upt.Ubereats.dto.request.RegisterRequest;
import com.eduscrum.upt.Ubereats.dto.response.LoginResponse;
import com.eduscrum.upt.Ubereats.entity.User;
import com.eduscrum.upt.Ubereats.security.CustomUserDetails;
import com.eduscrum.upt.Ubereats.security.JwtTokenProvider;
import com.eduscrum.upt.Ubereats.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for authentication operations.
 * Provides endpoints for login, registration, and current user retrieval.
 *
 * @version 0.9.1 (2025-11-28)
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;

    /**
     * Constructs a new AuthController with required dependencies.
     *
     * @param authenticationManager Manager for authentication operations
     * @param tokenProvider         Provider for JWT token generation
     * @param userService           Service for user operations
     */
    public AuthController(AuthenticationManager authenticationManager,
            JwtTokenProvider tokenProvider,
            UserService userService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userService = userService;
    }

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param loginRequest The request containing login credentials
     * @return ResponseEntity containing the login response with JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User data not found after successful authentication."));

            LoginResponse loginResponse = new LoginResponse(
                    jwt,
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole(),
                    user.getFullName());

            return ResponseEntity.ok(loginResponse);

        } catch (BadCredentialsException e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invalid email or password.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            if (userService.existsByEmail(registerRequest.getEmail())) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Email is already taken");
                return ResponseEntity.badRequest().body(response);
            }

            if (userService.existsByUsername(registerRequest.getUsername())) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Username is already taken");
                return ResponseEntity.badRequest().body(response);
            }

            User user = userService.registerUser(
                    registerRequest.getUsername(),
                    registerRequest.getEmail(),
                    registerRequest.getPassword(),
                    registerRequest.getFirstName(),
                    registerRequest.getLastName(),
                    registerRequest.getRole(),
                    registerRequest.getStudentNumber());

            Map<String, String> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("userId", user.getId().toString());
            return ResponseEntity.status(HttpStatus.CREATED).body(response); // ⭐️ Usar 201 CREATED

        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        Map<String, Object> response = new HashMap<>();
        response.put("principal", authentication.getPrincipal().toString());
        response.put("authorities", authentication.getAuthorities().toString());
        response.put("name", authentication.getName());
        return ResponseEntity.ok(response);
    }
}
