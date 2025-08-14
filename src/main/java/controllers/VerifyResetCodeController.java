package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Utilisateur;
import services.EmailService;
import services.UtilisateurCrud;

import java.io.IOException;
import java.util.regex.Pattern;

public class VerifyResetCodeController {
    @FXML
    private TextField codeField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label messageLabel;
    @FXML
    private Button resetButton;
    @FXML
    private Hyperlink resendCodeLink;

    private EmailService emailService;
    private UtilisateurCrud utilisateurCrud;
    private String userEmail;

    @FXML
    public void initialize() {
        emailService = new EmailService();
        utilisateurCrud = new UtilisateurCrud();

        // Add code field listener for formatting and validation
        codeField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Only allow digits and limit to 6 characters
            if (!newValue.matches("\\d*")) {
                codeField.setText(newValue.replaceAll("[^\\d]", ""));
            }

            if (newValue.length() > 6) {
                codeField.setText(newValue.substring(0, 6));
            }

            messageLabel.setVisible(false);
        });

        // Password validation listeners
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> messageLabel.setVisible(false));
        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> messageLabel.setVisible(false));
    }

    public void setEmail(String email) {
        this.userEmail = email;
    }

    @FXML
    private void handleResetPassword() {
        String code = codeField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        // Validate inputs
        if (code.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showMessage("Veuillez remplir tous les champs.", "error");
            return;
        }

        if (code.length() != 6) {
            showMessage("Le code doit contenir 6 chiffres.", "error");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showMessage("Les mots de passe ne correspondent pas.", "error");
            return;
        }

        // Check password strength
        int strength = calculatePasswordStrength(password);
        if (strength < 2) {
            showMessage("Mot de passe trop faible. Utilisez au moins 8 caractères, avec des lettres et des chiffres.", "error");
            return;
        }

        // Verify the code
        if (emailService.verifyCode(userEmail, code)) {
            // Update user password
            Utilisateur user = utilisateurCrud.getUtilisateurByEmail(userEmail);
            if (user != null) {
                user.setpassword(password);
                utilisateurCrud.modifierEntite(user);

                // Invalidate code after use
                emailService.invalidateCode(userEmail);

                showMessage("Mot de passe réinitialisé avec succès!", "success");

                // Disable reset button to prevent multiple resets
                resetButton.setDisable(true);

                // Navigate to login after 3 seconds
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                javafx.application.Platform.runLater(() -> navigateToLogin());
                            }
                        },
                        3000
                );
            } else {
                showMessage("Utilisateur non trouvé. Contactez l'administrateur.", "error");
            }
        } else {
            showMessage("Code invalide ou expiré.", "error");
        }
    }

    @FXML
    private void handleResendCode() {
        if (userEmail == null || userEmail.isEmpty()) {
            showMessage("Email non spécifié.", "error");
            return;
        }

        // Disable link to prevent spam
        resendCodeLink.setDisable(true);

        // Request a new code
        String newCode = emailService.sendPasswordResetCode(userEmail);

        if (newCode != null) {
            showMessage("Un nouveau code a été envoyé à votre adresse email.", "success");

            // Re-enable link after 30 seconds
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            javafx.application.Platform.runLater(() -> resendCodeLink.setDisable(false));
                        }
                    },
                    30000 // 30 seconds
            );
        } else {
            showMessage("Impossible d'envoyer un nouveau code.", "error");
            resendCodeLink.setDisable(false);
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
            Stage stage = (Stage) codeField.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showMessage("Erreur lors du chargement de la page de connexion.", "error");
        }
    }

    private void navigateToLogin() {
        try {
            // Load login view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/authentification.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Get current stage and set new scene
            Stage stage = (Stage) resetButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showMessage(String message, String type) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().removeAll("success-message", "error-message");
        messageLabel.getStyleClass().add(type.equals("success") ? "success-message" : "error-message");
        messageLabel.setVisible(true);
    }

    private int calculatePasswordStrength(String password) {
        int strength = 0;

        if (password.length() >= 8) strength++;
        if (Pattern.compile("[0-9]").matcher(password).find()) strength++;
        if (Pattern.compile("[a-z]").matcher(password).find()) strength++;
        if (Pattern.compile("[A-Z]").matcher(password).find()) strength++;
        if (Pattern.compile("[^a-zA-Z0-9]").matcher(password).find()) strength++;

        return strength;
    }
}