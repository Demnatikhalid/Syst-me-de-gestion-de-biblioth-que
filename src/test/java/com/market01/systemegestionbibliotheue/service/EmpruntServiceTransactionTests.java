package com.market01.systemegestionbibliotheue.service;

import com.market01.systemegestionbibliotheue.model.Emprunt;
import com.market01.systemegestionbibliotheue.repository.EmpruntRepository;
import com.market01.systemegestionbibliotheue.repository.LivreRepository;
import com.market01.systemegestionbibliotheue.repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(EmpruntServiceTransactionTests.Config.class)
class EmpruntServiceTransactionTests {

    @Autowired
    private EmpruntService empruntService;

    @Autowired
    private EmpruntRepository empruntRepository;

    @BeforeEach
    void setUp() {
        Emprunt emprunt = new Emprunt(42L, 3L, 7L, LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 15));

        when(empruntRepository.findAll(any(Sort.class))).thenReturn(List.of(emprunt));
        when(empruntRepository.findById(42L)).thenReturn(Optional.of(emprunt));
        when(empruntRepository.findByLivreId(7L)).thenReturn(List.of(emprunt));
        doAnswer(invocation -> {
            assertTrue(
                    TransactionSynchronizationManager.isActualTransactionActive(),
                    "La suppression des emprunts expires doit s'executer dans une transaction."
            );
            return null;
        }).when(empruntRepository).deleteByDateRetourLessThanEqual(any(LocalDate.class));
    }

    @Test
    void listerExecuteLeNettoyageDansUneTransaction() {
        assertDoesNotThrow(() -> empruntService.lister());
    }

    @Test
    void trouverParIdExecuteLeNettoyageDansUneTransaction() {
        assertDoesNotThrow(() -> empruntService.trouverParId(42L));
    }

    @Test
    void trouverParLivreIdExecuteLeNettoyageDansUneTransaction() {
        assertDoesNotThrow(() -> empruntService.trouverParLivreId(7L));
    }

    @Configuration
    @EnableTransactionManagement
    static class Config {

        @Bean
        EmpruntRepository empruntRepository() {
            return Mockito.mock(EmpruntRepository.class);
        }

        @Bean
        LivreRepository livreRepository() {
            return Mockito.mock(LivreRepository.class);
        }

        @Bean
        UtilisateurRepository utilisateurRepository() {
            return Mockito.mock(UtilisateurRepository.class);
        }

        @Bean
        LivreService livreService(LivreRepository livreRepository) {
            return new LivreService(livreRepository);
        }

        @Bean
        UtilisateurService utilisateurService(UtilisateurRepository utilisateurRepository) {
            return new UtilisateurService(utilisateurRepository);
        }

        @Bean
        EmpruntService empruntService(
                EmpruntRepository empruntRepository,
                UtilisateurService utilisateurService,
                LivreService livreService
        ) {
            return new EmpruntService(empruntRepository, utilisateurService, livreService);
        }

        @Bean
        PlatformTransactionManager transactionManager() {
            return new TestTransactionManager();
        }
    }

    static class TestTransactionManager extends AbstractPlatformTransactionManager {

        @Override
        protected Object doGetTransaction() {
            return new Object();
        }

        @Override
        protected void doBegin(Object transaction, TransactionDefinition definition) {
        }

        @Override
        protected void doCommit(DefaultTransactionStatus status) {
        }

        @Override
        protected void doRollback(DefaultTransactionStatus status) {
        }
    }
}
