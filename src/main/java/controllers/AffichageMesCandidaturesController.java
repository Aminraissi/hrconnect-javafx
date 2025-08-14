package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Candidature;
import models.OffreEmploi;
import services.CandidatureService;
import services.OffreEmploiService;
import utils.SessionManager;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class AffichageMesCandidaturesController {

    @FXML
    private VBox tableContent;

    private CandidatureService candidatureService = new CandidatureService();
    private OffreEmploiService offreEmploiService = new OffreEmploiService();

    @FXML
    public void initialize() {
        loadCandidatures();
    }

    private void loadCandidatures() {
        try {
            int candidatId = SessionManager.getCandidat().getId();
            List<Candidature> candidatures = candidatureService.getCandidaturesByCandidatId(candidatId);

            tableContent.getChildren().clear();

            // Ajouter l'en-tête de la table
            GridPane header = new GridPane();
            header.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 10;");
            header.getColumnConstraints().addAll(
                    createColumnConstraints(20),
                    createColumnConstraints(20),
                    createColumnConstraints(20),
                    createColumnConstraints(20),
                    createColumnConstraints(20)
            );
            header.add(createHeaderLabel("Titre de l'offre"), 0, 0);
            header.add(createHeaderLabel("Email"), 1, 0);
            header.add(createHeaderLabel("Téléphone"), 2, 0);
            header.add(createHeaderLabel("CV"), 3, 0);
            header.add(createHeaderLabel("Action"), 4, 0);
            tableContent.getChildren().add(header);

            // Ajouter les lignes de la table
            for (Candidature candidature : candidatures) {
                OffreEmploi offre = offreEmploiService.getById(candidature.getOffreEmploiId());
                if (offre != null) {
                    GridPane row = new GridPane();
                    row.setStyle("-fx-padding: 10; -fx-background-color: #ffffff;");
                    row.getColumnConstraints().addAll(
                            createColumnConstraints(20),
                            createColumnConstraints(20),
                            createColumnConstraints(20),
                            createColumnConstraints(20),
                            createColumnConstraints(20)
                    );

                    Label titreOffreLabel = createDataLabel(offre.getTitle());
                    Label emailLabel = createDataLabel(SessionManager.getCandidat().getEmail());
                    Label telephoneLabel = createDataLabel(SessionManager.getCandidat().getPhone());

                    Hyperlink cvLink = new Hyperlink("CV");
                    cvLink.setOnAction(event -> {
                        try {
                            File cvFile = new File(candidature.getCv());
                            if (cvFile.exists()) {
                                Desktop.getDesktop().open(cvFile);
                            } else {
                                System.out.println("Le fichier CV n'existe pas.");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                    HBox actionBox = new HBox(10);
                    Button redButton = new Button("✘");
                    redButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                    redButton.setOnAction(event -> handleDeleteCandidature(candidature));

                    actionBox.getChildren().addAll(redButton);

                    row.add(titreOffreLabel, 0, 0);
                    row.add(emailLabel, 1, 0);
                    row.add(telephoneLabel, 2, 0);
                    row.add(cvLink, 3, 0);
                    row.add(actionBox, 4, 0);
                    tableContent.getChildren().add(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur s'est produite lors du chargement des candidatures.");
        }
    }

    private ColumnConstraints createColumnConstraints(double percentWidth) {
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(percentWidth);
        return columnConstraints;
    }

    private Label createHeaderLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold;");
        GridPane.setHgrow(label, Priority.ALWAYS);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setPrefWidth(Region.USE_COMPUTED_SIZE);
        return label;
    }

    private Label createDataLabel(String text) {
        Label label = new Label(text);
        GridPane.setHgrow(label, Priority.ALWAYS);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setPrefWidth(Region.USE_COMPUTED_SIZE);
        return label;
    }

    private void handleDeleteCandidature(Candidature candidature) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette candidature ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                candidatureService.delete(candidature);
                loadCandidatures();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void retour() {
        try {
            // Charger la page d'affichage des offres d'emploi pour les candidats
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/affichageOffreCandidat.fxml"));
            Stage stage = (Stage) tableContent.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Offres d'emploi");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}