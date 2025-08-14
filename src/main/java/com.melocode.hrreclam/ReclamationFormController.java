package com.melocode.hrreclam;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReclamationFormController {

    @FXML
    private ComboBox<String> comboEmployeeId;

    @FXML
    private ComboBox<String> comboReclamationType;

    @FXML
    private TextArea txtDescription;

    public void submitReclamation() {
        String employeeName = comboEmployeeId.getValue();
        String reclamationType = comboReclamationType.getValue();
        String description = txtDescription.getText();

        if (employeeName == null || reclamationType == null || description.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        try (Connection conn = DatabaseConnection.connect()) {
            if (conn == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Connexion à la base de données échouée.");
                return;
            }

            String sql = "INSERT INTO reclamation (employee_name, type, description, status, priority) VALUES (?, ?, ?, 'Pending', 'Medium')";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, employeeName);
            pstmt.setString(2, reclamationType);
            pstmt.setString(3, description);
            int rowsInserted = pstmt.executeUpdate();

            if (rowsInserted > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Réclamation ajoutée avec succès.");
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter la réclamation.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Erreur: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        comboEmployeeId.setValue(null);
        comboReclamationType.setValue(null);
        txtDescription.clear();
    }

    public void initialize() {
        loadEmployeeNames();

        comboReclamationType.getItems().addAll(
                "Workplace Harassment",
                "Salary Issue",
                "Working Conditions",
                "Other"
        );
    }

    private void loadEmployeeNames() {
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT name FROM employees");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                comboEmployeeId.getItems().add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
