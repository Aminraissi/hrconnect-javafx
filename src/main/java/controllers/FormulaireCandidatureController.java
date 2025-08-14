package controllers;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.Candidat;
import models.Candidature;
import models.OffreEmploi;
import services.CandidatService;
import services.CandidatureService;
import services.OffreEmploiService;
import utils.ConfigReader;
import utils.ShowMenu;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class FormulaireCandidatureController implements ShowMenu {

    @FXML
    private TextField tnom;

    @FXML
    private TextField tprenom;

    @FXML
    private TextField temail;

    @FXML
    private TextField ttelephone;

    @FXML
    private TextArea tcv;

    @FXML
    private Button btnSoumettre;

    @FXML
    private Button btnAnnuler;

    @FXML
    private Label hrConnectTitle;

    @FXML
    private AnchorPane menu;

    private CandidatureService candidatureService = new CandidatureService();
    private CandidatService candidatService = new CandidatService();
    private OffreEmploiService offreEmploiService = new OffreEmploiService();
    private File fichierCV;
    private int offreEmploiId;
    private Candidature candidature;

    // Identifiants Twilio
    public static final String ACCOUNT_SID = ConfigReader.get("AMINE_TWILIO_SID");;
    public static final String AUTH_TOKEN = ConfigReader.get("AMINE_TWILIO_AUTH");;

    public void initialize() {
        // Initialiser le menu
        initializeMenu(menu);

        // Add hover animation to buttons
        addHoverAnimation(btnSoumettre);
        addHoverAnimation(btnAnnuler);

        // Add title animation
        addTitleAnimation();
    }

    public void setOffreEmploiId(int offreEmploiId) {
        this.offreEmploiId = offreEmploiId;
        System.out.println("OffreEmploiId set to: " + offreEmploiId); // Debug statement
    }

    public void setCandidat(Candidat candidat) {
        tnom.setText(candidat.getLastName());
        tprenom.setText(candidat.getFirstName());
        temail.setText(candidat.getEmail());
        ttelephone.setText(candidat.getPhone());
    }

    public void setCandidature(Candidature candidature) {
        this.candidature = candidature;
        Candidat candidat = null;
        try {
            candidat = candidatService.getById(candidature.getCandidatId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (candidat != null) {
            setCandidat(candidat);
        }
        tcv.setText(candidature.getCv());
    }

    @FXML
    void choisirFichierCV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir un fichier CV");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"),
                new FileChooser.ExtensionFilter("Fichiers Word", "*.doc", "*.docx")
        );
        fichierCV = fileChooser.showOpenDialog(new Stage());
        if (fichierCV != null) {
            tcv.setText(fichierCV.getAbsolutePath());
        }
    }

    @FXML
    void saveCandidature() {
        try {
            if (fichierCV == null && (candidature == null || candidature.getCv() == null)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Veuillez sélectionner un fichier CV.");
                alert.showAndWait();
                return;
            }

            if (fichierCV != null) {
                // Vérifier que le fichier est bien un PDF ou un fichier Word
                String fileName = fichierCV.getName().toLowerCase();
                if (!fileName.endsWith(".pdf") && !fileName.endsWith(".doc") && !fileName.endsWith(".docx")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText(null);
                    alert.setContentText("Le fichier doit être au format PDF ou Word.");
                    alert.showAndWait();
                    return;
                }

                File dossierCV = new File("cvs");
                if (!dossierCV.exists()) {
                    dossierCV.mkdir();
                }
                File fichierDestination = new File(dossierCV, fichierCV.getName());
                Files.copy(fichierCV.toPath(), fichierDestination.toPath(), StandardCopyOption.REPLACE_EXISTING);

                if (candidature == null) {
                    candidature = new Candidature();
                }
                candidature.setCv(fichierDestination.getAbsolutePath());
            }

            Candidat candidat = new Candidat(
                    tnom.getText(),
                    tprenom.getText(),
                    temail.getText(),
                    ttelephone.getText()
            );

            int candidatId = getCandidatId(candidat);
            if (candidatId == -1) {
                candidatId = candidatService.createAndGetId(candidat);
            }

            candidature.setCandidatId(candidatId);
            candidature.setOffreEmploiId(offreEmploiId);

            // Verify that the offreEmploiId exists in the offreEmploi table
            System.out.println("Checking if offreEmploiId exists: " + offreEmploiId); // Debug statement
            OffreEmploi offreEmploi = offreEmploiService.getById(offreEmploiId);
            if (offreEmploi == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("L'offre d'emploi sélectionnée n'existe pas.");
                alert.showAndWait();
                return;
            }

            if (candidature.getId() == 0) {
                candidatureService.create(candidature);
            } else {
                candidatureService.update(candidature);
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("La candidature a été enregistrée avec succès !");
            alert.showAndWait();

            // Récupérer la candidature avec sa référence
            Candidature savedCandidature = candidatureService.getLatestCandidatureByCandidat(candidatId);

            // Envoyer le SMS avec la référence
            String messageBody = String.format(
                    "Votre candidature a été enregistrée avec succès (Réf: %s). Nous allons la traiter dès que possible.",
                    savedCandidature.getReference()
            );
            sendSms(candidat.getPhone(), messageBody);

            // Rediriger vers la page d'affichage des offres pour les candidats
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/affichageOffreCandidat.fxml"));
            Stage stage = (Stage) tnom.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des offres d'emploi");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur s'est produite lors de l'enregistrement de la candidature.");
            alert.showAndWait();
        }
    }

    private void sendSms(String to, String messageBody) {
        try {
            // Ensure the phone number is in E.164 format
            if (!to.startsWith("+")) {
                to = "+216" + to; // Assuming the phone number is for Tunisia
            }

            Class<?> twilioClass = Class.forName("com.twilio.Twilio");
            Method initMethod = twilioClass.getMethod("init", String.class, String.class);
            initMethod.invoke(null, ACCOUNT_SID, AUTH_TOKEN);

            Class<?> messageClass = Class.forName("com.twilio.rest.api.v2010.account.Message");
            Class<?> phoneNumberClass = Class.forName("com.twilio.type.PhoneNumber");

            Object toPhoneNumber = phoneNumberClass.getConstructor(String.class).newInstance(to);
            Object fromPhoneNumber = phoneNumberClass.getConstructor(String.class).newInstance("+17814121282");

            Method creatorMethod = messageClass.getMethod("creator", phoneNumberClass, phoneNumberClass, String.class);
            Object messageCreator = creatorMethod.invoke(null, toPhoneNumber, fromPhoneNumber, messageBody);

            Method createMethod = messageCreator.getClass().getMethod("create");
            Object message = createMethod.invoke(messageCreator);

            Method getSidMethod = message.getClass().getMethod("getSid");
            String sid = (String) getSidMethod.invoke(message);

            System.out.println("SMS envoyé avec SID : " + sid);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'envoi du SMS : " + e.getMessage());
        }
    }

    @FXML
    void annuler() {
        try {
            // Charger la page d'affichage des offres pour les candidats
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/affichageOffreCandidat.fxml"));
            Stage stage = (Stage) tnom.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des offres d'emploi");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void afficherOffresRecrutement() {
        try {
            // Charger la page d'affichage des offres pour les candidats
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/affichageOffreCandidat.fxml"));
            Stage stage = (Stage) tnom.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des offres d'emploi");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        tnom.clear();
        tprenom.clear();
        temail.clear();
        ttelephone.clear();
        tcv.clear();
        fichierCV = null;
    }

    private int getCandidatId(Candidat candidat) throws Exception {
        List<Candidat> candidats = candidatService.getAll();
        for (Candidat c : candidats) {
            if (c.getLastName().equals(candidat.getLastName()) &&
                    c.getFirstName().equals(candidat.getFirstName()) &&
                    c.getEmail().equals(candidat.getEmail()) &&
                    c.getPhone().equals(candidat.getPhone())) {
                return c.getId();
            }
        }
        return -1;
    }

    @FXML
    void afficherCandidatures() {
        try {
            // Charger la page d'affichage des candidatures
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/affichageCandidature.fxml"));
            Parent root = loader.load();

            // Afficher la nouvelle scène
            Stage stage = (Stage) tnom.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des candidatures");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void deconnexion() {
        // Handle logout logic here
    }

    private void addHoverAnimation(Button button) {
        button.setOnMouseEntered(event -> {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), button);
            scaleTransition.setToX(1.1);
            scaleTransition.setToY(1.1);
            scaleTransition.play();
        });

        button.setOnMouseExited(event -> {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), button);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
        });
    }

    private void addTitleAnimation() {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(2000), hrConnectTitle);
        scaleTransition.setFromX(1.0);
        scaleTransition.setToX(1.1);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToY(1.1);
        scaleTransition.setCycleCount(ScaleTransition.INDEFINITE);
        scaleTransition.setAutoReverse(true);
        scaleTransition.play();
    }
}