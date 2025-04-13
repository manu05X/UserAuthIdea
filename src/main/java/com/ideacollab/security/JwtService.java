package com.ideacollab.security;

import com.ideacollab.model.Role;
import com.ideacollab.model.TokenValidationResult;
import com.ideacollab.model.User;
import com.ideacollab.repository.TokenBlacklistRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final int jwtExpirationMs;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    public JwtService(@Value("${app.jwt.secret}") String jwtSecret,
                      @Value("${app.jwt.expiration}") int jwtExpirationMs,
                      TokenBlacklistRepository tokenBlacklistRepository) {
        this.secretKey = createSecretKey(jwtSecret);
        this.jwtExpirationMs = jwtExpirationMs;
        this.tokenBlacklistRepository = tokenBlacklistRepository;
    }

    private SecretKey createSecretKey(String secret) {
        byte[] keyBytes = secret.getBytes();
        if (keyBytes.length < 32) {
            byte[] paddedKey = new byte[32];
            System.arraycopy(keyBytes, 0, paddedKey, 0, keyBytes.length);
            return Keys.hmacShaKeyFor(paddedKey);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());  // Add userId claim
        claims.put("roles", user.getRole()); // Add roles claim
        return buildToken(claims, user.getEmail());
    }

    private String buildToken(Map<String, Object> claims, String email) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email) // Email as the JWT subject
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public TokenValidationResult validateToken(String token) {
        try {
            //  Check if token is blacklisted
            if (tokenBlacklistRepository.existsByToken(token)) {
                return TokenValidationResult.invalid(); // Token was logged out
            }

            Claims claims = extractAllClaims(token);

            if (claims.getExpiration().before(new Date())) {
                return TokenValidationResult.expired();
            }

            String email = claims.getSubject();
            Long userId = claims.get("userId", Long.class);
            Role roles = Role.valueOf(claims.get("roles", String.class));

            return TokenValidationResult.valid(email, userId, roles);

        } catch (ExpiredJwtException e) {
            return TokenValidationResult.expired();
        } catch (JwtException e) {
            return TokenValidationResult.invalid();
        }
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}