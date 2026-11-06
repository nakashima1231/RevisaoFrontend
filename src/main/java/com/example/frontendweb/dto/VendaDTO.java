package com.example.frontendweb.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record VendaDTO(
        Long id,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate data,
        Double valor,
        ClienteDTO cliente,
        VendedorDTO vendedor
) {
    public VendaDTO() {
        this(null, null ,null, null, null);
    }

}