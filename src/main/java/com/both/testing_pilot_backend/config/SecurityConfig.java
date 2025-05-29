package com.both.testing_pilot_backend.config;

import com.both.testing_pilot_backend.jwt.JwtAuthEntryPoint;
import com.both.testing_pilot_backend.jwt.JwtAuthFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;


/**
 * SecurityConfig defines security settings for the application, including authentication,
 * authorization, session management, exception handling, and filter chain customization.
 * It uses JWT tokens for stateless authentication.
 */
@Configuration
@AllArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    // Custom filter that checks JWT token validity
    private final JwtAuthFilter jwtAuthFilter;

    // Handles unauthorized access attempts
    private final JwtAuthEntryPoint jwtAuthEntrypoint;

    // Allow the to access without authentication
    private final String[] WHITE_LIST_URL = new String[]{
            "/api/v1/auths/**",         // Authentication APIs (login, register, etc.)
            "/v3/api-docs/**",         // OpenAPI docs
            "/swagger-ui/**",          // Swagger UI
            "/swagger-ui.html",
            "/actuator/**",
            "/no-auth/**"
    };

    /**
     * Provides the authentication manager used to perform login authentication.
     *
     * @param configuration Spring's authentication configuration
     * @return AuthenticationManager bean
     * @throws Exception if failed to get the manager
     */
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Configures the security filter chain for handling HTTP security.
     * It defines:
     * - Public and protected endpoints
     * - Stateless session policy
     * - JWT filter integration
     * - Custom error handling
     *
     * @param http HttpSecurity object for configuration
     * @return Configured SecurityFilterChain
     * @throws Exception if an error occurs during setup
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Enable CORS and disable CSRF (appropriate for APIs)
                .cors(withDefaults()).csrf(AbstractHttpConfigurer::disable)

                // Define public and protected endpoints
                .authorizeHttpRequests(request -> request
                        .requestMatchers(
                                WHITE_LIST_URL
                        ).permitAll()                     // These are accessible without authentication
                        .anyRequest().authenticated()     // All other endpoints require JWT
                )

                // Use stateless sessions (no session data stored on server)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Use custom handler for authentication errors
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthEntrypoint))

                // Add JWT filter before Spring's built-in authentication filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
