package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Candidat;
import services.CandidatService;
import utils.SessionManager;

public class LoginController {

    @FXML
    private TextField temail;

    @FXML
    private TextField ttelephone;

    @FXML
    private Button btnLogin;

    private CandidatService candidatService = new CandidatService();

    @FXML
    void login() {
        try {
            String email = temail.getText();
            String phone = ttelephone.getText();

            Candidat candidat = candidatService.getByEmailAndPhone(email, phone);
            if (candidat != null) {
                // Stocker le candidat dans SessionManager
                SessionManager.setCandidat(candidat);

                // Charger la page d'affichage des offres d'emploi pour les candidats
                Parent root = FXMLLoader.load(getClass().getResource("/FXML/affichageOffreCandidat.fxml"));
                Stage stage = (Stage) btnLogin.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Liste des offres d'emploi");
                stage.show();
            } else {
                showAlert("Erreur de connexion", "Compte introuvable. Veuillez vérifier vos informations.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur s'est produite lors de la connexion.");
        }
    }

    @FXML
    void showLoginAlert() {
        showAlert("Connexion requise", "Vous devez être connecté pour accéder à cette page.");
    }

    @FXML
    void retour() {
        try {
            // Charger la page d'inscription
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/formulaireCandidat.fxml"));
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Inscription Candidat");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}