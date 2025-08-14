package controllers.ValiderConge;

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
import models.Demande_Conge;
import models.StatutDemande;
import models.Valider_Conge;
import services.ValiderCongeService;
import utils.ConfigReader;
import utils.ShowMenu;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.net.URL;
import java.util.ResourceBundle;

public class ModifierValiderCongeController implements Initializable, ShowMenu {

    private Demande_Conge demande;

    @FXML
    private AnchorPane menu;

    @FXML
    private MFXButton cancelBtn;

    @FXML
    private MFXComboBox<StatutDemande> statutConge;

    @FXML
    private MFXTextField commentaire;

    @FXML
    private MFXButton saveBtn;

    ValiderCongeService validerCongeService = new ValiderCongeService();

    @FXML
    void onSave(ActionEvent event) {

        try {
            if (statutConge.getValue() == null) {
                throw new InvalidInputException("Le statut est requis");
            }

            Valider_Conge validation = new Valider_Conge(
                    demande,
                    statutConge.getValue(),
                    commentaire.getText()
            );

            validerCongeService.createValidation(validation);

            // Envoyer un SMS via Twilio
            String ACCOUNT_SID = ConfigReader.get("ALA_TWILIO_SID");
            String AUTH_TOKEN = ConfigReader.get("ALA_TWILIO_AUTH");

            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

            String messageBody = "Votre demande de congé a été " + statutConge.getValue().toString() + ". Commentaire: " + commentaire.getText();
            Message message = Message.creator(
                    new PhoneNumber("+21695343410"),  // Numéro de téléphone du destinataire
                    new PhoneNumber("++15092837437"),  // Votre numéro Twilio
                    messageBody
            ).create();

            System.out.println("SMS envoyé avec l'ID: " + message.getSid());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Demande de congé modifiée avec succès et SMS envoyé");
            alert.showAndWait();

            // Rediriger vers la liste
            Parent root = FXMLLoader.load(getClass().getResource("/ValiderConge/ListeValiderConge.fxml"));
            statutConge.getScene().setRoot(root);

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
            alert.setContentText("Une erreur s'est produite: " + e.getMessage());
            alert.showAndWait();
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeMenu(menu);
        statutConge.getItems().addAll(StatutDemande.values());
    }

    public void setDemande(Demande_Conge demande) {
        this.demande = demande;
        statutConge.setValue(demande.getStatut());
        commentaire.setText(demande.getValidationCommentaire());
    }

    @FXML
    void OnClickCancelBtn() {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/ValiderConge/ListeValiderConge.fxml"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        statutConge.getScene().setRoot(root);
    }
}