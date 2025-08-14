package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;
import controllers.RoleController;

public class FxmlMain extends Application {
    private static final double MIN_WIDTH = 1200.00;
    private static final double MIN_HEIGHT = 800.00;

    @Override
    public void start(Stage stage) throws Exception {
        loadScene("/FXML/role.fxml", "Détermination du rôle", stage);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void afficherOffresCandidat() throws Exception {
        Stage stage = new Stage();
        loadScene("/FXML/affichageOffreCandidat.fxml", "Offres d'emploi pour les candidats", stage);
    }

    public static void loadScene(String fxmlPath, String title, Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(FxmlMain.class.getResource(fxmlPath));
        Parent root = loader.load();

        AnchorPane container = new AnchorPane(root);

        AnchorPane.setTopAnchor(root, 0.0);
        AnchorPane.setBottomAnchor(root, 0.0);
        AnchorPane.setLeftAnchor(root, 0.0);
        AnchorPane.setRightAnchor(root, 0.0);

        container.setMinSize(MIN_WIDTH, MIN_HEIGHT);
        container.setPrefSize(MIN_WIDTH, MIN_HEIGHT);
        container.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        Scene scene = new Scene(container, MIN_WIDTH, MIN_HEIGHT);

        stage.setScene(scene);
        stage.setTitle(title);
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);
        stage.setResizable(true);

        if (loader.getController() instanceof RoleController) {
            ((RoleController) loader.getController()).setStage(stage);
        }

        stage.setTitle(title);

        stage.show();
        container.requestLayout();
    }

    public static void changeScene(String fxmlPath, String title, Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(FxmlMain.class.getResource(fxmlPath));
        Parent root = loader.load();

        AnchorPane container = new AnchorPane(root);

        AnchorPane.setTopAnchor(root, 0.0);
        AnchorPane.setBottomAnchor(root, 0.0);
        AnchorPane.setLeftAnchor(root, 0.0);
        AnchorPane.setRightAnchor(root, 0.0);

        container.setMinSize(MIN_WIDTH, MIN_HEIGHT);
        container.setPrefSize(MIN_WIDTH, MIN_HEIGHT);
        container.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        Scene scene = new Scene(container, MIN_WIDTH, MIN_HEIGHT);

        stage.setTitle(title);
        stage.setScene(scene);

        stage.setTitle(title);

        container.requestLayout();

        System.out.println("Changing scene to: " + title);
    }

    public static void updateStageTitle(Stage stage, String title) {
        if (stage != null) {
            stage.setTitle(title);
            System.out.println("Updated stage title to: " + title);
        }
    }
}