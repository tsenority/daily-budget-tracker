package com.budgettracker.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

// Class responsible for generating and validating JWT tokens.
@Service
public class JwtService {

    @Value("${jwt.secret}") // Reads the JWT secret from application.properties.
    private String secretKey; // Stores the injected value.

    // Method to convert the configured JWT secret into a cryptographic key.
    private SecretKey getSigningKey() {

        // Converts the Base64 string into bytes
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);

        // Creates a cryptographic SecretKey and return it:
        return Keys.hmacShaKeyFor(keyBytes);

    }

    // Method that generates a JWT after a successful login.
    // The overall JWT generation pattern remains similar, only few variables change per project requirement.
    public String generateToken(String email) {

        // JWT tokens should expire for security reasons.
        Date issuedAt = new Date(); // Gets the current date and time.
        Date expiration = new Date(issuedAt.getTime() + 1000L * 60 * 60 * 24); // Represents 24 hours in milliseconds.

        return Jwts.builder() // Starts building a JWT.
                .subject(email) // Stores the user's email as the token's subject.
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(getSigningKey()) // Signs the token using our secret key.
                .compact(); // Converts the JWT into a String.
    }

    // Method for JWT verification. Follows almost the same pattern across Java Spring Boot projects using JJWT.
    public String extractEmail(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject(); // If the signature is invalid or the token has expired, JJWT throws an exception.
    }


}
