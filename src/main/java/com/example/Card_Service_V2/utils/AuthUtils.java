package com.example.Card_Service_V2.utils;

import com.example.Card_Service_V2.services.AuthServiceClient;
import com.example.Card_Service_V2.services.dtos.TokenValidationResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {
    
    private static AuthServiceClient authServiceClient;
    
    @Autowired
    public void setAuthServiceClient(AuthServiceClient authServiceClient) {
        AuthUtils.authServiceClient = authServiceClient;
    }

    public static boolean isAdmin(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) return false;
        
        TokenValidationResponse response = authServiceClient.validateTokenOnly(token);
        return response.isValid() && "ADMIN".equals(response.getRole());
    }
    
    public static boolean isUser(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) return false;
        
        TokenValidationResponse response = authServiceClient.validateTokenOnly(token);
        return response.isValid() && ("USER".equals(response.getRole()) || "ADMIN".equals(response.getRole()));
    }
    
    public static boolean canAccessUser(HttpServletRequest request, Integer requestedUserId) {
        String token = extractToken(request);
        if (token == null) return false;
        
        TokenValidationResponse response = authServiceClient.validateTokenOnly(token);
        
        if (!response.isValid()) {
            return false;
        }
        
        // ADMIN can access any user, USER can only access themselves
        if ("ADMIN".equals(response.getRole())) {
            return true;
        } else if ("USER".equals(response.getRole())) {
            return response.getUserId() != null && response.getUserId().equals(requestedUserId);
        }
        
        return false;
    }
    
    public static Integer getUserIdAsInt(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) return null;
        
        TokenValidationResponse response = authServiceClient.validateTokenOnly(token);
        return response.isValid() ? response.getUserId() : null;
    }
    
    private static String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
