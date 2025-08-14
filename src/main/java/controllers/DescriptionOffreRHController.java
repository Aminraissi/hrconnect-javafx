package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.OffreEmploi;
import utils.ShowMenu;

import java.net.URL;
import java.util.ResourceBundle;

public class DescriptionOffreRHController implements Initializable, ShowMenu {

    @FXML
    private AnchorPane menu;

    @FXML
    private Label titreLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label lieuLabel;

    @FXML
    private Button btnRetour;

    private OffreEmploi offre;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeMenu(menu);

        // Style des labels
        titreLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        descriptionLabel.setStyle("-fx-font-size: 16px; -fx-wrap-text: true; -fx-padding: 20 0;");
        lieuLabel.setStyle("-fx-font-size: 16px; -fx-padding: 10 0;");

        // Style du bouton
        String buttonStyle = "-fx-background-color: #1e3799; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;";
        btnRetour.setStyle(buttonStyle);
        btnRetour.setPrefWidth(200);
    }

    public void setOffre(OffreEmploi offre) {
        this.offre = offre;
        titreLabel.setText(offre.getTitle());
        descriptionLabel.setText(offre.getDescription());
        lieuLabel.setText("Lieu : " + offre.getLocation());
    }

    @FXML
    void retourListe() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/affichageOffre.fxml"));
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des offres");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void afficherOffresRecrutement() {
        try {
            // Charger la page d'affichage des offres
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/affichageOffre.fxml"));
            Stage stage = (Stage) titreLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Offres de recrutement");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void deconnexion() {
        // Ne rien faire pour désactiver le bouton
    }
}