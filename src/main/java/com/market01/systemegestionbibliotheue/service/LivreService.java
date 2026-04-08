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

    private String champObligatoire(String valeur, String nomChamp) {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException("Le champ '" + nomChamp + "' est obligatoire.");
        }
        return valeur.trim();
    }
}
