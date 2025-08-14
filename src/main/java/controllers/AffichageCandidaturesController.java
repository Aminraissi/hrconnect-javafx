package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Candidat;
import models.Candidature;
import models.OffreEmploi;
import services.CandidatService;
import services.CandidatureService;
import services.OffreEmploiService;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Base64;
import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.Initializable;
import utils.ShowMenu;

import java.util.ResourceBundle;
import okhttp3.*;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.application.Platform;
import javafx.concurrent.Task;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.concurrent.TimeUnit;
import java.net.SocketTimeoutException;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ButtonBar;

public class AffichageCandidaturesController implements Initializable, ShowMenu {

    @FXML
    private VBox tableContent;

    @FXML
    private ComboBox<String> offreComboBox;

    @FXML
    private VBox candidaturesContainer;

    @FXML
    private AnchorPane menu;

    private CandidatureService candidatureService = new CandidatureService();
    private CandidatService candidatService = new CandidatService();
    private OffreEmploiService offreEmploiService = new OffreEmploiService();

    // Configuration Affinda
    private static final String AFFINDA_API_KEY = System.getenv("AFFINDA_API_KEY");
    private static final String AFFINDA_API_URL = "https://api.affinda.com/v3/documents";
    private static final String AFFINDA_WORKSPACE_ID = System.getenv("AFFINDA_WORKSPACE_ID");
    private static final String AFFINDA_COLLECTION_ID = System.getenv("AFFINDA_COLLECTION_ID");

    // Configuration du client HTTP avec des timeouts plus longs
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();
    private static final Gson gson = new Gson();

    // Mise à jour des constantes Mailjet
    private static final String MAILJET_API_KEY_PUBLIC = System.getenv("MAILJET_API_KEY_PUBLIC");  // Nouvelle clé API
    private static final String MAILJET_API_KEY_PRIVATE = System.getenv("MAILJET_API_KEY_PRIVATE"); // Nouvelle clé privée
    private static final String MAILJET_TEMPLATE_ID_ACCEPTED = System.getenv("MAILJET_TEMPLATE_ID_ACCEPTED");  // Template d'acceptation
    private static final String MAILJET_TEMPLATE_ID_REJECTED = System.getenv("MAILJET_TEMPLATE_ID_REJECTED");  // Template de rejet

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialisation du menu avec le même style que DescriptionOffreRH
        if (menu != null) {
            menu.setPrefHeight(200.0);
            menu.setPrefWidth(200.0);
            menu.getStyleClass().add("menu");
            initializeMenu(menu);
        }

        if (tableContent == null) {
            System.out.println("Error: tableContent not injected!");
            return;
        }

        // Style des éléments
        if (offreComboBox != null) {
            offreComboBox.setStyle("-fx-font-size: 14px; -fx-padding: 5 10;");
        }

        if (candidaturesContainer != null) {
            candidaturesContainer.setSpacing(10);
            candidaturesContainer.setPadding(new Insets(20));
        }

