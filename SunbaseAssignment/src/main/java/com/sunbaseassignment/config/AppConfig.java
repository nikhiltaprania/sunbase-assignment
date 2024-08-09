package com.sunbaseassignment.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunbaseassignment.jwt.JwtAuthenticationFilter;
import com.sunbaseassignment.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;
import java.util.List;

/**
 * Configuration class for Spring Security setup and bean definitions.
 *
 * @author Nikhil Kumar
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AppConfig {
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    /**
     * Configures the security filter chain for HTTP requests.
     *
     * @param http The HttpSecurity object for configuring security settings.
     * @return The configured SecurityFilterChain.
     * @throws Exception If an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Configure session management to be stateless, meaning no session will be created or used by Spring Security.
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Disable CSRF (Cross-Site Request Forgery) protection as the application is stateless and doesn't use cookies.
                .csrf(AbstractHttpConfigurer::disable)

                // Configure CORS (Cross-Origin Resource Sharing) to allow requests from any origin, with any method, and allow credentials.
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration cfg = new CorsConfiguration();
                    cfg.setAllowedOriginPatterns(Collections.singletonList("*")); // Allow requests from any origin.
                    cfg.setAllowedMethods(Collections.singletonList("*")); // Allow all HTTP methods.
                    cfg.setAllowCredentials(true); // Allow credentials (like cookies) to be included.
                    cfg.setAllowedHeaders(Collections.singletonList("*")); // Allow all headers.
                    cfg.setExposedHeaders(List.of("Authorization")); // Expose the Authorization header in responses.
                    return cfg;
                }))

                // Configure authorization rules for different request patterns.
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/**", "/index").permitAll() // Allow public access to auth and index endpoints.
                        .anyRequest().authenticated()) // Require authentication for all other requests.

                // Add JWT authentication filter before the UsernamePasswordAuthenticationFilter in the filter chain.
                .addFilterBefore(new JwtAuthenticationFilter(userDetailsService, jwtUtil), UsernamePasswordAuthenticationFilter.class)

                // Configure form login to use a custom login page and allow public access to it.
                .formLogin(login -> login
                        .loginPage("/login-signup") // Set the custom login page URL.
                        .permitAll()); // Allow public access to the login page.

        return http.build(); // Build and return the configured SecurityFilterChain.
    }


    /**
     * Provides a BCryptPasswordEncoder bean for encoding passwords.
     *
     * @return A BCryptPasswordEncoder instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Provides an AuthenticationManager bean for managing authentication.
     *
     * @param configuration The AuthenticationConfiguration object.
     * @return An AuthenticationManager instance.
     * @throws Exception If an error occurs while retrieving the AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Provides a RestTemplate bean for making HTTP requests.
     *
     * @return A RestTemplate instance.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Provides an ObjectMapper bean for JSON processing.
     *
     * @return An ObjectMapper instance.
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}