package com.market01.systemegestionbibliotheue.dto;

import java.time.LocalDate;

public class RetourEmpruntRequest {

    private LocalDate dateRetour;

    public RetourEmpruntRequest() {
    }

    public LocalDate getDateRetour() {
        return dateRetour;
    }

    public void setDateRetour(LocalDate dateRetour) {
        this.dateRetour = dateRetour;
    }
}
