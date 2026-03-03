package com.project.kisan_setu.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh.expiration}")
    private long refreshExpiration;
    // SIGNING KEY
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // ACCESS TOKEN
    public String generateAccessToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("type", "ACCESS")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    // REFRESH TOKEN
    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("type", "REFRESH")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean validateAccessToken(String token, String email) {
        final Claims claims = extractAllClaims(token);

        return claims.getSubject().equals(email)
                && !isTokenExpired(token)
                && "ACCESS".equals(claims.get("type"));
    }

    public boolean validateRefreshToken(String token) {
        final Claims claims = extractAllClaims(token);

        return !isTokenExpired(token)
                && "REFRESH".equals(claims.get("type"));
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractAllClaims(token)
                    .getExpiration()
                    .before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token expired");
        } catch (UnsupportedJwtException e) {
            throw new RuntimeException("Unsupported token");
        } catch (MalformedJwtException e) {
            throw new RuntimeException("Malformed token");
        } catch (SignatureException e) {
            throw new RuntimeException("Invalid signature");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Token is empty");
        }
    }
}