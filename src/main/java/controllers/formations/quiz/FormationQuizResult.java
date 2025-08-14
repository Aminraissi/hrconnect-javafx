package controllers.formations.quiz;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.utils.SwingFXUtils;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import models.Formation;
import models.Quiz;
import models.QuizReponse;
import okhttp3.*;
import services.FormationService;
import services.QuizService;
import utils.ConfigReader;
import utils.ShowMenu;
import utils.enums.QuizType;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.imageio.ImageIO;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FormationQuizResult implements Initializable, ShowMenu {

    @FXML
    private AnchorPane menu;

    @FXML
    private VBox vbox;

    QuizService qs = new QuizService();

    Formation formation;

    int currentQuizIndex = 0;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeMenu(menu);

        vbox.setSpacing(20);

        if(formation != null){
            try {
                List<Quiz> quizzes = qs.getAll(formation.getId());
                List<QuizReponse> reponses = qs.getUserReponses(formation.getId(),18);

                if(currentQuizIndex == quizzes.size()){
                    Label label = new Label("Merci !");
                    label.setFont(new Font(20));
                    vbox.getChildren().add(label);

                    vbox.setAlignment(javafx.geometry.Pos.CENTER);
                }else{

                    Quiz quiz = quizzes.get(currentQuizIndex);

                    Label question = new Label(quiz.getQuestion());
                    question.setFont(new Font("Arial", 20));
                    vbox.getChildren().add(question);

                    ToggleGroup group = new ToggleGroup();
                    RadioButton reponse1 = new RadioButton(quiz.getReponse1());
                    reponse1.setFont(new Font("Arial", 15));
                    reponse1.setToggleGroup(group);
                    vbox.getChildren().add(reponse1);
                    RadioButton reponse2 = new RadioButton(quiz.getReponse2());
                    reponse2.setFont(new Font("Arial", 15));
                    reponse2.setToggleGroup(group);
                    vbox.getChildren().add(reponse2);
                    RadioButton reponse3 = new RadioButton(quiz.getReponse3());
                    reponse3.setFont(new Font("Arial", 15));
                    reponse3.setToggleGroup(group);
                    vbox.getChildren().add(reponse3);


                    String correctRep;

                    if(quiz.getNumRepCorrect() == 1){
                        reponse1.setStyle("-fx-background-color: green;");
                        correctRep = quiz.getReponse1();
                    }else if(quiz.getNumRepCorrect() == 2) {
                        reponse2.setStyle("-fx-background-color: green;");
                        correctRep = quiz.getReponse2();
                    }else{
                        reponse3.setStyle("-fx-background-color: green;");
                        correctRep = quiz.getReponse3();
                    }

                    // cocher les reponses du l'utilisateur
                    if (reponses.stream().anyMatch(r -> r.getQuiz_id() == quiz.getId())) {
                        QuizReponse reponse = reponses.stream().filter(r -> r.getQuiz_id() == quiz.getId()).findFirst().get();
                        if (reponse.getNum_reponse() == 1) {
                            reponse1.setSelected(true);
                        } else if (reponse.getNum_reponse() == 2) {
                            reponse2.setSelected(true);
                        } else if(reponse.getNum_reponse() == 3) {
                            reponse3.setSelected(true);
                        }
                    }


                    Text text = new Text(GeminiApi(quiz.getQuestion(),correctRep));
                    text.setWrappingWidth(400);
                    vbox.getChildren().add(text);


                    MFXButton next = new MFXButton("Suivant");
                    next.setMinWidth(200);
                    next.getStyleClass().add("save-btn");
                    vbox.getChildren().add(next);

                    next.setOnAction(event -> {
                        currentQuizIndex++;
                        vbox.getChildren().clear();
                        initialize(null,null);
                    });

                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }



    public void setFormation(Formation formation) throws SQLException {
        this.formation = formation;
        initialize(null,null);
    }

    private String GeminiApi(String quizTitle,String reponse) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key="
                + ConfigReader.get("HAYTHEM_GEMINI_API");

        // Creating the JSON request using Gson
        Gson gson = new Gson();

        // Constructing the request body
        JsonObject messagePart = new JsonObject();
        messagePart.addProperty("text", "Expliquer la question suivtante  : " + quizTitle + " et pour quoi la reponse correcte est "  + reponse + " repondre en 3 ou 4 lignes");

        JsonObject contentPart = new JsonObject();
        contentPart.add("parts", gson.toJsonTree(Collections.singletonList(messagePart)));

        JsonObject requestBody = new JsonObject();
        requestBody.add("contents", gson.toJsonTree(Collections.singletonList(contentPart)));

        try {
            // Convert request body to JSON string
            String jsonPayload = gson.toJson(requestBody);

            // Create request body
            RequestBody body = RequestBody.create(jsonPayload, MediaType.get("application/json; charset=utf-8"));

            // Build request
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            // Execute request
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    // Parse JSON response
                    String responseBody = response.body().string();
                    JsonElement rootNode = JsonParser.parseString(responseBody);

                    // Extract the corrected text
                    JsonObject firstCandidate = rootNode.getAsJsonObject()
                            .getAsJsonArray("candidates")
                            .get(0)
                            .getAsJsonObject();

                    JsonObject content = firstCandidate.getAsJsonObject("content");
                    String correctedText = content.getAsJsonArray("parts")
                            .get(0)
                            .getAsJsonObject()
                            .get("text")
                            .getAsString();

                   return correctedText;
                } else {
                    System.out.println("Request failed: " + response.code());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}
