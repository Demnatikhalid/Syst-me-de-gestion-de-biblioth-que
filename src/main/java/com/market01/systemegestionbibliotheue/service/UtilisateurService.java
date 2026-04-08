package com.market01.systemegestionbibliotheue.service;

import com.market01.systemegestionbibliotheue.dto.CreateUtilisateurRequest;
import com.market01.systemegestionbibliotheue.dto.LoginRequest;
import com.market01.systemegestionbibliotheue.dto.UtilisateurSessionResponse;
import com.market01.systemegestionbibliotheue.exception.ConflictException;
import com.market01.systemegestionbibliotheue.exception.NotFoundException;
import com.market01.systemegestionbibliotheue.exception.UnauthorizedException;
import com.market01.systemegestionbibliotheue.model.Utilisateur;
import com.market01.systemegestionbibliotheue.repository.UtilisateurRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UtilisateurService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    public List<Utilisateur> lister() {
        return utilisateurRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public Utilisateur trouverParId(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable avec l'id " + id + "."));
    }

    @Transactional
    public Utilisateur creer(CreateUtilisateurRequest request) {
        String email = champObligatoire(request.getEmail(), "email");
        if (!email.contains("@")) {
            throw new IllegalArgumentException("L'email doit contenir '@'.");
        }
        if (utilisateurRepository.existsByEmail(email)) {
            throw new ConflictException("Un utilisateur avec l'email " + email + " existe deja.");
        }

        Utilisateur utilisateur = new Utilisateur(
                null,
                champObligatoire(request.getNom(), "nom"),
                email,
                champObligatoire(request.getAdresse(), "adresse"),
                champObligatoire(request.getTelephone(), "telephone"),
                passwordEncoder.encode(champObligatoire(request.getMotDePasse(), "motDePasse"))
        );
        return utilisateurRepository.save(utilisateur);
    }

    public UtilisateurSessionResponse authentifier(LoginRequest request) {
        String email = champObligatoire(request.getEmail(), "email");
        String motDePasse = champObligatoire(request.getMotDePasse(), "motDePasse");

        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Email ou mot de passe incorrect."));

        if (utilisateur.getMotDePasse() == null || !passwordEncoder.matches(motDePasse, utilisateur.getMotDePasse())) {
            throw new UnauthorizedException("Email ou mot de passe incorrect.");
        }

        return toSessionResponse(utilisateur);
    }

    public UtilisateurSessionResponse toSessionResponse(Utilisateur utilisateur) {
        return new UtilisateurSessionResponse(
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getEmail(),
                utilisateur.getAdresse(),
                utilisateur.getTelephone()
        );
    }

    private String champObligatoire(String valeur, String nomChamp) {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException("Le champ '" + nomChamp + "' est obligatoire.");
        }
        return valeur.trim();
    }
}
