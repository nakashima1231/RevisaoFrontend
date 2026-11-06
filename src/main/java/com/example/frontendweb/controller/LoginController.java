package com.example.frontendweb.controller;

import com.example.frontendweb.dto.LoginDTO;
import com.example.frontendweb.dto.TokenDTO;
import com.example.frontendweb.dto.UsuarioDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @Autowired
    private RestClient restClient;

    @GetMapping("/login")
    public String telaLogin(Model model) {
        // Passa um objeto limpo para o formulário no Thymeleaf
        model.addAttribute("loginData", new LoginDTO());
        return "login";
    }

    @PostMapping("/login")
    public String realizarLogin(@ModelAttribute("loginData") LoginDTO loginDTO, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            TokenDTO tokenDto = restClient.post()
                    .uri("/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(loginDTO)
                    .retrieve()
                    .body(TokenDTO.class);

            if (tokenDto != null && tokenDto.token() != null) {
                session.setAttribute("tokenJwt", tokenDto.token());
                session.setAttribute("usuarioLogado", new UsuarioDTO(null, loginDTO.username(), null, tokenDto.role()));
                return "redirect:/produtos";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Falha ao realizar login: "+e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Destrói a sessão e remove o tokenJwt
        return "redirect:/login";
    }
}