package com.melocode.semin.models;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Participation {
    private IntegerProperty ID_Participation;
    private IntegerProperty ID_Employe;
    private IntegerProperty ID_Seminaire;
    private StringProperty statut;
    private ObjectProperty<LocalDate> dateInscription;
    private StringProperty evaluation;
    private StringProperty certificat;

    public Participation(int ID_Participation, int ID_Employe, int ID_Seminaire, String statut, LocalDate dateInscription, String evaluation, String certificat) {
        this.ID_Participation = new SimpleIntegerProperty(ID_Participation);
        this.ID_Employe = new SimpleIntegerProperty(ID_Employe);
        this.ID_Seminaire = new SimpleIntegerProperty(ID_Seminaire);
        this.statut = new SimpleStringProperty(statut);
        this.dateInscription = new SimpleObjectProperty<>(dateInscription);
        this.evaluation = new SimpleStringProperty(evaluation);
        this.certificat = new SimpleStringProperty(certificat);
    }

    // Getters and Setters

    public int getID_Participation() {
        return ID_Participation.get();
    }

    public IntegerProperty ID_ParticipationProperty() {
        return ID_Participation;
    }

    public void setID_Participation(int ID_Participation) {
        this.ID_Participation.set(ID_Participation);
    }

    public int getID_Employe() {
        return ID_Employe.get();
    }

    public IntegerProperty ID_EmployeProperty() {
        return ID_Employe;
    }

    public void setID_Employe(int ID_Employe) {
        this.ID_Employe.set(ID_Employe);
    }

    public int getID_Seminaire() {
        return ID_Seminaire.get();
    }

    public IntegerProperty ID_SeminaireProperty() {
        return ID_Seminaire;
    }

    public void setID_Seminaire(int ID_Seminaire) {
        this.ID_Seminaire.set(ID_Seminaire);
    }

    public String getStatut() {
        return statut.get();
    }

    public StringProperty statutProperty() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut.set(statut);
    }

    public LocalDate getDate_inscription() {
        return dateInscription.get();
    }

    public ObjectProperty<LocalDate> dateInscriptionProperty() {
        return dateInscription;
    }

    public void setDate_inscription(LocalDate dateInscription) {
        this.dateInscription.set(dateInscription);
    }

    public String getEvaluation() {
        return evaluation.get();
    }

    public StringProperty evaluationProperty() {
        return evaluation;
    }

    public void setEvaluation(String evaluation) {
        this.evaluation.set(evaluation);
    }

    public String getCertificat() {
        return certificat.get();
    }

    public StringProperty certificatProperty() {
        return certificat;
    }

    public void setCertificat(String certificat) {
        this.certificat.set(certificat);
    }
}
