package controllers.Employe;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import models.Employe;
import services.EmployeService;
import utils.ShowMenu;
import utils.TableCell;
import utils.TableRow;
import utils.enums.TableRowType;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ListeEmployeController implements Initializable, ShowMenu {

    @FXML
    private AnchorPane menu;

    EmployeService employeService = new EmployeService();

    @FXML
    private VBox vbox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeMenu(menu);

        TableRow header = new TableRow(TableRowType.HEADER);

        header.addCell(new TableCell("Nom", 200));
        header.addCell(new TableCell("Prénom", 200));
        header.addCell(new TableCell("Solde Congés", 150));
        header.addCell(new TableCell("Action", 200));

        vbox.getChildren().add(header.build());

        try {
            List<Employe> employes = employeService.getAll();

            if (employes.isEmpty()) {
                TableRow row = new TableRow(TableRowType.BODY);
                row.addCell(new TableCell("Aucun employé trouvé", 140));
                vbox.getChildren().add(row.build());
            } else {
                for (int i = 0; i < employes.size(); i++) {
                    int finalI = i;
                    TableRow row = new TableRow(TableRowType.BODY);
                    row.addCell(new TableCell(employes.get(i).getNom(), 200));
                    row.addCell(new TableCell(employes.get(i).getPrenom(), 200));
                    row.addCell(new TableCell(String.valueOf(employes.get(i).getSoldeConges()), 150));

                    row.addAction("EDIT", "table-edit-btn", () -> {
                        Parent root = null;
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Employe/ModifierEmploye.fxml"));
                            root = loader.load();
                            ModifierEmployeController controller = loader.getController();
                            controller.setEmploye(employes.get(finalI));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        vbox.getScene().setRoot(root);
                    });

                    row.addAction("TRASH", "table-delete-btn", () -> {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Confirmation de suppression");
                        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cet employé ?");
                        alert.setContentText("Cette action est irréversible.");

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.get() == ButtonType.OK) {
                            try {
                                employeService.delete(employes.get(finalI));
                                vbox.getChildren().clear();
                                initialize(location, resources);
                            } catch (Exception e) {
                                System.out.println(e);
                            }
                        }
                    });

                    vbox.getChildren().add(row.build());
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void AjouterEmploye() {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/Employe/AjouterEmploye.fxml"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        vbox.getScene().setRoot(root);
    }
}