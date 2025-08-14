package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.Candidature;
import services.CandidatureService;
import utils.SessionManager;
import utils.ShowMenu;
import java.util.List;

public class SuiviCandidaturesController implements ShowMenu {

    @FXML
    private AnchorPane menu;

    @FXML
    private VBox candidaturesContainer;

    @FXML
    private Label hrConnectTitle;

    @FXML
    private TextField referenceField;

    private CandidatureService candidatureService = new CandidatureService();

    @FXML
    public void initialize() {
        // Initialiser le menu
        initializeMenu(menu);
    }

    @FXML
    void rafraichirCandidatures() {
        try {
            candidaturesContainer.getChildren().clear();
            int candidatId = SessionManager.getCandidat().getId();
            List<Candidature> candidatures = candidatureService.getCandidaturesByCandidat(candidatId);

            for (Candidature candidature : candidatures) {
                HBox candidatureBox = createCandidatureBox(candidature);
                candidaturesContainer.getChildren().add(candidatureBox);
            }

            if (candidatures.isEmpty()) {
                Label emptyLabel = new Label("Vous n'avez pas encore de candidatures.");
                emptyLabel.setStyle("-fx-padding: 20;");
                candidaturesContainer.getChildren().add(emptyLabel);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors du chargement des candidatures.");
        }
    }

    @FXML
    void rechercherCandidature() {
        try {
            String reference = referenceField.getText().trim();
            if (reference.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez entrer une référence.");
                return;
            }

            candidaturesContainer.getChildren().clear();
            String status = candidatureService.getCandidatureStatus(reference);

            if (status != null) {
                Label statusLabel = new Label();
                statusLabel.setStyle("-fx-padding: 20;");

                switch (status.toLowerCase()) {
                    case "en cours":
                        statusLabel.setText("Votre candidature est en cours de traitement");
                        break;
                    case "acceptée":
                        statusLabel.setText("Félicitations ! Votre candidature a été acceptée");
                        break;
                    case "refusée":
                        statusLabel.setText("Désolé, votre candidature n'a pas été retenue");
                        break;
                    default:
                        statusLabel.setText("Statut: " + status);
                }

                candidaturesContainer.getChildren().add(statusLabel);
            } else {
                Label notFoundLabel = new Label("Aucune candidature trouvée avec cette référence.");
                notFoundLabel.setStyle("-fx-padding: 20;");
                candidaturesContainer.getChildren().add(notFoundLabel);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de la recherche.");
        }
    }

    private HBox createCandidatureBox(Candidature candidature) {
        HBox box = new HBox();
        box.setSpacing(10);
        box.setStyle("-fx-padding: 10; -fx-background-color: white; -fx-border-color: #ccc; -fx-border-radius: 5;");

        VBox infoBox = new VBox();
        infoBox.setSpacing(5);

        Label statusLabel = new Label("Statut: " + candidature.getStatus());
        infoBox.getChildren().add(statusLabel);
        box.getChildren().add(infoBox);

        return box;
    }

    @FXML
    void retourOffres() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/affichageOffreCandidat.fxml"));
            Stage stage = (Stage) candidaturesContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Offres d'emploi");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors du retour à la page des offres.");
        }
    }

    @FXML
    void deconnexion() {
        try {
            SessionManager.setCandidat(null);
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/role.fxml"));
            Stage stage = (Stage) candidaturesContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Sélection du rôle");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de la déconnexion.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 