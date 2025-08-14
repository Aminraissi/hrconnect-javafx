package controllers.Absence;

import exceptions.InvalidInputException;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import models.Absence;
import services.AbsenceService;
import utils.ShowMenu;

import java.io.File;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ResourceBundle;

public class ModifierAbsenceController implements Initializable, ShowMenu {

    private Absence absence;

    @FXML
    private AnchorPane menu;

    @FXML
    private MFXButton cancelBtn;

    @FXML
    private MFXTextField employeIdField;

    @FXML
    private MFXComboBox<Absence.Motif> motifField;

    @FXML
    private MFXTextField justificatifField;

    @FXML
    private TextArea remarqueField;

    @FXML
    private MFXButton saveBtn;

    AbsenceService absenceService = new AbsenceService();

    @FXML
    void onSave(ActionEvent event) {
        try {
            if (motifField.getValue() == null) {
                throw new InvalidInputException("Le motif est requis");
            }

            int employeId = Integer.parseInt(employeIdField.getText());
            Absence.Motif motif = motifField.getValue();
            String justificatif = justificatifField.getText();
            if (justificatif != null && !(justificatif.endsWith(".pdf") || justificatif.endsWith(".jpg") || justificatif.endsWith(".png"))) {
                throw new InvalidInputException("Justificatif must be a PDF or an image file (jpg, png)");
            }
            String remarque = remarqueField.getText();

            absence.setEmployeId(employeId);
            absence.setMotif(motif);
            absence.setJustificatif(justificatif);
            absence.setRemarque(remarque);
            absence.setDateEnregistrement(new Timestamp(System.currentTimeMillis()));

            absenceService.updateAbsence(absence);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Absence modifiée avec succès");
            alert.showAndWait();

            // redirect to list
            Parent root = null;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Absence/ListeAbsence.fxml"));
                root = loader.load();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            employeIdField.getScene().setRoot(root);

        } catch (InvalidInputException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("An error has occurred");
            alert.showAndWait();
        }
    }

    @FXML
    void onUploadJustificatif(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            justificatifField.setText(selectedFile.getAbsolutePath());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeMenu(menu);

        motifField.getItems().addAll(Absence.Motif.values());
    }

    public void setAbsence(Absence absence) {
        this.absence = absence;

        // Initialize the form with the absence data
        employeIdField.setText(String.valueOf(absence.getEmployeId()));
        motifField.setValue(absence.getMotif());
        justificatifField.setText(absence.getJustificatif());
        remarqueField.setText(absence.getRemarque());
    }

    @FXML
    void onClickCancelBtn(ActionEvent event) {
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Absence/ListeAbsence.fxml"));
            root = loader.load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        employeIdField.getScene().setRoot(root);
    }
}