package com.budgettracker.security;

import com.budgettracker.entity.User;
import com.budgettracker.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import java.util.Collections;
import java.util.List;

// This class Processes JWT authentication for incoming requests
@Component // 	Registers the filter as a Spring bean
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService; // Declares the service class dependency for JWT Extraction
    private final UserRepository userRepository; // Declares the repo dependency to retrieve the user from MySQL using the email extracted from the JWT.

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) { // 	Receives the dependency
        this.jwtService = jwtService; // Assigns it to the class field
        this.userRepository = userRepository;
    }


    @Override
    protected void doFilterInternal(
            HttpServletRequest request, // Contains the incoming HTTP request
            HttpServletResponse response,// Represents the HTTP response
            FilterChain filterChain) // Allows the request to continue through the remaining filters
            throws ServletException, IOException {

            // Step 1: Read the Authorization header
                String authHeader = request.getHeader("Authorization");

                // Step 2: Check whether the header contains a Bearer token
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    filterChain.doFilter(request, response);
                    return;
                }

                // Extract the JWT token
                String token = authHeader.substring(7); // // Removes the first seven(substring 7) characters ("Bearer ") and returns the JWT.

                // To extract JWT token and throw an Exception if the token is expired or invalid
                String email;

                try { // attempts to verify the JWT and extract the email.
                    email = jwtService.extractEmail(token);
                } catch (JwtException | IllegalArgumentException e) { // handles invalid or expired JWTs.
                    System.out.println("JWT FILTER: Invalid token detected!");

                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT"); // rejects the request.
                    return; // stops processing the request in this filter.
                }

                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid email or password")); // To retrieve the user from MySQL.

                    // to tell Spring Security who the authenticated user is so it can recognize that user and make access-control decisions.
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                user.getEmail(), // Identifies the authenticated user.
                                null, // No password is needed.
                                List.of(
                                        new SimpleGrantedAuthority("ROLE_" + user.getRole().name())// For Spring Security to recognize user and admin roles
                                )
                        );
                // SecurityContextHolder is the place where Spring Security keeps the current request's authentication information.
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Step 3: Continue to the next filter
                filterChain.doFilter(request, response);
            }
}
