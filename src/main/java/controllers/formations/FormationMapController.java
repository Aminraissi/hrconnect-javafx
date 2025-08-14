package controllers.formations;

import exceptions.InvalidInputException;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import models.Formateur;
import models.Formation;
import utils.ConfigReader;
import utils.ShowMenu;
import utils.SmsTwilio;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.ResourceBundle;

public class FormationMapController implements ShowMenu, Initializable {

    @FXML
    private AnchorPane menu;


    @FXML
    private ImageView mapImage;

    @FXML
    private Text title;

    Formation formation;

    @FXML
    private MFXTextField phone;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeMenu(menu);
        if(formation != null) {
            String url = "https://maps.googleapis.com/maps/api/staticmap?" +
            "center=" + formation.getLat() + "," + formation.getLng() +
                    "&zoom=15&size=600x400" +
                    "&markers=color:red%7Clabel:A%7C" + formation.getLat() + "," + formation.getLng() +
                    "&key=" + ConfigReader.get("HAYTHEM_GOOGLE_MAPS");

            System.out.println(url);
            mapImage.setImage(new Image(url));
        }
    }

    public void setFormation(Formation formation) throws SQLException {
        this.formation = formation;
        title.setText(formation.getPlace());
        initialize(null, null);
    }

    @FXML
    void shareLocation() {
       try{
           if(phone.getText().isEmpty()) {
               throw  new InvalidInputException("Le numéro de téléphone est requis");
           }else if(phone.getText().length() != 8) {
               throw  new InvalidInputException("Le numéro de téléphone doit contenir 8 chiffres");
           }else if(!phone.getText().matches("[0-9]+")) {
               throw  new InvalidInputException("Le numéro de téléphone doit contenir que des chiffres");
           }

           SmsTwilio sms = new SmsTwilio();

           sms.send(Integer.parseInt(phone.getText()), "Bonjour, voici la localisation de la formation " + formation.getTitle() + " : " +
                   "https://maps.google.com/?q=" + formation.getLat() + "," + formation.getLng());


           Alert alert = new Alert(Alert.AlertType.INFORMATION);
           alert.setTitle("Succès");
           alert.setHeaderText(null);
           alert.setContentText("La localisation a été partagée avec succès");
           alert.showAndWait();

       }catch (InvalidInputException e){
           Alert alert = new Alert(Alert.AlertType.ERROR);
           alert.setTitle("Erreur");
           alert.setHeaderText(null);
           alert.setContentText(e.getMessage());
           alert.showAndWait();
       }catch (Exception e){
           System.out.println(e);
           System.out.println(e.getStackTrace());
           Alert alert = new Alert(Alert.AlertType.ERROR);
           alert.setTitle("Erreur");
           alert.setHeaderText(null);
           alert.setContentText("An error has occured");
           alert.showAndWait();
       }
    }
}
