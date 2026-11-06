package com.example.frontendweb.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public record ProdutoDTO(
        Long id,
        String nome,
        Double preco,
        String foto,
        String manual,
        CategoriaDTO categoria,
        @JsonAlias("nomeCategoria")
        String nomeCategoria
) {
    public String getNomeCategoriaExibicao() {
        if (nomeCategoria != null) {
            return nomeCategoria;
        }
        if (categoria != null && categoria.nome() != null) {
            return categoria.nome();
        }
        return "Sem Categoria";
    }
    public ProdutoDTO(){this(null, null, null, null, null, null, null);}
}

