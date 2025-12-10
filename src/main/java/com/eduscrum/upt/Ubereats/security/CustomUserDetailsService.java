package com.eduscrum.upt.Ubereats.security;

import com.eduscrum.upt.Ubereats.entity.User;
import com.eduscrum.upt.Ubereats.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service that loads user-specific data for Spring Security.
 * Implements UserDetailsService to integrate with Spring Security
 * authentication.
 *
 * @version 0.9.1 (2025-11-28)
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
     * Loads user by email (used as username in authentication).
     *
     * @param email The email to search for
     * @return UserDetails for the found user
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Convert User entity to Spring Security UserDetails
        return CustomUserDetails.build(user);
    }
}
