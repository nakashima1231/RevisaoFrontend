package com.example.frontendweb.controller;

import com.example.frontendweb.dto.UsuarioDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final RestClient restClient;

    public UsuarioController(RestClient restClient) {
        this.restClient = restClient;
    }

    @GetMapping
    public String listar(Model model) {
        List<UsuarioDTO> usuarios = restClient.get()
                .uri("/usuarios")
                .retrieve()
                .body(new ParameterizedTypeReference<List<UsuarioDTO>>() {});

        model.addAttribute("usuarios", usuarios);
        return "usuarios/lista";
    }

    @GetMapping("/novo")
    public String novoForm(Model model) {
        model.addAttribute("usuario", new UsuarioDTO());
        return "usuarios/form";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {
        UsuarioDTO usuario = restClient.get()
                .uri("/usuarios/{id}", id)
                .retrieve()
                .body(UsuarioDTO.class);

        model.addAttribute("usuario", usuario);
        return "usuarios/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute UsuarioDTO usuarioDTO) {
        if (usuarioDTO.id() == null) {
            restClient.post()
                    .uri("/usuarios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(usuarioDTO)
                    .retrieve()
                    .toBodilessEntity();
        } else {
            restClient.put()
                    .uri("/usuarios/{id}", usuarioDTO.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(usuarioDTO)
                    .retrieve()
                    .toBodilessEntity();
        }

        return "redirect:/usuarios";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        restClient.delete()
                .uri("/usuarios/{id}", id)
                .retrieve()
                .toBodilessEntity();

        return "redirect:/usuarios";
    }
}