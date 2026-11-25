package com.budgettracker.security;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration // "This class contains configuration instructions for the application." It does not create our security rules automatically.
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // This prevents Spring Boot from registering your JWT filter separately as a servlet filter. Your JWT filter will still run through Spring Security
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        FilterRegistrationBean<JwtAuthenticationFilter> registration =
                new FilterRegistrationBean<>(jwtAuthenticationFilter);

        registration.setEnabled(false);

        return registration;
    }


    // Method 1: Configures security rules:
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // We are disabling it because we are using JWT token for authentication.
        http.csrf(csrf -> csrf.disable()); // Spring Security enables CSRF protection by default.

        // Enable CORS support
        http.cors(Customizer.withDefaults()); // Whole method for cors configuration at the bottom.

        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );


        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/error").permitAll() // 	Public access allowed
                .requestMatchers("/api/admin/**").hasRole("ADMIN") // 	Only administrators allowed
                .anyRequest().authenticated() // 	Any authenticated user allowed
        );

        // defines what Spring Security should do when authentication is required but missing or invalid.
        http.exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) ->
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                )
        );

        // "Runs our JWT authentication filter before the standard username/password authentication filter."
        //This allows our filter to verify JWTs and establish authentication before Spring Security evaluates access to protected endpoints.
        http.addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
        );




        return http.build(); // Builds the security filter chain

    }

    // CORS Configuration method and settings
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:5173",
                        "http://localhost:5174"
                )
        );

        configuration.setAllowedMethods( // 	Allows GET, POST, PUT, DELETE, OPTIONS
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );

        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));// 	Allows JWT Authorization and Content-Type headers

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration); // 	Applies the configuration to API paths

        return source;
    }

    // Method 2: Configures password hashing:
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
