package com.example.frontendweb.dto;

public record UsuarioDTO(
        Long id,
        String username,
        String password,
        String role
) {
    public UsuarioDTO() {
        this(null, null, null, null);
    }
}

