package com.market01.systemegestionbibliotheue.dto;

import com.market01.systemegestionbibliotheue.model.Emprunt;
import com.market01.systemegestionbibliotheue.model.Livre;

public class LivreAvecEmpruntResponse {

    private Livre livre;
    private Emprunt emprunt;

    public LivreAvecEmpruntResponse() {
    }

    public LivreAvecEmpruntResponse(Livre livre, Emprunt emprunt) {
        this.livre = livre;
        this.emprunt = emprunt;
    }

    public Livre getLivre() {
        return livre;
    }

    public void setLivre(Livre livre) {
        this.livre = livre;
    }

    public Emprunt getEmprunt() {
        return emprunt;
    }

    public void setEmprunt(Emprunt emprunt) {
        this.emprunt = emprunt;
    }
}
