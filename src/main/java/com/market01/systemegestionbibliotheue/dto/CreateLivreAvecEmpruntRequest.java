package com.market01.systemegestionbibliotheue.dto;

import java.time.LocalDate;

public class CreateLivreAvecEmpruntRequest {

    private String titre;
    private String auteur;
    private String categorie;
    private String isbn;
    private Long utilisateurId;
    private LocalDate dateDebutEmprunt;
    private LocalDate dateFinEmprunt;

    public CreateLivreAvecEmpruntRequest() {
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Long getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(Long utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public LocalDate getDateDebutEmprunt() {
        return dateDebutEmprunt;
    }

    public void setDateDebutEmprunt(LocalDate dateDebutEmprunt) {
        this.dateDebutEmprunt = dateDebutEmprunt;
    }

    public LocalDate getDateFinEmprunt() {
        return dateFinEmprunt;
    }

    public void setDateFinEmprunt(LocalDate dateFinEmprunt) {
        this.dateFinEmprunt = dateFinEmprunt;
    }
}
