package com.market01.systemegestionbibliotheue.controller;

import com.market01.systemegestionbibliotheue.dto.CreateLivreRequest;
import com.market01.systemegestionbibliotheue.model.Livre;
import com.market01.systemegestionbibliotheue.service.LivreService;
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
@RequestMapping("/api/livres")
public class LivreController {

    private final LivreService livreService;

    public LivreController(LivreService livreService) {
        this.livreService = livreService;
    }

    @GetMapping
    public List<Livre> lister() {
        return livreService.lister();
    }

    @GetMapping("/{id}")
    public Livre trouverParId(@PathVariable Long id) {
        return livreService.trouverParId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Livre creer(@RequestBody CreateLivreRequest request) {
        return livreService.creer(request);
    }
}
