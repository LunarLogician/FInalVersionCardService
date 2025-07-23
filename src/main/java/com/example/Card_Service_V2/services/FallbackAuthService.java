package com.example.Card_Service_V2.services;

import com.example.Card_Service_V2.services.dtos.TokenValidationResponse;
import org.springframework.stereotype.Service;

/**
 * Fallback authentication service that decodes auth service tokens locally
 * when the remote auth service is unavailable
 */
@Service
public class FallbackAuthService {
    
    // REMOVED: Local JwtUtil dependency - now purely auth service token decoding
    
    /**
     * Validates auth service token locally as fallback
     */
    public TokenValidationResponse validateTokenLocally(String token) {
        try {
            System.out.println("🔄 FallbackAuthService: Attempting auth service token decode (no local JWT)");
            
            // Only try to decode auth service tokens
            TokenValidationResponse authServiceResponse = decodeAuthServiceToken(token);
            if (authServiceResponse.isValid()) {
                return authServiceResponse;
            }
            
            // No more local JWT fallback - auth service tokens only
            return TokenValidationResponse.builder()
                .valid(false)
                .message("Token validation failed - not a valid auth service token")
                .hasPermission(false)
                .build();
                
        } catch (Exception e) {
            return TokenValidationResponse.builder()
                .valid(false)
                .message("Token validation failed: " + e.getMessage())
                .hasPermission(false)
                .build();
        }
    }
    
    /**
     * Decode auth service token manually
     */
    private TokenValidationResponse decodeAuthServiceToken(String token) {
        try {
            System.out.println("🔍 FallbackAuthService: Attempting to decode auth service token");
            String[] tokenParts = token.split("\\.");
            if (tokenParts.length != 3) {
                System.out.println("❌ FallbackAuthService: Invalid JWT format - not 3 parts");
                return TokenValidationResponse.builder()
                    .valid(false)
                    .message("Invalid JWT format")
                    .build();
            }
            
            // Decode payload
            java.util.Base64.Decoder decoder = java.util.Base64.getUrlDecoder();
            String payload = new String(decoder.decode(tokenParts[1]));
            System.out.println("📋 FallbackAuthService: Decoded payload: " + payload);
            
            // Parse JSON manually or use ObjectMapper
            // For simplicity, using string parsing (in production, use Jackson ObjectMapper)
            if (payload.contains("\"role\":\"ADMIN\"") || payload.contains("\"role\":\"USER\"")) {
                String role = extractFieldFromPayload(payload, "role");
                String idStr = extractFieldFromPayload(payload, "id");
                String sub = extractFieldFromPayload(payload, "sub");
                
                System.out.println("🎯 FallbackAuthService: Extracted - role: " + role + ", id: " + idStr + ", sub: " + sub);
                
                // Check expiration
                String expStr = extractFieldFromPayload(payload, "exp");
                if (expStr != null) {
                    long exp = Long.parseLong(expStr);
                    long currentTime = System.currentTimeMillis() / 1000;
                    System.out.println("⏰ FallbackAuthService: Token exp: " + exp + ", current: " + currentTime);
                    if (exp < currentTime) {
                        System.out.println("❌ FallbackAuthService: Token expired");
                        return TokenValidationResponse.builder()
                            .valid(false)
                            .message("Token expired")
                            .build();
                    }
                }
                
                Integer userId = null;
                if (idStr != null) {
                    userId = Integer.parseInt(idStr);
                }
                
                System.out.println("✅ FallbackAuthService: Auth service token validated locally");
                return TokenValidationResponse.builder()
                    .valid(true)
                    .role(role)
                    .userId(userId)
                    .username(sub)
                    .message("Auth service token validated locally (signature not verified)")
                    .hasPermission(true)
                    .build();
            }
            
            System.out.println("❌ FallbackAuthService: Unrecognized token format - no role found");
            return TokenValidationResponse.builder()
                .valid(false)
                .message("Unrecognized token format")
                .build();
                
        } catch (Exception e) {
            System.out.println("❌ FallbackAuthService: Error decoding auth service token: " + e.getMessage());
            return TokenValidationResponse.builder()
                .valid(false)
                .message("Error decoding auth service token: " + e.getMessage())
                .build();
        }
    }
    
    /**
     * Extract field value from JWT payload JSON string
     */
    private String extractFieldFromPayload(String payload, String fieldName) {
        try {
            String pattern = "\"" + fieldName + "\":";
            int startIndex = payload.indexOf(pattern);
            if (startIndex == -1) return null;
            
            startIndex += pattern.length();
            
            // Skip whitespace and quotes
            while (startIndex < payload.length() && 
                   (payload.charAt(startIndex) == ' ' || payload.charAt(startIndex) == '"')) {
                startIndex++;
            }
            
            int endIndex = startIndex;
            while (endIndex < payload.length() && 
                   payload.charAt(endIndex) != ',' && 
                   payload.charAt(endIndex) != '}' && 
                   payload.charAt(endIndex) != '"') {
                endIndex++;
            }
            
            String result = payload.substring(startIndex, endIndex);
            System.out.println("🔍 FallbackAuthService: Extracted " + fieldName + " = " + result);
            return result;
        } catch (Exception e) {
            System.out.println("❌ FallbackAuthService: Error extracting " + fieldName + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Check basic permissions locally
     */
    public boolean hasBasicPermission(String role, String method, String url) {
        if ("ADMIN".equals(role)) {
            return true; // Admin has all permissions
        }
        
        if ("USER".equals(role)) {
            // Basic user permissions
            if ("GET".equals(method) && url.contains("/cards")) {
                return true; // Users can list their cards
            }
            if ("POST".equals(method) && url.contains("/cards")) {
                return true; // Users can create cards
            }
            if ("PUT".equals(method) && url.contains("/cards") && !url.contains("/block") && !url.contains("/unblock")) {
                return true; // Users can update their cards (but not block/unblock)
            }
        }
        
        return false;
    }
}
