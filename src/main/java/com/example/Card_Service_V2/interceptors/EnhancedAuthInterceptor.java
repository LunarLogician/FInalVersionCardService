package com.example.Card_Service_V2.interceptors;

import com.example.Card_Service_V2.services.AuthServiceClient;
import com.example.Card_Service_V2.services.dtos.TokenValidationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class EnhancedAuthInterceptor implements HandlerInterceptor {
    
    @Autowired
    private AuthServiceClient authServiceClient;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        String uri = request.getRequestURI();
        
        if (uri.contains("/internal/") || uri.contains("/auth/")) {
            return true;
        }
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Authorization header required\"}");
            return false;
        }
        
        String token = authHeader.substring(7);

        TokenValidationResponse validationResponse = authServiceClient.validateTokenOnly(token);

        if (!validationResponse.isValid()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"" + validationResponse.getMessage() + "\"}");
            return false;
        }
        
        if (validationResponse.getUserId() != null) {
            request.setAttribute("userId", validationResponse.getUserId().toString());
        }
        request.setAttribute("role", validationResponse.getRole());
        request.setAttribute("username", validationResponse.getUsername());
        
        return true;
    }
}
