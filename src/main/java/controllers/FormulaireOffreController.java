package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.OffreEmploi;
import services.OffreEmploiService;
import utils.ShowMenu;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Optional;

public class FormulaireOffreController implements ShowMenu {

    @FXML
    private AnchorPane menu;

    @FXML
    private Button btnsave;

    @FXML
    private Button btncancel;

    @FXML
    private TextField ttitre;

    @FXML
    private TextField tdescription;

    @FXML
    private TextField tlieu;

    private OffreEmploiService offreService = new OffreEmploiService();
    private OffreEmploi offreToUpdate; // Offre à mettre à jour

    private static final String LANGUAGETOOL_API_URL = "https://api.languagetool.org/v2/check";

    @FXML
    public void initialize() {
        // Initialiser le menu
        initializeMenu(menu);

        // Ajouter les écouteurs de focus pour chaque champ
        ttitre.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // Quand le champ perd le focus
                correctSpelling(ttitre);
            }
        });

        tdescription.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                correctSpelling(tdescription);
            }
        });

        tlieu.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                correctSpelling(tlieu);
            }
        });
    }

    private void correctSpelling(TextField textField) {
        String text = textField.getText();
        if (text.isEmpty() || text.length() < 3) return;

        try {
            String params = String.format("text=%s&language=fr",
                    URLEncoder.encode(text, StandardCharsets.UTF_8));

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(LANGUAGETOOL_API_URL))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(params))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() == 200) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response.body());
                                JSONArray matches = jsonResponse.getJSONArray("matches");

                                if (matches.length() > 0) {
                                    Platform.runLater(() -> {
                                        // Collecter toutes les corrections
                                        StringBuilder corrections = new StringBuilder();
                                        for (int i = 0; i < matches.length(); i++) {
                                            JSONObject match = matches.getJSONObject(i);

                                            // Vérifier que c'est bien une erreur d'orthographe
                                            String ruleId = match.getJSONObject("rule").getString("id");
                                            if (!ruleId.equals("FR_SPELLING_RULE")) continue;

                                            JSONArray replacements = match.getJSONArray("replacements");
                                            if (replacements.length() > 0) {
                                                int offset = match.getInt("offset");
                                                int length = match.getInt("length");
                                                String errorText = text.substring(offset, offset + length);
                                                String suggestion = replacements.getJSONObject(0).getString("value");

                                                if (!textField.getText().contains(errorText)) continue;

                                                corrections.append("\"").append(errorText)
                                                        .append("\" → \"").append(suggestion).append("\"\n");
                                            }
                                        }

                                        // Si des corrections ont été trouvées, afficher une seule boîte de dialogue
                                        if (corrections.length() > 0) {
                                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                            alert.setTitle("Corrections suggérées");
                                            alert.setHeaderText("Des erreurs ont été détectées");
                                            alert.setContentText("Voulez-vous appliquer les corrections suivantes ?\n\n" +
                                                    corrections.toString());

                                            Optional<ButtonType> result = alert.showAndWait();
                                            if (result.isPresent() && result.get() == ButtonType.OK) {
                                                String correctedText = textField.getText();
                                                for (int i = 0; i < matches.length(); i++) {
                                                    JSONObject match = matches.getJSONObject(i);
                                                    if (!match.getJSONObject("rule").getString("id").equals("FR_SPELLING_RULE"))
                                                        continue;

                                                    JSONArray replacements = match.getJSONArray("replacements");
                                                    if (replacements.length() > 0) {
                                                        int offset = match.getInt("offset");
                                                        int length = match.getInt("length");
                                                        String errorText = text.substring(offset, offset + length);
                                                        String suggestion = replacements.getJSONObject(0).getString("value");
                                                        correctedText = correctedText.replace(errorText, suggestion);
                                                    }
                                                }
                                                textField.setText(correctedText);
                                            }
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                System.out.println("Erreur lors du traitement de la réponse: " + e.getMessage());
                            }
                        } else {
                            System.out.println("Erreur API: " + response.statusCode() + " - " + response.body());
                        }
                    })
                    .exceptionally(e -> {
                        e.printStackTrace();
                        System.out.println("Erreur de connexion: " + e.getMessage());
                        return null;
                    });

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur générale: " + e.getMessage());
        }
    }

    @FXML
    void saveoffre() {
        try {
            // Vérifier si on est en mode mise à jour ou création
            if (offreToUpdate == null) {
                // Mode création : créer une nouvelle offre
                OffreEmploi offre = new OffreEmploi(
                        ttitre.getText(),
                        tdescription.getText(),
                        tlieu.getText()
                );
                offreService.create(offre);

                // Afficher un message de succès
                showAlert("Succès", "L'offre a été créée avec succès !");
            } else {
                // Mode mise à jour : mettre à jour l'offre existante
                offreToUpdate.setTitle(ttitre.getText());
                offreToUpdate.setDescription(tdescription.getText());
                offreToUpdate.setLocation(tlieu.getText());
                offreService.update(offreToUpdate);

                // Afficher un message de succès
                showAlert("Succès", "L'offre a été mise à jour avec succès !");
            }

            // Retourner à la page d'affichage des offres d'emploi
            retournerAffichageOffres();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Une erreur s'est produite lors de l'enregistrement de l'offre.");
        }
    }

    @FXML
    void cancel() {
        // Retourner à la page d'affichage des offres d'emploi
        retournerAffichageOffres();
    }

    private void retournerAffichageOffres() {
        try {
            // Charger la page d'affichage des offres
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/affichageOffre.fxml"));
            Stage stage = (Stage) btnsave.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des offres d'emploi");
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

    public void setOffreToUpdate(OffreEmploi offre) {
        this.offreToUpdate = offre;

        // Remplir les champs du formulaire avec les données de l'offre à mettre à jour
        ttitre.setText(offre.getTitle());
        tdescription.setText(offre.getDescription());
        tlieu.setText(offre.getLocation());
    }

    @FXML
    void afficherOffresRecrutement() {
        try {
            // Charger la page d'affichage des offres
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/affichageOffre.fxml"));
            Stage stage = (Stage) ttitre.getScene().getWindow();
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