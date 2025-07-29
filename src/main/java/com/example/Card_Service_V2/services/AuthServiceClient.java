package com.example.Card_Service_V2.services;

import com.example.Card_Service_V2.services.dtos.TokenValidationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthServiceClient {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${auth.service.base.url:http://localhost:8083/v1/auth}")
    private String authServiceBaseUrl;
    
    
     
    public TokenValidationResponse validateTokenOnly(String token) {
        try {
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            // Only set Authorization header, no need for Content-Type or body
            String trimmedToken = token == null ? "" : token.trim();
            String bearerToken = trimmedToken.startsWith("Bearer ") ? trimmedToken : "Bearer " + trimmedToken;
            System.out.println("Sending Authorization header: " + bearerToken); // Log the token being sent
            headers.set("Authorization", bearerToken);
            org.springframework.http.HttpEntity<Void> entity = new org.springframework.http.HttpEntity<>(headers);

            TokenValidationResponse response = restTemplate.postForObject(
                authServiceBaseUrl + "/validate-simple",
                entity,
                TokenValidationResponse.class
            );

            return response != null ? response : createInvalidResponse("No response from auth service");
        } catch (Exception e) {
            return createInvalidResponse("Auth service unavailable: " + e.getMessage());
        }
    }
     
  
    
    private TokenValidationResponse createInvalidResponse(String message) {
        return TokenValidationResponse.builder()
            .valid(false)
            .message(message)
            .hasPermission(false)
            .build();
    }
}
