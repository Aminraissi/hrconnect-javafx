package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Utilisateur;
import services.UtilisateurCrud;

import java.util.regex.Pattern;

/**
 * Interface for parent controllers that need to be refreshed after user operations
 */
interface UserManagementController {
    void refreshUsers();
}

public class AddUserDialogController {
    @FXML
    private TextField cinField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private CheckBox showPasswordCheckBox;
    @FXML
    private Label passwordStrengthLabel;
    @FXML
    private RadioButton adminRadio;
    @FXML
    private RadioButton rhRadio;
    @FXML
    private RadioButton employeRadio;
    @FXML
    private RadioButton managerRadio;
    @FXML
    private RadioButton userRadio;
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;

    private UtilisateurCrud utilisateurCrud = new UtilisateurCrud();
    private UserManagementController parentController;
    private ToggleGroup roleGroup;
    private boolean isRhMode = false;

    @FXML
    public void initialize() {
        // Create toggle group for role selection
        roleGroup = new ToggleGroup();
        adminRadio.setToggleGroup(roleGroup);
        rhRadio.setToggleGroup(roleGroup);
        employeRadio.setToggleGroup(roleGroup);
        managerRadio.setToggleGroup(roleGroup);
        userRadio.setToggleGroup(roleGroup);

        // Default selection
        userRadio.setSelected(true);

        // Password visibility toggle
        setupPasswordVisibility();

        // Password strength checker
        setupPasswordStrengthChecker();
    }

    /**
     * Configure the dialog for RH mode (limit role options)
     * When in RH mode, only USER and EMPLOYEE roles are available
     */
    public void setRhMode(boolean isRh) {
        this.isRhMode = isRh;

        if (isRh) {
            // RH can only add USER (stagiaires) or EMPLOYE
            adminRadio.setDisable(true);
            rhRadio.setDisable(true);
            managerRadio.setDisable(true);

            // Default selection for RH
            userRadio.setSelected(true);
        } else {
            // Admin can add all roles
            adminRadio.setDisable(false);
            rhRadio.setDisable(false);
            managerRadio.setDisable(false);
        }
    }

