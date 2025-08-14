package com.melocode.hrreclam;

import javafx.beans.property.*;

public class Reclamation {
    private final IntegerProperty id;
    private final StringProperty employeeName;
    private final StringProperty type;
    private final StringProperty description;
    private final StringProperty dateOfSubmission;
    private final StringProperty status;
    private final StringProperty priority;

    // Default constructor (needed for TableView and database use)
    public Reclamation() {
        this.id = new SimpleIntegerProperty();
        this.employeeName = new SimpleStringProperty();
        this.type = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
        this.dateOfSubmission = new SimpleStringProperty();
        this.status = new SimpleStringProperty();
        this.priority = new SimpleStringProperty();
    }

    // Constructor with parameters
    public Reclamation(int id, String employeeName, String type, String description, String dateOfSubmission, String status, String priority) {
        this.id = new SimpleIntegerProperty(id);
        this.employeeName = new SimpleStringProperty(employeeName);
        this.type = new SimpleStringProperty(type);
        this.description = new SimpleStringProperty(description);
        this.dateOfSubmission = new SimpleStringProperty(dateOfSubmission);
        this.status = new SimpleStringProperty(status);
        this.priority = new SimpleStringProperty(priority);
    }

    // Getters for JavaFX Properties
    public IntegerProperty idProperty() { return id; }
    public StringProperty employeeNameProperty() { return employeeName; }
    public StringProperty typeProperty() { return type; }
    public StringProperty descriptionProperty() { return description; }
    public StringProperty dateOfSubmissionProperty() { return dateOfSubmission; }
    public StringProperty statusProperty() { return status; }
    public StringProperty priorityProperty() { return priority; }

    // Standard Getters
    public int getId() { return id.get(); }
    public String getEmployeeName() { return employeeName.get(); }
    public String getType() { return type.get(); }
    public String getDescription() { return description.get(); }
    public String getDateOfSubmission() { return dateOfSubmission.get(); }
    public String getStatus() { return status.get(); }
    public String getPriority() { return priority.get(); }

    // Standard Setters
    public void setId(int id) { this.id.set(id); }
    public void setEmployeeName(String employeeName) { this.employeeName.set(employeeName); }
    public void setType(String type) { this.type.set(type); }
    public void setDescription(String description) { this.description.set(description); }
    public void setDateOfSubmission(String dateOfSubmission) { this.dateOfSubmission.set(dateOfSubmission); }
    public void setStatus(String status) { this.status.set(status); }
    public void setPriority(String priority) { this.priority.set(priority); }

    // toString() method for debugging
    @Override
    public String toString() {
        return "Reclamation{" +
                "id=" + getId() +
                ", employeeName='" + getEmployeeName() + '\'' +
                ", type='" + getType() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", dateOfSubmission='" + getDateOfSubmission() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", priority='" + getPriority() + '\'' +
                '}';
    }
}
