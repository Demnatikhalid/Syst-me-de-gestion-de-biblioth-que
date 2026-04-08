package com.market01.systemegestionbibliotheue.service;

import com.market01.systemegestionbibliotheue.dto.CreateEmpruntRequest;
import com.market01.systemegestionbibliotheue.dto.CreateLivreAvecEmpruntRequest;
import com.market01.systemegestionbibliotheue.dto.CreateLivreRequest;
import com.market01.systemegestionbibliotheue.dto.LivreAvecEmpruntResponse;
import com.market01.systemegestionbibliotheue.model.Emprunt;
import com.market01.systemegestionbibliotheue.model.Livre;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BibliothequeService {

    private final LivreService livreService;
    private final EmpruntService empruntService;

    public BibliothequeService(LivreService livreService, EmpruntService empruntService) {
        this.livreService = livreService;
        this.empruntService = empruntService;
    }

    @Transactional
    public LivreAvecEmpruntResponse creerLivreAvecEmprunt(CreateLivreAvecEmpruntRequest request) {
        CreateLivreRequest createLivreRequest = new CreateLivreRequest();
        createLivreRequest.setTitre(request.getTitre());
        createLivreRequest.setAuteur(request.getAuteur());
        createLivreRequest.setCategorie(request.getCategorie());
        createLivreRequest.setIsbn(request.getIsbn());

        Livre livre = livreService.creer(createLivreRequest);

        CreateEmpruntRequest createEmpruntRequest = new CreateEmpruntRequest();
        createEmpruntRequest.setUtilisateurId(request.getUtilisateurId());
        createEmpruntRequest.setLivreId(livre.getId());
        createEmpruntRequest.setDateEmprunt(request.getDateDebutEmprunt());
        createEmpruntRequest.setDateRetour(request.getDateFinEmprunt());

        Emprunt emprunt = empruntService.creer(createEmpruntRequest);
        return new LivreAvecEmpruntResponse(livre, emprunt);
    }
}
