package models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Employe {
    int id;
    String nom;
    String prenom;
    String email;
    private final IntegerProperty soldeConges = new SimpleIntegerProperty();


     public Employe(String nom, String prenom, int soldeConges) {
        setNom(nom);
        setPrenom(prenom);
        setSoldeConges(soldeConges);
    }


    public  Employe(){}

    public Employe(int id, String nom, String prenom,String email) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getSoldeConges() { return soldeConges.get(); }
    public void setSoldeConges(int value) { soldeConges.set(value); }
    public IntegerProperty soldeCongesProperty() { return soldeConges; }
}
