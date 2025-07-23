package com.example.Card_Service_V2.services.dtos;

public class TokenValidationResponse {
    private boolean valid;
    private String message;
    private String role;
    private Integer userId;
    private String username;
    private boolean hasPermission;

    // Default constructor
    public TokenValidationResponse() {}

    // Constructor with all fields
    public TokenValidationResponse(boolean valid, String message, String role, Integer userId, String username, boolean hasPermission) {
        this.valid = valid;
        this.message = message;
        this.role = role;
        this.userId = userId;
        this.username = username;
        this.hasPermission = hasPermission;
    }

    // Builder pattern
    public static TokenValidationResponseBuilder builder() {
        return new TokenValidationResponseBuilder();
    }

    // Getters and Setters
    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isHasPermission() {
        return hasPermission;
    }

    public void setHasPermission(boolean hasPermission) {
        this.hasPermission = hasPermission;
    }

    // Builder class
    public static class TokenValidationResponseBuilder {
        private boolean valid;
        private String message;
        private String role;
        private Integer userId;
        private String username;
        private boolean hasPermission;

        public TokenValidationResponseBuilder valid(boolean valid) {
            this.valid = valid;
            return this;
        }

        public TokenValidationResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public TokenValidationResponseBuilder role(String role) {
            this.role = role;
            return this;
        }

        public TokenValidationResponseBuilder userId(Integer userId) {
            this.userId = userId;
            return this;
        }

        public TokenValidationResponseBuilder username(String username) {
            this.username = username;
            return this;
        }

        public TokenValidationResponseBuilder hasPermission(boolean hasPermission) {
            this.hasPermission = hasPermission;
            return this;
        }

        public TokenValidationResponse build() {
            return new TokenValidationResponse(valid, message, role, userId, username, hasPermission);
        }
    }
}
