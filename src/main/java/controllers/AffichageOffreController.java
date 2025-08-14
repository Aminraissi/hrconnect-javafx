package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.OffreEmploi;
import services.OffreEmploiService;
import utils.ShowMenu;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AffichageOffreController implements Initializable, ShowMenu {

    @FXML
    private AnchorPane menu;

    @FXML
    private VBox offresContainer;

    private OffreEmploiService offreService = new OffreEmploiService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeMenu(menu);
        refreshOffres();
    }

    @FXML
    public void initialize() {
        refreshOffres();
    }

    private void refreshOffres() {
        try {
            offresContainer.getChildren().clear();
            List<OffreEmploi> offres = offreService.getAll();

            for (OffreEmploi offre : offres) {
                HBox offreBox = createOffreBox(offre);
                if (offreBox != null) {
                    offresContainer.getChildren().add(offreBox);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HBox createOffreBox(OffreEmploi offre) {
        try {
            // Création du HBox principal avec style
            HBox offreBox = new HBox();
            offreBox.setSpacing(20);
            offreBox.setPadding(new Insets(20));
            offreBox.setMaxWidth(Double.MAX_VALUE);
            offreBox.setMinHeight(120);
            offreBox.setPrefHeight(150);
            offreBox.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 10);");

            // Création du VBox pour les informations
            VBox infoBox = new VBox();
            infoBox.setSpacing(15);
            infoBox.setPadding(new Insets(10, 0, 10, 0));
            HBox.setHgrow(infoBox, Priority.ALWAYS);

            // Titre
            Label titreLabel = new Label(offre.getTitle());
            titreLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

            // Lieu
            Label lieuLabel = new Label("Lieu : " + offre.getLocation());
            lieuLabel.setStyle("-fx-font-size: 14px;");

            // Ajout des labels au VBox
            infoBox.getChildren().addAll(titreLabel, lieuLabel);

            // Création du HBox pour les boutons
            HBox buttonsBox = new HBox();
            buttonsBox.setSpacing(15);
            buttonsBox.setAlignment(Pos.CENTER_RIGHT);

            // Création des boutons avec taille augmentée
            Button modifierBtn = new Button("Modifier");
            Button supprimerBtn = new Button("Supprimer");
            Button descriptionBtn = new Button("Description");
            Button candidaturesBtn = new Button("Afficher les candidatures");

            // Style des boutons
            String buttonStyle = "-fx-background-color: #1e3799; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 15;";
            modifierBtn.setStyle(buttonStyle);
            supprimerBtn.setStyle(buttonStyle);
            descriptionBtn.setStyle(buttonStyle);
            candidaturesBtn.setStyle(buttonStyle);

            // Actions des boutons
            modifierBtn.setOnAction(event -> modifierOffre(offre));
            supprimerBtn.setOnAction(event -> supprimerOffre(offre));
            descriptionBtn.setOnAction(event -> afficherDescription(offre));
            candidaturesBtn.setOnAction(event -> afficherCandidatures(offre));

            // Ajout des boutons au HBox
            buttonsBox.getChildren().addAll(modifierBtn, supprimerBtn, descriptionBtn, candidaturesBtn);

            // Assemblage final
            offreBox.getChildren().addAll(infoBox, buttonsBox);

            return offreBox;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void modifierOffre(OffreEmploi offre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/formulaireOffre.fxml"));
            Parent root = loader.load();
            FormulaireOffreController controller = loader.getController();
            controller.setOffreToUpdate(offre);
            Stage stage = (Stage) offresContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier une offre d'emploi");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void supprimerOffre(OffreEmploi offre) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette offre d'emploi ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                offreService.delete(offre);
                refreshOffres();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void afficherDescription(OffreEmploi offre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/descriptionOffreRH.fxml"));
            Parent root = loader.load();
            DescriptionOffreRHController controller = loader.getController();
            controller.setOffre(offre);
            Stage stage = (Stage) offresContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Description de l'offre");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void afficherCandidatures(OffreEmploi offre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/affichageCandidature.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) offresContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des candidatures");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void ajouterOffre() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/formulaireOffre.fxml"));
            Stage stage = (Stage) offresContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter une offre d'emploi");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void afficherOffres(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText("Vous êtes déjà sur la page des offres d'emploi.");
        alert.showAndWait();
    }

    @FXML
    void afficherOffresRecrutement() {
        try {
            // Vérifier si nous sommes déjà sur la page des offres d'emploi
            Stage stage = (Stage) offresContainer.getScene().getWindow();
            if (stage.getTitle().equals("Liste des offres d'emploi")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Vous êtes déjà sur la page des offres d'emploi.");
                alert.showAndWait();
            } else {
                // Charger la page d'affichage des offres d'emploi
                Parent root = FXMLLoader.load(getClass().getResource("/FXML/affichageOffre.fxml"));
                stage.setScene(new Scene(root));
                stage.setTitle("Liste des offres d'emploi");
                stage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void exitToRole() {
        try {
            // Charger la page de sélection du rôle
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/role.fxml"));
            Stage stage = (Stage) offresContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Sélection du rôle");
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