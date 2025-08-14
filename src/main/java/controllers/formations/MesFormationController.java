package controllers.formations;

import controllers.formations.quiz.FormationQuiz;
import controllers.formations.quiz.FormationQuizResult;
import controllers.formations.quiz.ListeQuizController;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import models.Formation;
import services.FormationService;
import utils.ShowMenu;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class MesFormationController implements Initializable, ShowMenu {

    @FXML
    private AnchorPane menu;

    @FXML
    private VBox vbox;

    FormationService fs = new FormationService();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeMenu(menu);

        vbox.setSpacing(10);

        List<Formation> formations = null;
        try {
            formations = fs.getMesFormation(18);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if(formations.size() == 0){
            Label label = new Label("Vous n'êtes inscrit à aucune formation");
            label.setFont(new Font(20));
            vbox.getChildren().add(label);
        }else{

            for(int i=0;i<formations.size();i++){

                final Formation formation = formations.get(i);

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/formations/components/FormationListItem.fxml"));
                    Parent xmlContent = loader.load();

                    ImageView imageView = (ImageView) loader.getNamespace().get("imageView");
                    imageView.setImage(new Image(formation.getImage()));


                    Label title = (Label) loader.getNamespace().get("title");
                    title.setText(formation.getTitle());

                    Label place = (Label) loader.getNamespace().get("place");
                    if (formation.isIs_online()) {
                        place.setText("En ligne");
                    } else {
                        place.setText(formation.getPlace());
                        place.setOnMouseClicked(event -> {
                            if(formation.getLat() != null & formation.getLat() != null) {
                                Parent root = null;
                                try {
                                    FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/formations/FormationMap.fxml"));
                                    root = loader1.load();
                                    FormationMapController controller = loader1.getController();
                                    controller.setFormation(formation);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                                vbox.getScene().setRoot(root);
                            }else{
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Erreur");
                                alert.setHeaderText("Erreur");
                                alert.setContentText("Aucune coordonnée GPS n'est disponible pour cette formation");
                                alert.showAndWait();
                            }
                        });
                    }

                    Label description = (Label) loader.getNamespace().get("description");
                    title.setText(formation.getDescription());


                    Label formateur = (Label) loader.getNamespace().get("formateur");
                    formateur.setText(formation.getFormateur().getFirstName() + " " + formation.getFormateur().getLastName());

                    Label StarDate = (Label) loader.getNamespace().get("startDate");
                    StarDate.setText(formation.getStart_date().toString());

                    MFXButton inscrireButton = (MFXButton) loader.getNamespace().get("inscrireBtn");

                    if(!formation.isHas_quiz()){
                        inscrireButton.setVisible(false);
                    }

                    if(formation.isHas_quiz() && !fs.userAnsweredTheQuiz(18,formation.getId()) ){
                        inscrireButton.setText("Passer le quiz");
                        inscrireButton.setOnAction(event -> {
                            if (!formation.isHas_quiz()) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Information Dialog");
                                alert.setHeaderText(null);
                                alert.setContentText("Cette formation n'a pas de quiz");
                                alert.showAndWait();
                                return;
                            }
                            Parent root = null;
                            try {
                                FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/formations/quiz/FormationQuiz.fxml"));
                                root = loader1.load();
                                FormationQuiz controller = loader1.getController();
                                controller.setFormation(formation);
                                vbox.getScene().setRoot(root);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                    if(fs.userAnsweredTheQuiz(18,formation.getId()) ){
                        inscrireButton.setText("Resultat du quiz");
                        inscrireButton.setOnAction(event -> {
                            if (!formation.isHas_quiz()) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Information Dialog");
                                alert.setHeaderText(null);
                                alert.setContentText("Cette formation n'a pas de quiz");
                                alert.showAndWait();
                                return;
                            }
                            Parent root = null;
                            try {
                                FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/formations/quiz/FormationQuizResult.fxml"));
                                root = loader1.load();
                                FormationQuizResult controller = loader1.getController();
                                controller.setFormation(formation);
                                vbox.getScene().setRoot(root);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }

                    vbox.getChildren().add(xmlContent);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }
}
