package com.eduscrum.upt.Ubereats.security;

import com.eduscrum.upt.Ubereats.entity.User;
import com.eduscrum.upt.Ubereats.entity.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom UserDetails implementation for Spring Security.
 * Wraps User entity and provides Spring Security compatible authentication
 * details.
 *
 * @author UberEats
 * @version 0.9.1
 */
public class CustomUserDetails implements UserDetails {
    private Long id;
    private String email;
    private String password;
    private UserRole role;
    private Collection<? extends GrantedAuthority> authorities;

    /**
     * Full constructor for creating UserDetails.
     *
     * @param id          The user ID
     * @param email       The user email
     * @param password    The encoded password
     * @param role        The user role
     * @param authorities The granted authorities
     */
    public CustomUserDetails(Long id, String email, String password, UserRole role,
            Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.authorities = authorities;
    }

    /**
     * Static factory method to create UserDetails from User entity.
     *
     * @param user The User entity to convert
     * @return CustomUserDetails instance
     */
    public static CustomUserDetails build(User user) {
        // Convert UserRole to Spring Security GrantedAuthority
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());

        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                Collections.singletonList(authority));
    }

    /** @return The user ID */
    public Long getId() {
        return id;
    }

    /** @return The user role */
    public UserRole getRole() {
        return role;
    }

    /** {@inheritDoc} */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /** {@inheritDoc} */
    @Override
    public String getPassword() {
        return password;
    }

    /** {@inheritDoc} */
    @Override
    public String getUsername() {
        return email;
    } // Use email as username

    /** {@inheritDoc} */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
