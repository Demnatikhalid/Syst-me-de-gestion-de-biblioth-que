package com.market01.systemegestionbibliotheue.repository;

import com.market01.systemegestionbibliotheue.model.Livre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LivreRepository extends JpaRepository<Livre, Long> {

    boolean existsByIsbn(String isbn);
}
