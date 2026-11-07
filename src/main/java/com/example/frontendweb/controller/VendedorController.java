package com.example.frontendweb.controller;

import com.example.frontendweb.dto.CategoriaDTO;
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
import java.util.List;

@Controller
@RequestMapping("/vendedors")
public class VendedorController {

    private final RestClient restClient;

    public VendedorController(RestClient restClient) {
        this.restClient = restClient;
    }

    @Value("${api.base-url}")
    private String apiBaseUrl;

    @GetMapping
    public String listar(Model model) {
        List<VendedorDTO> vendedors = restClient.get()
                .uri("/vendedors")
                .retrieve()
                .body(new ParameterizedTypeReference<List<VendedorDTO>>() {});

        model.addAttribute("apiBaseUrl", apiBaseUrl);
        model.addAttribute("vendedors", vendedors);
        return "vendedors/lista";
    }

    @GetMapping("/novo")
    public String novoForm(Model model) {
        model.addAttribute("vendedor", new VendedorDTO());
        model.addAttribute("categorias", buscarCategorias());
        return "vendedors/form";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {
        VendedorDTO Vendedor = restClient.get()
                .uri("/vendedors/{id}", id)
                .retrieve()
                .body(VendedorDTO.class);

        model.addAttribute("Vendedor", Vendedor);
        model.addAttribute("categorias", buscarCategorias());
        return "vendedors/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute VendedorDTO VendedorDTO,
                         @RequestParam(value = "categoria.id", required = false) Long categoriaId,
                         @RequestParam(value = "fotoFile", required = false) MultipartFile fotoFile,
                         @RequestParam(value = "manualFile", required = false) MultipartFile manualFile) throws IOException {

        CategoriaDTO categoriaComId = (categoriaId != null) ? new CategoriaDTO(categoriaId, null) : null;

        VendedorDTO payload = new VendedorDTO(
                VendedorDTO.id(),
                VendedorDTO.nome(),
                VendedorDTO.foto()
        );

        MultiValueMap<String, Object> multipartBody = new LinkedMultiValueMap<>();
        HttpHeaders jsonHeader = new HttpHeaders();
        jsonHeader.setContentType(MediaType.APPLICATION_JSON);
        multipartBody.add("Vendedor", new org.springframework.http.HttpEntity<>(payload, jsonHeader));

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
                    .uri("/vendedors")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(multipartBody)
                    .retrieve()
                    .toBodilessEntity();
        } else {
            restClient.put()
                    .uri("/vendedors/{id}", payload.id())
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(multipartBody)
                    .retrieve()
                    .toBodilessEntity();
        }
        return "redirect:/vendedors";
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
                .uri("/vendedors/{id}", id)
                .retrieve()
                .toBodilessEntity();

        return "redirect:/vendedors";
    }
}