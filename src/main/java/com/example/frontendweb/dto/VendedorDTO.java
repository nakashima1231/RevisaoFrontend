package com.example.frontendweb.dto;

public record VendedorDTO(
        Long id,
        String nome,
        String foto
) {
    public VendedorDTO() {
        this(null, null, null);
    }
}
