package org.example.smartlawgt.integration.jwt;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationMs}")
    private long jwtExpirationMs;

    @Value("${jwt.refreshExpirationMs}")
    private long refreshExpirationMs;

    public String generateAccessToken(UUID userId, String userName, String email, String role) {
        return generateToken(userId, userName, email, role, jwtExpirationMs);
    }

    public String generateRefreshToken(UUID userId, String userName, String email, String role) {
        return generateToken(userId, userName, email, role, refreshExpirationMs);
    }

    private String generateToken(UUID userId, String userName, String email, String role, long expiration) {
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId.toString())
                .claim("userName", userName)
                .claim("email", email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }


    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
    }

    public String getEmailFromToken(String token) {
        return getAllClaimsFromToken(token).get("email", String.class);
    }

    public String getUsernameFromToken(String token) {
        return getAllClaimsFromToken(token).get("userName", String.class);
    }

    public String getRoleFromToken(String token) {
        return getAllClaimsFromToken(token).get("role", String.class);
    }

    public UUID getUserIdFromToken(String token) {
        String id = getAllClaimsFromToken(token).get("userId", String.class);
        return UUID.fromString(id);
    }

    public Date getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }

    public boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token).before(new Date());
    }
}
