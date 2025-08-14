package com.melocode.semin.models;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Seminaire {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty nom = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> dateDebut = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> dateFin = new SimpleObjectProperty<>();
    private final StringProperty lieu = new SimpleStringProperty();
    private final StringProperty formateur = new SimpleStringProperty();
    private final DoubleProperty cout = new SimpleDoubleProperty();
    private final StringProperty type = new SimpleStringProperty();

    public Seminaire(int id, String nom, String description, LocalDate dateDebut, LocalDate dateFin, String lieu, String formateur, double cout, String type) {
        this.id.set(id);
        this.nom.set(nom);
        this.description.set(description);
        this.dateDebut.set(dateDebut);
        this.dateFin.set(dateFin);
        this.lieu.set(lieu);
        this.formateur.set(formateur);
        this.cout.set(cout);
        this.type.set(type);
    }

    // Getters pour les propriétés
    public IntegerProperty idProperty() { return id; }
    public StringProperty nomProperty() { return nom; }
    public StringProperty descriptionProperty() { return description; }
    public ObjectProperty<LocalDate> dateDebutProperty() { return dateDebut; }
    public ObjectProperty<LocalDate> dateFinProperty() { return dateFin; }
    public StringProperty lieuProperty() { return lieu; }
    public StringProperty formateurProperty() { return formateur; }
    public DoubleProperty coutProperty() { return cout; }
    public StringProperty typeProperty() { return type; }

    // Getters classiques
    public int getId() { return id.get(); }
    public String getNom() { return nom.get(); }
    public String getDescription() { return description.get(); }
    public LocalDate getDateDebut() { return dateDebut.get(); }
    public LocalDate getDateFin() { return dateFin.get(); }
    public String getLieu() { return lieu.get(); }
    public String getFormateur() { return formateur.get(); }
    public double getCout() { return cout.get(); }
    public String getType() { return type.get(); }

    // Setters
    public void setId(int id) { this.id.set(id); }
    public void setNom(String nom) { this.nom.set(nom); }
    public void setDescription(String description) { this.description.set(description); }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut.set(dateDebut); }
    public void setDateFin(LocalDate dateFin) { this.dateFin.set(dateFin); }
    public void setLieu(String lieu) { this.lieu.set(lieu); }
    public void setFormateur(String formateur) { this.formateur.set(formateur); }
    public void setCout(double cout) { this.cout.set(cout); }
    public void setType(String type) { this.type.set(type); }
}
