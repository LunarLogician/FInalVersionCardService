package com.example.Card_Service_V2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Card_Service_V2.utils.JwtUtil;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private JwtUtil jwtUtil;

    public Map<String, Object> validateToken(String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return null;
            }

            String token = authHeader.substring(7);

            if (!jwtUtil.validateToken(token) || jwtUtil.isTokenExpired(token)) {
                return null;
            }

            String userId = jwtUtil.extractUserId(token);
            String role = jwtUtil.extractRole(token);

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("valid", true);
            userInfo.put("userId", userId);
            userInfo.put("role", role);

            return userInfo;

        } catch (Exception e) {
            return null;
        }
    }
}