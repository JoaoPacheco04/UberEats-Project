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
 * Main security configuration class for Spring Security.
 * Configures JWT authentication, password encoding, and endpoint security.
 *
 * @author UberEats
 * @version 0.9.1
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

        /**
         * Creates the DAO authentication provider.
         *
         * @param customUserDetailsService Service for loading user details
         * @param passwordEncoder          Encoder for password verification
         * @return Configured DaoAuthenticationProvider
         */
        @Bean
        public DaoAuthenticationProvider authenticationProvider(CustomUserDetailsService customUserDetailsService,
                        PasswordEncoder passwordEncoder) {
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(customUserDetailsService);
                authProvider.setPasswordEncoder(passwordEncoder);
                return authProvider;
        }

        /**
         * Configures the security filter chain with JWT authentication and
         * authorization rules.
         *
         * @param http                    The HttpSecurity to configure
         * @param jwtAuthenticationFilter The JWT authentication filter
         * @param authenticationProvider  The authentication provider
         * @return Configured SecurityFilterChain
         * @throws Exception if configuration fails
         */
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http,
                        JwtAuthenticationFilter jwtAuthenticationFilter,
                        DaoAuthenticationProvider authenticationProvider) throws Exception {

                http
                                // Enable CORS with defaults (uses CorsConfigurationSource bean)
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(authz -> authz
                                                // 1. Allow preflight requests
                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                                                // 2. Public access for authentication
                                                .requestMatchers("/api/auth/**", "/error").permitAll()

                                                // 3. Teacher-only access for management tasks
                                                .requestMatchers(HttpMethod.POST, "/api/courses/**", "/api/projects/**",
                                                                "/api/teams/**")
                                                .hasAuthority("ROLE_TEACHER")
                                                .requestMatchers(HttpMethod.PUT, "/api/courses/**", "/api/projects/**",
                                                                "/api/teams/**")
                                                .hasAuthority("ROLE_TEACHER")
                                                .requestMatchers(HttpMethod.DELETE, "/api/courses/**",
                                                                "/api/projects/**",
                                                                "/api/teams/**")
                                                .hasAuthority("ROLE_TEACHER")

                                                // 3.5 Sprint management - allow both teachers and students
                                                .requestMatchers(HttpMethod.POST, "/api/sprints/**")
                                                .hasAnyAuthority("ROLE_TEACHER", "ROLE_STUDENT")
                                                .requestMatchers(HttpMethod.PUT, "/api/sprints/**")
                                                .hasAnyAuthority("ROLE_TEACHER", "ROLE_STUDENT")
                                                .requestMatchers(HttpMethod.DELETE, "/api/sprints/**")
                                                .hasAuthority("ROLE_TEACHER")

                                                .requestMatchers(HttpMethod.DELETE, "/api/user-stories/**")
                                                .hasAnyAuthority("ROLE_TEACHER", "ROLE_STUDENT")
                                                .requestMatchers(HttpMethod.POST, "/api/achievements")
                                                .hasAuthority("ROLE_TEACHER")

                                                // 4. Enrollment - allow both teachers and students
                                                .requestMatchers(HttpMethod.POST, "/api/enrollments")
                                                .hasAnyAuthority("ROLE_TEACHER", "ROLE_STUDENT")

                                                // 5. Student-only access
                                                .requestMatchers(HttpMethod.POST, "/api/progress-metrics")
                                                .hasAuthority("ROLE_STUDENT")

                                                // 5. Authenticated users (Students and Teachers) can manage their work
                                                .requestMatchers(HttpMethod.POST, "/api/user-stories").authenticated()
                                                .requestMatchers(HttpMethod.PUT, "/api/user-stories/**").authenticated()

                                                // 6. General authenticated access for all other API requests
                                                .requestMatchers("/api/**").authenticated()

                                                // Fallback for any other request
                                                .anyRequest().authenticated())
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        /**
         * Creates the password encoder bean.
         *
         * @return BCryptPasswordEncoder instance
         */
        @Bean
        public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
                org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
                configuration.setAllowedOrigins(java.util.Arrays.asList("http://localhost:5173",
                                "http://localhost:5174", "http://localhost:3000"));
                configuration.setAllowedMethods(
                                java.util.Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                configuration.setAllowedHeaders(java.util.Arrays.asList("Authorization", "Content-Type",
                                "X-Requested-With", "Accept", "Origin"));
                configuration.setExposedHeaders(java.util.Arrays.asList("Authorization"));
                configuration.setAllowCredentials(true);
                configuration.setMaxAge(3600L);

                org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        /**
         * Creates the authentication manager bean.
         *
         * @param config The authentication configuration
         * @return Configured AuthenticationManager
         * @throws Exception if configuration fails
         */
        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }
}
