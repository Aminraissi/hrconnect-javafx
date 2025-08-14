package controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Utilisateur;
import models.enums.Role;
import services.UtilisateurCrud;
import utils.SessionManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ProfilRhController {
    // Profile Form Fields
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
    private CheckBox showPasswordCheck;

    // Navigation Elements
    @FXML
    private Button profileButton;
    @FXML
    private Button membersButton;
    @FXML
    private Button employeesButton;
    @FXML
    private Button statsButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button logoutButton;

    // Content Containers
    @FXML
    private VBox profileContent;
    @FXML
    private VBox membersContent;
    @FXML
    private VBox employeesContent;
    @FXML
    private VBox statsContent;
    @FXML
    private VBox settingsContent;

    // User Lists
    @FXML
    private VBox memberContainer;
    @FXML
    private VBox employeeContainer;

    // Search Fields
    @FXML
    private TextField memberSearchField;
    @FXML
    private TextField employeeSearchField;

    // Stats Labels
    @FXML
    private Label employeeCountLabel;
    @FXML
    private Label internCountLabel;
    @FXML
    private Label totalUsersLabel;

    // Info Labels
    @FXML
    private Label userNameLabel;
    @FXML
    private Label userRoleLabel;
    @FXML
    private Label dateTimeLabel;
    @FXML
    private Label sessionInfoLabel;

    private UtilisateurCrud utilisateurCrud = new UtilisateurCrud();
    private Utilisateur currentUser;
    private Timer timer;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        // Set current user from session
        currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser != null) {
            setupUserInfo();
            setupPasswordVisibility();
            setupNavigation();
            updateDateTime();
            startDateTimeUpdater();
            loadUserData();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur de session", "Utilisateur non connecté");
            handleLogout();
        }

        // Add search field listeners for real-time search
        memberSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            handleMemberSearch(newValue);
        });

        employeeSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            handleEmployeeSearch(newValue);
        });
    }

    private void setupUserInfo() {
        userNameLabel.setText(currentUser.getPrenom() + " " + currentUser.getNom());
        userRoleLabel.setText(currentUser.getroles());

        // Format session start time
        LocalDateTime now = LocalDateTime.now();
        sessionInfoLabel.setText("Session active depuis: " + now.format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    private void setupPasswordVisibility() {
        showPasswordCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
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

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (showPasswordCheck.isSelected()) {
                passwordTextField.setText(newValue);
            }
        });

        passwordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (showPasswordCheck.isSelected()) {
                passwordField.setText(newValue);
            }
        });
    }

    private void setupNavigation() {
        // Clear any existing CSS classes and set initial view
        profileButton.getStyleClass().add("active-nav");

        // Profile Button
        profileButton.setOnAction(event -> {
            resetNavigation();
            profileButton.getStyleClass().add("active-nav");
            showContent(profileContent);
        });

        // Members Button
        membersButton.setOnAction(event -> {
            resetNavigation();
            membersButton.getStyleClass().add("active-nav");
            showContent(membersContent);
            loadMembers();
        });

        // Employees Button
        employeesButton.setOnAction(event -> {
            resetNavigation();
            employeesButton.getStyleClass().add("active-nav");
            showContent(employeesContent);
            loadEmployees();
        });

        // Stats Button
        statsButton.setOnAction(event -> {
            resetNavigation();
            statsButton.getStyleClass().add("active-nav");
            showContent(statsContent);
            updateStats();
        });

        // Settings Button
        settingsButton.setOnAction(event -> {
            resetNavigation();
            settingsButton.getStyleClass().add("active-nav");
            showContent(settingsContent);
        });
    }

    private void resetNavigation() {
        profileButton.getStyleClass().remove("active-nav");
        membersButton.getStyleClass().remove("active-nav");
        employeesButton.getStyleClass().remove("active-nav");
        statsButton.getStyleClass().remove("active-nav");
        settingsButton.getStyleClass().remove("active-nav");
    }

    private void showContent(VBox content) {
        // Hide all content containers
        profileContent.setVisible(false);
        membersContent.setVisible(false);
        employeesContent.setVisible(false);
        statsContent.setVisible(false);
        settingsContent.setVisible(false);

        // Show the selected content
        content.setVisible(true);
    }

    private void loadUserData() {
        // Set profile form fields
        nomField.setText(currentUser.getNom());
        prenomField.setText(currentUser.getPrenom());
        emailField.setText(currentUser.getEmail());

        // Don't display actual password for security reasons
        passwordField.setText("");
        passwordTextField.setText("");
    }

    private void startDateTimeUpdater() {
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> updateDateTime());
            }
        }, 0, 60000); // Update every minute
    }

    private void updateDateTime() {
        LocalDateTime now = LocalDateTime.now();
        dateTimeLabel.setText(now.format(dateFormatter));
    }

    public void loadMembers() {
        // Clear the VBox before loading new members
        memberContainer.getChildren().clear();

        // Get all users with role USER (Stagiaires)
        List<Utilisateur> members = utilisateurCrud.getUtilisateursByRole(Role.USER);

        if (members.isEmpty()) {
            Label noResultsLabel = new Label("Aucun stagiaire trouvé");
            noResultsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            memberContainer.getChildren().add(noResultsLabel);
            return;
        }

        // Create a UI component for each member
        for (Utilisateur member : members) {
            VBox memberCard = createUserCard(member, "stagiaire");
            memberContainer.getChildren().add(memberCard);
        }
    }

    public void loadEmployees() {
        // Clear the VBox before loading new employees
        employeeContainer.getChildren().clear();

        // Get all users with role EMPLOYE
        List<Utilisateur> employees = utilisateurCrud.getUtilisateursByRole(Role.EMPLOYE);

        if (employees.isEmpty()) {
            Label noResultsLabel = new Label("Aucun employé trouvé");
            noResultsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            employeeContainer.getChildren().add(noResultsLabel);
            return;
        }

        // Create a UI component for each employee
        for (Utilisateur employee : employees) {
            VBox employeeCard = createUserCard(employee, "employé");
            employeeContainer.getChildren().add(employeeCard);
        }
    }

    private VBox createUserCard(Utilisateur user, String userType) {
        VBox userCard = new VBox(10);
        userCard.getStyleClass().add("rh-card");

        // User info section
        HBox topSection = new HBox(15);
        topSection.setAlignment(Pos.CENTER_LEFT);

        // User icon
        FontAwesomeIconView userIcon = new FontAwesomeIconView(
                userType.equals("employé") ? FontAwesomeIcon.USER_CIRCLE : FontAwesomeIcon.GRADUATION_CAP);
        userIcon.setSize("32");
        userIcon.getStyleClass().add("section-icon");

        // User details
        VBox userInfo = new VBox(5);
        userInfo.getStyleClass().add("rh-card-info");

        Label nameLabel = new Label(user.getPrenom() + " " + user.getNom());
        nameLabel.getStyleClass().add("rh-name");

        Label emailLabel = new Label(user.getEmail());
        emailLabel.getStyleClass().add("rh-email");

        Label roleLabel = new Label("Role: " + user.getroles());
        roleLabel.setStyle("-fx-text-fill: #90CAF9;");

        userInfo.getChildren().addAll(nameLabel, emailLabel, roleLabel);

        // Badge for ID
        Label idBadge = new Label("ID: " + user.getId());
        idBadge.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-padding: 3 8; -fx-background-radius: 3px;");

        HBox.setHgrow(userInfo, Priority.ALWAYS);
        topSection.getChildren().addAll(userIcon, userInfo, idBadge);

        // Actions section
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Button viewButton = new Button("Voir détails");
        viewButton.getStyleClass().add("action-button");
        viewButton.setOnAction(event -> handleViewUser(user));

        Button modifyButton = new Button("Modifier");
        modifyButton.getStyleClass().add("primary-button");
        modifyButton.setOnAction(event -> handleModifyUser(user));

        Button deleteButton = new Button("Supprimer");
        deleteButton.getStyleClass().add("secondary-button");
        deleteButton.setOnAction(event -> handleDeleteUser(user));

        actions.getChildren().addAll(viewButton, modifyButton, deleteButton);

        userCard.getChildren().addAll(topSection, actions);
        return userCard;
    }

    private void handleViewUser(Utilisateur user) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails de l'utilisateur");
        alert.setHeaderText("Information complète");

        VBox detailsBox = new VBox(10);
        detailsBox.getChildren().addAll(
                new Label("ID: " + user.getId()),
                new Label("CIN: " + user.getCin()),
                new Label("Nom: " + user.getNom()),
                new Label("Prénom: " + user.getPrenom()),
                new Label("Email: " + user.getEmail()),
                new Label("Téléphone: " + user.gettel()),
                new Label("Rôle: " + user.getroles())
        );

        alert.getDialogPane().setContent(detailsBox);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/alert-styles.css").toExternalForm());

        Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        okButton.getStyleClass().add("primary-button");

        alert.showAndWait();
    }

    private void handleModifyUser(Utilisateur user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditUserDialog.fxml"));
            Parent root = loader.load();

            EditUserDialogController controller = loader.getController();
            controller.setUser(user);
            controller.setParentController(this); // Pass RH controller

            Stage stage = new Stage();
            stage.setTitle("Modifier Utilisateur");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // The refresh will be handled by the controller's callback

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page de modification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleDeleteUser(Utilisateur user) {
        // Confirm deletion with an alert
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmer la suppression");
        confirmAlert.setHeaderText("Êtes-vous sûr de vouloir supprimer cet utilisateur ?");
        confirmAlert.setContentText("Cette action est irréversible.");

        // Apply modern styling to alert
        DialogPane dialogPane = confirmAlert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/alert-styles.css").toExternalForm());

        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.setText("OK");
        okButton.getStyleClass().add("primary-button");

        Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
        cancelButton.setText("Annuler");
        cancelButton.getStyleClass().add("cancel");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Delete the user
                utilisateurCrud.supprimerEntite(user.getId());

                // Refresh appropriate list
                if (user.getroles().equals("USER")) {
                    loadMembers();
                } else if (user.getroles().equals("EMPLOYE")) {
                    loadEmployees();
                }

                // Update statistics if visible
                if (statsContent.isVisible()) {
                    updateStats();
                }

                // Show success message
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur supprimé avec succès");
            }
        });
    }

    private void updateStats() {
        // Count employees
        List<Utilisateur> employees = utilisateurCrud.getUtilisateursByRole(Role.EMPLOYE);
        employeeCountLabel.setText(String.valueOf(employees.size()));

        // Count interns (USER role)
        List<Utilisateur> interns = utilisateurCrud.getUtilisateursByRole(Role.USER);
        internCountLabel.setText(String.valueOf(interns.size()));

        // Total users (all roles)
        int totalUsers = utilisateurCrud.afficherEntite().size();
        totalUsersLabel.setText(String.valueOf(totalUsers));
    }

    private void handleMemberSearch(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            loadMembers(); // Reload all members if search term is empty
        } else {
            // Search for members matching the search term
            List<Utilisateur> searchResults = utilisateurCrud.searchByNameOrEmail(searchTerm, Role.USER);

            // Clear the container
            memberContainer.getChildren().clear();

            if (searchResults.isEmpty()) {
                Label noResultsLabel = new Label("Aucun résultat trouvé");
                noResultsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                memberContainer.getChildren().add(noResultsLabel);
                return;
            }

            // Display matching members
            for (Utilisateur member : searchResults) {
                VBox memberCard = createUserCard(member, "stagiaire");
                memberContainer.getChildren().add(memberCard);
            }
        }
    }

    private void handleEmployeeSearch(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            loadEmployees(); // Reload all employees if search term is empty
        } else {
            // Search for employees matching the search term
            List<Utilisateur> searchResults = utilisateurCrud.searchByNameOrEmail(searchTerm, Role.EMPLOYE);

            // Clear the container
            employeeContainer.getChildren().clear();

            if (searchResults.isEmpty()) {
                Label noResultsLabel = new Label("Aucun résultat trouvé");
                noResultsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                employeeContainer.getChildren().add(noResultsLabel);
                return;
            }

            // Display matching employees
            for (Utilisateur employee : searchResults) {
                VBox employeeCard = createUserCard(employee, "employé");
                employeeContainer.getChildren().add(employeeCard);
            }
        }
    }

    @FXML
    private void handleAddMember() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddUserDialog.fxml"));
            Parent root = loader.load();

            AddUserDialogController controller = loader.getController();
            controller.setParentController(this); // Pass the RH controller
            controller.presetRoleToUser();  // Preset to USER role

            Stage stage = new Stage();
            stage.setTitle("Ajouter un Stagiaire");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // No need to call loadMembers() here as the controller will do it via refreshUsers()

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de charger la page d'ajout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddEmployee() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddUserDialog.fxml"));
            Parent root = loader.load();

            AddUserDialogController controller = loader.getController();
            controller.setParentController(this); // Pass the RH controller
            controller.presetRoleToEmployee(); // Preset to EMPLOYE role

            Stage stage = new Stage();
            stage.setTitle("Ajouter un Employé");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // No need to call loadEmployees() here as the controller will do it via refreshUsers()

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de charger la page d'ajout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleModifyProfile() {
        // Validate inputs
        if (nomField.getText().trim().isEmpty() ||
                prenomField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty()) {

            showAlert(Alert.AlertType.ERROR, "Erreur de saisie",
                    "Veuillez remplir tous les champs obligatoires.");
            return;
        }

        // Store original password
        String originalPassword = currentUser.getpassword();

        // Update user object
        currentUser.setNom(nomField.getText().trim());
        currentUser.setPrenom(prenomField.getText().trim());
        currentUser.setEmail(emailField.getText().trim());

        // Update password only if a new one is provided
        String currentPassword = passwordField.isVisible() ? passwordField.getText() : passwordTextField.getText();
        if (!currentPassword.isEmpty()) {
            currentUser.setpassword(currentPassword);
        } else {
            // Restore original password if field is empty
            currentUser.setpassword(originalPassword);
        }

        // Save changes
        utilisateurCrud.modifierEntite(currentUser);

        // Update session with the modified user
        SessionManager.getInstance().setCurrentUser(currentUser);

        // Update displayed name
        userNameLabel.setText(currentUser.getPrenom() + " " + currentUser.getNom());

        // Show success message
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Profil mis à jour avec succès !");
    }

    @FXML
    private void handleReset() {
        loadUserData();
    }

    @FXML
    private void handleLogout() {
        try {
            // Clear session
            SessionManager.getInstance().logout();

            // Stop timer
            if (timer != null) {
                timer.cancel();
            }

            // Navigate to login page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/authentification.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Authentification");
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current window
            ((Stage) logoutButton.getScene().getWindow()).close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Apply modern styling to alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/alert-styles.css").toExternalForm());

        // Style the OK button
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        if (okButton != null) {
            okButton.getStyleClass().add("primary-button");
        }

        alert.showAndWait();
    }
}