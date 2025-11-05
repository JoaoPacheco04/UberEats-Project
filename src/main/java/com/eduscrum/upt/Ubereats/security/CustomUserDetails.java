package com.eduscrum.upt.Ubereats.security;

import com.eduscrum.upt.Ubereats.entity.User;
import com.eduscrum.upt.Ubereats.entity.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom UserDetails implementation for Spring Security
 * Wraps User entity and provides Spring Security compatible authentication details
 */
public class CustomUserDetails implements UserDetails {
    private Long id;
    private String email;
    private String password;
    private UserRole role;
    private Collection<? extends GrantedAuthority> authorities;


     //Full constructor for creating UserDetails
    public CustomUserDetails(Long id, String email, String password, UserRole role,
                             Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.authorities = authorities;
    }


     //Static factory method to create UserDetails from User entity
    public static CustomUserDetails build(User user) {
        // Convert UserRole to Spring Security GrantedAuthority
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());

        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                Collections.singletonList(authority)
        );
    }

    // === CUSTOM GETTERS ===
    public Long getId() { return id; }
    public UserRole getRole() { return role; }

    // === USERDETAILS INTERFACE IMPLEMENTATION ===
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override
    public String getPassword() { return password; }
    @Override
    public String getUsername() { return email; } // Use email as username
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}