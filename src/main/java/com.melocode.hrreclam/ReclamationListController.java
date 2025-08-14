package com.melocode.hrreclam;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReclamationListController {

    @FXML
    private TableView<Reclamation> tableReclamations;

    @FXML
    private TableColumn<Reclamation, Integer> colId;

    @FXML
    private TableColumn<Reclamation, String> colEmployeeName;

    @FXML
    private TableColumn<Reclamation, String> colType;

    @FXML
    private TableColumn<Reclamation, String> colDescription;

    @FXML
    private TableColumn<Reclamation, String> colDateSubmission;

    @FXML
    private TableColumn<Reclamation, String> colStatus;

    @FXML
    private TableColumn<Reclamation, String> colPriority;

    @FXML
    private TextField txtReclamationId;

    @FXML
    public void initialize() {
        configureTable();
        refreshTable();
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

    @FXML
    public void refreshTable() {
        try (Connection conn = DatabaseConnection.connect()) {
            if (conn == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Connexion à la base de données échouée.");
                return;
            }

            String sql = "SELECT * FROM reclamation";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            ObservableList<Reclamation> reclamationsList = FXCollections.observableArrayList();

            while (rs.next()) {
                Reclamation reclamation = new Reclamation(
                        rs.getInt("id"),
                        rs.getString("employee_name"),
                        rs.getString("type"),
                        rs.getString("description"),
                        rs.getString("date_of_submission"),
                        rs.getString("status"),
                        rs.getString("priority")
                );
                reclamationsList.add(reclamation);
            }

            tableReclamations.setItems(reclamationsList);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Erreur: " + e.getMessage());
        }
    }

    @FXML
    public void openAddReclamationForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adem/reclamation-form.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter une Réclamation");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le formulaire: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
