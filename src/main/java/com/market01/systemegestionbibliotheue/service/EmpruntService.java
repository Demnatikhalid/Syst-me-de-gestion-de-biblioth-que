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
        return empruntRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public Emprunt trouverParId(Long id) {
        return empruntRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Emprunt introuvable avec l'id " + id + "."));
    }

    @Transactional
    public Emprunt creer(CreateEmpruntRequest request) {
        Long utilisateurId = idObligatoire(request.getUtilisateurId(), "utilisateurId");
        Long livreId = idObligatoire(request.getLivreId(), "livreId");

        utilisateurService.trouverParId(utilisateurId);
        livreService.trouverParId(livreId);

        boolean livreDejaEmprunte = empruntRepository.existsByLivreIdAndDateRetourIsNull(livreId);

        if (livreDejaEmprunte) {
            throw new ConflictException("Le livre " + livreId + " est deja emprunte.");
        }

        LocalDate dateEmprunt = request.getDateEmprunt() != null ? request.getDateEmprunt() : LocalDate.now();
        Emprunt emprunt = new Emprunt(null, utilisateurId, livreId, dateEmprunt, null);
        return empruntRepository.save(emprunt);
    }

    @Transactional
    public Emprunt retourner(Long id, RetourEmpruntRequest request) {
        Emprunt emprunt = trouverParId(id);
        if (emprunt.getDateRetour() != null) {
            throw new ConflictException("Cet emprunt a deja ete retourne.");
        }

        LocalDate dateRetour = request != null && request.getDateRetour() != null
                ? request.getDateRetour()
                : LocalDate.now();

        if (dateRetour.isBefore(emprunt.getDateEmprunt())) {
            throw new IllegalArgumentException("La date de retour ne peut pas etre anterieure a la date d'emprunt.");
        }

        emprunt.setDateRetour(dateRetour);
        return empruntRepository.save(emprunt);
    }

    private Long idObligatoire(Long id, String nomChamp) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Le champ '" + nomChamp + "' est obligatoire.");
        }
        return id;
    }
}
