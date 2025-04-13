package com.ideacollab.service;


import com.ideacollab.dto.UserDto;
import com.ideacollab.exception.UserAlreadyExistsException;
import com.ideacollab.exception.UserDoesNotExistException;
import com.ideacollab.model.*;
import com.ideacollab.repository.SessionRepository;
import com.ideacollab.repository.TokenBlacklistRepository;
import com.ideacollab.repository.UserRepository;
import com.ideacollab.security.JwtService;
import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;

import java.time.Instant;
import java.util.*;

@Service
public class AuthService {
    private final UserRepository userRepository;

    // Not to create a new instance each time it is used we will keep it in SpringSecurity and make its bean their,
    // so we do not need to create a instance each time only single instance of it will be present across the app.
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final SessionRepository sessionRepository;


    @Autowired
    public AuthService(UserRepository userRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder,
                       JwtService jwtService,
                       TokenBlacklistRepository tokenBlacklistRepository,
                       SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtService = jwtService;
        this.tokenBlacklistRepository = tokenBlacklistRepository;
        this.sessionRepository = sessionRepository;
    }

    public UserDto signUp(String email, String password, String name) throws UserAlreadyExistsException {
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("User with email " + email + " already exists");
        }

        String employeeId = "EMP" + System.currentTimeMillis() + RandomStringUtils.randomAlphanumeric(4);

        User user = new User();
        user.setEmail(email);
        user.setHashedPassword(bCryptPasswordEncoder.encode(password));
        user.setName(name);
        user.setEmployeeId(employeeId);

        User savedUser = userRepository.save(user);
        return UserDto.fromUser(savedUser);

    }

    public ResponseEntity<UserDto> login(String email, String password) throws UserDoesNotExistException {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new UserDoesNotExistException("User does not exist with email: " + email);
        }

        User user = userOptional.get();

        if (!bCryptPasswordEncoder.matches(password, user.getHashedPassword())) {
            throw new UserDoesNotExistException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);

        UserDto userDto = UserDto.fromUser(user);
        userDto.setToken(token);

        MultiValueMap<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE, "auth-token=" + token + "; HttpOnly; Secure; SameSite=Strict");


        return new ResponseEntity<>(userDto, headers, HttpStatus.OK);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // 1. Extract token from request
        String token = extractTokenFromRequest(request);

        // 2. Clear client-side cookie
        clearAuthCookie(response);

        // 3. Invalidate token server-side
        if (token != null && !token.isBlank()) {
            invalidateToken(token);
        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        // Check cookies first
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("auth-token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        // Fallback to Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }


    private void clearAuthCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("auth-token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private void invalidateToken(String token) {
        try {
            Instant expiry = jwtService.extractExpiration(token).toInstant();

            // Skip if already blacklisted
            if (!tokenBlacklistRepository.existsByToken(token)) {
                TokenBlacklist entry = new TokenBlacklist();
                entry.setToken(token);
                entry.setExpiryDate(expiry);
                tokenBlacklistRepository.save(entry);
            }
        } catch (JwtException e) {
            // Handle invalid token format
            throw new RuntimeException("Failed to invalidate malformed token: " + e.getMessage());
            //logger.warn("Failed to invalidate malformed token: " + e.getMessage());
        }
    }

    public TokenValidationResult validateToken(String token) {
        return jwtService.validateToken(token);
    }
}




/*
    public SessionStatus validate(String token, Long userId) {
        // Step 1: Find the session by token and userId
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);

        if (sessionOptional.isEmpty()) {
            //log.error("No session found for token: {} and userId: {}", token, userId);
            return SessionStatus.ENDED;
        }

        Session session = sessionOptional.get();

        // Step 2: Check if the session status is ACTIVE
        if (!SessionStatus.ACTIVE.equals(session.getSessionStatus())) {
            //log.error("Session status is not ACTIVE for userId: {}, token: {}", userId, token);
            return SessionStatus.INVALID;
        }

        try {
            // Step 3: Parse and validate the JWT token
            Jws<Claims> claimsJws = Jwts
                    .parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);

            // Extract claims (e.g., email and expiryAt)
            String email = (String) claimsJws.getPayload().get("email");
            //Long expiryAt = (Long) claimsJws.getPayload().get("expiryAt");

//            // Step 4: Check if the token has expired
//            if (new Date().getTime() > expiryAt) {
//                //log.error("Token has expired for userId: {}, email: {}", userId, email);
//                return SessionStatus.INVALID;
//            }

            //log.info("Token successfully validated for userId: {}, email: {}", userId, email);
            return SessionStatus.ACTIVE;
        } catch (JwtException ex) {
            // Step 5: Handle invalid tokens
            //log.error("Invalid token for userId: {}, error: {}", userId, ex.getMessage());
            return SessionStatus.INVALID;
        }
    }

 */



//}


/*

The HTTP headers in a response are implemented as a MultiValueMap because:

Headers Can Have Multiple Values:
HTTP allows the same header name to have multiple values. For example:
    Set-Cookie: auth-token=abcd1234
    Set-Cookie: session-id=xyz789

In such cases, a MultiValueMap is a suitable data structure because it allows mapping a single key (header name) to multiple values.
Here, MultiValueMapAdapter wraps a HashMap to allow storing multiple values for the Set-Cookie header.
 */