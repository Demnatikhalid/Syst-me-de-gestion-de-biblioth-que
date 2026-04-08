package com.market01.systemegestionbibliotheue.repository;

import com.market01.systemegestionbibliotheue.model.Emprunt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EmpruntRepository extends JpaRepository<Emprunt, Long> {

    List<Emprunt> findByLivreId(Long livreId);

    void deleteByDateRetourLessThanEqual(LocalDate date);
}
