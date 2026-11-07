package com.example.frontendweb.controller;

import com.example.frontendweb.dto.CategoriaDTO;
import com.example.frontendweb.dto.ClienteDTO;
import com.example.frontendweb.dto.VendaDTO;
import com.example.frontendweb.dto.VendedorDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/vendas")
public class VendaController {

    private final RestClient restClient;

    public VendaController(RestClient restClient) {
        this.restClient = restClient;
    }

    @Value("${api.base-url}")
    private String apiBaseUrl;

    @GetMapping
    public String listar(Model model) {
        List<VendaDTO> Vendas = restClient.get()
                .uri("/vendas")
                .retrieve()
                .body(new ParameterizedTypeReference<List<VendaDTO>>() {});

        model.addAttribute("apiBaseUrl", apiBaseUrl);
        model.addAttribute("Vendas", Vendas);
        return "vendas/lista";
    }

    @GetMapping("/novo")
    public String novoForm(Model model) {
        model.addAttribute("venda", new VendaDTO());
        model.addAttribute("categorias", buscarCategorias());
        return "vendas/form";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {
        VendaDTO Venda = restClient.get()
                .uri("/vendas/{id}", id)
                .retrieve()
                .body(VendaDTO.class);

        model.addAttribute("Venda", Venda);
        model.addAttribute("categorias", buscarCategorias());
        return "vendas/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute VendaDTO VendaDTO,
                         @RequestParam(value = "categoria.id", required = false) Long categoriaId,
                         @RequestParam(value = "fotoFile", required = false) MultipartFile fotoFile,
                         @RequestParam(value = "manualFile", required = false) MultipartFile manualFile) throws IOException {

        CategoriaDTO categoriaComId = (categoriaId != null) ? new CategoriaDTO(categoriaId, null) : null;


        VendaDTO payload = new VendaDTO(
                VendaDTO.id(),
                VendaDTO.data(),
                VendaDTO.valor(),
                VendaDTO.cliente(),
                VendaDTO.vendedor()
        );

        MultiValueMap<String, Object> multipartBody = new LinkedMultiValueMap<>();
        HttpHeaders jsonHeader = new HttpHeaders();
        jsonHeader.setContentType(MediaType.APPLICATION_JSON);
        multipartBody.add("Venda", new org.springframework.http.HttpEntity<>(payload, jsonHeader));

        if (fotoFile != null && !fotoFile.isEmpty()) {
            multipartBody.add("foto", new ByteArrayResource(fotoFile.getBytes()) {
                @Override
                public String getFilename() {
                    return fotoFile.getOriginalFilename();
                }
            });
        }

        if (manualFile != null && !manualFile.isEmpty()) {
            multipartBody.add("manual", new ByteArrayResource(manualFile.getBytes()) {
                @Override
                public String getFilename() {
                    return manualFile.getOriginalFilename();
                }
            });
        }

        if (payload.id() == null) {
            restClient.post()
                    .uri("/vendas")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(multipartBody)
                    .retrieve()
                    .toBodilessEntity();
        } else {
            restClient.put()
                    .uri("/vendas/{id}", payload.id())
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(multipartBody)
                    .retrieve()
                    .toBodilessEntity();
        }
        return "redirect:/vendas";
    }

    private List<CategoriaDTO> buscarCategorias() {
        return restClient.get()
                .uri("/categorias")
                .retrieve()
                .body(new ParameterizedTypeReference<List<CategoriaDTO>>() {});
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        restClient.delete()
                .uri("/vendas/{id}", id)
                .retrieve()
                .toBodilessEntity();

        return "redirect:/vendas";
    }
}