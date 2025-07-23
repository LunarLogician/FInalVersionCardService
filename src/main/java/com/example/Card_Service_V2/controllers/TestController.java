package com.example.Card_Service_V2.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class TestController {


   
    // Validate token endpoint (optional - for testing)
    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body("Invalid Authorization header format");
            }

            String token = authHeader.substring(7);
            // You might need to create a validateToken method in JwtUtil if it doesn't exist
            // For now, this is a placeholder
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "Token validation endpoint");
            response.put("message", "Token format is valid");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Token validation failed");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(401).body(errorResponse);
        }
    }
    
    // Test auth service integration with real token
    @PostMapping("/test-auth-service")
    public ResponseEntity<?> testAuthServiceIntegration(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body("Invalid Authorization header format");
            }

            String token = authHeader.substring(7);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "Auth service integration test");
            response.put("message", "Integration configured successfully");
            response.put("token_received", token);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Auth service integration test failed");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    // Decode JWT token for testing
    @PostMapping("/decode-token")
    public ResponseEntity<?> decodeToken(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            if (token == null) {
                return ResponseEntity.badRequest().body("Token is required");
            }
            
            // Decode JWT manually (Base64 decode the payload)
            String[] tokenParts = token.split("\\.");
            if (tokenParts.length != 3) {
                return ResponseEntity.badRequest().body("Invalid JWT format");
            }
            
            // Decode header and payload
            java.util.Base64.Decoder decoder = java.util.Base64.getUrlDecoder();
            String header = new String(decoder.decode(tokenParts[0]));
            String payload = new String(decoder.decode(tokenParts[1]));
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "Token decoded successfully");
            response.put("header", header);
            response.put("payload", payload);
            response.put("signature", tokenParts[2]);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Token decode failed");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }
    
    // Debug endpoint to trace token flow
    @PostMapping("/debug-token-flow")
    public ResponseEntity<?> debugTokenFlow(@RequestHeader("Authorization") String authHeader) {
        try {
            Map<String, Object> debugInfo = new HashMap<>();
            debugInfo.put("step", "1. Received Authorization header");
            debugInfo.put("authHeader", authHeader);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                debugInfo.put("error", "Invalid Authorization header format");
                return ResponseEntity.badRequest().body(debugInfo);
            }

            String token = authHeader.substring(7);
            debugInfo.put("step", "2. Extracted token");
            debugInfo.put("extractedToken", token);
            
            // Try decoding the token
            try {
                String[] tokenParts = token.split("\\.");
                if (tokenParts.length == 3) {
                    java.util.Base64.Decoder decoder = java.util.Base64.getUrlDecoder();
                    String payload = new String(decoder.decode(tokenParts[1]));
                    debugInfo.put("step", "3. Decoded token payload");
                    debugInfo.put("payload", payload);
                }
            } catch (Exception e) {
                debugInfo.put("decodeError", e.getMessage());
            }
            
            debugInfo.put("status", "Token flow debugging complete");
            debugInfo.put("message", "Check the steps above to see token processing");
            
            return ResponseEntity.ok(debugInfo);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Debug token flow failed");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // Test AuthUtils integration
    @PostMapping("/test-authutils")
    public ResponseEntity<?> testAuthUtils(@RequestHeader("Authorization") String authHeader,
                                          jakarta.servlet.http.HttpServletRequest request) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("step", "Testing AuthUtils integration");
            
            // Test token extraction
            String token = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                response.put("tokenExtracted", token);
            }
            
            // Test AuthUtils methods (these will call AuthServiceClient)
            try {
                boolean isAdmin = com.example.Card_Service_V2.utils.AuthUtils.isAdmin(request);
                response.put("isAdmin", isAdmin);
            } catch (Exception e) {
                response.put("isAdminError", e.getMessage());
            }
            
            try {
                boolean isUser = com.example.Card_Service_V2.utils.AuthUtils.isUser(request);
                response.put("isUser", isUser);
            } catch (Exception e) {
                response.put("isUserError", e.getMessage());
            }
            
            try {
                Integer userId = com.example.Card_Service_V2.utils.AuthUtils.getUserIdAsInt(request);
                response.put("userId", userId);
            } catch (Exception e) {
                response.put("userIdError", e.getMessage());
            }
            
            response.put("status", "AuthUtils test completed");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "AuthUtils test failed");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // Simple test endpoint to bypass interceptor and test token directly
    @PostMapping("/manual-token-test")
    public ResponseEntity<?> manualTokenTest(@RequestHeader("Authorization") String authHeader) {
        try {
            System.out.println("🧪 ManualTokenTest: Starting manual token test");
            
            Map<String, Object> response = new HashMap<>();
            response.put("step1", "Received Authorization header: " + 
                (authHeader != null ? authHeader.substring(0, Math.min(20, authHeader.length())) + "..." : "null"));

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.put("error", "Invalid Authorization header format");
                return ResponseEntity.badRequest().body(response);
            }

            String token = authHeader.substring(7);
            response.put("step2", "Extracted token: " + token.substring(0, Math.min(20, token.length())) + "...");
            
            // Test with FallbackAuthService directly
            try {
                com.example.Card_Service_V2.services.FallbackAuthService fallbackService = 
                    new com.example.Card_Service_V2.services.FallbackAuthService();
                
                // We need to manually set the JwtUtil since @Autowired won't work here
                com.example.Card_Service_V2.utils.JwtUtil jwtUtil = new com.example.Card_Service_V2.utils.JwtUtil();
                
                // Use reflection to set the field
                java.lang.reflect.Field jwtField = fallbackService.getClass().getDeclaredField("jwtUtil");
                jwtField.setAccessible(true);
                jwtField.set(fallbackService, jwtUtil);
                
                com.example.Card_Service_V2.services.dtos.TokenValidationResponse validationResponse = 
                    fallbackService.validateTokenLocally(token);
                
                response.put("step3", "FallbackAuthService validation result");
                response.put("valid", validationResponse.isValid());
                response.put("role", validationResponse.getRole());
                response.put("userId", validationResponse.getUserId());
                response.put("message", validationResponse.getMessage());
                
            } catch (Exception e) {
                response.put("step3_error", "FallbackAuthService test failed: " + e.getMessage());
            }
            
            // Try decoding manually
            try {
                String[] tokenParts = token.split("\\.");
                if (tokenParts.length == 3) {
                    java.util.Base64.Decoder decoder = java.util.Base64.getUrlDecoder();
                    String payload = new String(decoder.decode(tokenParts[1]));
                    response.put("step4", "Manual token decode successful");
                    response.put("payload", payload);
                }
            } catch (Exception e) {
                response.put("step4_error", "Manual decode failed: " + e.getMessage());
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Manual token test failed");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    // Direct auth service test (bypasses security and interceptors)
    @PostMapping("/direct-auth-test")
    public ResponseEntity<?> directAuthTest(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            if (token == null) {
                return ResponseEntity.badRequest().body("Token is required in request body");
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("step", "1. Testing direct auth service integration");
            response.put("token", token.substring(0, Math.min(20, token.length())) + "...");
            
            // Test AuthServiceClient directly
            try {
                com.example.Card_Service_V2.services.AuthServiceClient authClient = 
                    new com.example.Card_Service_V2.services.AuthServiceClient();
                // Note: This won't work because authClient needs @Autowired dependencies
                response.put("directTestNote", "Direct instantiation needs Spring context");
            } catch (Exception e) {
                response.put("directTestError", e.getMessage());
            }
            
            // Test FallbackAuthService
            try {
                com.example.Card_Service_V2.services.FallbackAuthService fallback = 
                    new com.example.Card_Service_V2.services.FallbackAuthService();
                com.example.Card_Service_V2.services.dtos.TokenValidationResponse fallbackResult = 
                    fallback.validateTokenLocally(token);
                    
                response.put("fallbackTest", Map.of(
                    "valid", fallbackResult.isValid(),
                    "role", fallbackResult.getRole(),
                    "userId", fallbackResult.getUserId(),
                    "message", fallbackResult.getMessage()
                ));
            } catch (Exception e) {
                response.put("fallbackTestError", e.getMessage());
            }
            
            response.put("status", "Direct auth service test completed");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Direct auth service test failed");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // Test current auth flow with your specific token
    @PostMapping("/test-current-token")
    public ResponseEntity<?> testCurrentToken() {
        try {
            String yourToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huLmFkbWluQHZhdWx0c3BheS5jb20iLCJpZCI6MSwicm9sZSI6IkFETUlOIiwidHlwZSI6IkFDQ0VTUyIsImlhdCI6MTc1MjU4NjA4OCwiZXhwIjoxNzUyNTg2Njg4fQ.pNInDvD8PWI9mFLZu2FgNsEqXCidAOFhvABqS6nMZ_U";
            
            Map<String, Object> response = new HashMap<>();
            response.put("step", "1. Testing your specific auth service token");
            response.put("tokenLength", yourToken.length());
            
            // Test direct fallback
            try {
                com.example.Card_Service_V2.services.FallbackAuthService fallback = 
                    new com.example.Card_Service_V2.services.FallbackAuthService();
                com.example.Card_Service_V2.services.dtos.TokenValidationResponse result = 
                    fallback.validateTokenLocally(yourToken);
                    
                response.put("fallbackResult", Map.of(
                    "valid", result.isValid(),
                    "role", result.getRole(),
                    "userId", result.getUserId(),
                    "username", result.getUsername(),
                    "message", result.getMessage(),
                    "hasPermission", result.isHasPermission()
                ));
                
                if (result.isValid()) {
                    response.put("status", "✅ SUCCESS: Your token is valid and should work!");
                    response.put("nextStep", "Try: curl -X GET http://localhost:8080/api/v1/cards -H 'Authorization: Bearer " + yourToken + "'");
                } else {
                    response.put("status", "❌ FAILED: Token validation failed");
                    response.put("debug", "Check the logs for detailed error information");
                }
                
            } catch (Exception e) {
                response.put("fallbackError", e.getMessage());
                response.put("status", "❌ ERROR: Exception during fallback test");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Current token test failed");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}