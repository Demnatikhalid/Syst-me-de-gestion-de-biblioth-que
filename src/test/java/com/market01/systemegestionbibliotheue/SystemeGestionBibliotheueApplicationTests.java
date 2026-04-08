package com.market01.systemegestionbibliotheue;

import com.market01.systemegestionbibliotheue.dto.CreateEmpruntRequest;
import com.market01.systemegestionbibliotheue.dto.CreateLivreRequest;
import com.market01.systemegestionbibliotheue.dto.CreateUtilisateurRequest;
import com.market01.systemegestionbibliotheue.dto.LoginRequest;
import com.market01.systemegestionbibliotheue.dto.RetourEmpruntRequest;
import com.market01.systemegestionbibliotheue.dto.UtilisateurSessionResponse;
import com.market01.systemegestionbibliotheue.exception.ConflictException;
import com.market01.systemegestionbibliotheue.model.Emprunt;
import com.market01.systemegestionbibliotheue.model.Livre;
import com.market01.systemegestionbibliotheue.model.Utilisateur;
import com.market01.systemegestionbibliotheue.repository.EmpruntRepository;
import com.market01.systemegestionbibliotheue.repository.LivreRepository;
import com.market01.systemegestionbibliotheue.repository.UtilisateurRepository;
import com.market01.systemegestionbibliotheue.service.EmpruntService;
import com.market01.systemegestionbibliotheue.service.LivreService;
import com.market01.systemegestionbibliotheue.service.UtilisateurService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class SystemeGestionBibliotheueApplicationTests {

    @Test
    void utilisateurPeutEmprunterPuisRetournerUnLivre() {
        LivreRepository livreRepository = Mockito.mock(LivreRepository.class);
        UtilisateurRepository utilisateurRepository = Mockito.mock(UtilisateurRepository.class);
        EmpruntRepository empruntRepository = Mockito.mock(EmpruntRepository.class);

        LivreService livreService = new LivreService(livreRepository);
        UtilisateurService utilisateurService = new UtilisateurService(utilisateurRepository);
        EmpruntService empruntService = new EmpruntService(empruntRepository, utilisateurService, livreService);

        final Livre[] livreSauvegarde = new Livre[1];
        final Utilisateur[] utilisateurSauvegarde = new Utilisateur[1];
        final Emprunt[] empruntSauvegarde = new Emprunt[1];

        when(livreRepository.existsByIsbn("9780132350884")).thenReturn(false);
        when(livreRepository.save(any(Livre.class))).thenAnswer(invocation -> {
            Livre livre = invocation.getArgument(0);
            livre.setId(1L);
            livreSauvegarde[0] = livre;
            return livre;
        });
        when(livreRepository.findById(1L)).thenAnswer(invocation -> Optional.ofNullable(livreSauvegarde[0]));

        when(utilisateurRepository.existsByEmail("khalid@example.com")).thenReturn(false);
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(invocation -> {
            Utilisateur utilisateur = invocation.getArgument(0);
            utilisateur.setId(1L);
            utilisateurSauvegarde[0] = utilisateur;
            return utilisateur;
        });
        when(utilisateurRepository.findById(1L)).thenAnswer(invocation -> Optional.ofNullable(utilisateurSauvegarde[0]));
        when(utilisateurRepository.findByEmail("khalid@example.com")).thenAnswer(invocation -> Optional.ofNullable(utilisateurSauvegarde[0]));

        when(empruntRepository.existsByLivreIdAndDateRetourIsNull(1L)).thenAnswer(invocation ->
                empruntSauvegarde[0] != null
                        && empruntSauvegarde[0].getLivreId().equals(1L)
                        && empruntSauvegarde[0].getDateRetour() == null
        );
        when(empruntRepository.save(any(Emprunt.class))).thenAnswer(invocation -> {
            Emprunt emprunt = invocation.getArgument(0);
            if (emprunt.getId() == null) {
                emprunt.setId(1L);
            }
            empruntSauvegarde[0] = emprunt;
            return emprunt;
        });
        when(empruntRepository.findById(1L)).thenAnswer(invocation -> Optional.ofNullable(empruntSauvegarde[0]));

        CreateLivreRequest livreRequest = new CreateLivreRequest();
        livreRequest.setTitre("Clean Code");
        livreRequest.setAuteur("Robert C. Martin");
        livreRequest.setCategorie("Informatique");
        livreRequest.setIsbn("9780132350884");
        Livre livre = livreService.creer(livreRequest);

        CreateUtilisateurRequest utilisateurRequest = new CreateUtilisateurRequest();
        utilisateurRequest.setNom("Khalid");
        utilisateurRequest.setEmail("khalid@example.com");
        utilisateurRequest.setAdresse("Casablanca");
        utilisateurRequest.setTelephone("0600000000");
        utilisateurRequest.setMotDePasse("secret123");
        Utilisateur utilisateur = utilisateurService.creer(utilisateurRequest);

        CreateEmpruntRequest empruntRequest = new CreateEmpruntRequest();
        empruntRequest.setUtilisateurId(utilisateur.getId());
        empruntRequest.setLivreId(livre.getId());
        empruntRequest.setDateEmprunt(LocalDate.of(2026, 4, 8));
        Emprunt emprunt = empruntService.creer(empruntRequest);

        assertEquals(1L, livre.getId());
        assertEquals(1L, utilisateur.getId());
        assertEquals(1L, emprunt.getId());
        assertEquals(utilisateur.getId(), emprunt.getUtilisateurId());
        assertEquals(livre.getId(), emprunt.getLivreId());
        assertNotNull(utilisateur.getMotDePasse());
        assertTrue(!utilisateur.getMotDePasse().equals("secret123"));

        assertThrows(ConflictException.class, () -> empruntService.creer(empruntRequest));

        RetourEmpruntRequest retourRequest = new RetourEmpruntRequest();
        retourRequest.setDateRetour(LocalDate.of(2026, 4, 10));
        Emprunt empruntRetourne = empruntService.retourner(emprunt.getId(), retourRequest);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("khalid@example.com");
        loginRequest.setMotDePasse("secret123");
        UtilisateurSessionResponse session = utilisateurService.authentifier(loginRequest);

        assertNotNull(empruntRetourne.getDateRetour());
        assertEquals(LocalDate.of(2026, 4, 10), empruntRetourne.getDateRetour());
        assertEquals(utilisateur.getEmail(), session.getEmail());
        assertEquals(utilisateur.getNom(), session.getNom());
    }

}
