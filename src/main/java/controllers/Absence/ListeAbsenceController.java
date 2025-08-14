package controllers.Absence;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import models.Absence;
import models.Employe;
import services.AbsenceService;
import services.EmployeService;
import utils.ShowMenu;
import utils.TableCell;
import utils.TableRow;
import utils.enums.TableRowType;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ListeAbsenceController implements Initializable, ShowMenu {

    @FXML
    private AnchorPane menu;

    @FXML
    private VBox vbox;

    AbsenceService absenceService = new AbsenceService();
    EmployeService employeService = new EmployeService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeMenu(menu);

        TableRow header = new TableRow(TableRowType.HEADER);

        header.addCell(new TableCell("Nom", 140));
        header.addCell(new TableCell("Prénom", 140));
        header.addCell(new TableCell("Motif", 140));
        header.addCell(new TableCell("Justificatif", 140));
        header.addCell(new TableCell("Remarque", 200));
        header.addCell(new TableCell("Date Enregistrement", 140));
        header.addCell(new TableCell("Action", 250));

        vbox.getChildren().add(header.build());

        try {
            List<Absence> absences = absenceService.getAllAbsences();

            if (absences.isEmpty()) {
                TableRow row = new TableRow(TableRowType.BODY);
                row.addCell(new TableCell("Aucune absence trouvée", 140));
                vbox.getChildren().add(row.build());
            } else {
                for (int i = 0; i < absences.size(); i++) {
                    int finalI = i;
                    Absence absence = absences.get(i);
                    Employe employe = employeService.getEmployeById(absence.getEmployeId());

                    TableRow row = new TableRow(TableRowType.BODY);
                    row.addCell(new TableCell(employe.getNom(), 140));
                    row.addCell(new TableCell(employe.getPrenom(), 140));
                    row.addCell(new TableCell(absence.getMotif().name(), 140));
                    row.addCell(new TableCell(absence.getJustificatif(), 140));
                    row.addCell(new TableCell(absence.getRemarque(), 200));
                    row.addCell(new TableCell(absence.getDateEnregistrement().toString(), 250));

                    row.addAction("EDIT", "table-edit-btn", () -> {
                        Parent root = null;
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Absence/ModifierAbsence.fxml"));
                            root = loader.load();
                            ModifierAbsenceController controller = loader.getController();
                            controller.setAbsence(absences.get(finalI));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        vbox.getScene().setRoot(root);
                    });

                    row.addAction("TRASH", "table-delete-btn", () -> {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Confirmation de suppression");
                        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette absence ?");
                        alert.setContentText("Cette action est irréversible.");

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.get() == ButtonType.OK) {
                            try {
                                absenceService.deleteAbsence(absences.get(finalI).getId());
                                vbox.getChildren().clear();
                                initialize(location, resources);
                            } catch (SQLException e) {
                                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                                errorAlert.setTitle("Erreur");
                                errorAlert.setHeaderText(null);
                                errorAlert.setContentText("Une erreur s'est produite lors de la suppression de l'absence.");
                                errorAlert.showAndWait();
                            }
                        }
                    });

                    vbox.getChildren().add(row.build());
                }
            }

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur s'est produite lors du chargement des absences.");
            alert.showAndWait();
        }
    }

    @FXML
    void onAdd() {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/Absence/AjouterAbsence.fxml"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        vbox.getScene().setRoot(root);
    }

    @FXML
    void onShowStats() {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/Absence/stats_absence.fxml"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        vbox.getScene().setRoot(root);
    }
}