        loadOffres();
        refreshCandidatures();
    }

    private void loadOffres() {
        try {
            List<OffreEmploi> offres = offreEmploiService.getAll();
            for (OffreEmploi offre : offres) {
                offreComboBox.getItems().add(offre.getTitle());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOffreSelection() {
        String selectedTitle = offreComboBox.getSelectionModel().getSelectedItem();
        if (selectedTitle != null) {
            try {
                OffreEmploi selectedOffre = offreEmploiService.getByTitle(selectedTitle);
                if (selectedOffre != null) {
                    refreshCandidatures(selectedOffre.getId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void refreshCandidatures() {
        try {
            tableContent.getChildren().clear();
            List<Candidature> candidatures = candidatureService.getAllCandidatures();

            // En-tête
            GridPane header = new GridPane();
            header.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10;");
            header.getColumnConstraints().addAll(
                    createColumnConstraints(25),  // Titre
                    createColumnConstraints(25),  // Email
                    createColumnConstraints(20),  // Téléphone
                    createColumnConstraints(15),  // CV
                    createColumnConstraints(15)   // Actions
            );

            // Ajouter du padding et des marges
            header.setHgap(20); // Espacement horizontal entre les colonnes
            header.setVgap(15); // Espacement vertical
            header.setPadding(new Insets(10, 20, 10, 20)); // Padding autour du header

            header.add(createHeaderLabel("Titre"), 0, 0);
            header.add(createHeaderLabel("Email"), 1, 0);
            header.add(createHeaderLabel("Téléphone"), 2, 0);
            header.add(createHeaderLabel("CV"), 3, 0);
            header.add(createHeaderLabel("Actions"), 4, 0);

            tableContent.getChildren().add(header);

            for (Candidature candidature : candidatures) {
                Candidat candidat = candidatService.getById(candidature.getCandidatId());
                if (candidat != null) {
                    GridPane row = new GridPane();
                    row.setStyle("-fx-padding: 10; -fx-background-color: #ffffff; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");
                    row.getColumnConstraints().addAll(
                            createColumnConstraints(25),
                            createColumnConstraints(25),
                            createColumnConstraints(20),
                            createColumnConstraints(15),
                            createColumnConstraints(15)
                    );

                    // Ajouter du padding et des marges
                    row.setHgap(20); // Espacement horizontal entre les colonnes
                    row.setVgap(15); // Espacement vertical
                    row.setPadding(new Insets(10, 20, 10, 20)); // Padding autour de chaque ligne

                    // Ajouter les informations dans la ligne
                    row.add(createDataLabel(offreEmploiService.getById(candidature.getOffreEmploiId()).getTitle()), 0, 0);
                    row.add(createDataLabel(candidat.getEmail()), 1, 0);
                    row.add(createDataLabel(candidat.getPhone()), 2, 0);

                    // Créer le lien CV avec le chemin stocké
                    Hyperlink cvLink = new Hyperlink("Voir CV");
                    cvLink.setUserData(candidature.getCv()); // Stocker le chemin du CV
                    cvLink.setOnAction(this::handleViewCV);
                    row.add(cvLink, 3, 0);

                    // Boutons d'action
                    HBox actionBox = new HBox(10);
                    Button acceptButton = new Button("✔");
                    acceptButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
                    acceptButton.setOnAction(e -> handleAcceptCandidature(candidature));

                    Button rejectButton = new Button("✘");
                    rejectButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                    rejectButton.setOnAction(e -> handleRejectCandidature(candidature));

                    actionBox.getChildren().addAll(acceptButton, rejectButton);
                    row.add(actionBox, 4, 0);

                    tableContent.getChildren().add(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors du chargement des candidatures.");
        }
    }

    @FXML
    private void handleViewCV(ActionEvent event) {
        // Récupérer le CV de la candidature sélectionnée
        Hyperlink link = (Hyperlink) event.getSource();
        GridPane row = (GridPane) link.getParent();
        String cvPath = link.getUserData().toString(); // Stocker le chemin du CV dans userData

        try {
            File cvFile = new File(cvPath);
            if (cvFile.exists()) {
                // Ouvrir le CV
                Desktop.getDesktop().open(cvFile);

                // Analyser le CV avec l'API
                parseResume(cvPath);

                // Afficher un message de chargement
                showAlert(Alert.AlertType.INFORMATION, "Information",
                        "L'analyse du CV est en cours. Veuillez patienter...");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Le fichier CV n'existe pas.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le fichier CV.");
        }
    }

    private Label createHeaderLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold;");
        return label;
    }

    private Label createDataLabel(String text) {
        return new Label(text != null ? text : "");
    }

    private ColumnConstraints createColumnConstraints(double percentage) {
        ColumnConstraints column = new ColumnConstraints();
        column.setPercentWidth(percentage);
        return column;
    }

    private void handleRejectCandidature(Candidature candidature) {
        System.out.println("=== DÉBUT REJET CANDIDATURE ===");
        try {
            // Mettre à jour le statut avant l'envoi de l'email
            candidature.setStatus("rejected");
            candidatureService.update(candidature);
            System.out.println("Statut de la candidature mis à jour: rejected");

            // Envoyer l'email de rejet
            System.out.println("Tentative d'envoi d'email de rejet...");
            sendEmail(candidature, "rejected");
            System.out.println("Email de rejet envoyé avec succès");

            // Supprimer la candidature
            System.out.println("Suppression de la candidature...");
            candidatureService.delete(candidature);
            System.out.println("Candidature supprimée avec succès");

            refreshCandidatures();

            Platform.runLater(() -> {
                showAlert(Alert.AlertType.INFORMATION, "Succès",
                        "La candidature a été refusée et supprimée avec succès.");
            });
        } catch (Exception e) {
            System.err.println("ERREUR lors du rejet de la candidature:");
            e.printStackTrace();
            Platform.runLater(() -> {
                showAlert(Alert.AlertType.ERROR, "Erreur",
                        "Une erreur est survenue lors du refus de la candidature: " + e.getMessage());
            });
        }
        System.out.println("=== FIN REJET CANDIDATURE ===");
    }

    private void handleAcceptCandidature(Candidature candidature) {
        System.out.println("=== DÉBUT ACCEPTATION CANDIDATURE ===");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("En acceptant cette candidature, l'offre d'emploi sera clôturée et toutes les autres candidatures seront supprimées. Voulez-vous continuer ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Vérifier que la candidature n'est pas nulle
                if (candidature == null) {
                    throw new IllegalArgumentException("La candidature ne peut pas être nulle");
                }

                // Vérifier et mettre à jour le statut
                String newStatus = "accepted";
                candidature.setStatus(newStatus);

                // Log pour le débogage
                System.out.println("Mise à jour de la candidature - ID: " + candidature.getId() + ", Status: " + candidature.getStatus());

                // Mettre à jour la candidature
                boolean updateSuccess = candidatureService.update(candidature);
                if (!updateSuccess) {
                    throw new Exception("Échec de la mise à jour de la candidature");
                }

                // Envoyer l'email d'acceptation
                System.out.println("Tentative d'envoi d'email d'acceptation...");
                sendEmail(candidature, "accepted");
                System.out.println("Email d'acceptation envoyé avec succès");

                // Récupérer toutes les candidatures pour cette offre
                List<Candidature> candidatures = candidatureService.getCandidaturesByOffreEmploiId(candidature.getOffreEmploiId());
                System.out.println("Nombre de candidatures trouvées: " + candidatures.size());

                // Traiter les autres candidatures
                for (Candidature otherCandidature : candidatures) {
                    if (otherCandidature.getId() != candidature.getId()) {
                        // Mettre à jour le statut avant l'envoi de l'email
                        otherCandidature.setStatus("rejected");
                        candidatureService.update(otherCandidature);
                        System.out.println("Statut de la candidature " + otherCandidature.getId() + " mis à jour: rejected");

                        sendEmail(otherCandidature, "rejected");
                        candidatureService.delete(otherCandidature);
                        System.out.println("Candidature " + otherCandidature.getId() + " traitée et supprimée");
                    }
                }

                // Supprimer l'offre d'emploi
                System.out.println("Suppression de l'offre d'emploi...");
                OffreEmploi offreEmploi = offreEmploiService.getById(candidature.getOffreEmploiId());
                offreEmploiService.delete(offreEmploi);
                System.out.println("Offre d'emploi supprimée avec succès");

                refreshCandidatures();

                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.INFORMATION, "Succès",
                            "La candidature a été acceptée, l'offre a été clôturée et les autres candidatures ont été supprimées.");
                });
            } catch (Exception e) {
                System.err.println("ERREUR lors de l'acceptation de la candidature:");
                e.printStackTrace();
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.ERROR, "Erreur",
                            "Une erreur est survenue lors de l'acceptation de la candidature: " + e.getMessage());
                });
            }
        }
        System.out.println("=== FIN ACCEPTATION CANDIDATURE ===");
    }

    private void sendEmail(Candidature candidature, String status) {
        try {
            System.out.println("=== DÉBUT DE L'ENVOI D'EMAIL ===");
            System.out.println("Status: " + status);

            Candidat candidat = candidatService.getById(candidature.getCandidatId());
            OffreEmploi offreEmploi = offreEmploiService.getById(candidature.getOffreEmploiId());

            if (candidat == null || offreEmploi == null) {
                throw new Exception("Données candidat ou offre manquantes");
            }

            String templateId = status.equals("accepted") ? MAILJET_TEMPLATE_ID_ACCEPTED : MAILJET_TEMPLATE_ID_REJECTED;
            System.out.println("Template ID utilisé: " + templateId);

            // Test de l'authentification
            String auth = MAILJET_API_KEY_PUBLIC + ":" + MAILJET_API_KEY_PRIVATE;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
            System.out.println("Clés utilisées - Public: " + MAILJET_API_KEY_PUBLIC.substring(0, 5) + "..., Private: " + MAILJET_API_KEY_PRIVATE.substring(0, 5) + "...");

            String jsonPayload = "{"
                    + "\"Messages\":[{"
                    + "\"From\": {\"Email\": \"aminraissi43@gmail.com\", \"Name\": \"Service Recrutement\"},"
                    + "\"To\": [{\"Email\": \"" + candidat.getEmail() + "\", \"Name\": \"" + candidat.getFirstName() + " " + candidat.getLastName() + "\"}],"
                    + "\"TemplateID\": " + templateId + ","
                    + "\"TemplateLanguage\": true,"
                    + "\"Subject\": \"" + (status.equals("accepted") ? "Félicitations ! Votre candidature a été acceptée" : "Mise à jour sur votre candidature") + "\","
                    + "\"Variables\": {"
                    + "\"firstName\": \"" + candidat.getFirstName() + "\","
                    + "\"lastName\": \"" + candidat.getLastName() + "\","
                    + "\"jobTitle\": \"" + offreEmploi.getTitle() + "\""
                    + "}"
                    + "}]"
                    + "}";

            URL url = new URL("https://api.mailjet.com/v3.1/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Basic " + encodedAuth);
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Code de réponse: " + responseCode);

            StringBuilder response = new StringBuilder();
            try (java.util.Scanner scanner = new java.util.Scanner(
                    responseCode >= 400 ? conn.getErrorStream() : conn.getInputStream())
                    .useDelimiter("\\A")) {
                response.append(scanner.hasNext() ? scanner.next() : "");
            }
            System.out.println("Réponse complète: " + response.toString());

            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                System.out.println("Email envoyé avec succès!");
            } else {
                throw new Exception("Échec de l'envoi de l'email. Code: " + responseCode +
                        ", Réponse: " + response.toString());
            }

        } catch (Exception e) {
            System.err.println("ERREUR lors de l'envoi de l'email: " + e.getMessage());
            e.printStackTrace();
            Platform.runLater(() -> {
                showAlert(Alert.AlertType.ERROR, "Erreur d'envoi d'email",
                        "Une erreur est survenue lors de l'envoi de l'email: " + e.getMessage());
            });
        }
        System.out.println("=== FIN DE L'ENVOI D'EMAIL ===");
    }

    @FXML
    void afficherOffresRecrutement() {
        try {
            // Charger la page d'affichage des offres d'emploi
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/affichageOffre.fxml"));
            Stage stage = (Stage) tableContent.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des offres d'emploi");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void deconnexion() {
        // Ne rien faire pour désactiver le bouton
    }

    private void refreshCandidatures(int offreId) {
        try {
            tableContent.getChildren().clear();
            List<Candidature> candidatures = candidatureService.getCandidaturesByOffreEmploiId(offreId);

            // En-tête
            GridPane header = new GridPane();
            header.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10;");
            header.getColumnConstraints().addAll(
                    createColumnConstraints(25),  // Titre
                    createColumnConstraints(25),  // Email
                    createColumnConstraints(20),  // Téléphone
                    createColumnConstraints(15),  // CV
                    createColumnConstraints(15)   // Actions
            );

            // Ajouter du padding et des marges
            header.setHgap(20); // Espacement horizontal entre les colonnes
            header.setVgap(15); // Espacement vertical
            header.setPadding(new Insets(10, 20, 10, 20)); // Padding autour du header

            header.add(createHeaderLabel("Titre"), 0, 0);
            header.add(createHeaderLabel("Email"), 1, 0);
            header.add(createHeaderLabel("Téléphone"), 2, 0);
            header.add(createHeaderLabel("CV"), 3, 0);
            header.add(createHeaderLabel("Actions"), 4, 0);

            tableContent.getChildren().add(header);

            for (Candidature candidature : candidatures) {
                Candidat candidat = candidatService.getById(candidature.getCandidatId());
                if (candidat != null) {
                    GridPane row = new GridPane();
                    row.setStyle("-fx-padding: 10; -fx-background-color: #ffffff; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");
                    row.getColumnConstraints().addAll(
                            createColumnConstraints(25),
                            createColumnConstraints(25),
                            createColumnConstraints(20),
                            createColumnConstraints(15),
                            createColumnConstraints(15)
                    );

                    // Ajouter du padding et des marges
                    row.setHgap(20); // Espacement horizontal entre les colonnes
                    row.setVgap(15); // Espacement vertical
                    row.setPadding(new Insets(10, 20, 10, 20)); // Padding autour de chaque ligne

                    // Ajouter les informations dans la ligne
                    row.add(createDataLabel(offreEmploiService.getById(candidature.getOffreEmploiId()).getTitle()), 0, 0);
                    row.add(createDataLabel(candidat.getEmail()), 1, 0);
                    row.add(createDataLabel(candidat.getPhone()), 2, 0);

                    // Créer le lien CV avec le chemin stocké
                    Hyperlink cvLink = new Hyperlink("Voir CV");
                    cvLink.setUserData(candidature.getCv()); // Stocker le chemin du CV
                    cvLink.setOnAction(this::handleViewCV);
                    row.add(cvLink, 3, 0);

                    // Boutons d'action
                    HBox actionBox = new HBox(10);
                    Button acceptButton = new Button("✔");
                    acceptButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
                    acceptButton.setOnAction(e -> handleAcceptCandidature(candidature));

                    Button rejectButton = new Button("✘");
                    rejectButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                    rejectButton.setOnAction(e -> handleRejectCandidature(candidature));

                    actionBox.getChildren().addAll(acceptButton, rejectButton);
                    row.add(actionBox, 4, 0);

                    tableContent.getChildren().add(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors du chargement des candidatures.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void afficherCandidatures() {
        try {
            // Charger la page d'affichage des candidatures
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/affichageCandidatures.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tableContent.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des candidatures");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors du chargement des candidatures.");
        }
    }

    private void showResultDialog(String title, String content) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(null);

        // Création d'une TextArea pour afficher le contenu
        TextArea textArea = new TextArea(content);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefWidth(600);
        textArea.setPrefHeight(400);

        // Style CSS pour une meilleure lisibilité
        textArea.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14px;");

        // Création d'un ScrollPane contenant la TextArea
        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        // Ajout du ScrollPane au dialog
        dialog.getDialogPane().setContent(scrollPane);

        // Définition de la taille minimale du dialog
        dialog.getDialogPane().setPrefSize(700, 500);

        // Ajout du bouton Fermer
        ButtonType closeButton = new ButtonType("Fermer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);

        // Affichage du dialog
        dialog.showAndWait();
    }

    private void parseResume(String cvPath) {
        try {
            File cvFile = new File(cvPath);
            if (!cvFile.exists()) {
                System.out.println("CV file not found: " + cvPath);
                return;
            }

            // Vérifications du fichier
            if (cvFile.length() > 10 * 1024 * 1024) {
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.ERROR,
                            "Erreur",
                            "Le fichier CV est trop volumineux (max 10MB).");
                });
                return;
            }

            String fileName = cvFile.getName().toLowerCase();
            if (!fileName.endsWith(".pdf") && !fileName.endsWith(".doc") && !fileName.endsWith(".docx")) {
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.ERROR,
                            "Erreur",
                            "Format de fichier non supporté. Utilisez PDF, DOC ou DOCX.");
                });
                return;
            }

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", cvFile.getName(),
                            RequestBody.create(MediaType.parse("application/octet-stream"), cvFile))
                    .addFormDataPart("collection", AFFINDA_COLLECTION_ID)
                    .addFormDataPart("workspace", AFFINDA_WORKSPACE_ID)
                    .addFormDataPart("wait", "true")
                    .build();

            Request request = new Request.Builder()
                    .url(AFFINDA_API_URL)
                    .addHeader("Authorization", "Bearer " + AFFINDA_API_KEY)
                    .addHeader("Accept", "application/json")
                    .post(requestBody)
                    .build();

            System.out.println("Sending request to Affinda...");
            System.out.println("URL: " + AFFINDA_API_URL);
            System.out.println("File path: " + cvPath);
            System.out.println("Collection ID: " + AFFINDA_COLLECTION_ID);
            System.out.println("Workspace ID: " + AFFINDA_WORKSPACE_ID);

            Platform.runLater(() -> {
                showAlert(Alert.AlertType.INFORMATION,
                        "Information",
                        "Analyse du CV en cours...\nCela peut prendre jusqu'à une minute.");
            });

            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body().string();
                System.out.println("Response from Affinda: " + responseBody);

                if (response.isSuccessful()) {
                    JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
                    StringBuilder analysisResult = new StringBuilder();
                    analysisResult.append("RÉSULTATS DE L'ANALYSE DU CV\n");
                    analysisResult.append("================================\n\n");

                    if (jsonResponse.has("data")) {
                        JsonObject data = jsonResponse.getAsJsonObject("data");

                        // Résumé
                        if (data.has("summary")) {
                            JsonObject summary = data.getAsJsonObject("summary");
                            if (summary.has("parsed")) {
                                analysisResult.append("📋 RÉSUMÉ\n")
                                        .append("----------------\n")
                                        .append(summary.get("parsed").getAsString())
                                        .append("\n\n");
                            }
                        }

                        // Texte brut
                        if (data.has("rawText")) {
                            String rawText = data.get("rawText").getAsString();

                            // Compétences
                            if (rawText.contains("COMPÉTENCES")) {
                                analysisResult.append("💪 COMPÉTENCES\n")
                                        .append("----------------\n");
                                String[] lines = rawText.split("\n");
                                boolean inSkillsSection = false;

                                for (String line : lines) {
                                    if (line.contains("COMPÉTENCES")) {
                                        inSkillsSection = true;
                                        continue;
                                    }
                                    if (inSkillsSection) {
                                        if (line.trim().isEmpty() || line.contains("CENTRES")) {
                                            break;
                                        }
                                        if (line.trim().startsWith("•")) {
                                            analysisResult.append(line.trim()).append("\n");
                                        }
                                    }
                                }
                                analysisResult.append("\n");
                            }

                            // Expérience professionnelle
                            if (rawText.contains("EXPERIENCES PROFESSIONNELLES")) {
                                analysisResult.append("💼 EXPÉRIENCE PROFESSIONNELLE\n")
                                        .append("--------------------------------\n");
                                String[] lines = rawText.split("\n");
                                boolean inExperienceSection = false;

                                for (String line : lines) {
                                    if (line.contains("EXPERIENCES PROFESSIONNELLES")) {
                                        inExperienceSection = true;
                                        continue;
                                    }
                                    if (inExperienceSection) {
                                        if (line.contains("FORMATION")) {
                                            break;
                                        }
                                        if (!line.trim().isEmpty()) {
                                            analysisResult.append(line.trim()).append("\n");
                                        }
                                    }
                                }
                                analysisResult.append("\n");
                            }

                            // Formation
                            if (rawText.contains("FORMATION")) {
                                analysisResult.append("🎓 FORMATION\n")
                                        .append("----------------\n");
                                String[] lines = rawText.split("\n");
                                boolean inEducationSection = false;

                                for (String line : lines) {
                                    if (line.contains("FORMATION")) {
                                        inEducationSection = true;
                                        continue;
                                    }
                                    if (inEducationSection) {
                                        if (line.contains("COPYRIGHT")) {
                                            break;
                                        }
                                        if (!line.trim().isEmpty()) {
                                            analysisResult.append(line.trim()).append("\n");
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Platform.runLater(() -> {
                        showResultDialog("Résultats de l'analyse du CV", analysisResult.toString());
                    });
                } else {
                    System.out.println("Error analyzing CV: " + response.code());
                    String errorMessage = "Erreur lors de l'analyse du CV.\n";
                    if (response.code() == 401) {
                        errorMessage += "Erreur d'authentification. Veuillez vérifier la clé API.";
                    } else if (response.code() == 404) {
                        errorMessage += "Service non trouvé. Veuillez vérifier l'URL de l'API.";
                    } else {
                        errorMessage += "Code: " + response.code() + "\nRéponse: " + responseBody;
                    }

                    final String finalErrorMessage = errorMessage;
                    Platform.runLater(() -> {
                        showAlert(Alert.AlertType.ERROR,
                                "Erreur",
                                finalErrorMessage);
                    });
                }
            } catch (SocketTimeoutException e) {
                System.out.println("Timeout during API call: " + e.getMessage());
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.ERROR,
                            "Erreur de timeout",
                            "L'analyse du CV a pris trop de temps. Veuillez réessayer.");
                });
            } catch (IOException e) {
                System.out.println("IO Error during API call: " + e.getMessage());
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.ERROR,
                            "Erreur",
                            "Une erreur est survenue lors de la communication avec le serveur: " + e.getMessage());
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                showAlert(Alert.AlertType.ERROR,
                        "Erreur",
                        "Une erreur est survenue lors de l'analyse du CV: " + e.getMessage());
            });
        }
    }
}