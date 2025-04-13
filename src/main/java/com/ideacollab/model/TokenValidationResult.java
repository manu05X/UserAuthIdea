package com.ideacollab.model;

import java.util.List;

public class TokenValidationResult {

    private final SessionStatus status;
    private final String email;
    private final Long userId;
    private final Role roles;

    // Private constructor
    private TokenValidationResult(SessionStatus status, String email, Long userId, Role roles) {
        this.status = status;
        this.email = email;
        this.userId = userId;
        this.roles = roles;
    }

    public SessionStatus getStatus() {
        return status;
    }

    // Factory methods
    public static TokenValidationResult valid(String email, Long userId, Role roles) {
        return new TokenValidationResult(SessionStatus.ACTIVE, email, userId, roles);
    }

    public static TokenValidationResult expired() {
        return new TokenValidationResult(SessionStatus.EXPIRED, null, null, null);
    }

    public static TokenValidationResult invalid() {
        return new TokenValidationResult(SessionStatus.INVALID, null, null, null);
    }

    // Getters
    public boolean isValid() { return status == SessionStatus.ACTIVE; }
    public boolean isExpired() { return status == SessionStatus.EXPIRED; }
    public boolean isInvalid() { return status == SessionStatus.INVALID; }
    public String getEmail() { return email; }
    public Long getUserId() { return userId; }
    public Role getRoles() { return roles; }




}