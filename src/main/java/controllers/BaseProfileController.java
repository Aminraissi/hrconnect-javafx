package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.Utilisateur;
import utils.SessionManager;

import java.io.IOException;

/**
 * Base controller for all profile pages with common functionality.
 */
public abstract class BaseProfileController {

    @FXML
    protected Label userNameLabel;

    @FXML
    protected Label userRoleLabel;

    @FXML
    protected Label userEmailLabel;

    /**
     * Initialize the controller with user data from session.
     */
    @FXML
    public void initialize() {
        // Get the current user from the session
        Utilisateur currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Display user information if fields exist
            if (userNameLabel != null) {
                userNameLabel.setText(currentUser.getNom() + " " + currentUser.getPrenom());
            }

            if (userRoleLabel != null) {
                userRoleLabel.setText("Rôle: " + currentUser.getroles());
            }

            if (userEmailLabel != null) {
                userEmailLabel.setText(currentUser.getEmail());
            }

            // Call the child-specific initialization
            initializeProfileSpecifics(currentUser);
        } else {
            // If no user in session, redirect to login
            showAlert(Alert.AlertType.WARNING, "Session invalide",
                    "Aucune session utilisateur active. Redirection vers la page de connexion.");
            redirectToLogin();
        }
    }

    /**
     * Method to be implemented by child controllers for profile-specific initialization.
     */
    protected abstract void initializeProfileSpecifics(Utilisateur currentUser);

    /**
     * Handle logout action.
     */
    @FXML
    public void handleLogout(ActionEvent event) {
        // Clear the session
        SessionManager.getInstance().logout();

        // Navigate back to login page
        redirectToLogin();
    }

    /**
     * Handle edit profile action.
     */
    @FXML
    public void handleEditProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditProfilePage.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Modifier votre profil");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de navigation",
                    "Impossible de charger la page de modification du profil: " + e.getMessage());
        }
    }

    /**
     * Redirect to login page.
     */
    protected void redirectToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/authentification.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Connexion");
            stage.setScene(new Scene(root));
            stage.show();

            // Close current window if it exists
            if (userNameLabel != null) {
                Stage currentStage = (Stage) userNameLabel.getScene().getWindow();
                currentStage.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de navigation",
                    "Impossible de charger la page de connexion: " + e.getMessage());
        }
    }

    /**
     * Show alert dialog.
     */
    protected void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}