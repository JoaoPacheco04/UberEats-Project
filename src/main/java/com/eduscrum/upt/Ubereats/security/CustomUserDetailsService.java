package com.eduscrum.upt.Ubereats.security;

import com.eduscrum.upt.Ubereats.entity.User;
import com.eduscrum.upt.Ubereats.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service that loads user-specific data for Spring Security
 * Implements UserDetailsService to integrate with Spring Security authentication
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;


    //Constructor injection for UserService dependency
    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }


     //Loads user by email (used as username in authentication)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Convert User entity to Spring Security UserDetails
        return CustomUserDetails.build(user);
    }
}
