package com.melocode.hrreclam;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import java.net.URL;
import java.sql.*;
import javafx.event.ActionEvent;
import java.io.IOException;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

public class ReclamationDashboardController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private TextField txtReclamationId;

    @FXML
    private ComboBox<String> comboEmployeeId, comboReclamationType;

    @FXML
    private TextArea txtDescription;

    @FXML
    private TableView<Reclamation> tableReclamations;

    @FXML
    private TableColumn<Reclamation, Integer> colId;

    @FXML
    private TableColumn<Reclamation, String> colEmployeeName, colType, colDescription, colDateSubmission, colStatus, colPriority;

    @FXML
    private TextArea txtChatbotResponse;

    @FXML
    private TextField txtChatInput;

    @FXML
    private Button btnSendChat;

    private static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY"); // Replace with your OpenAI API key
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    @FXML
    public void initialize() {
        setBackgroundImage();
        configureTable();
        loadEmployeeNames();
        populateReclamationTypes();
        refreshTable();
    }

    private void setBackgroundImage() {
        URL imageUrl = getClass().getResource("/images/final.png");

        if (imageUrl == null) {
            System.err.println("❌ Image not found! Check the path.");
            return;
        }

        Image image = new Image(imageUrl.toExternalForm());

        BackgroundImage backgroundImage = new BackgroundImage(
                image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, true, true) // Ensure full-screen scaling
        );

        rootPane.setBackground(new Background(backgroundImage));
    }

    @FXML
    public void deleteReclamation() {
        String reclamationId = txtReclamationId.getText().trim();

        if (reclamationId.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez saisir un ID de réclamation.");
            return;
        }

        try (Connection conn = DatabaseConnection.connect()) {
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement deleteTicketsStmt = conn.prepareStatement("DELETE FROM ticket_reclamation WHERE reclamation_id = ?")) {
                deleteTicketsStmt.setInt(1, Integer.parseInt(reclamationId));
                deleteTicketsStmt.executeUpdate();
            }

            try (PreparedStatement deleteReclamationStmt = conn.prepareStatement("DELETE FROM reclamation WHERE id = ?")) {
                deleteReclamationStmt.setInt(1, Integer.parseInt(reclamationId));
                int rowsAffected = deleteReclamationStmt.executeUpdate();

                if (rowsAffected > 0) {
                    conn.commit(); // Commit transaction
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Réclamation supprimée avec succès.");
                    refreshTable();
                } else {
                    conn.rollback(); // Rollback if no record found
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Aucune réclamation trouvée avec cet ID.");
                }
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Erreur: " + e.getMessage());
        }
    }
    private void loadEmployeeNames() {
        String sql = "SELECT name FROM employees";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            ObservableList<String> employeeNames = FXCollections.observableArrayList();

            while (rs.next()) {
                employeeNames.add(rs.getString("name"));
            }

            Platform.runLater(() -> {
                if (comboEmployeeId != null) {
                    comboEmployeeId.setItems(employeeNames);
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void configureTable() {
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colEmployeeName.setCellValueFactory(cellData -> cellData.getValue().employeeNameProperty());
        colType.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        colDescription.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        colDateSubmission.setCellValueFactory(cellData -> cellData.getValue().dateOfSubmissionProperty());
        colStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        colPriority.setCellValueFactory(cellData -> cellData.getValue().priorityProperty());
    }

    private void populateReclamationTypes() {
        if (comboReclamationType != null) {
            comboReclamationType.getItems().setAll("Workplace Harassment", "Salary Issue", "Working Conditions", "Other");
        }
    }

    @FXML
    private void submitReclamation() {
        String employeeName = comboEmployeeId.getValue();
        String reclamationType = comboReclamationType.getValue();
        String description = txtDescription.getText().trim();

        if (employeeName == null || reclamationType == null || description.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        String sql = "INSERT INTO reclamation (employee_name, type, description, date_of_submission, status, priority) " +
                "VALUES (?, ?, ?, NOW(), 'Pending', 'Medium')";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, employeeName);
            pstmt.setString(2, reclamationType);
            pstmt.setString(3, description);

            if (pstmt.executeUpdate() > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Réclamation ajoutée avec succès.");
                clearFields();
                refreshTable();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter la réclamation.");
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Erreur: " + e.getMessage());
        }
    }

    @FXML
    private void openChatbot(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/adem/chatBot.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Chatbot");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void sendChatbotMessage() {
        String userMessage = txtChatInput.getText();
        if (userMessage.isEmpty()) return;

        String response = getChatbotResponse(userMessage);
        txtChatbotResponse.appendText("\nYou: " + userMessage + "\nChatbot: " + response + "\n");
        txtChatInput.clear();
    }

    private String getChatbotResponse(String message) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "gpt-3.5-turbo");
            requestBody.put("messages", new org.json.JSONArray()
                    .put(new JSONObject().put("role", "system").put("content", "You are a helpful assistant."))
                    .put(new JSONObject().put("role", "user").put("content", message))
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OPENAI_API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + OPENAI_API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject jsonResponse = new JSONObject(response.body());
            return jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Unable to reach ChatGPT.";
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    @FXML
    private void refreshTable() {
        ObservableList<Reclamation> reclamationList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM reclamation";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                reclamationList.add(new Reclamation(
                        rs.getInt("id"),
                        rs.getString("employee_name"),
                        rs.getString("type"),
                        rs.getString("description"),
                        rs.getString("date_of_submission"),
                        rs.getString("status"),
                        rs.getString("priority")
                ));
            }

            Platform.runLater(() -> tableReclamations.setItems(reclamationList));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        comboEmployeeId.setValue(null);
        comboReclamationType.setValue(null);
        txtDescription.clear();
        txtReclamationId.clear();
    }
}
