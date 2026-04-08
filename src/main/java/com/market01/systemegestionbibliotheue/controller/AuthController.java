package com.market01.systemegestionbibliotheue.controller;

import com.market01.systemegestionbibliotheue.dto.CreateUtilisateurRequest;
import com.market01.systemegestionbibliotheue.dto.LoginRequest;
import com.market01.systemegestionbibliotheue.dto.UtilisateurSessionResponse;
import com.market01.systemegestionbibliotheue.model.Utilisateur;
import com.market01.systemegestionbibliotheue.service.UtilisateurService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UtilisateurService utilisateurService;

    public AuthController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UtilisateurSessionResponse register(@RequestBody CreateUtilisateurRequest request) {
        Utilisateur utilisateur = utilisateurService.creer(request);
        return utilisateurService.toSessionResponse(utilisateur);
    }

    @PostMapping("/login")
    public UtilisateurSessionResponse login(@RequestBody LoginRequest request) {
        return utilisateurService.authentifier(request);
    }
}
