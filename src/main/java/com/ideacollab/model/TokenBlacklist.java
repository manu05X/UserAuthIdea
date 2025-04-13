package com.ideacollab.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Instant;

// TokenBlacklist.java (Entity)
@Entity
@Table(name = "token_blacklist")
public class TokenBlacklist extends BaseModel {
    private String token;
    private Instant expiryDate;
    // getters/setters


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }
}

