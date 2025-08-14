package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Utilisateur;
import services.UtilisateurCrud;

public class EditProfilePageController {

    @FXML
    private TextField cinField;

    @FXML
    private TextField telField;

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField roleField;

    private Utilisateur user;
    private UtilisateurCrud utilisateurCrud = new UtilisateurCrud();
    private String role;
    private ParentController parentController;

    public void setUser(Utilisateur user) {
        this.user = user;
        cinField.setText(String.valueOf(user.getCin()));
        telField.setText(user.gettel());
        nomField.setText(user.getNom());
        prenomField.setText(user.getPrenom());
        emailField.setText(user.getEmail());
        passwordField.setText(user.getpassword());
        roleField.setText(user.getroles());
    }

    public void setParentController(ParentController parentController) {
        this.parentController = parentController;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @FXML
    private void handleSave() {
        user.setCin(Integer.parseInt(cinField.getText()));
        user.settel(telField.getText());
        user.setNom(nomField.getText());
        user.setPrenom(prenomField.getText());
        user.setEmail(emailField.getText());
        user.setpassword(passwordField.getText());
        user.setroles(roleField.getText());

        utilisateurCrud.modifierEntite(user);

        // Reload the user list in the parent controller
        parentController.loadMembers();

        // Close the dialog
        Stage stage = (Stage) cinField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleReturn() {
        Stage stage = (Stage) cinField.getScene().getWindow();
        stage.close();
    }
}