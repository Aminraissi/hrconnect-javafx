package controllers.formations.quiz;

import exceptions.InvalidInputException;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXRadioButton;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import models.Formation;
import models.Quiz;
import services.QuizService;
import utils.ShowMenu;
import utils.enums.QuizType;

import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;

public class AjouterQuizController implements Initializable, ShowMenu  {

    QuizService qs = new QuizService();

    @FXML
    private MFXButton cancelBtn;

    @FXML
    private AnchorPane menu;

    @FXML
    private MFXButton saveBtn;

    @FXML
    private MFXTextField reponse1;

    @FXML
    private MFXTextField reponse2;

    @FXML
    private MFXTextField reponse3;

    @FXML
    private MFXTextField title;

    @FXML
    private MFXToggleButton isRep1Correct;

    @FXML
    private MFXToggleButton isRep2Correct;

    @FXML
    private MFXToggleButton isRep3Correct;


    @FXML
    void OnClickCancelBtn() {
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/formations/quiz/ListeQuiz.fxml"));
            root = loader.load();
            ListeQuizController controller = loader.getController();
            controller.setFormation(formation);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        cancelBtn.getScene().setRoot(root);
    }

    @FXML
    void onSave() {
        try {
            if (title.getText().isEmpty()) {
                throw new InvalidInputException("Le titre est requis");
            } else if (reponse1.getText().isEmpty() || reponse2.getText().isEmpty() || reponse3.getText().isEmpty()) {
                throw new InvalidInputException("Ajouter 3 reponses");
            }else if(reponse1.getText().equals(reponse2.getText()) || reponse1.getText().equals(reponse3.getText()) || reponse2.getText().equals(reponse3.getText())){
                throw new InvalidInputException("Les réponses ne peuvent pas être identiques");
            }else if(!isRep1Correct.isSelected() && !isRep2Correct.isSelected() && !isRep3Correct.isSelected()){
                throw new InvalidInputException("Choisissez la bonne réponse");
            }

            Quiz quiz = new Quiz();
            quiz.setFormation_id(formation.getId());
            quiz.setQuestion(title.getText());
            quiz.setReponse1(reponse1.getText());
            quiz.setReponse2(reponse2.getText());
            quiz.setReponse3(reponse3.getText());

            int numRepCorrect = 0;
            if (isRep1Correct.isSelected()) {
                numRepCorrect = 1;
            } else if (isRep2Correct.isSelected()) {
                numRepCorrect = 2;
            } else if (isRep3Correct.isSelected()) {
                numRepCorrect = 3;
            }

            quiz.setNumRepCorrect(numRepCorrect);

            qs.create(quiz);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Quiz ajouté avec succès");
            alert.showAndWait();

            //redirect to list
            Parent root = null;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/formations/quiz/ListeQuiz.fxml"));
                root = loader.load();
                ListeQuizController controller = loader.getController();
                controller.setFormation(formation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            cancelBtn.getScene().setRoot(root);

        } catch (InvalidInputException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } catch (Exception e) {
            System.out.println(e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("An error has occured");
            alert.showAndWait();
        }

    }


    private Formation formation;

    public void initialize(URL location, ResourceBundle resources) {
        initializeMenu(menu);
        isRep1Correct.setOnAction(e -> {
            if (isRep1Correct.isSelected()) {
                isRep2Correct.setSelected(false);
                isRep3Correct.setSelected(false);
            }
        });
        isRep2Correct.setOnAction(e -> {
            if (isRep2Correct.isSelected()) {
                isRep1Correct.setSelected(false);
                isRep3Correct.setSelected(false);
            }
        });
        isRep3Correct.setOnAction(e -> {
            if (isRep3Correct.isSelected()) {
                isRep1Correct.setSelected(false);
                isRep2Correct.setSelected(false);
            }
        });
    }

    public void setFormation(Formation formation) throws SQLException {
        this.formation = formation;
    }

}
