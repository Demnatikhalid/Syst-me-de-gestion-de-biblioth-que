package com.market01.systemegestionbibliotheue.dto;

public class UtilisateurSessionResponse {

    private Long id;
    private String nom;
    private String email;
    private String adresse;
    private String telephone;

    public UtilisateurSessionResponse() {
    }

    public UtilisateurSessionResponse(Long id, String nom, String email, String adresse, String telephone) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.adresse = adresse;
        this.telephone = telephone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
