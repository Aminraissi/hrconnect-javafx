package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Utilisateur;
import services.UtilisateurCrud;

import java.io.IOException;
import java.util.List;

public class ProfilManagerController implements ParentController {
    @FXML
    private TextField searchUserField;
    @FXML
    private ComboBox<String> sortComboBox;
    @FXML
    private VBox userContainer; // For displaying all users
    @FXML
    private VBox sidePanel; // Side panel for navigation

    private UtilisateurCrud utilisateurCrud = new UtilisateurCrud();

    @FXML
    public void initialize() {
        // Add sorting options to the ComboBox
        sortComboBox.getItems().addAll("id", "nom", "prenom", "email", "role");
        sortComboBox.setValue("id"); // Default sorting by ID

        setupSidePanel();
        loadAllUsers();
    }

    private void setupSidePanel() {
        Button userManagementButton = new Button("Gestion des Utilisateurs");
        userManagementButton.setOnAction(event -> loadAllUsers());
        userManagementButton.getStyleClass().add("side-button");

        sidePanel.getChildren().add(userManagementButton);
    }

    private void loadAllUsers() {
        List<Utilisateur> users = utilisateurCrud.afficherEntite();
        loadUsers(users);
    }

    private void loadUsers(List<Utilisateur> users) {
        // Clear the VBox before loading new users
        userContainer.getChildren().clear();

        // Create a UI component for each user
        for (Utilisateur user : users) {
            HBox userBox = createUserBox(user);
            userContainer.getChildren().add(userBox);
        }
    }

    @FXML
    private void handleSort() {
        String sortBy = sortComboBox.getValue();
        List<Utilisateur> sortedUsers = utilisateurCrud.sortUtilisateurs(sortBy);
        loadUsers(sortedUsers);
    }

    @FXML
    private void handleSearchUser() {
        String searchTerm = searchUserField.getText().trim();
        if (searchTerm.isEmpty()) {
            showAlert("Veuillez entrer un terme de recherche.");
            return;
        }

        // Fetch users matching the search term
        List<Utilisateur> users = utilisateurCrud.searchByNameOrEmail(searchTerm);

        // Clear the VBox before loading new results
        userContainer.getChildren().clear();

        // Display the results
        for (Utilisateur user : users) {
            HBox userBox = createUserBox(user);
            userContainer.getChildren().add(userBox);
        }
    }

    private HBox createUserBox(Utilisateur user) {
        HBox userBox = new HBox(10); // Spacing between elements
        userBox.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-width: 1; -fx-background-color: #004080; -fx-border-radius: 5; -fx-background-radius: 5;");

        // Add user details (Nom, Prenom, Email, Role)
        Label nomLabel = new Label("Nom: " + user.getNom());
        Label prenomLabel = new Label("Prénom: " + user.getPrenom());
        Label emailLabel = new Label("Email: " + user.getEmail());
        Label roleLabel = new Label("Role: " + user.getroles());

        nomLabel.setStyle("-fx-text-fill: white;");
        prenomLabel.setStyle("-fx-text-fill: white;");
        emailLabel.setStyle("-fx-text-fill: white;");
        roleLabel.setStyle("-fx-text-fill: white;");

        // Add CRUD buttons
        Button modifyButton = new Button("Modifier");
        modifyButton.setOnAction(event -> handleModifyUser(user));
        modifyButton.getStyleClass().add("modify-button");

        Button deleteButton = new Button("Supprimer");
        deleteButton.setOnAction(event -> handleDeleteUser(user));
        deleteButton.getStyleClass().add("delete-button");

        // Add a "Contact" button
        Button contactButton = new Button("Contact");
        contactButton.setOnAction(event -> handleContactUser(user));
        contactButton.getStyleClass().add("contact-button");

        // Add all components to the HBox
        userBox.getChildren().addAll(nomLabel, prenomLabel, emailLabel, roleLabel, modifyButton, deleteButton, contactButton);

        return userBox;
    }

    private void handleModifyUser(Utilisateur user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditProfilePage.fxml"));
            Parent root = loader.load();

            EditProfilePageController controller = loader.getController();
            controller.setUser(user);
            controller.setParentController(this);
            controller.setRole(user.getroles());

            Stage stage = new Stage();
            stage.setTitle("Modifier Profil");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteUser(Utilisateur user) {
        // Delete the user from the database
        utilisateurCrud.supprimerEntite(user.getId());

        // Reload the users
        loadAllUsers();
    }

    private void handleContactUser(Utilisateur user) {
        // Open a mailing dialog to contact the user
        openMailingDialog(user.getEmail());
    }

    private void openMailingDialog(String recipientEmail) {
        // Create a dialog for sending an email
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Envoyer un message");
        dialog.setHeaderText("Envoyer un message à " + recipientEmail);

        // Set the buttons
        ButtonType sendButtonType = new ButtonType("Envoyer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(sendButtonType, ButtonType.CANCEL);

        // Create the form
        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Entrez votre message ici...");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Message:"), 0, 0);
        grid.add(messageArea, 1, 0);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a message
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == sendButtonType) {
                return messageArea.getText();
            }
            return null;
        });

        // Show the dialog and handle the result
        dialog.showAndWait().ifPresent(message -> {
            // Send the email (you can integrate your mailing logic here)
            System.out.println("Message envoyé à " + recipientEmail + ": " + message);
            showAlert("Message envoyé avec succès !");
        });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }

    @Override
    public void loadMembers() {
        loadAllUsers();
    }
}