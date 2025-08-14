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
import services.DemandeCongeService;
import services.ValiderCongeService;
import utils.ShowMenu;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class ValiderCongeController implements Initializable, ShowMenu {

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
    DemandeCongeService demandeCongeService = new DemandeCongeService();

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

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Demande de congé validée avec succès");
            alert.showAndWait();

            //redirect to list
            Parent root = FXMLLoader.load(getClass().getResource("/DemandeConge/ListeDemandeConge.fxml"));
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
            alert.setContentText("An error has occured");
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
    }

    @FXML
    void OnClickCancelBtn() {
        Parent root = null;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/DemandeConge/ListeDemandeConge.fxml")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        statutConge.getScene().setRoot(root);
    }
}