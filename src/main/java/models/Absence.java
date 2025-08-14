package models;

import java.sql.Timestamp;

public class Absence {




    public enum Motif {
        MALADIE, CONGE, AUTRE
    }

    private int id;
    private int employeId;
    private Motif motif;
    private String justificatif;
    private String remarque;
    private Timestamp dateEnregistrement;

    // Default Constructor
    public Absence() {
    }

    // Constructor with all fields except ID
    public Absence(int employeId, Motif motif, String justificatif, String remarque, Timestamp dateEnregistrement) {
        this.employeId = employeId;
        this.motif = motif;
        this.justificatif = justificatif;
        this.remarque = remarque;
        this.dateEnregistrement = dateEnregistrement;
    }

    // Constructor with all fields including ID
    public Absence(int id, int employeId, Motif motif, String justificatif, String remarque, Timestamp dateEnregistrement) {
        this.id = id;
        this.employeId = employeId;
        this.motif = motif;
        this.justificatif = justificatif;
        this.remarque = remarque;
        this.dateEnregistrement = dateEnregistrement;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEmployeId() {
        return employeId;
    }

    public void setEmployeId(int employeId) {
        this.employeId = employeId;
    }

    public Motif getMotif() {
        return motif;
    }

    public void setMotif(Motif motif) {
        this.motif = motif;
    }

    public String getJustificatif() {
        return justificatif;
    }

    public void setJustificatif(String justificatif) {
        this.justificatif = justificatif;
    }

    public String getRemarque() {
        return remarque;
    }

    public void setRemarque(String remarque) {
        this.remarque = remarque;
    }

    public Timestamp getDateEnregistrement() {
        return dateEnregistrement;
    }

    public void setDateEnregistrement(Timestamp dateEnregistrement) {
        this.dateEnregistrement = dateEnregistrement;
    }
}