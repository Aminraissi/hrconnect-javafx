package controllers.formations;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import models.Employe;
import models.Formation;
import services.FormationService;
import utils.ShowMenu;
import utils.TableCell;
import utils.TableRow;
import utils.enums.TableRowType;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ListeParticipantsController implements Initializable, ShowMenu {

    @FXML
    private AnchorPane menu;

    @FXML
    private VBox vbox;

    Formation formation;

    FormationService fs = new FormationService();

    @FXML
    private Text title;

    public void initialize(URL location, ResourceBundle resources) {
        initializeMenu(menu);

        if(formation == null) return;

        TableRow header = new TableRow(TableRowType.HEADER);
        header.addCell(new TableCell("ID",50));
        header.addCell(new TableCell("Nom", 150));
        header.addCell(new TableCell("Prenom", 150));
        header.addCell(new TableCell("Email", 150));

        vbox.getChildren().add(header.build());

        try {
            List<Employe> employes = fs.getFormationParticipants(formation.getId());

            title.setText("Liste des participants à la formation " + formation.getTitle());

            if(employes.isEmpty()) {
                TableRow row = new TableRow(TableRowType.BODY);
                row.addCell(new TableCell("Aucun participant", 100));
                vbox.getChildren().add(row.build());
            }else{
                for (Employe employe : employes) {
                    TableRow row = new TableRow(TableRowType.BODY);
                    row.addCell(new TableCell(String.valueOf(employe.getId()), 50));
                    row.addCell(new TableCell(employe.getNom(), 150));
                    row.addCell(new TableCell(employe.getPrenom(), 150));
                    row.addCell(new TableCell(String.valueOf(employe.getEmail()), 150));
                    vbox.getChildren().add(row.build());
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setFormation(Formation formation) throws SQLException {
        this.formation = formation;
        initialize(null, null);
    }

    @FXML
    void redirectToContacterParticipants() {
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/formations/ContacterParticipants.fxml"));
            root = loader.load();
            ContacterParticipantsController controller = loader.getController();
            controller.setFormation(formation);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        menu.getScene().setRoot(root);
    }

}
