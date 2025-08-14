package com.melocode.semin;



import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
/*

public class MainMenu extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/melocode/semin/views/menu.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
*/



import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class MainMenu extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Change the path to login.fxml
            URL fxmlLocation = getClass().getResource("/com/melocode/semin/views/login.fxml");
            System.out.println("FXML Location: " + fxmlLocation);
            if (fxmlLocation == null) {
                throw new IllegalStateException("FXML file not found at /com/melocode/semin/views/login.fxml");
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/melocode/semin/views/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setTitle("Login");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de l'interface utilisateur.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

