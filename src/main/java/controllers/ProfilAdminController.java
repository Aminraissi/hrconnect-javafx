package controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.Utilisateur;
import services.UtilisateurCrud;
import utils.SessionManager;
import utils.ShowMenu;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class ProfilAdminController implements  ShowMenu {
    @FXML
    private VBox userContainer;
    @FXML
    private ComboBox<String> sortComboBox;
    @FXML
    private TextField searchField;
    @FXML
    private Label dateTimeLabel;
    @FXML
    private Label userNameLabel;
    @FXML
    private Label userRoleLabel;
    @FXML
    private Label sessionInfoLabel;
    @FXML
    private AnchorPane menu;

    private Timer timer;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private UtilisateurCrud utilisateurCrud = new UtilisateurCrud();
    private Utilisateur currentUser;
    private List<Utilisateur> allUsers; // Store all users for filtering

    @FXML
    public void initialize() {


        initializeMenu(menu);
        // Set current user from session
        currentUser = SessionManager.getInstance().getCurrentUser();

        // Initialize user info
        if (currentUser != null) {
            userNameLabel.setText(currentUser.getPrenom() + " " + currentUser.getNom());
            userRoleLabel.setText(currentUser.getroles());

            // Format session start time
            LocalDateTime now = LocalDateTime.now();
            sessionInfoLabel.setText("Session active depuis: " + now.format(DateTimeFormatter.ofPattern("HH:mm")));
        }

        // Add sorting options to the ComboBox
        sortComboBox.getItems().addAll("id", "nom", "prenom", "email", "role");
        sortComboBox.setValue("id"); // Default sorting by ID

        // Update date and time
        updateDateTime();
        startDateTimeUpdater();

        // Load all users initially
        allUsers = utilisateurCrud.afficherEntite();
        loadUsers(allUsers);

        // Add search field listener for real-time search
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            performSearch(newValue);
        });
    }

    private void performSearch(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            loadUsers(allUsers); // Show all users if search is empty
            return;
        }

        searchTerm = searchTerm.toLowerCase().trim();
        final String searchValue = searchTerm; // Final copy for lambda

        // Filter users based on the search term across multiple fields
        List<Utilisateur> filteredUsers = allUsers.stream()
                .filter(user ->
                        String.valueOf(user.getId()).contains(searchValue) ||
                                (user.getNom() != null && user.getNom().toLowerCase().contains(searchValue)) ||
                                (user.getPrenom() != null && user.getPrenom().toLowerCase().contains(searchValue)) ||
                                (user.getEmail() != null && user.getEmail().toLowerCase().contains(searchValue)) ||
                                (user.getroles() != null && user.getroles().toLowerCase().contains(searchValue)) ||
                                (user.gettel() != null && user.gettel().contains(searchValue))
                )
                .collect(Collectors.toList());

        loadUsers(filteredUsers);
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

    @FXML
    private void handleSort() {
        String sortBy = sortComboBox.getValue();
        List<Utilisateur> sortedUsers = utilisateurCrud.sortUtilisateurs(sortBy);

        // Apply current search filter to sorted results
        String currentSearch = searchField.getText().trim();
        if (!currentSearch.isEmpty()) {
            performSearch(currentSearch);
        } else {
            loadUsers(sortedUsers);
        }
        allUsers = sortedUsers; // Update the base list with the sorted order
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
            ((Stage) userNameLabel.getScene().getWindow()).close();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de se déconnecter: " + e.getMessage());
        }
    }

    private void loadUsers(List<Utilisateur> users) {
        // Clear the VBox before loading new users
        userContainer.getChildren().clear();

        if (users.isEmpty()) {
            Label noResultsLabel = new Label("Aucun résultat trouvé");
            noResultsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            userContainer.getChildren().add(noResultsLabel);
            return;
        }

        // Create a UI component for each user
        for (Utilisateur user : users) {
            VBox userCard = createUserCard(user);
            userContainer.getChildren().add(userCard);
        }
    }

    public void loadUsers() {
        allUsers = utilisateurCrud.afficherEntite(); // Refresh the list of all users
        loadUsers(allUsers); // Default load all users
    }

    private VBox createUserCard(Utilisateur user) {
        VBox userCard = new VBox(10); // Spacing between elements
        userCard.getStyleClass().add("rh-card");

        // User info section
        HBox topSection = new HBox(15);
        topSection.setAlignment(Pos.CENTER_LEFT);

        // User icon
        FontAwesomeIconView userIcon = new FontAwesomeIconView(FontAwesomeIcon.USER_CIRCLE);
        userIcon.setSize("32");
        userIcon.setStyleClass("section-icon");

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
        try {
            // Create a custom dialog instead of using Alert
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Détails de l'utilisateur");
            dialogStage.initOwner(userContainer.getScene().getWindow());

            // Create root container
            BorderPane root = new BorderPane();
            root.setStyle("-fx-background-color: #263238; -fx-padding: 20;");

            // Header
            HBox header = new HBox(15);
            header.setAlignment(Pos.CENTER_LEFT);
            header.setStyle("-fx-padding: 0 0 15 0; -fx-border-color: transparent transparent #546E7A transparent; -fx-border-width: 0 0 1 0;");

            FontAwesomeIconView userIcon = new FontAwesomeIconView(FontAwesomeIcon.USER_CIRCLE);
            userIcon.setSize("36");
            userIcon.setStyle("-fx-fill: #64B5F6;");

            Label headerLabel = new Label(user.getPrenom() + " " + user.getNom());
            headerLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

            header.getChildren().addAll(userIcon, headerLabel);

            // Content - User details
            VBox detailsBox = new VBox(15);
            detailsBox.setStyle("-fx-padding: 20 0;");

            // Function to create a styled detail row
            BiFunction<String, String, HBox> createDetailRow = (label, value) -> {
                HBox row = new HBox(15);
                row.setAlignment(Pos.CENTER_LEFT);

                Label labelNode = new Label(label);
                labelNode.setStyle("-fx-text-fill: #B0BEC5; -fx-min-width: 100;");

                Label valueNode = new Label(value);
                valueNode.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

                row.getChildren().addAll(labelNode, valueNode);
                return row;
            };

            detailsBox.getChildren().addAll(
                    createDetailRow.apply("ID:", String.valueOf(user.getId())),
                    createDetailRow.apply("CIN:", String.valueOf(user.getCin())),                    createDetailRow.apply("Prénom:", user.getPrenom()),
                    createDetailRow.apply("Email:", user.getEmail()),
                    createDetailRow.apply("Téléphone:", user.gettel()),
                    createDetailRow.apply("Rôle:", user.getroles())
            );

            // Role badge
            HBox roleBadge = new HBox();
            roleBadge.setAlignment(Pos.CENTER_LEFT);
            roleBadge.setStyle("-fx-padding: 15 0;");

            Label badgeLabel = new Label(user.getroles());
            badgeLabel.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 5 15; -fx-background-radius: 20;");

            roleBadge.getChildren().add(badgeLabel);

            // Close button
            Button closeButton = new Button("Fermer");
            closeButton.setStyle("-fx-background-color: #546E7A; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 4;");
            closeButton.setOnAction(e -> dialogStage.close());

            HBox buttonBox = new HBox();
            buttonBox.setAlignment(Pos.CENTER_RIGHT);
            buttonBox.setStyle("-fx-padding: 15 0 0 0;");
            buttonBox.getChildren().add(closeButton);

            // Add all elements to the root
            root.setTop(header);
            root.setCenter(detailsBox);
            root.setBottom(buttonBox);

            // Create scene and show dialog
            Scene scene = new Scene(root, 400, 450);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'afficher les détails: " + e.getMessage());
        }
    }

    private void handleModifyUser(Utilisateur user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditUserDialog.fxml"));
            Parent root = loader.load();

            EditUserDialogController controller = loader.getController();
            controller.setUser(user);
            controller.setParentController(this); // Pass admin controller

            Stage stage = new Stage();
            stage.setTitle("Modifier Utilisateur");
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de charger la page de modification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {
        performSearch(searchField.getText());
    }

    @FXML
    private void handleAddUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddUserDialog.fxml"));
            Parent root = loader.load();

            AddUserDialogController controller = loader.getController();
            controller.setParentController(this); // This now accepts an Object

            Stage stage = new Stage();
            stage.setTitle("Ajouter Utilisateur");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.showAndWait();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de charger la page d'ajout: " + e.getMessage());
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
        dialogPane.getStyleClass().add("modern-dialog");
        dialogPane.setStyle("-fx-background-color: #263238;");
        confirmAlert.getDialogPane().getButtonTypes().forEach(buttonType -> {
            Button button = (Button) dialogPane.lookupButton(buttonType);
            if (buttonType == ButtonType.OK) {
                button.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
            } else {
                button.setStyle("-fx-background-color: #546E7A; -fx-text-fill: white;");
            }
        });

        Label headerLabel = (Label) dialogPane.lookup(".header-panel .label");
        if (headerLabel != null) {
            headerLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        }

        Label contentLabel = (Label) dialogPane.lookup(".content");
        if (contentLabel != null) {
            contentLabel.setStyle("-fx-text-fill: #CFD8DC;");
        }

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                utilisateurCrud.supprimerEntite(user.getId());
                loadUsers();

                // Show success message
                showAlert(Alert.AlertType.INFORMATION, "Succès",
                        "L'utilisateur a été supprimé avec succès.");
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Apply modern styling to alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #263238;");
        dialogPane.getStyleClass().add("modern-dialog");

        // Style the content text
        Label contentLabel = (Label) dialogPane.lookup(".content");
        if (contentLabel != null) {
            contentLabel.setStyle("-fx-text-fill: white;");
        }

        // Style the buttons
        alert.getDialogPane().getButtonTypes().forEach(buttonType -> {
            Button button = (Button) dialogPane.lookupButton(buttonType);
            button.setStyle("-fx-background-color: #546E7A; -fx-text-fill: white;");
        });

        alert.showAndWait();
    }

    // Utility interface for creating detail rows
    private interface BiFunction<T, U, R> {
        R apply(T t, U u);
    }
}