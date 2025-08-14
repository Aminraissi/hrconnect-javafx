package com.melocode.semin.controllers;

import com.melocode.semin.dao.SeminaireDAO;
import com.melocode.semin.models.Seminaire;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.time.LocalDate;

public class SeminaireControllerv1 {

    private static final int PAGE_SIZE = 10; // Number of seminars per page
    private int totalPages;
    private int currentPage = 0;

    @FXML
    private TextField nomField, lieuField, formateurField, coutField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private DatePicker dateDebutField, dateFinField;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private TableView<Seminaire> tableViewSeminaire;
    @FXML
    private TableColumn<Seminaire, String> columnNom, columnDescription, columnLieu, columnFormateur, columnType;
    @FXML
    private TableColumn<Seminaire, LocalDate> columnDateDebut, columnDateFin;
    @FXML
    private TableColumn<Seminaire, Double> columnCout;
    @FXML
    private Label statsLabel;
    @FXML
    private BarChart<String, Number> seminaireStatsChart;
    @FXML
    private Pagination pagination;  // Pagination control

    private Seminaire selectedSeminaire;






    private ObservableList<Seminaire> seminaireList;

    public void setStageToFullScreen(Stage stage) {
        stage.setFullScreen(true);
    }



    @FXML
    private void afficherSeminaire() {
        ObservableList<Seminaire> seminaireList = SeminaireDAO.recupererTousLesSeminaires();
        tableViewSeminaire.setItems(seminaireList);
    }

    @FXML
    public void initialize() {
        // Initialize the ComboBox for seminar types
        typeComboBox.setItems(FXCollections.observableArrayList("Workshop", "Conference", "Webinar", "Training", "Seminar"));

        // Initialize the TableView columns
        columnNom.setCellValueFactory(cellData -> cellData.getValue().nomProperty());
        columnDescription.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        columnDateDebut.setCellValueFactory(cellData -> cellData.getValue().dateDebutProperty());
        columnDateFin.setCellValueFactory(cellData -> cellData.getValue().dateFinProperty());
        columnLieu.setCellValueFactory(cellData -> cellData.getValue().lieuProperty());
        columnFormateur.setCellValueFactory(cellData -> cellData.getValue().formateurProperty());
        columnCout.setCellValueFactory(cellData -> cellData.getValue().coutProperty().asObject());
        columnType.setCellValueFactory(cellData -> cellData.getValue().typeProperty());

        // Load seminars into TableView and handle pagination
        afficherSeminaire();

        // Display seminar statistics
        afficherStatistiques();

        // Pagination event listener

    }




    @FXML
    private void handlechange(MouseEvent event) {
        if (event.getClickCount() == 2) { // Double click detected
            selectedSeminaire = tableViewSeminaire.getSelectionModel().getSelectedItem();
            if (selectedSeminaire != null) {
                // Load the selected seminar's details into input fields for update
                nomField.setText(selectedSeminaire.getNom());
                descriptionField.setText(selectedSeminaire.getDescription());
                dateDebutField.setValue(selectedSeminaire.getDateDebut());
                dateFinField.setValue(selectedSeminaire.getDateFin());
                lieuField.setText(selectedSeminaire.getLieu());
                formateurField.setText(selectedSeminaire.getFormateur());
                coutField.setText(String.valueOf(selectedSeminaire.getCout()));
                typeComboBox.setValue(selectedSeminaire.getType());
            }
        }
    }

