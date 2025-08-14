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
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ResourceBundle;

public class AjouterAbsenceController implements Initializable, ShowMenu {

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
                throw new InvalidInputException("Le justificatif doit être un fichier PDF ou une image (jpg, png)");
            }

            if (justificatif != null && justificatif.endsWith(".pdf")) {
                if (!containsWord(justificatif, "CERTIFICAT MEDICAL")) {
                    throw new InvalidInputException("Le fichier PDF ne contient pas le mot 'CERTIFICAT MEDICAL'");
                }
            }

            if (justificatif != null && (justificatif.endsWith(".jpg") || justificatif.endsWith(".png"))) {
                if (!containsWordInImage(justificatif, "CERTIFICAT MEDICAL")) {
                    throw new InvalidInputException("Le fichier image ne contient pas le mot 'CERTIFICAT MEDICAL'");
                }
            }

            String remarque = remarqueField.getText();

            Absence absence = new Absence();
            absence.setEmployeId(employeId);
            absence.setMotif(motif);
            absence.setJustificatif(justificatif);
            absence.setRemarque(remarque);
            absence.setDateEnregistrement(new Timestamp(System.currentTimeMillis()));

            absenceService.createAbsence(absence);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Absence ajoutée avec succès");
            alert.showAndWait();

            Parent root = new FXMLLoader(getClass().getResource("/Absence/ListeAbsence.fxml")).load();
            employeIdField.getScene().setRoot(root);
        } catch (InvalidInputException e) {
            showErrorAlert(e.getMessage());
        } catch (Exception e) {
            showErrorAlert("Une erreur est survenue");
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

    @FXML
    void onClickCancelBtn(ActionEvent event) {
        try {
            Parent root = new FXMLLoader(getClass().getResource("/Absence/ListeAbsence.fxml")).load();
            employeIdField.getScene().setRoot(root);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean containsWord(String filePath, String wordToFind) {
        try (PDDocument document = PDDocument.load(new File(filePath))) {
            String text = new PDFTextStripper().getText(document);
            return text.toLowerCase().contains(wordToFind.toLowerCase());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean containsWordInImage(String filePath, String wordToFind) {
        Tesseract tesseract = new Tesseract();
        try {
            tesseract.setDatapath("C:\\Users\\bente\\OneDrive\\Bureau\\pi\\hrconnect-javafx\\src\\main\\java\\controllers\\Absence\\tessdata");
            tesseract.setLanguage("fra");
            String text = tesseract.doOCR(new File(filePath));
            return text.toLowerCase().contains(wordToFind.toLowerCase());
        } catch (TesseractException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}