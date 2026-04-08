package com.market01.systemegestionbibliotheue.repository;

import com.market01.systemegestionbibliotheue.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    boolean existsByEmail(String email);
}