    private void setupPasswordVisibility() {
        showPasswordCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { // Show password
                passwordTextField.setText(passwordField.getText());
                passwordTextField.setVisible(true);
                passwordField.setVisible(false);
            } else { // Hide password
                passwordField.setText(passwordTextField.getText());
                passwordField.setVisible(true);
                passwordTextField.setVisible(false);
            }
        });

        // Keep password fields in sync
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            passwordTextField.setText(newValue);
            checkPasswordStrength(newValue);
        });

        passwordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            passwordField.setText(newValue);
            checkPasswordStrength(newValue);
        });
    }

    private void setupPasswordStrengthChecker() {
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            checkPasswordStrength(newValue);
        });
    }

    private void checkPasswordStrength(String password) {
        if (password.isEmpty()) {
            passwordStrengthLabel.setText("");
            return;
        }

        int strength = 0;

        // Length check
        if (password.length() >= 8) strength++;

        // Contains digits check
        if (Pattern.compile("[0-9]").matcher(password).find()) strength++;

        // Contains lowercase check
        if (Pattern.compile("[a-z]").matcher(password).find()) strength++;

        // Contains uppercase check
        if (Pattern.compile("[A-Z]").matcher(password).find()) strength++;

        // Contains special characters check
        if (Pattern.compile("[^a-zA-Z0-9]").matcher(password).find()) strength++;

        // Set appropriate feedback based on strength
        switch (strength) {
            case 0:
            case 1:
                passwordStrengthLabel.setText("Mot de passe faible");
                passwordStrengthLabel.getStyleClass().removeAll("password-strength-medium", "password-strength-strong");
                passwordStrengthLabel.getStyleClass().add("password-strength-weak");
                break;
            case 2:
            case 3:
                passwordStrengthLabel.setText("Mot de passe moyen");
                passwordStrengthLabel.getStyleClass().removeAll("password-strength-weak", "password-strength-strong");
                passwordStrengthLabel.getStyleClass().add("password-strength-medium");
                break;
            case 4:
            case 5:
                passwordStrengthLabel.setText("Mot de passe fort");
                passwordStrengthLabel.getStyleClass().removeAll("password-strength-weak", "password-strength-medium");
                passwordStrengthLabel.getStyleClass().add("password-strength-strong");
                break;
        }
    }

    /**
     * Allow both Admin and RH controllers to be set as parent
     */
    public void setParentController(Object controller) {
        if (controller instanceof ProfilAdminController) {
            this.parentController = new UserManagementController() {
                @Override
                public void refreshUsers() {
                    ((ProfilAdminController) controller).loadUsers();
                }
            };
            setRhMode(false);
        } else if (controller instanceof ProfilRhController) {
            this.parentController = new UserManagementController() {
                @Override
                public void refreshUsers() {
                    ProfilRhController rhController = (ProfilRhController) controller;
                    rhController.loadMembers();
                    rhController.loadEmployees();
                }
            };
            setRhMode(true);
        }
    }

    /**
     * Preset the role selection to USER
     */
    public void presetRoleToUser() {
        userRadio.setSelected(true);
    }

    /**
     * Preset the role selection to EMPLOYE
     */
    public void presetRoleToEmployee() {
        employeRadio.setSelected(true);
    }

    @FXML
    private void handleSave() {
        try {
            // Validate inputs
            if (nomField.getText().trim().isEmpty() ||
                    prenomField.getText().trim().isEmpty() ||
                    emailField.getText().trim().isEmpty() ||
                    cinField.getText().trim().isEmpty() ||
                    phoneField.getText().trim().isEmpty() ||
                    (passwordField.isVisible() ? passwordField.getText().isEmpty() : passwordTextField.getText().isEmpty()) ||
                    roleGroup.getSelectedToggle() == null) {

                showAlert(Alert.AlertType.ERROR, "Champs obligatoires",
                        "Veuillez remplir tous les champs obligatoires.");
                return;
            }

            // Validate email format
            String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
            if (!Pattern.compile(emailRegex).matcher(emailField.getText()).matches()) {
                showAlert(Alert.AlertType.ERROR, "Format d'email invalide",
                        "Veuillez entrer une adresse email valide.");
                return;
            }

            // Check if user already exists
            if (utilisateurCrud.utilisateurExisteDeja(cinField.getText(), emailField.getText())) {
                showAlert(Alert.AlertType.ERROR, "Utilisateur existe déjà",
                        "Un utilisateur avec ce CIN ou cet email existe déjà.");
                return;
            }

            // Create new user
            Utilisateur newUser = new Utilisateur();
            newUser.setCin(Integer.parseInt(cinField.getText().trim()));
            newUser.settel(phoneField.getText().trim());
            newUser.setNom(nomField.getText().trim());
            newUser.setPrenom(prenomField.getText().trim());
            newUser.setEmail(emailField.getText().trim());
            newUser.setpassword(passwordField.isVisible() ? passwordField.getText() : passwordTextField.getText());

            // Set role
            String role = "";
            if (adminRadio.isSelected()) role = "ADMIN";
            else if (rhRadio.isSelected()) role = "RH";
            else if (employeRadio.isSelected()) role = "EMPLOYE";
            else if (managerRadio.isSelected()) role = "MANAGER";
            else if (userRadio.isSelected()) role = "USER";

            newUser.setroles(role);

            // Save to database
            utilisateurCrud.ajouterEntite(newUser);

            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Succès",
                    "Utilisateur ajouté avec succès.");

            // Refresh user list in parent controller
            if (parentController != null) {
                parentController.refreshUsers();
            }

            // Close the dialog
            closeDialog();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de format",
                    "Le CIN doit être un nombre entier.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Une erreur est survenue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        // Style alert to match the desired appearance
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/modern-style.css").toExternalForm());

        // Set specific class for buttons
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        if (okButton != null) {
            okButton.getStyleClass().add("primary-button");
        }

        Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
        if (cancelButton != null) {
            cancelButton.getStyleClass().add("cancel");
        }

        alert.showAndWait();
    }
}