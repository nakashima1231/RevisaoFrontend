package com.example.frontendweb.controller;

import com.example.frontendweb.dto.ClienteDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.List;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final RestClient restClient;

    public ClienteController(RestClient restClient) {
        this.restClient = restClient;
    }

    @GetMapping
    public String listar(Model model) {
        List<ClienteDTO> clientes = restClient.get()
                .uri("/clientes")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ClienteDTO>>() {});

        model.addAttribute("clientes", clientes);
        return "clientes/lista";
    }

    @GetMapping("/novo")
    public String novoForm(Model model) {
        // Novo Cliente sem Cliente associada
        model.addAttribute("Cliente", new ClienteDTO());
        return "clientes/form";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {
        ClienteDTO Cliente = restClient.get()
                .uri("/clientes/{id}", id)
                .retrieve()
                .body(ClienteDTO.class);

        model.addAttribute("cliente", Cliente);
        return "clientes/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute ClienteDTO ClienteDTO) {

        ClienteDTO payload = new ClienteDTO(
                ClienteDTO.id(),
                ClienteDTO.nome(),
                ClienteDTO.renda()
        );

        if (payload.id() == null) {
            restClient.post()
                    .uri("/clientes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
        } else {
            restClient.put()
                    .uri("/clientes/{id}", payload.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
        }

        return "redirect:/clientes";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        restClient.delete()
                .uri("/clientes/{id}", id)
                .retrieve()
                .toBodilessEntity();

        return "redirect:/clientes";
    }
}