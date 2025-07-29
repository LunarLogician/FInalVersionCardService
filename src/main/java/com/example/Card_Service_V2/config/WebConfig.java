package com.example.Card_Service_V2.config;

import com.example.Card_Service_V2.interceptors.EnhancedAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private EnhancedAuthInterceptor authInterceptor;
    
    @Override
public void addInterceptors(@org.springframework.lang.NonNull InterceptorRegistry registry) {
    registry.addInterceptor(authInterceptor)
            .addPathPatterns("/api/v1/cards/**")
            .excludePathPatterns(
                "/api/v1/cards/internal/**",
                "/api/v1/auth/**",
                "/actuator/**"
            );
}
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOriginPatterns("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true);
}
}
