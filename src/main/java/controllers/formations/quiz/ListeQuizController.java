package controllers.formations.quiz;

import controllers.formations.ModifierFormationController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import models.Formateur;
import models.Formation;
import models.Quiz;
import services.QuizService;
import utils.ShowMenu;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.control.Button;
import utils.TableCell;
import utils.TableRow;
import utils.enums.QuizType;
import utils.enums.TableRowType;


public class ListeQuizController implements Initializable,ShowMenu {

    @FXML
    private AnchorPane menu;

    @FXML
    private Text foamtionTitle;

    @FXML
    private Button ajouterQuiz;

    @FXML
    private VBox vbox;


    private Formation formation;

    QuizService qz = new QuizService();


    @FXML
    void ajouterQuestion() {
        System.out.println("hello");
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/formations/quiz/AjouterQuiz.fxml"));
            root = loader.load();
            AjouterQuizController controller = loader.getController();
            controller.setFormation(formation);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ajouterQuiz.getScene().setRoot(root);
    }

    public void initialize(URL location, ResourceBundle resources) {
        initializeMenu(menu);

        if(formation != null) {
            TableRow header = new TableRow(TableRowType.HEADER);
            header.addCell(new TableCell("ID", 50));
            header.addCell(new TableCell("Question", 300));
            header.addCell(new TableCell("Response 1", 150));
            header.addCell(new TableCell("Response 2", 150));
            header.addCell(new TableCell("Response 3", 150));
            header.addCell(new TableCell("?", 150));


            vbox.getChildren().add(header.build());

            try {
                List<Quiz> quizs = qz.getAll(formation.getId());

                if (quizs.isEmpty()) {
                    TableRow row = new TableRow(TableRowType.BODY);
                    row.addCell(new TableCell("Aucun question trouvée", 140));
                    vbox.getChildren().add(row.build());
                } else {
                    for (int i = 0; i < quizs.size(); i++) {
                        int finalI = i;
                        TableRow row = new TableRow(TableRowType.BODY);

                        row.addCell(new TableCell(String.valueOf(quizs.get(i).getId()), 50));
                        row.addCell(new TableCell(String.valueOf(quizs.get(i).getQuestion()), 300));

                        row.addCell(new TableCell(String.valueOf(quizs.get(i).getReponse1()), 150));
                        row.addCell(new TableCell(String.valueOf(quizs.get(i).getReponse2()), 150));
                        row.addCell(new TableCell(String.valueOf(quizs.get(i).getReponse3()), 150));


                        row.addAction("EDIT", "table-edit-btn", () -> {
                            Parent root = null;
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/formations/quiz/ModifierQuiz.fxml"));
                                root = loader.load();
                                ModifierQuizController controller = loader.getController();
                                controller.setData(formation, quizs.get(finalI));
                            } catch (IOException | SQLException e) {
                                throw new RuntimeException(e);
                            }
                            vbox.getScene().setRoot(root);
                        });

                        row.addAction("TRASH", "table-delete-btn", () -> {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Confirmation de suppression");
                            alert.setHeaderText("Êtes-vous sûr de vouloir supprimer la question ?");
                            alert.setContentText("Cette action est irréversible.");

                            Optional<ButtonType> result = alert.showAndWait();
                            if (result.get() == ButtonType.OK) {
                                try {
                                    qz.delete(quizs.get(finalI).getId());
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
    }

    public void setFormation(Formation formation) throws SQLException {
        this.formation = formation;

        foamtionTitle.setText(this.formation.getTitle());

        initialize(null,null);
    }



}
