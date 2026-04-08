package com.market01.systemegestionbibliotheue.controller;

import com.market01.systemegestionbibliotheue.dto.CreateUtilisateurRequest;
import com.market01.systemegestionbibliotheue.model.Utilisateur;
import com.market01.systemegestionbibliotheue.service.UtilisateurService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @GetMapping
    public List<Utilisateur> lister() {
        return utilisateurService.lister();
    }

    @GetMapping("/{id}")
    public Utilisateur trouverParId(@PathVariable Long id) {
        return utilisateurService.trouverParId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Utilisateur creer(@RequestBody CreateUtilisateurRequest request) {
        return utilisateurService.creer(request);
    }
}
