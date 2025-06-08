package com.dealsfinder.paymentservice.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication instanceof UsernamePasswordAuthenticationToken authToken) {
                String jwtToken = (String) authToken.getCredentials();
                if (jwtToken != null && !jwtToken.isEmpty()) {
                    requestTemplate.header("Authorization", "Bearer " + jwtToken);
                } else {
                    System.out.println("Warning: No JWT token found in SecurityContext");
                }
            }
        };
    }
}