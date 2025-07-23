package com.example.Card_Service_V2.services;

import com.example.Card_Service_V2.services.dtos.TokenValidationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceClient {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${auth.service.base.url:http://localhost:8080/api/v1/auth}")
    private String authServiceBaseUrl;
    
    
     
    public TokenValidationResponse validateTokenOnly(String token) {
        try {
            Map<String, String> request = new HashMap<>();
            request.put("token", token.startsWith("Bearer ") ? token.substring(7) : token);
            System.out.println("Validating token: " + request.get("token"));
            
            TokenValidationResponse response = restTemplate.postForObject(
                authServiceBaseUrl + "/validate-simple", 
                request, 
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
