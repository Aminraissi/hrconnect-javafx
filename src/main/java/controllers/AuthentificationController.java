package controllers;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Utilisateur;
import services.UtilisateurCrud;
import utils.SessionManager;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class AuthentificationController {

    @FXML
    private TextField tfemail;

    @FXML
    private PasswordField tfmdp;

    @FXML
    private TextField tfshowpassword;

    @FXML
    private CheckBox show;

    @FXML
    private Button btn_auth;

    @FXML
    private Button btn_annul;

    @FXML
    private Hyperlink hyperlink;

    @FXML
    private Button googleSignInButton;

    // 🛠️ Google Sign-In Configuration
    private static final String CLIENT_ID = System.getenv("GOOGLE_CLIENT_ID");
    private static final String CLIENT_SECRET = System.getenv("GOOGLE_CLIENT_SECRET");
    private static final String REDIRECT_URI = System.getenv("GOOGLE_REDIRECT_URI");

    private static final List<String> SCOPES = Arrays.asList(
            "https://www.googleapis.com/auth/userinfo.profile",
            "https://www.googleapis.com/auth/userinfo.email");

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    private UtilisateurCrud userService = new UtilisateurCrud();

    @FXML
    void initialize() {
        setupPasswordVisibilityToggle();
    }

    private void setupPasswordVisibilityToggle() {
        show.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { // Show password
                tfshowpassword.setText(tfmdp.getText());
                tfshowpassword.setVisible(true);
                tfmdp.setVisible(false);
            } else { // Hide password
                tfmdp.setText(tfshowpassword.getText());
                tfmdp.setVisible(true);
                tfshowpassword.setVisible(false);
            }
        });

        tfmdp.textProperty().addListener((observable, oldValue, newValue) -> {
            if (show.isSelected()) {
                tfshowpassword.setText(newValue);
            }
        });

        tfshowpassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if (show.isSelected()) {
                tfmdp.setText(newValue);
            }
        });
    }

    @FXML
    void authenticate(ActionEvent event) {
        String email = tfemail.getText().trim();
        String password = tfmdp.isVisible() ? tfmdp.getText() : tfshowpassword.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie",
                    "Veuillez remplir tous les champs requis");
            return;
        }

        // Authenticate user - direct login without reCAPTCHA
        boolean isAuthenticated = userService.authenticateUser(email, password);
        System.out.println("Authenticating user: " + email + " with password: " + password);
        System.out.println("Authentication result: " + isAuthenticated);

        if (isAuthenticated) {
            // Get user details
            Utilisateur user = userService.getUtilisateurByEmail(email);

            if (user != null) {
                // Create a session for the authenticated user
                SessionManager.getInstance().setCurrentUser(user);

                // Navigate to appropriate page based on role
                navigateToUserProfile(user.getroles());
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur d'authentification",
                        "Impossible de récupérer les détails de l'utilisateur");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur d'authentification",
                    "Email ou mot de passe incorrect");
        }
    }

    private void navigateToUserProfile(String role) {
        try {
            String fxmlPath = "";

            // Determine which FXML to load based on user role
            // Note: Handling both with and without "ROLE_" prefix
            String normalizedRole = role;
            if (role.startsWith("ROLE_")) {
                normalizedRole = role.substring(5);
            }

            switch (normalizedRole) {
                case "[\"ROLE_ADMIN\"]":
                    fxmlPath = "/Menu.fxml";
                    break;
                case "[\"ROLE_USER\"]":
                    fxmlPath = "/ProfilMembre.fxml";
                    break;
                case "[\"ROLE_EMPLOYE\"]":
                    fxmlPath = "/ProfilEmploye.fxml";
                    break;
                case "[\"ROLE_MANAGER\"]":
                    fxmlPath = "/ProfilManager.fxml";
                    break;
                case "[\"ROLE_RH\"]":
                    fxmlPath = "/ProfilRh.fxml";
                    break;
                default:
                    showAlert(Alert.AlertType.ERROR, "Erreur de navigation",
                            "Rôle non reconnu: " + normalizedRole);
                    return;
            }

            // Load the FXML for the user's profile
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Create a new scene
            Stage stage = new Stage();
            stage.setTitle("Profil " + normalizedRole);
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();

            // Close the login window
            Stage loginStage = (Stage) btn_auth.getScene().getWindow();
            loginStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de navigation",
                    "Impossible de charger la page de profil: " + e.getMessage());
        }
    }

    @FXML
    void annuler(ActionEvent event) {
        // Close the current window
        Stage stage = (Stage) btn_annul.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleForgotPassword() {
        try {
            // Load forgot password view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ForgotPassword.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Get current stage and set new scene
            Stage stage = (Stage) tfemail.getScene().getWindow(); // Change emailField to tfemail
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de charger la page de réinitialisation de mot de passe.");
        }
    }
    @FXML
    void goToRegistration(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/inscription.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Inscription");
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current window
            Stage currentStage = (Stage) hyperlink.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de navigation",
                    "Impossible de charger la page d'inscription: " + e.getMessage());
        }
    }

    // ✅ Google Sign-In
    @FXML
    void handleGoogleSignIn(ActionEvent event) {
        try {
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, JSON_FACTORY, CLIENT_ID, CLIENT_SECRET, SCOPES)
                    .setAccessType("offline")
                    .build();

            // 🔹 Ouvrir l'URL de connexion dans le navigateur
            String authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
            Desktop.getDesktop().browse(new URI(authorizationUrl));

            // 🔹 Demander à l'utilisateur d'entrer le code
            String code = showInputDialog("Entrez le code Google obtenu:");

            if (code == null || code.trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Authentification annulée",
                        "L'authentification avec Google a été annulée");
                return;
            }

            // 🔹 Échanger le code contre un token
            GoogleTokenResponse tokenResponse = flow.newTokenRequest(code).setRedirectUri(REDIRECT_URI).execute();
            Credential credential = flow.createAndStoreCredential(tokenResponse, "user");

            // 🔹 Récupérer les informations utilisateur
            Oauth2 oauth2 = new Oauth2.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName("RHCONNECT").build();
            Userinfo userinfo = oauth2.userinfo().get().execute();

            String email = userinfo.getEmail();

            // Check if user exists in our database
            Utilisateur user = userService.getUtilisateurByEmail(email);

            if (user != null) {
                // User exists, create session and navigate
                SessionManager.getInstance().setCurrentUser(user);
                navigateToUserProfile(user.getroles());
            } else {
                // User doesn't exist, show registration prompt
                showAlert(Alert.AlertType.INFORMATION, "Nouvel utilisateur",
                        "Bienvenue! Vous devez vous inscrire pour utiliser votre compte Google.");
                // Redirect to registration page with pre-filled Google info
                goToRegistration(event);
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de connexion",
                    "Impossible de se connecter via Google: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ✅ Affichage d'alertes
    private void showAlert(Alert.AlertType type, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    // ✅ Demander un code à l'utilisateur
    private String showInputDialog(String message) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Google Sign-In");
        dialog.setHeaderText(null);
        dialog.setContentText(message);
        return dialog.showAndWait().orElse(null);
    }
}