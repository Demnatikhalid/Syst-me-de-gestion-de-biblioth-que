package com.market01.systemegestionbibliotheue.service;

import com.market01.systemegestionbibliotheue.dto.CreateLivreRequest;
import com.market01.systemegestionbibliotheue.exception.ConflictException;
import com.market01.systemegestionbibliotheue.exception.NotFoundException;
import com.market01.systemegestionbibliotheue.model.Livre;
import com.market01.systemegestionbibliotheue.repository.LivreRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LivreService {

    private final LivreRepository livreRepository;

    public LivreService(LivreRepository livreRepository) {
        this.livreRepository = livreRepository;
    }

    public List<Livre> lister() {
        return livreRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public Livre trouverParId(Long id) {
        return livreRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Livre introuvable avec l'id " + id + "."));
    }

    @Transactional
    public Livre creer(CreateLivreRequest request) {
        String isbn = champObligatoire(request.getIsbn(), "isbn");
        if (livreRepository.existsByIsbn(isbn)) {
            throw new ConflictException("Un livre avec l'ISBN " + isbn + " existe deja.");
        }

        Livre livre = new Livre(
                null,
                champObligatoire(request.getTitre(), "titre"),
                champObligatoire(request.getAuteur(), "auteur"),
                champObligatoire(request.getCategorie(), "categorie"),
                isbn
        );
        return livreRepository.save(livre);
    }

    @Transactional
    public Livre modifier(Long id, CreateLivreRequest request) {
        Livre livre = trouverParId(id);
        String isbn = champObligatoire(request.getIsbn(), "isbn");
        if (livreRepository.existsByIsbnAndIdNot(isbn, id)) {
            throw new ConflictException("Un livre avec l'ISBN " + isbn + " existe deja.");
        }

        livre.setTitre(champObligatoire(request.getTitre(), "titre"));
        livre.setAuteur(champObligatoire(request.getAuteur(), "auteur"));
        livre.setCategorie(champObligatoire(request.getCategorie(), "categorie"));
        livre.setIsbn(isbn);
        return livreRepository.save(livre);
    }

    @Transactional
    public void supprimer(Long id) {
        Livre livre = trouverParId(id);
        livreRepository.delete(livre);
    }

    private String champObligatoire(String valeur, String nomChamp) {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException("Le champ '" + nomChamp + "' est obligatoire.");
        }
        return valeur.trim();
    }
}
