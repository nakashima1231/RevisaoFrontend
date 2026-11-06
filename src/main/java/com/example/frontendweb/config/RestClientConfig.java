package com.example.frontendweb.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient(@Value("${api.base-url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestInterceptor((request, body, execution) -> {
                    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                    if (attributes != null) {
                        HttpServletRequest httpRequest = attributes.getRequest();
                        HttpSession session = httpRequest.getSession(false);

                        if (session != null) {
                            String token = (String) session.getAttribute("tokenJwt");

                            if (token != null && !token.isBlank()) {
                                String authHeader = token.startsWith("Bearer ") ? token : "Bearer " + token;
                                request.getHeaders().add(HttpHeaders.AUTHORIZATION, authHeader);
                            }
                        }
                    }
                    return execution.execute(request, body);
                })
                .build();
    }
}