    @FXML
    private void ajouterSeminaire() {
        if (!validateFields()) return;

        try {
            Seminaire seminaire = new Seminaire(
                    0,
                    nomField.getText(),
                    descriptionField.getText(),
                    dateDebutField.getValue(),
                    dateFinField.getValue(),
                    lieuField.getText(),
                    formateurField.getText(),
                    Double.parseDouble(coutField.getText()),
                    typeComboBox.getValue()
            );

            SeminaireDAO.ajouterSeminaire(seminaire);
            afficherSeminaire(); // Mise à jour immédiate de la table
            clearFields(); // Nettoyage des champs après ajout
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Séminaire ajouté avec succès !");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le champ 'Coût' doit être un nombre valide.");
        }
    }

    @FXML
    private void handleTableClick(MouseEvent event) {
        if (event.getClickCount() == 2) { // Double click detected
            selectedSeminaire = tableViewSeminaire.getSelectionModel().getSelectedItem();
            if (selectedSeminaire != null) {
                // Load the selected seminar's details into input fields for update
                nomField.setText(selectedSeminaire.getNom());
                descriptionField.setText(selectedSeminaire.getDescription());
                dateDebutField.setValue(selectedSeminaire.getDateDebut());
                dateFinField.setValue(selectedSeminaire.getDateFin());
                lieuField.setText(selectedSeminaire.getLieu());
                formateurField.setText(selectedSeminaire.getFormateur());
                coutField.setText(String.valueOf(selectedSeminaire.getCout()));
                typeComboBox.setValue(selectedSeminaire.getType());
            }
        }
    }

    @FXML
    private void searchSeminaire() {
        String nom = nomField.getText();
        LocalDate dateDebut = dateDebutField.getValue();
        LocalDate dateFin = dateFinField.getValue();

        ObservableList<Seminaire> seminaireList = SeminaireDAO.searchSeminaire(nom, dateDebut, dateFin);
        tableViewSeminaire.setItems(seminaireList);
    }

    @FXML
    private void updateSeminaire() {
        if (selectedSeminaire == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner un séminaire à mettre à jour.");
            return;
        }

        if (!validateFields()) return;

        try {
            selectedSeminaire.setNom(nomField.getText());
            selectedSeminaire.setDescription(descriptionField.getText());
            selectedSeminaire.setDateDebut(dateDebutField.getValue());
            selectedSeminaire.setDateFin(dateFinField.getValue());
            selectedSeminaire.setLieu(lieuField.getText());
            selectedSeminaire.setFormateur(formateurField.getText());
            selectedSeminaire.setCout(Double.parseDouble(coutField.getText()));
            selectedSeminaire.setType(typeComboBox.getValue());

            SeminaireDAO.modifierSeminaire(selectedSeminaire);
            afficherSeminaire(); // Refresh the table
            clearFields(); // Clear input fields
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Séminaire mis à jour avec succès !");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le champ 'Coût' doit être un nombre valide.");
        }
    }






    private void afficherStatistiques() {
        // Example of creating a bar chart with seminar statistics (e.g., seminar types count)
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Type de Séminaire");

        // Example data: Count the number of seminars for each type
        series.getData().add(new XYChart.Data<>("Workshop", 10));
        series.getData().add(new XYChart.Data<>("Conference", 15));
        series.getData().add(new XYChart.Data<>("Webinar", 20));

        seminaireStatsChart.getData().clear();
        seminaireStatsChart.getData().add(series);
    }

    private boolean validateFields() {
        if (nomField.getText().isEmpty() || descriptionField.getText().isEmpty() ||
                dateDebutField.getValue() == null || dateFinField.getValue() == null ||
                lieuField.getText().isEmpty() || formateurField.getText().isEmpty() ||
                coutField.getText().isEmpty() || typeComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Erreur", "Veuillez remplir tous les champs.");
            return false;
        }
        // Check if start date is before end date
        if (dateDebutField.getValue().isAfter(dateFinField.getValue())) {
            showAlert(Alert.AlertType.WARNING, "Erreur", "La date de début ne peut pas être après la date de fin.");
            return false;
        }
        return true;
    }

    private void clearFields() {
        nomField.clear();
        descriptionField.clear();
        dateDebutField.setValue(null);
        dateFinField.setValue(null);
        lieuField.clear();
        formateurField.clear();
        coutField.clear();
        typeComboBox.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
