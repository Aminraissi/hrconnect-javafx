package utils;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

public interface ShowMenu {



    default public void initializeMenu(AnchorPane menu) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Menu.fxml"));
            AnchorPane menuPane = loader.load();
            menu.getChildren().clear();
            menu.getChildren().add(menuPane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
