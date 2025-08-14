package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Utilisateur;
import models.enums.Role;
import services.UtilisateurCrud;

/**
 * Interface for parent controllers that need to be refreshed after user operations
 */
interface UserUpdateController {
    void refreshUserData(Utilisateur user);
}

public class EditUserDialogController {
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
    private ComboBox<String> roleComboBox;
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;

    private Utilisateur user;
    private UtilisateurCrud utilisateurCrud = new UtilisateurCrud();
    private UserUpdateController parentController;
    private boolean isRhMode = false;
    private String originalRole;

    @FXML
    public void initialize() {
        // Populate role ComboBox with all possible roles
        for (Role role : Role.values()) {
            roleComboBox.getItems().add(role.name());
        }
    }

    /**
     * Configure dialog for RH mode (limited role options)
     */
    public void setRhMode(boolean isRh) {
        this.isRhMode = isRh;

        if (isRh) {
            // RH can only modify to USER or EMPLOYE roles
            // First, store all items
            String currentRole = roleComboBox.getValue();
            roleComboBox.getItems().clear();

            // Add only allowed roles for RH
            roleComboBox.getItems().addAll("USER", "EMPLOYE");

            // Try to restore previous selection if allowed
            if (currentRole != null && (currentRole.equals("USER") || currentRole.equals("EMPLOYE"))) {
                roleComboBox.setValue(currentRole);
            } else {
                // Default to USER if previous role not allowed
                roleComboBox.setValue("USER");
            }
        } else {
            // Admin can modify to any role
            // Check if we need to repopulate
            if (roleComboBox.getItems().size() != Role.values().length) {
                String currentRole = roleComboBox.getValue();
                roleComboBox.getItems().clear();

                for (Role role : Role.values()) {
                    roleComboBox.getItems().add(role.name());
                }

                // Try to restore previous selection
                if (currentRole != null && !currentRole.isEmpty()) {
                    roleComboBox.setValue(currentRole);
                }
            }
        }
    }

    public void setUser(Utilisateur user) {
        this.user = user;
        this.originalRole = user.getroles();

        // Populate fields with user data
        cinField.setText(String.valueOf(user.getCin()));
        phoneField.setText(user.gettel());
        nomField.setText(user.getNom());
        prenomField.setText(user.getPrenom());
        emailField.setText(user.getEmail());

        // Password field is left empty - will only be updated if a new one is provided
        passwordField.setText("");

        // Set role and apply RH mode restrictions if needed
        roleComboBox.setValue(user.getroles());
        if (isRhMode) {
            setRhMode(true);
        }
    }

    /**
     * Allow both Admin and RH controllers to be set as parent
     */
    public void setParentController(Object controller) {
        if (controller instanceof ProfilAdminController) {
            this.parentController = new UserUpdateController() {
                @Override
                public void refreshUserData(Utilisateur updatedUser) {
                    ((ProfilAdminController) controller).loadUsers();
                }
            };
            setRhMode(false);
        } else if (controller instanceof ProfilRhController) {
            this.parentController = new UserUpdateController() {
                @Override
                public void refreshUserData(Utilisateur updatedUser) {
                    ProfilRhController rhController = (ProfilRhController) controller;
                    // Refresh the appropriate list based on the user's role
                    if (updatedUser.getroles().equals("USER")) {
                        rhController.loadMembers();
                    } else if (updatedUser.getroles().equals("EMPLOYE")) {
                        rhController.loadEmployees();
                    } else {
                        // If role was changed to something else, refresh both
                        rhController.loadMembers();
                        rhController.loadEmployees();
                    }
                }
            };
            setRhMode(true);
        }
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
                    roleComboBox.getValue() == null) {

                showAlert(Alert.AlertType.ERROR, "Champs obligatoires",
                        "Veuillez remplir tous les champs obligatoires.");
                return;
            }

            // Validate role changes if in RH mode
            if (isRhMode && !isAllowedRoleChangeForRH(originalRole, roleComboBox.getValue())) {
                showAlert(Alert.AlertType.ERROR, "Modification interdite",
                        "En tant que RH, vous ne pouvez pas modifier ce type d'utilisateur vers ce rôle.");
                return;
            }

            // Update user object with form values
            user.setCin(Integer.parseInt(cinField.getText().trim()));
            user.settel(phoneField.getText().trim());
            user.setNom(nomField.getText().trim());
            user.setPrenom(prenomField.getText().trim());
            user.setEmail(emailField.getText().trim());
            user.setroles(roleComboBox.getValue());

            // Update password only if a new one is provided
            if (!passwordField.getText().isEmpty()) {
                user.setpassword(passwordField.getText());
            }

            // Save changes to database
            utilisateurCrud.modifierEntite(user);

            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Succès",
                    "Utilisateur modifié avec succès.");

            // Refresh user list in parent controller
            if (parentController != null) {
                parentController.refreshUserData(user);
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

    /**
     * Check if the role change is allowed for RH users
     */
    private boolean isAllowedRoleChangeForRH(String originalRole, String newRole) {
        // RH can only modify USER and EMPLOYE roles
        if (originalRole.equals("ADMIN") || originalRole.equals("RH") || originalRole.equals("MANAGER")) {
            return false;
        }

        // RH can only change to USER or EMPLOYE
        return newRole.equals("USER") || newRole.equals("EMPLOYE");
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

        // Apply modern styling to alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/modern-style.css").toExternalForm());

        // Style buttons
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