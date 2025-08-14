package controllers.formations.quiz;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.utils.SwingFXUtils;
import jakarta.activation.DataHandler;
import jakarta.mail.*;
import jakarta.mail.internet.*;
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
import models.Formation;
import models.Quiz;
import models.Utilisateur;
import services.FormationService;
import services.QuizService;
import utils.ConfigReader;
import utils.SessionManager;
import utils.ShowMenu;
import utils.enums.QuizType;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

public class FormationQuiz implements Initializable, ShowMenu {

    @FXML
    private AnchorPane menu;

    @FXML
    private VBox vbox;

    QuizService qs = new QuizService();

    Formation formation;

    int currentQuizIndex = 0;

    List<Boolean> reponsesResultats = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeMenu(menu);

        vbox.setSpacing(20);

        if(formation != null){
            try {
                List<Quiz> quizzes = qs.getAll(formation.getId());
                if(currentQuizIndex == quizzes.size()){
                    Label label = new Label("Vous avez terminé le quiz");
                    label.setFont(new Font(20));
                    vbox.getChildren().add(label);

                    int score = 0;
                    for (Boolean reponseResultat : reponsesResultats) {
                        if(reponseResultat){
                            score++;
                        }
                    }

                    boolean passed = score >= (quizzes.size() +1) / 2;


                    FontAwesomeIconView icon = new FontAwesomeIconView();
                    icon.setGlyphName(passed ? FontAwesomeIcon.CHECK_CIRCLE.name() : FontAwesomeIcon.TIMES_CIRCLE.name());
                    icon.setSize("200");
                    icon.setFill(passed ? javafx.scene.paint.Color.GREEN : javafx.scene.paint.Color.RED);

                    vbox.getChildren().add(icon);

                    Label scoreLabel = new Label("Votre score est : " + score + "/" + quizzes.size());
                    scoreLabel.setFont(new Font(40));


                    if(passed) {
                        scoreLabel.setStyle("-fx-text-fill: green");
                    }else{
                        scoreLabel.setStyle("-fx-text-fill: red");
                    }

                    vbox.getChildren().add(scoreLabel);

                    vbox.setAlignment(javafx.geometry.Pos.CENTER);

                    if(passed){
                        generateAttestation();
                    }


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


                    MFXButton next = new MFXButton("Suivant");
                    next.setMinWidth(200);
                    next.getStyleClass().add("save-btn");
                    vbox.getChildren().add(next);

                    next.setOnAction(event -> {

                        Toggle selectedToggle = group.getSelectedToggle();

                        RadioButton selectedRadioButton = (RadioButton) selectedToggle;

                        String selected = selectedRadioButton.getText();

                        int numReponseChoisie = 0;
                        if(selected.equals(quiz.getReponse1())) {
                            numReponseChoisie = 1;
                        }else if(selected.equals(quiz.getReponse2())) {
                            numReponseChoisie = 2;
                        }else{
                            numReponseChoisie = 3;
                        }

                        if(numReponseChoisie == quiz.getNumRepCorrect()) {
                            reponsesResultats.add(true);
                        }else{
                            reponsesResultats.add(false);
                        }

                        try {
                            qs.enregisterReponse(18, quiz.getId(), numReponseChoisie);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

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

    private void generateAttestation(){

        Image image = new Image(getClass().getResource("/images/attestation.jpg").toExternalForm());

        // Create a canvas with the same size as the image
        Canvas canvas = new Canvas(image.getWidth(), image.getHeight());
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.drawImage(image, 0, 0);

        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Arial", 30));
        Utilisateur currentUser = SessionManager.getInstance().getCurrentUser();
        gc.fillText(currentUser.getNom() + " " + currentUser.getPrenom(), 290, 310);


        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Arial", 30));
        gc.fillText(formation.getTitle(), 295, 370);


        gc.setFill(Color.BLACK); // Text color
        gc.setFont(new Font("Arial", 12));
        gc.fillText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 155, 442);


        WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(new SnapshotParameters(), writableImage);

        File file = new File("attestations/output.png");
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
            System.out.println("Image saved as: " + file.getAbsolutePath());
            envoyerAttestationByMail();
        } catch (IOException e) {
            System.err.println("Failed to save image: " + e.getMessage());
        }
    }


    private void envoyerAttestationByMail(){

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");  // Enable STARTTLS
        properties.put("mail.smtp.host", ConfigReader.get("HAYTHEM_STMP_HOST"));
        properties.put("mail.smtp.port", ConfigReader.get("HAYTHEM_STMP_PORT"));

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(ConfigReader.get("HAYTHEM_STMP_FROM"), ConfigReader.get("HAYTHEM_STMP_PASSWORD"));
            }
        });

        try{
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(ConfigReader.get("HAYTHEM_STMP_FROM")));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("haithemdridiweb@gmail.com"));
            message.setSubject("Attestation de réussite de la formation " + formation.getTitle());
            Multipart multipart = new MimeMultipart();
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText("Bonjour, veuillez trouver ci-joint votre attestation de réussite de la formation " + formation.getTitle());

            MimeBodyPart attachmentPart = new MimeBodyPart();
            DataSource source = new FileDataSource("attestations/output.png");
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName("attestation.jpg");
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(attachmentPart);


            message.setContent(multipart);
            Transport.send(message);
        }catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void setFormation(Formation formation) throws SQLException {
        this.formation = formation;
        initialize(null,null);
    }
}
