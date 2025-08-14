package controllers.DemandeConge;

import exceptions.InvalidInputException;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import jfxtras.scene.control.CalendarTextField;
import models.Demande_Conge;
import models.Employe;
import models.TypeConge;
import services.DemandeCongeService;
import services.EmployeService;
import utils.ShowMenu;

import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ResourceBundle;

// Importations correctes pour Simple Java Mail 8.0.0
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.api.mailer.config.TransportStrategy;

public class AjouterDemandeCongeController implements ShowMenu, Initializable {

    @FXML
    private AnchorPane menu;

    @FXML
    private MFXButton cancelBtn;

    @FXML
    private CalendarTextField dateDebut;

    @FXML
    private CalendarTextField dateFin;

    @FXML
    private MFXComboBox<TypeConge> typeConge;

    @FXML
    private MFXButton saveBtn;

    @FXML
    private MFXTextField employeId;

    DemandeCongeService demandeCongeService = new DemandeCongeService();

    @FXML
    void onSave(ActionEvent event) {

        try {
            if (employeId.getText().isEmpty()) {
                throw new InvalidInputException("L'identifiant de l'employé est requis");
            } else if (dateDebut.getCalendar().getTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().isBefore(LocalDate.now())) {
                throw new InvalidInputException("La date de début doit être supérieure ou égale à la date actuelle");
            } else if (!dateFin.getText().isEmpty() && dateFin.getCalendar().getTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().isBefore(dateDebut.getCalendar().getTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate())) {
                throw new InvalidInputException("La date de fin doit être supérieure ou égale à la date de début");
            }

            EmployeService employeService = new EmployeService();
            Employe employe = employeService.getEmployeById(Integer.parseInt(employeId.getText()));

            List<Demande_Conge> demandes = demandeCongeService.getAll();
            long totalLeaveTaken = demandes.stream()
                    .filter(d -> d.getEmploye().getId() == employe.getId())
                    .mapToLong(d -> ChronoUnit.DAYS.between(d.getDateDebut(), d.getDateFin()) + 1)
                    .sum();

            long remainingLeaves = employe.getSoldeConges() - totalLeaveTaken;

            if (remainingLeaves < -7) {
                throw new InvalidInputException("Le solde de congé restant est inférieur à -7 jours. Vous ne pouvez pas créer une nouvelle demande de congé.");
            }

            Demande_Conge demande = new Demande_Conge(
                    employe,
                    typeConge.getValue(),
                    dateDebut.getCalendar().getTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate(),
                    dateFin.getCalendar().getTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
            );

            demandeCongeService.create(demande);

            // Envoyer un email avec les détails de la demande de congé
            sendEmail(demande);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Demande de congé ajoutée avec succès");
            alert.showAndWait();

            // Rediriger vers la liste des demandes de congé
            Parent root = FXMLLoader.load(getClass().getResource("/DemandeConge/ListeDemandeConge.fxml"));
            employeId.getScene().setRoot(root);

        } catch (InvalidInputException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur s'est produite : " + e.getMessage());
            alert.showAndWait();
        }

    }

    private void sendEmail(Demande_Conge demande) {
        try {
            // Configuration du serveur SMTP (exemple avec Gmail)
            Mailer mailer = MailerBuilder
                    .withSMTPServer("smtp.gmail.com", 587, "chikenbrain26@gmail.com", "cull zslx rdjy apqu")
                    .withTransportStrategy(TransportStrategy.SMTP_TLS)
                    .buildMailer();

            // Création de l'email
            Email email = EmailBuilder.startingBlank()
                    .from("chikenbrain26@gmail.com")
                    .to("chikenbrain26@gmail.com") // Adresse email du destinataire
                    .withSubject("Nouvelle Demande de Congé")
                    .withPlainText("Bonjour,\n\n" +
                            "Une nouvelle demande de congé a été enregistrée avec succès.\n" +
                            "Détails de la demande :\n" +
                            "Employé : " + demande.getEmploye().getNom() + " " + demande.getEmploye().getPrenom() + "\n" +
                            "Type de congé : " + demande.getTypeConge() + "\n" +
                            "Date de début : " + demande.getDateDebut() + "\n" +
                            "Date de fin : " + demande.getDateFin() + "\n\n" +
                            "Cordialement,\nVotre entreprise")
                    .buildEmail();

            // Envoi de l'email
            mailer.sendMail(email);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur d'envoi d'email");
            alert.setHeaderText(null);
            alert.setContentText("L'email n'a pas pu être envoyé : " + e.getMessage());
            alert.showAndWait();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeMenu(menu);

        typeConge.getItems().addAll(TypeConge.values());
    }

    @FXML
    void OnClickCancelBtn() {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/DemandeConge/ListeDemandeConge.fxml"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        employeId.getScene().setRoot(root);
    }
}