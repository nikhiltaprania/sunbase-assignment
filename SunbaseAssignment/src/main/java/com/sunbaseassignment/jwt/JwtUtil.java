package com.sunbaseassignment.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for handling JWT operations such as token creation, validation, and claims extraction.
 *
 * @author Nikhil Kumar
 */
@Component
public class JwtUtil {
    private final SecretKey secretKey;
    private final long expiration;

    /**
     * Constructor to initialize the secret key and expiration time.
     *
     * @param key        The secret key for signing the JWT.
     * @param expiration The expiration time for the JWT.
     */
    public JwtUtil(@Value("${jwt.key}") String key, @Value("${jwt.expiration}") long expiration) {
        this.secretKey = Keys.hmacShaKeyFor(key.getBytes());
        this.expiration = expiration;
    }

    /**
     * Extracts all claims from the JWT token.
     *
     * @param token The JWT token from which claims are extracted.
     * @return Claims object containing all claims.
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extracts a specific claim from the JWT token.
     *
     * @param <T>            The type of the claim to be extracted.
     * @param token          The JWT token from which the claim is extracted.
     * @param claimsResolver A function to resolve the desired claim.
     * @return The claim of type T.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Creates a new JWT token with the given claims and subject.
     *
     * @param claims  The claims to be included in the token.
     * @param subject The subject of the token.
     * @return The generated JWT token.
     */
    public String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Extracts the username (subject) from the JWT token.
     *
     * @param token The JWT token from which the username is extracted.
     * @return The username (subject) of the token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from the JWT token.
     *
     * @param token The JWT token from which the expiration date is extracted.
     * @return The expiration date of the token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Checks if the JWT token is expired.
     *
     * @param token The JWT token to be checked.
     * @return True if the token is expired, false otherwise.
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Generates a new JWT token for the given user details.
     *
     * @param userDetails The user details for whom the token is generated.
     * @return The generated JWT token.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Validates the JWT token against the given user details.
     *
     * @param token       The JWT token to be validated.
     * @param userDetails The user details to validate against.
     * @return True if the token is valid, false otherwise.
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}