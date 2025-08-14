package controllers;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.OffreEmploi;
import utils.SessionManager;
import models.Candidat;
import utils.ShowMenu;

import java.net.URL;
import java.util.ResourceBundle;

public class DescriptionOffreController implements Initializable, ShowMenu {

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

    @FXML
    private Button btnPostuler;

    @FXML
    private Label hrConnectTitle;

    private OffreEmploi offre;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeMenu(menu);

        // Style des labels
        titreLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        descriptionLabel.setStyle("-fx-font-size: 16px; -fx-wrap-text: true; -fx-padding: 20 0;");
        lieuLabel.setStyle("-fx-font-size: 16px; -fx-padding: 10 0;");

        // Style des boutons
        String buttonStyle = "-fx-background-color: #1e3799; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;";
        btnPostuler.setStyle(buttonStyle);
        btnRetour.setStyle(buttonStyle);

        // Définir une largeur préférée pour les boutons
        btnPostuler.setPrefWidth(200);
        btnRetour.setPrefWidth(200);

        // Add hover animation to buttons
        addHoverAnimation(btnRetour);
        addHoverAnimation(btnPostuler);

        // Add title animation
        if (hrConnectTitle != null) {
            addTitleAnimation();
        }
    }

    public void setOffre(OffreEmploi offre) {
        this.offre = offre;
        titreLabel.setText(offre.getTitle());
        descriptionLabel.setText(offre.getDescription());
        lieuLabel.setText("Lieu : " + offre.getLocation());
    }

    @FXML
    void postuler() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/formulaireCandidature.fxml"));
            Parent root = loader.load();

            FormulaireCandidatureController controller = loader.getController();
            controller.setOffreEmploiId(offre.getId());

            Candidat candidat = SessionManager.getCandidat();
            if (candidat != null) {
                controller.setCandidat(candidat);
            } else {
                System.out.println("Candidat is null");
            }

            Stage stage = (Stage) titreLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Postuler à l'offre");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void retourListe() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/affichageOffreCandidat.fxml"));
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Offres d'emploi");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void afficherOffres() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/affichageOffreCandidat.fxml"));
            Stage stage = (Stage) titreLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Offres d'emploi");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void deconnexion() {
        // Ne rien faire pour désactiver le bouton
    }

    private void addHoverAnimation(Button button) {
        button.setOnMouseEntered(event -> {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), button);
            scaleTransition.setToX(1.1);
            scaleTransition.setToY(1.1);
            scaleTransition.play();
        });

        button.setOnMouseExited(event -> {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), button);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
        });
    }

    private void addTitleAnimation() {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(2000), hrConnectTitle);
        scaleTransition.setFromX(1.0);
        scaleTransition.setToX(1.1);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToY(1.1);
        scaleTransition.setCycleCount(ScaleTransition.INDEFINITE);
        scaleTransition.setAutoReverse(true);
        scaleTransition.play();
    }
}