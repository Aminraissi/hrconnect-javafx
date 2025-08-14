package models;

import java.time.LocalDate;

public class Valider_Conge {
    private int id;
    private Demande_Conge demande;
    private StatutDemande statut;
    private String commentaire;
    private LocalDate dateValidation;

    // Constructeurs
    public Valider_Conge() {}

    public Valider_Conge(Demande_Conge demande, StatutDemande statut, String commentaire) {
        this.demande = demande;
        this.statut = statut;
        this.commentaire = commentaire;
        this.dateValidation = LocalDate.now();
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Demande_Conge getDemande() { return demande; }
    public void setDemande(Demande_Conge demande) { this.demande = demande; }
    public StatutDemande getStatut() { return statut; }
    public void setStatut(StatutDemande statut) { this.statut = statut; }
    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
    public LocalDate getDateValidation() { return dateValidation; }
    public void setDateValidation(LocalDate dateValidation) { this.dateValidation = dateValidation; }
}