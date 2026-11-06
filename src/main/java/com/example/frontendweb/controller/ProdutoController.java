package com.example.frontendweb.controller;

import com.example.frontendweb.dto.CategoriaDTO;
import com.example.frontendweb.dto.ProdutoDTO;
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
@RequestMapping("/produtos")
public class ProdutoController {

    private final RestClient restClient;

    public ProdutoController(RestClient restClient) {
        this.restClient = restClient;
    }

    @Value("${api.base-url}")
    private String apiBaseUrl;

    @GetMapping
    public String listar(Model model) {
        List<ProdutoDTO> produtos = restClient.get()
                .uri("/produtos")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ProdutoDTO>>() {});

        model.addAttribute("apiBaseUrl", apiBaseUrl);
        model.addAttribute("produtos", produtos);
        return "produtos/lista";
    }

    @GetMapping("/novo")
    public String novoForm(Model model) {
        model.addAttribute("produto", new ProdutoDTO());
        model.addAttribute("categorias", buscarCategorias());
        return "produtos/form";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {
        ProdutoDTO produto = restClient.get()
                .uri("/produtos/{id}", id)
                .retrieve()
                .body(ProdutoDTO.class);

        model.addAttribute("produto", produto);
        model.addAttribute("categorias", buscarCategorias());
        return "produtos/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute ProdutoDTO produtoDTO,
                         @RequestParam(value = "categoria.id", required = false) Long categoriaId,
                         @RequestParam(value = "fotoFile", required = false) MultipartFile fotoFile,
                         @RequestParam(value = "manualFile", required = false) MultipartFile manualFile) throws IOException {

        CategoriaDTO categoriaComId = (categoriaId != null) ? new CategoriaDTO(categoriaId, null) : null;

        ProdutoDTO payload = new ProdutoDTO(
                produtoDTO.id(),
                produtoDTO.nome(),
                produtoDTO.preco(),
                produtoDTO.foto(),
                produtoDTO.manual(),
                categoriaComId,
                null
        );

        MultiValueMap<String, Object> multipartBody = new LinkedMultiValueMap<>();
        HttpHeaders jsonHeader = new HttpHeaders();
        jsonHeader.setContentType(MediaType.APPLICATION_JSON);
        multipartBody.add("produto", new org.springframework.http.HttpEntity<>(payload, jsonHeader));

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
                    .uri("/produtos")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(multipartBody)
                    .retrieve()
                    .toBodilessEntity();
        } else {
            restClient.put()
                    .uri("/produtos/{id}", payload.id())
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(multipartBody)
                    .retrieve()
                    .toBodilessEntity();
        }
        return "redirect:/produtos";
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
                .uri("/produtos/{id}", id)
                .retrieve()
                .toBodilessEntity();

        return "redirect:/produtos";
    }
}