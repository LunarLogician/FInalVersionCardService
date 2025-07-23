package com.example.Card_Service_V2.filters;

import com.example.Card_Service_V2.services.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

// DISABLED: Using EnhancedAuthInterceptor instead for auth service integration
// @Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private AuthService authService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        
        if (isPublicEndpoint(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Missing or invalid Authorization header\"}");
            response.setContentType("application/json");
            return;
        }

        Map<String, Object> userInfo = authService.validateToken(authHeader);
        
        if (userInfo == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Invalid or expired token\"}");
            response.setContentType("application/json");
            return;
        }

        request.setAttribute("userInfo", userInfo);
        request.setAttribute("userId", userInfo.get("userId"));
        request.setAttribute("role", userInfo.get("role"));

        String role = (String) userInfo.get("role");
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(
                userInfo.get("userId"), 
                null, 
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
            );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/test/") ||           // Test endpoints
               path.startsWith("/public/") ||         // Public endpoints
               path.equals("/health") ||              // Health check
               path.equals("/actuator/health") ||     // Actuator health
               path.startsWith("/api/v1/cards/internal/"); // Internal service endpoints
    }
}
