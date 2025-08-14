package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Utilisateur;
import services.UtilisateurCrud;

import java.io.IOException;

public class EditProfileDialogController {

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
    private ProfilAdminController parentController;

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

    public void setParentController(ProfilAdminController parentController) {
        this.parentController = parentController;
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
        parentController.loadUsers();

        // Return to the main profile view
        handleReturn();
    }

    @FXML
    private void handleReturn() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfilAdmin.fxml"));
            Parent root = loader.load();

            ProfilAdminController controller = loader.getController();
            controller.loadUsers();

            Stage stage = (Stage) cinField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}