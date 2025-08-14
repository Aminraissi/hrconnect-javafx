package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RoleController {

    private Stage stage; // Stage actuel

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    void espaceRH() {
        redirectToPage(true);
    }

    @FXML
    void espaceCandidat() {
        redirectToPage(false);
    }

    private void redirectToPage(boolean isRH) {
        try {
            Parent root;
            if (isRH) {
                // Rediriger vers la page d'affichage des offres pour les RH
                root = FXMLLoader.load(getClass().getResource("/FXML/affichageOffre.fxml"));
            } else {
                // Rediriger vers le formulaire de candidat pour les candidats
                root = FXMLLoader.load(getClass().getResource("/FXML/formulaireCandidat.fxml"));
            }
            stage.setScene(new Scene(root));
            stage.setTitle(isRH ? "Affichage des offres d'emploi" : "Formulaire du candidat");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}