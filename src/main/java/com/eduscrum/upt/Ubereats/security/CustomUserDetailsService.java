package com.eduscrum.upt.Ubereats.security;

import com.eduscrum.upt.Ubereats.entity.User;
import com.eduscrum.upt.Ubereats.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service that loads user-specific data for Spring Security.
 * Implements UserDetailsService to integrate with Spring Security
 * authentication.
 *
 * @author Joao
 * @author Ana
 * @version 0.9.2
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    /**
     * Constructs a new CustomUserDetailsService with required dependencies.
     *
     * @param userService Service for user data access
     */
    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Loads user by email OR username (supports both for authentication).
     *
     * @param emailOrUsername The email or username to search for
     * @return UserDetails for the found user
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    public UserDetails loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException {
        // Try to find by email first
        Optional<User> userOpt = userService.findByEmail(emailOrUsername);

        // If not found by email, try by username
        if (userOpt.isEmpty()) {
            userOpt = userService.findByUsername(emailOrUsername);
        }

        User user = userOpt.orElseThrow(
                () -> new UsernameNotFoundException("User not found with email or username: " + emailOrUsername));

        // Convert User entity to Spring Security UserDetails
        return CustomUserDetails.build(user);
    }
}
