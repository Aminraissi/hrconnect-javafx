package controllers.formations;

import com.fasterxml.jackson.databind.JsonNode;
import exceptions.InvalidInputException;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import models.Employe;
import models.Formation;
import okhttp3.*;
import services.FormationService;
import utils.ConfigReader;
import utils.ShowMenu;
import utils.TableCell;
import utils.TableRow;
import utils.enums.TableRowType;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import com.fasterxml.jackson.databind.ObjectMapper;


import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;



import java.util.HashMap;
import java.util.Map;

public class ContacterParticipantsController implements Initializable, ShowMenu {

    @FXML
    private MFXButton cancelBtn;

    @FXML
    private AnchorPane menu;

    @FXML
    private TextArea message;

    @FXML
    private MFXButton saveBtn;

    Formation formation;

    FormationService fs = new FormationService();

    @FXML
    private MFXTextField sujet;


    @FXML
    private Text title;

    @FXML
    private MFXButton corrigerBtn;

    List<String> emails = new ArrayList();

    String lastMessageVersion = null;

    @FXML
    void OnClickCancelBtn(ActionEvent event) {
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/formations/ListeParticipants.fxml"));
            root = loader.load();
            ListeParticipantsController controller = loader.getController();
            controller.setFormation(formation);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        menu.getScene().setRoot(root);
    }

    @FXML
    void onSave(ActionEvent event) {

        try{
            if(emails.isEmpty()) {
                throw new InvalidInputException("Aucun participant à contacter");
            }else if(sujet.getText().isEmpty()){
                throw new InvalidInputException("Le sujet ne peut pas être vide");
            }else if(message.getText().isEmpty()){
                throw new InvalidInputException("Le message ne peut pas être vide");
            }

            // Run email sending in a background thread
            Task<Void> emailTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    for (String email : emails) {
                        envoyerEmail(email);  // Send emails in a separate thread
                    }
                    return null;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Succès");
                    alert.setHeaderText(null);
                    alert.setContentText("Emails envoyés avec succès");
                    alert.showAndWait();
                    message.setText("");
                    saveBtn.setDisable(false);
                }

                @Override
                protected void failed() {
                    super.failed();
                    saveBtn.setDisable(false);
                    throw new Error("Une erreur s'est produite lors de l'envoi des emails.");
                }
            };

            // Run the task in a separate thread
            Thread emailThread = new Thread(emailTask);
            emailThread.setDaemon(true);  // Allow the thread to close when the app exits
            emailThread.start();

            saveBtn.setDisable(true);

        }catch (InvalidInputException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }catch (Exception e){
            System.out.println(e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur s'est produite");
            alert.showAndWait();
        }
    }

    public void initialize(URL location, ResourceBundle resources) {
        initializeMenu(menu);
        if(formation == null) return;

        try {
            List<Employe> employes = fs.getFormationParticipants(formation.getId());
            employes.forEach(employe -> emails.add(employe.getEmail()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setFormation(Formation formation) throws SQLException {
        this.formation = formation;
        title.setText("Contacter les participants de la formation " + formation.getTitle());
        sujet.setText("Message a propos la formation " + formation.getTitle());
        initialize(null, null);
    }

    @FXML
    void improveMsg() {
        if(lastMessageVersion != null){
            message.setText(lastMessageVersion);
            lastMessageVersion = null;
            corrigerBtn.setText("Corriger et améliorer");
        }else {
            lastMessageVersion = message.getText();
            Task<Void> improveMsgTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    GeminiApi();
                    corrigerBtn.setDisable(true);
                    return null;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    corrigerBtn.setText("Annuler la correction");
                    corrigerBtn.setDisable(false);
                }

                @Override
                protected void failed() {
                    super.failed();
                    corrigerBtn.setDisable(false);
                }
            };

            // Run the task in a separate thread
            Thread improveMsgTThread = new Thread(improveMsgTask);
            improveMsgTThread.setDaemon(true);
            improveMsgTThread.start();

        }
    }


    private void GeminiApi() {
        OkHttpClient client = new OkHttpClient();
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key="
                + ConfigReader.get("HAYTHEM_GEMINI_API");

        // Creating the JSON request using Gson
        Gson gson = new Gson();

        // Constructing the request body
        JsonObject messagePart = new JsonObject();
        messagePart.addProperty("text", "repondre uniquement avec le text corriger de cette paragphrase : " + message.getText());

        JsonObject contentPart = new JsonObject();
        contentPart.add("parts", gson.toJsonTree(Collections.singletonList(messagePart)));

        JsonObject requestBody = new JsonObject();
        requestBody.add("contents", gson.toJsonTree(Collections.singletonList(contentPart)));

        try {
            // Convert request body to JSON string
            String jsonPayload = gson.toJson(requestBody);

            // Create request body
            RequestBody body = RequestBody.create(jsonPayload, MediaType.get("application/json; charset=utf-8"));

            // Build request
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            // Execute request
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    // Parse JSON response
                    String responseBody = response.body().string();
                    JsonElement rootNode = JsonParser.parseString(responseBody);

                    // Extract the corrected text
                    JsonObject firstCandidate = rootNode.getAsJsonObject()
                            .getAsJsonArray("candidates")
                            .get(0)
                            .getAsJsonObject();

                    JsonObject content = firstCandidate.getAsJsonObject("content");
                    String correctedText = content.getAsJsonArray("parts")
                            .get(0)
                            .getAsJsonObject()
                            .get("text")
                            .getAsString();

                    message.setText(correctedText);
                } else {
                    System.out.println("Request failed: " + response.code());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void envoyerEmail(String email){
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.emailjs.com/api/v1.0/email/send";

        // Create a Map for template parameters
        Map<String, String> templateParams = new HashMap<>();
        templateParams.put("subject", sujet.getText());
        templateParams.put("to", email);
        templateParams.put("message", message.getText());

        // Create the main request payload Map
        Map<String, Object> payload = new HashMap<>();
        payload.put("user_id", ConfigReader.get("HAYTHEM_MAIL_JS_USER_ID"));
        payload.put("accessToken", ConfigReader.get("HAYTHEM_MAIL_JS_ACCESS_TOKEN"));
        payload.put("service_id", "service_tvxohrv");
        payload.put("template_id", "template_kh0dxxn");
        payload.put("template_params", templateParams);

        try {
            // Convert Map to JSON string
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonPayload = objectMapper.writeValueAsString(payload);

            // Create request body
            RequestBody body = RequestBody.create(jsonPayload, MediaType.get("application/json; charset=utf-8"));

            // Build request
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            // Execute request
            try (Response response = client.newCall(request).execute()) {
                System.out.println("Response: " + response.body().string());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
