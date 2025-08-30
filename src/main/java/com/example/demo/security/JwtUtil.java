package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "mysecretkeymysecretkeymysecretkey123";
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7;

    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    public Claims parseClaims(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Optional<String> getUsernameIfTokenValid(String token) {
        try {
            Claims claims = parseClaims(token);
            String username = claims.getSubject();
            Date exp = claims.getExpiration();
            if (username == null) return Optional.empty();
            if (exp != null && exp.before(new Date())) return Optional.empty();
            return Optional.of(username);
        } catch (JwtException | IllegalArgumentException ex) {
            // invalid token (expired, malformed, unsupported, etc.)
            return Optional.empty();
        }
    }
}
