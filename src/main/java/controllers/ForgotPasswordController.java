package controllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import services.EmailService;
import services.UtilisateurCrud;

import java.io.IOException;
import java.util.regex.Pattern;

public class ForgotPasswordController {
    @FXML
    private TextField emailField;
    @FXML
    private Label messageLabel;
    @FXML
    private Button submitButton;

    private EmailService emailService;
    private UtilisateurCrud utilisateurCrud;

    @FXML
    public void initialize() {
        emailService = new EmailService();
        utilisateurCrud = new UtilisateurCrud();

        // Add email validation listener
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            messageLabel.setVisible(false);
        });
    }

    @FXML
    private void handleSubmit() {
        String email = emailField.getText().trim();

        // Validate email
        if (email.isEmpty()) {
            showMessage("Veuillez entrer votre adresse email.", "error");
            return;
        }

        // Validate email format
        if (!isValidEmail(email)) {
            showMessage("Format d'email invalide.", "error");
            return;
        }

        // Check if email exists
        if (utilisateurCrud.getUtilisateurByEmail(email) == null) {
            showMessage("Cette adresse email n'est pas associée à un compte.", "error");
            return;
        }

        // Disable button to prevent multiple submissions
        submitButton.setDisable(true);

        // Send verification code
        String code = emailService.sendPasswordResetCode(email);

        if (code != null) {
            showMessage("Un code de réinitialisation a été envoyé à votre adresse email.", "success");

            // Navigate to verification page with email after short delay
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(event -> navigateToVerifyCode(email));
            pause.play();
        } else {
            showMessage("Impossible d'envoyer l'email. Veuillez réessayer plus tard.", "error");
            submitButton.setDisable(false);
        }
    }

    private void navigateToVerifyCode(String email) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/VerifyResetCode.fxml"));
            Parent root = loader.load();

            // Pass email to the verification controller
            VerifyResetCodeController controller = loader.getController();
            controller.setEmail(email);

            Scene scene = new Scene(root);
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showMessage("Erreur lors du chargement de la page de vérification.", "error");
            submitButton.setDisable(false);
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            // Load login view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/authentification.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Get current stage and set new scene
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showMessage("Erreur lors du chargement de la page de connexion.", "error");
        }
    }

    private void showMessage(String message, String type) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().removeAll("success-message", "error-message");
        messageLabel.getStyleClass().add(type.equals("success") ? "success-message" : "error-message");
        messageLabel.setVisible(true);
    }

    private boolean isValidEmail(String email) {
        // Simple email validation pattern
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }
}