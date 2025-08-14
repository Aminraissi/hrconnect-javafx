package com.melocode.semin.controllers;



import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MenuController {

    @FXML
    private Button goToSeminaireButton;
    @FXML
    private Button goToParticipationButton;

    @FXML
    private  Button goToParticipationCrudButton;

    @FXML
    private void goToSeminaire() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/melocode/semin/views/SeminaireView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) goToSeminaireButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToParticipation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/melocode/semin/views/participation.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) goToParticipationButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void goToParticipationCrud() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/melocode/semin/views/participationcrud.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) goToParticipationCrudButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
