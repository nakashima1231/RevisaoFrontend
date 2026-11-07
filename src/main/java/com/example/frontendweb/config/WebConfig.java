package com.example.frontendweb.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/categorias/**")
                .addPathPatterns("/produtos/**")
                .addPathPatterns("/usuarios/**")
                .addPathPatterns("/cliente/**")
                .addPathPatterns("vendedors/**")
                .addPathPatterns("/vendas/**")
                .excludePathPatterns("/login", "/logout"); // Libera telas de autenticação
    }
}

