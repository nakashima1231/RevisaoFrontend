package com.example.frontendweb.dto;

public record ClienteDTO(
        Long id,
        String nome,
        Double renda
) {
    public ClienteDTO() {

        this(null, null, null)
        ;
    }


}
