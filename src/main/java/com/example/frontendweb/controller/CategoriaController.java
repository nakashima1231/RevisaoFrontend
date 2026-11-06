package com.example.frontendweb.controller;

import com.example.frontendweb.dto.CategoriaDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.List;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    private final RestClient restClient;

    public CategoriaController(RestClient restClient) {
        this.restClient = restClient;
    }

    @GetMapping
    public String listar(Model model) {
        List<CategoriaDTO> categorias = restClient.get()
                .uri("/categorias")
                .retrieve()
                .body(new ParameterizedTypeReference<List<CategoriaDTO>>() {});

        model.addAttribute("categorias", categorias);
        return "categorias/lista";
    }

    @GetMapping("/novo")
    public String novoForm(Model model) {
        // Novo categoria sem categoria associada
        model.addAttribute("categoria", new CategoriaDTO());
        return "categorias/form";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {
        CategoriaDTO categoria = restClient.get()
                .uri("/categorias/{id}", id)
                .retrieve()
                .body(CategoriaDTO.class);

        model.addAttribute("categoria", categoria);
        return "categorias/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute CategoriaDTO categoriaDTO) {

        CategoriaDTO payload = new CategoriaDTO(
                categoriaDTO.id(),
                categoriaDTO.nome()
        );

        if (payload.id() == null) {
            restClient.post()
                    .uri("/categorias")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
        } else {
            restClient.put()
                    .uri("/categorias/{id}", payload.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
        }

        return "redirect:/categorias";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        restClient.delete()
                .uri("/categorias/{id}", id)
                .retrieve()
                .toBodilessEntity();

        return "redirect:/categorias";
    }
}