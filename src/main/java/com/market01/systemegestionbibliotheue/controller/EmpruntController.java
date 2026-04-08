package com.market01.systemegestionbibliotheue.controller;

import com.market01.systemegestionbibliotheue.dto.CreateEmpruntRequest;
import com.market01.systemegestionbibliotheue.dto.RetourEmpruntRequest;
import com.market01.systemegestionbibliotheue.model.Emprunt;
import com.market01.systemegestionbibliotheue.service.EmpruntService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/emprunts")
public class EmpruntController {

    private final EmpruntService empruntService;

    public EmpruntController(EmpruntService empruntService) {
        this.empruntService = empruntService;
    }

    @GetMapping
    public List<Emprunt> lister() {
        return empruntService.lister();
    }

    @GetMapping("/{id}")
    public Emprunt trouverParId(@PathVariable Long id) {
        return empruntService.trouverParId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Emprunt creer(@RequestBody CreateEmpruntRequest request) {
        return empruntService.creer(request);
    }

    @PutMapping("/{id}/retour")
    public Emprunt retourner(@PathVariable Long id, @RequestBody(required = false) RetourEmpruntRequest request) {
        return empruntService.retourner(id, request);
    }
}
