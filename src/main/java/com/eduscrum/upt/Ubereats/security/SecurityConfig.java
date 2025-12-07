package com.eduscrum.upt.Ubereats.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * Main security configuration class for Spring Security
 * Configures JWT authentication, password encoding, and endpoint security
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public DaoAuthenticationProvider authenticationProvider(CustomUserDetailsService customUserDetailsService,
                                                            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter,
                                                   DaoAuthenticationProvider authenticationProvider) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // 1. Public access for authentication
                        .requestMatchers("/api/auth/**").permitAll()

                        // 2. Teacher-only access for management tasks
                        .requestMatchers(HttpMethod.POST, "/api/courses/**", "/api/projects/**", "/api/sprints/**", "/api/teams/**", "/api/enrollments").hasAuthority("ROLE_TEACHER")
                        .requestMatchers(HttpMethod.PUT, "/api/courses/**", "/api/projects/**", "/api/sprints/**", "/api/teams/**").hasAuthority("ROLE_TEACHER")
                        .requestMatchers(HttpMethod.DELETE, "/api/courses/**", "/api/projects/**", "/api/sprints/**", "/api/teams/**").hasAuthority("ROLE_TEACHER")
                        .requestMatchers(HttpMethod.DELETE, "/api/user-stories/**").hasAuthority("ROLE_TEACHER")
                        .requestMatchers(HttpMethod.POST, "/api/achievements").hasAuthority("ROLE_TEACHER")

                        // 3. Student-only access
                        .requestMatchers(HttpMethod.POST, "/api/progress-metrics").hasAuthority("ROLE_STUDENT")

                        // 4. Authenticated users (Students and Teachers) can manage their work
                        .requestMatchers(HttpMethod.POST, "/api/user-stories").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/user-stories/**").authenticated()

                        // 5. General authenticated access for all other API requests
                        .requestMatchers("/api/**").authenticated()

                        // Fallback for any other request
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}