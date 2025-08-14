package controllers.Employe;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import models.Employe;
import services.EmployeService;
import utils.ShowMenu;

import java.net.URL;
import java.util.ResourceBundle;

public class AjouterEmployeController implements Initializable, ShowMenu {

    @FXML
    private AnchorPane menu;

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField soldeCongesField;

    EmployeService employeService = new EmployeService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeMenu(menu);
    }

    @FXML
    void EnregistrerEmploye() {
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        int soldeConges;

        try {
            soldeConges = Integer.parseInt(soldeCongesField.getText());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText(null);
            alert.setContentText("Le solde des congés doit être un nombre entier.");
            alert.showAndWait();
            return;
        }

        Employe employe = new Employe(nom, prenom, soldeConges);

        try {
            employeService.create(employe);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Employé ajouté avec succès.");
            alert.showAndWait();

            RetourListeEmploye();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur s'est produite lors de l'ajout de l'employé.");
            alert.showAndWait();
        }
    }

    @FXML
    void RetourListeEmploye() {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/Employe/ListeEmploye.fxml"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        menu.getScene().setRoot(root);
    }
}