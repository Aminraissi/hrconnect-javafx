package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import models.OffreEmploi;
import services.OffreEmploiService;
import java.util.List;

public class OffreEmploiController {

    @FXML
    private Button btnclear;

    @FXML
    private Button btndelete;

    @FXML
    private Button btnsave;

    @FXML
    private Button btnupdate;

    @FXML
    private ListView<OffreEmploi> table;

    @FXML
    private TextField tdescription;

    @FXML
    private TextField tentreprise;

    @FXML
    private TextField tlieu;

    @FXML
    private TextField ttitre;

    private OffreEmploiService offreService = new OffreEmploiService();

    @FXML
    public void initialize() {
        refreshTable();
    }

    @FXML
    void saveoffre(ActionEvent event) {
        try {
            OffreEmploi offre = new OffreEmploi(
                    ttitre.getText(),
                    tdescription.getText(),
                    tlieu.getText()
            );

            offreService.create(offre);
            clearFields();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void updateoffre(ActionEvent event) {
        try {
            OffreEmploi selectedOffre = table.getSelectionModel().getSelectedItem();
            if (selectedOffre != null) {
                selectedOffre.setTitle(ttitre.getText());
                selectedOffre.setDescription(tdescription.getText());
                selectedOffre.setLocation(tlieu.getText());
                offreService.update(selectedOffre);
                refreshTable();
                clearFields();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void clearoffre(ActionEvent event) {
        clearFields();
    }

    @FXML
    void afficherOffres(ActionEvent event) {
        try {
            // Charger la page d'affichage
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/affichageOffre.fxml"));
            Stage stage = (Stage) btnsave.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des offres d'emploi");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void retourFormulaire(ActionEvent event) {
        try {
            // Charger la page du formulaire
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/formulaireOffre.fxml"));
            Stage stage = (Stage) table.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Formulaire de gestion des offres d'emploi");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshTable() {
        try {
            List<OffreEmploi> offres = offreService.getAll();
            table.getItems().clear();
            table.getItems().addAll(offres);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        ttitre.clear();
        tdescription.clear();
        tentreprise.clear();
        tlieu.clear();
    }}