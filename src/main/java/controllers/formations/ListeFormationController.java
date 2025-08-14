package controllers.formations;

import controllers.formations.quiz.ListeQuizController;
import io.github.palexdev.materialfx.utils.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import models.Formation;
import services.FormationService;
import utils.ShowMenu;
import utils.TableCell;
import utils.TableRow;
import utils.enums.TableRowType;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;


import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import javafx.scene.SnapshotParameters;


public class ListeFormationController implements Initializable, ShowMenu {

    @FXML
    private AnchorPane menu;

    FormationService fs = new FormationService();

    @FXML
    private VBox vbox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeMenu(menu);

        TableRow header = new TableRow(TableRowType.HEADER);
        header.addCell(new TableCell("ID",50));
        header.addCell(new TableCell("Titre",250));
        header.addCell(new TableCell("Lieu", 80));
        header.addCell(new TableCell("Disponible pour", 140));
        header.addCell(new TableCell("Prix", 80));
        header.addCell(new TableCell("Date de début", 140));
        header.addCell(new TableCell("Date de fin", 140));
        header.addCell(new TableCell("Participants", 100));
        header.addCell(new TableCell("?", 140));


        vbox.getChildren().add(header.build());

        try {
            List<Formation> formations = fs.getAll();

            if(formations.isEmpty()){
                TableRow row = new TableRow(TableRowType.BODY);
                row.addCell(new TableCell("Aucune formation trouvée", 140));
                vbox.getChildren().add(row.build());
            }else{
                for(int i = 0;i<formations.size();i++) {
                    int finalI = i;
                    TableRow row = new TableRow(TableRowType.BODY);

                    row.addCell(new TableCell(String.valueOf(formations.get(i).getId()),50));
                    row.addCell(new TableCell(String.valueOf(formations.get(i).getTitle()),250));
                    row.addCell(new TableCell(formations.get(i).isIs_online() ? "En ligne" : formations.get(i).getPlace(),80));

                    String dispoPour = "";
                    if(formations.get(i).isAvailable_for_employee() && formations.get(i).isAvailable_for_intern()){
                        dispoPour = "Employés/Stagaires";
                    }else if(formations.get(i).isAvailable_for_employee()){
                        dispoPour = "Employes";
                    }else if(formations.get(i).isAvailable_for_intern()){
                        dispoPour = "Stagaires";
                    }
                    row.addCell(new TableCell(dispoPour,140));
                    row.addCell(new TableCell(formations.get(i).isFree() ? "Gratuit" : String.valueOf(formations.get(i).getPrice() + " DT"),80));
                    row.addCell(new TableCell(String.valueOf(formations.get(i).getStart_date()),140));
                    row.addCell(new TableCell(String.valueOf(formations.get(i).getEnd_date()),140));
                    row.addCell(new TableCell(String.valueOf(formations.get(i).getNb_participant()),100));

                    row.addAction("USER","table-show-btn",()->{
                        Parent root = null;
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/formations/ListeParticipants.fxml"));
                            root = loader.load();
                            ListeParticipantsController controller = loader.getController();
                            controller.setFormation(formations.get(finalI));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        vbox.getScene().setRoot(root);
                    });


                    row.addAction("QUESTION","table-show-btn",()->{
                        Parent root = null;
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/formations/quiz/ListeQuiz.fxml"));
                            root = loader.load();
                            ListeQuizController controller = loader.getController();
                            controller.setFormation(formations.get(finalI));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        vbox.getScene().setRoot(root);
                    });

                    row.addAction("EDIT","table-edit-btn",()->{
                        Parent root = null;
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/formations/ModifierFormation.fxml"));
                            root = loader.load();
                            ModifierFormationController controller = loader.getController();
                            controller.setFormation(formations.get(finalI));
                        } catch (IOException | SQLException e) {
                            throw new RuntimeException(e);
                        }
                        vbox.getScene().setRoot(root);
                    });

                    row.addAction("TRASH","table-delete-btn",()->{
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Confirmation de suppression");
                        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette formation ?");
                        alert.setContentText("Cette action est irréversible.");

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.get() == ButtonType.OK){
                            try{
                                fs.delete(formations.get(finalI).getId());
                                vbox.getChildren().clear();
                                initialize(location,resources);
                            }catch (Exception e){
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
    void AjouterFormation() {
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/formations/AjouterFormation.fxml"));
            root = loader.load();
           } catch (Exception e) {
            throw new RuntimeException(e);
        }
        vbox.getScene().setRoot(root);
    }
}
