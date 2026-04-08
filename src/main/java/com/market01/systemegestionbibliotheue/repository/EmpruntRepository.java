package com.market01.systemegestionbibliotheue.repository;

import com.market01.systemegestionbibliotheue.model.Emprunt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpruntRepository extends JpaRepository<Emprunt, Long> {

    boolean existsByLivreIdAndDateRetourIsNull(Long livreId);
}
