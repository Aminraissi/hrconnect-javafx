package models;

public class Candidature {
    private int id;
    private int candidatId; // Reference to the candidat table
    private int offreEmploiId; // Reference to the offre_emploi table
    private String cv; // Path to the CV file
    private String reference;
    private String status;

    // Constructeurs
    public Candidature() {
        this.status = "En cours";
    }

    public Candidature(int candidatId, int offreEmploiId, String cv) {
        this.candidatId = candidatId;
        this.offreEmploiId = offreEmploiId;
        this.cv = cv;
    }

    public Candidature(int id, int candidatId, int offreEmploiId, String cv) {
        this.id = id;
        this.candidatId = candidatId;
        this.offreEmploiId = offreEmploiId;
        this.cv = cv;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCandidatId() {
        return candidatId;
    }

    public void setCandidatId(int candidatId) {
        this.candidatId = candidatId;
    }

    public int getOffreEmploiId() {
        return offreEmploiId;
    }

    public void setOffreEmploiId(int offreEmploiId) {
        this.offreEmploiId = offreEmploiId;
    }

    public String getCv() {
        return cv;
    }

    public void setCv(String cv) {
        this.cv = cv;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Candidature{" +
                "id=" + id +
                ", candidatId=" + candidatId +
                ", offreEmploiId=" + offreEmploiId +
                ", cv='" + cv + '\'' +
                '}';
    }
}