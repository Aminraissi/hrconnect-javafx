package models;

import java.time.LocalDate;

public class Demande_Conge {
    private int id;
    private Employe employe;
    private TypeConge typeConge;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private StatutDemande statut;
    private String validationCommentaire; // Nouveau champ pour stocker le commentaire de validation

    // Constructeurs
    public Demande_Conge() {}

    public Demande_Conge(Employe employe, TypeConge typeConge, LocalDate dateDebut, LocalDate dateFin) {
        this.employe = employe;
        this.typeConge = typeConge;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = StatutDemande.EN_ATTENTE; // Statut par défaut
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Employe getEmploye() { return employe; }
    public void setEmploye(Employe employe) { this.employe = employe; }
    public TypeConge getTypeConge() { return typeConge; }
    public void setTypeConge(TypeConge typeConge) { this.typeConge = typeConge; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public StatutDemande getStatut() { return statut; }
    public void setStatut(StatutDemande statut) { this.statut = statut; }
    public String getValidationCommentaire() { return validationCommentaire; }
    public void setValidationCommentaire(String validationCommentaire) { this.validationCommentaire = validationCommentaire; }
}