package com.example.frontendweb.dto;

public record LoginDTO(
        String username,
        String password
) {
    public LoginDTO() {
        this(null, null);
    }
}

