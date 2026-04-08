package com.market01.systemegestionbibliotheue.service;

import com.market01.systemegestionbibliotheue.dto.CreateEmpruntRequest;
import com.market01.systemegestionbibliotheue.dto.RetourEmpruntRequest;
import com.market01.systemegestionbibliotheue.exception.ConflictException;
import com.market01.systemegestionbibliotheue.exception.NotFoundException;
import com.market01.systemegestionbibliotheue.model.Emprunt;
import com.market01.systemegestionbibliotheue.repository.EmpruntRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmpruntService {

    private final EmpruntRepository empruntRepository;
    private final UtilisateurService utilisateurService;
    private final LivreService livreService;

    public EmpruntService(EmpruntRepository empruntRepository, UtilisateurService utilisateurService, LivreService livreService) {
        this.empruntRepository = empruntRepository;
        this.utilisateurService = utilisateurService;
        this.livreService = livreService;
    }

    public List<Emprunt> lister() {
        nettoyerEmpruntsExpires();
        return empruntRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public Emprunt trouverParId(Long id) {
        nettoyerEmpruntsExpires();
        return empruntRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Emprunt introuvable avec l'id " + id + "."));
    }

    @Transactional
    public Emprunt creer(CreateEmpruntRequest request) {
        nettoyerEmpruntsExpires();

        Long utilisateurId = idObligatoire(request.getUtilisateurId(), "utilisateurId");
        Long livreId = idObligatoire(request.getLivreId(), "livreId");

        utilisateurService.trouverParId(utilisateurId);
        livreService.trouverParId(livreId);

        LocalDate aujourdHui = LocalDate.now();
        boolean livreDejaEmprunte = empruntRepository.findByLivreId(livreId).stream()
                .anyMatch(emprunt -> emprunt.getDateRetour() == null || !emprunt.getDateRetour().isBefore(aujourdHui));

        if (livreDejaEmprunte) {
            throw new ConflictException("Le livre " + livreId + " est deja emprunte.");
        }

        LocalDate dateEmprunt = request.getDateEmprunt() != null ? request.getDateEmprunt() : LocalDate.now();
        LocalDate dateRetour = request.getDateRetour();
        if (dateRetour == null) {
            throw new IllegalArgumentException("Le champ 'dateRetour' est obligatoire.");
        }
        if (dateRetour.isBefore(dateEmprunt)) {
            throw new IllegalArgumentException("La date de fin d'emprunt ne peut pas etre anterieure a la date de debut.");
        }

        Emprunt emprunt = new Emprunt(null, utilisateurId, livreId, dateEmprunt, dateRetour);
        return empruntRepository.save(emprunt);
    }

    @Transactional
    public Emprunt retourner(Long id, RetourEmpruntRequest request) {
        Emprunt emprunt = trouverParId(id);
        LocalDate dateRetour = request != null && request.getDateRetour() != null
                ? request.getDateRetour()
                : LocalDate.now();

        if (dateRetour.isBefore(emprunt.getDateEmprunt())) {
            throw new IllegalArgumentException("La date de retour ne peut pas etre anterieure a la date d'emprunt.");
        }

        emprunt.setDateRetour(dateRetour);
        empruntRepository.delete(emprunt);
        return emprunt;
    }

    @Transactional
    public void nettoyerEmpruntsExpires() {
        empruntRepository.deleteByDateRetourLessThanEqual(LocalDate.now());
    }

    private Long idObligatoire(Long id, String nomChamp) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Le champ '" + nomChamp + "' est obligatoire.");
        }
        return id;
    }
}
