package controllers.formations;

import exceptions.InvalidInputException;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyComboBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.scene.image.ImageView;
import jfxtras.scene.control.CalendarTextField;
import org.json.JSONArray;
import org.json.JSONObject;
import models.Formateur;
import models.Formation;
import services.FormateurService;
import services.FormationService;
import utils.GoogleMap;
import utils.PlaceInfo;
import utils.ShowMenu;
import utils.UploadImage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;



public class AjouterFormationController implements ShowMenu, Initializable {

    @FXML
    private AnchorPane menu;

    @FXML
    private MFXButton cancelBtn;

    @FXML
    private CalendarTextField dateDebut;

    @FXML
    private CalendarTextField datefin;

    @FXML
    private MFXTextField description;

    @FXML
    private MFXTextField emplacement;

    @FXML
    private MFXLegacyComboBox<Formateur> formateur;

    @FXML
    private MFXToggleButton pourEmployes;

    @FXML
    private MFXToggleButton pourStagaires;

    @FXML
    private MFXButton saveBtn;

    @FXML
    private MFXTextField title;

    @FXML
    private MFXToggleButton typeFormation;

    @FXML
    private MFXButton chooseImage;

    @FXML
    private ImageView imageView;

    @FXML
    private MFXTextField price;

    @FXML
    private ListView<String> suggestionsList;


    FormationService fs = new FormationService();

    String imagePath;

    Double lat,lng;

    List<PlaceInfo> suggestions = new ArrayList<>();

    @FXML
    void onSave(ActionEvent event) {

        try {
            if(imagePath == null){
                throw new InvalidInputException("Choisir une image pour la formation");
            } else if (title.getText().isEmpty()) {
                throw new InvalidInputException("Le titre est requis");
            } else if (description.getText().isEmpty()) {
                throw new InvalidInputException("La description est requise");
            } else if (formateur.getValue() == null) {
                throw new InvalidInputException("Choisir un formateur");
            } else if (!typeFormation.isSelected() && emplacement.getText().isEmpty()) {
                throw new InvalidInputException("L'emplacement est requis pour une formation en présentiel");
            } else if (dateDebut.getCalendar().getTime().before(new Date())) {
                throw new InvalidInputException("La date de début doit être supérieure à la date actuelle");
            } else if (!datefin.getText().isEmpty() && datefin.getCalendar().getTime().before(dateDebut.getCalendar().getTime())) {
                throw new InvalidInputException("La date de fin doit être supérieure à la date de début");
            }else if(!pourEmployes.isSelected() && !pourStagaires.isSelected()) {
                throw new InvalidInputException("Choisir au moins une catégorie de personnes");
            }else if(price.getText().isEmpty()) {
                throw new InvalidInputException("Le prix est requis");
            }else if(Double.parseDouble(price.getText()) < 0) {
                throw new InvalidInputException("Le prix doit être supérieur ou égale à 0");
            }


            UploadImage uploadImage = new UploadImage();
            String imageUrl = uploadImage.uploadImage(new File(imagePath));


            Formation formation = new Formation(
                    formateur.getValue().getId(),
                    imageUrl,
                    title.getText(),
                    description.getText(),
                    emplacement.getText(),
                    typeFormation.isSelected(),
                    pourEmployes.isSelected(),
                    pourStagaires.isSelected(),
                    dateDebut.getCalendar().getTime(),
                    Double.parseDouble(price.getText()),
                    lat,
                    lng
            );

            if (!datefin.getText().isEmpty()) {
                formation.setEnd_date(datefin.getCalendar().getTime());
            }

            fs.create(formation);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Formation ajoutée avec succès");
            alert.showAndWait();

            //redirect to list

            Parent root = null;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/formations/ListeFormation.fxml"));
                root = loader.load();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            title.getScene().setRoot(root);

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

    @FXML
    void ChooseImage() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        try {
            File selectedFile = fileChooser.showOpenDialog(chooseImage.getScene().getWindow());
            imagePath = selectedFile.getAbsolutePath();
            imageView.setImage(new javafx.scene.image.Image(selectedFile.toURI().toString()));
        } catch (Exception e) {
            System.out.println(e);
            imagePath = null;
            imageView.setImage(null);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        initializeMenu(menu);

        suggestionsList.setVisible(false);
        suggestionsList.setManaged(false);
        suggestionsList.setMinWidth(100);
        suggestionsList.setMinHeight(130);

        emplacement.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 2) { // Search after 3 characters
                suggestionsList.getItems().clear();
                suggestions.clear();
                suggestions.addAll(GoogleMap.fetchPlaceSuggestions(newValue));
                suggestionsList.getItems().addAll(suggestions.stream().map(PlaceInfo::getPlaceName).toList());
                suggestionsList.setVisible(!suggestionsList.getItems().isEmpty());
                suggestionsList.setManaged(!suggestionsList.getItems().isEmpty());// Sho
            } else {
                suggestionsList.getItems().clear();
                suggestionsList.setVisible(false);
                suggestionsList.setManaged(false);
            }
        });

        suggestionsList.setOnMouseClicked(event -> {
            String selectedPlace = suggestionsList.getSelectionModel().getSelectedItem();
            if (selectedPlace != null) {
                emplacement.setText(selectedPlace);

                // Get the place_id of the selected place
                PlaceInfo selectedPlaceInfo = suggestions.stream()
                        .filter(placeInfo -> placeInfo.getPlaceName().equals(selectedPlace))
                        .findFirst()
                        .orElse(null);

                if (selectedPlaceInfo != null) {
                    String placeId = selectedPlaceInfo.getPlaceId();

                    List<Double> coordinates =  GoogleMap.fetchPlaceDetails(placeId);

                    lat = coordinates.get(0);
                    lng = coordinates.get(1);
                }

                suggestionsList.setVisible(false);
                suggestionsList.setManaged(false);
            }
        });


        FormateurService fs = new FormateurService();
        List<Formateur> formateurs = new ArrayList<>();

        try {
            formateurs.addAll(fs.getAll());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        // Custom cell factory to show only the name in the dropdown list
        formateur.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Formateur formateur, boolean empty) {
                super.updateItem(formateur, empty);
                setText(empty || formateur == null ? "" : formateur.getFirstName() + " " + formateur.getLastName());
            }
        });

        // show name when selected
        formateur.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Formateur formateur, boolean empty) {
                super.updateItem(formateur, empty);
                setText(empty || formateur == null ? "" : formateur.getFirstName() + " " + formateur.getLastName());
            }
        });


        // Add formateurs to the ComboBox
        formateur.getItems().addAll(formateurs);

        typeFormation.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                emplacement.setDisable(true);
            } else {
                emplacement.setDisable(false);
            }
        });

    }

    @FXML
    void OnClickCancelBtn() {
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/formations/ListeFormation.fxml"));
            root = loader.load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        title.getScene().setRoot(root);
    }
}
