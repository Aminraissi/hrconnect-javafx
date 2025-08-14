package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import models.MdpHash;
import models.Utilisateur;
import services.UtilisateurCrud;

import java.net.URL;
import java.util.ResourceBundle;

public class ResetMdpController {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button tfreinit;

    private static final String RESET_TOKEN = "static-reset-token";

    @FXML
    void initialize() {
        tfreinit.setOnAction(event -> {
            String newPassword = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                System.out.println("Veuillez entrer votre nouveau mot de passe");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                System.out.println("Les mots de passe ne correspondent pas");
                return;
            }

            // Assuming the token is passed as a URL parameter to this controller
            if (isValidToken(RESET_TOKEN)) {
                updatePassword(RESET_TOKEN, newPassword);
            } else {
                System.out.println("Invalid or expired token");
            }
        });
    }

    private boolean isValidToken(String token) {
        return RESET_TOKEN.equals(token);
    }

    private void updatePassword(String token, String newPassword) {
        String email = "user@example.com"; // Replace with logic to get email from token
        UtilisateurCrud utilisateurCrud = new UtilisateurCrud();
        Utilisateur user = utilisateurCrud.getUtilisateurByEmail(email);

        if (user != null) {
            user.setpassword(MdpHash.hashPassword(newPassword));
            utilisateurCrud.modifierEntite(user);
            System.out.println("Mot de passe mis à jour avec succès");
        } else {
            System.out.println("Utilisateur non trouvé");
        }
    }
}