package controllers.formations;

import controllers.formations.quiz.ListeQuizController;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import models.Formation;
import services.FormationService;
import utils.ShowMenu;
import utils.StripeUtil;
import utils.TableCell;
import utils.TableRow;
import utils.enums.TableRowType;
import javafx.scene.image.ImageView;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ToutesLesFormationsController implements Initializable, ShowMenu {

    @FXML
    private AnchorPane menu;

    @FXML
    private VBox vbox;

    FormationService fs = new FormationService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeMenu(menu);

        vbox.setSpacing(10);

        List<Formation> formations = null;
        try {
            formations = fs.getAllFormationsForUser();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if(formations.size() == 0){
            Label label = new Label("Aucune formation disponible pour vous");
            label.setFont(new Font(20));
            vbox.getChildren().add(label);
        }else{

            for(int i=0;i<formations.size();i++){

                final Formation formation = formations.get(i);

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/formations/components/FormationListItem.fxml"));
                    Parent xmlContent = loader.load();

                    ImageView imageView = (ImageView) loader.getNamespace().get("imageView");
                    imageView.setImage(new Image(formation.getImage()));


                    Label title = (Label) loader.getNamespace().get("title");
                    title.setText(formation.getTitle());

                    Label place = (Label) loader.getNamespace().get("place");
                    if(formation.isIs_online()){
                        place.setText("En ligne");
                    }else {
                        place.setText(formation.getPlace());
                        place.setOnMouseClicked(event -> {
                            if(formation.getLat() != null & formation.getLat() != null) {
                                Parent root = null;
                                try {
                                    FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/formations/FormationMap.fxml"));
                                    root = loader1.load();
                                    FormationMapController controller = loader1.getController();
                                    controller.setFormation(formation);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                                vbox.getScene().setRoot(root);
                            }else{
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Erreur");
                                alert.setHeaderText("Erreur");
                                alert.setContentText("Aucune coordonnée GPS n'est disponible pour cette formation");
                                alert.showAndWait();
                            }
                        });
                    }

                    Label description = (Label) loader.getNamespace().get("description");
                    title.setText(formation.getDescription());


                    Label formateur = (Label) loader.getNamespace().get("formateur");
                    formateur.setText(formation.getFormateur().getFirstName() + " " + formation.getFormateur().getLastName());

                    Label StarDate = (Label) loader.getNamespace().get("startDate");
                    StarDate.setText(formation.getStart_date().toString());

                    boolean isFree = formation.isFree();

                    MFXButton inscrireButton = (MFXButton) loader.getNamespace().get("inscrireBtn");

                    if(isFree){
                        inscrireButton.setText("S'inscire");
                    }else{
                        inscrireButton.setText("S'inscire (" + formation.getPrice()+ " DT)");
                    }
                    inscrireButton.setOnAction(event -> {
                        if(formation.isFree()) {
                            try {
                                fs.participerFormation(formation.getId(), 18);

                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Inscription");
                                alert.setHeaderText("Inscription réussie");
                                alert.setContentText("Vous êtes inscrit à la formation " + formation.getTitle());
                                alert.showAndWait();

                                //vbox.getChildren().clear();
                                //initialize(location, resources);

                                Parent root = null;
                                try {
                                    FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/formations/MesFormations.fxml"));
                                    root = loader1.load();
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                                vbox.getScene().setRoot(root);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else{
                            try {

                                WebView webView = new WebView();
                                String clientSecret = StripeUtil.createPaymentIntent(1);


                                String stripeJsUrl = getClass().getResource("/stripe.html").toExternalForm() + "?clientSecret=" + clientSecret;

                                webView.getEngine().load(stripeJsUrl);

                                webView.getEngine().setOnAlert(e -> {
                                    String data = e.getData();

                                    if(data.startsWith("error")){
                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                        alert.setTitle("Erreur");
                                        alert.setContentText(data);
                                        alert.setHeaderText(null);
                                        alert.showAndWait();
                                    }else{
                                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                        alert.setTitle("Paiement");
                                        alert.setHeaderText("Paiement effectué");
                                        alert.setContentText("Vous êtes inscrit à la formation " + formation.getTitle());
                                        alert.setHeaderText(null);
                                        alert.showAndWait();

                                        try {
                                            fs.participerFormation(formation.getId(), 18);
                                            Parent root = null;
                                            try {
                                                FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/formations/MesFormations.fxml"));
                                                root = loader1.load();
                                            } catch (Exception er) {
                                                throw new RuntimeException(er);
                                            }
                                            vbox.getScene().setRoot(root);
                                        } catch (Exception ex) {
                                            throw new RuntimeException(ex);
                                        }


                                    }

                                });


                                vbox.getChildren().clear();
                                vbox.getChildren().add(webView);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    vbox.getChildren().add(xmlContent);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }

}
