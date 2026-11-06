package com.example.frontendweb.dto;

public record CategoriaDTO(
        Long id,
        String nome
) {
    public CategoriaDTO() {
        this(null, null);
    }
}