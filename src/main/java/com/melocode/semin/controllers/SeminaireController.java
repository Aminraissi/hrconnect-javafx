package com.melocode.semin.controllers;

import com.melocode.semin.dao.SeminaireDAO;
import com.melocode.semin.models.Seminaire;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class SeminaireController {

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

    @FXML
    private Button showCalendarBtn;
    @FXML
    private AnchorPane calendarContainer;

    private WebView webView;
    private WebEngine webEngine;
    private Stage calendarStage;
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
        webView = new WebView();
        webEngine = webView.getEngine();
        URL url = getClass().getResource("/calendar.html");  // ✅ Correct
        if (url == null) {
            System.err.println("Error: Could not find calendar.html");
            return;
        }

        webEngine.load(url.toExternalForm());
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                Platform.runLater(() -> {
                    try {
                        // Expose Java method to JavaScript
                        JSObject window = (JSObject) webEngine.executeScript("window");
                        window.setMember("javaApp", this);
                        System.out.println("✅ JavaScript can now call Java methods.");
                        loadSeminarsToCalendar();
                    } catch (Exception e) {
                        System.err.println("Error exposing Java methods to JavaScript: " + e.getMessage());
                    }
                });
            }
        });

    }
    @FXML
    private void showCalendar() {
        if (calendarStage == null) {
            calendarStage = new Stage();
            calendarStage.setTitle("Calendrier des Séminaires");

            // Debug WebEngine errors
            webEngine.setOnError(event -> System.err.println("WebEngine error: " + event.getMessage()));

            // Add an event listener to check when loading finishes
            webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    System.out.println("✅ Calendar HTML loaded successfully.");
                    loadSeminarsToCalendar(); // Now we can call this safely
                } else {
                    System.out.println("⚠ WebEngine state changed: " + newState);
                }
            });

            Scene scene = new Scene(webView, 900, 600);
            Platform.runLater(() -> {
                calendarStage.setScene(scene);
                calendarStage.initModality(Modality.APPLICATION_MODAL);
                calendarStage.show();
            });
        } else {
            calendarStage.show();
        }
    }


    public void addSeminar(String dateStr) {
        Platform.runLater(() -> {
            Dialog<Seminaire> dialog = new Dialog<>();
            dialog.setTitle("Ajouter un Séminaire");
            dialog.setHeaderText("Ajouter un séminaire pour la date: " + dateStr);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);

            TextField nomField = new TextField();
            TextArea descriptionField = new TextArea();
            DatePicker dateDebutField = new DatePicker(LocalDate.parse(dateStr));
            DatePicker dateFinField = new DatePicker();
            TextField lieuField = new TextField();
            TextField formateurField = new TextField();
            TextField coutField = new TextField();

            ComboBox<String> typeComboBox = new ComboBox<>();
            typeComboBox.setItems(FXCollections.observableArrayList("Workshop", "Conference", "Webinar", "Training", "Seminar"));
            typeComboBox.getSelectionModel().selectFirst();  // Default selection

            grid.add(new Label("Nom:"), 0, 0);
            grid.add(nomField, 1, 0);
            grid.add(new Label("Description:"), 0, 1);
            grid.add(descriptionField, 1, 1);
            grid.add(new Label("Date Début:"), 0, 2);
            grid.add(dateDebutField, 1, 2);
            grid.add(new Label("Date Fin:"), 0, 3);
            grid.add(dateFinField, 1, 3);
            grid.add(new Label("Lieu:"), 0, 4);
            grid.add(lieuField, 1, 4);
            grid.add(new Label("Formateur:"), 0, 5);
            grid.add(formateurField, 1, 5);
            grid.add(new Label("Coût (€):"), 0, 6);
            grid.add(coutField, 1, 6);
            grid.add(new Label("Type:"), 0, 7);
            grid.add(typeComboBox, 1, 7);

            dialog.getDialogPane().setContent(grid);

            ButtonType addButton = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

            dialog.setResultConverter(buttonType -> {
                if (buttonType == addButton) {
                    if (nomField.getText().trim().isEmpty() || descriptionField.getText().trim().isEmpty() ||
                            dateDebutField.getValue() == null || dateFinField.getValue() == null ||
                            lieuField.getText().trim().isEmpty() || formateurField.getText().trim().isEmpty() ||
                            coutField.getText().trim().isEmpty() || typeComboBox.getValue() == null) {
                        showAlert(Alert.AlertType.WARNING, "Erreur", "Veuillez remplir tous les champs.");
                        return null;
                    }

                    if (dateDebutField.getValue().isAfter(dateFinField.getValue())) {
                        showAlert(Alert.AlertType.WARNING, "Erreur", "La date de début ne peut pas être après la date de fin.");
                        return null;
                    }

                    return new Seminaire(
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
                }
                return null;
            });

            dialog.showAndWait().ifPresent(seminaire -> {
                SeminaireDAO.ajouterSeminaire(seminaire);
                loadSeminarsToCalendar();
                System.out.println("✅ Seminar added successfully for " + dateStr);
            });
        });
    }

    private void loadSeminarsToCalendar() {
        List<Seminaire> seminars = SeminaireDAO.recupererTousLesSeminaires();

        String jsonData = seminars.stream()
                .map(s -> String.format("{\"title\": \"%s\", \"start\": \"%s\", \"end\": \"%s\"}",
                        escapeJson(s.getNom()), s.getDateDebut(), s.getDateFin()))
                .collect(Collectors.joining(",", "[", "]"));

        // Debug JSON output
        System.out.println("✅ JSON Data Sent to JS: " + jsonData);

        Platform.runLater(() -> {
            try {
                webEngine.executeScript("loadSeminars('" + jsonData.replace("'", "\\'") + "');");
            } catch (Exception e) {
                System.err.println("Error executing JavaScript function: " + e.getMessage());
            }
        });
    }

    /**
     * Escape special JSON characters in Java.
     */
    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
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
