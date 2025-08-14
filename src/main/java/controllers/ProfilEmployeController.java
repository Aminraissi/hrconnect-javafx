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
import models.enums.Role;
import services.UtilisateurCrud;

import java.io.IOException;
import java.util.List;

public class ProfilEmployeController implements ParentController {
    @FXML
    private TextField searchStagiaireField;
    @FXML
    private VBox stagiaireContainer; // For displaying Stagiaires
    @FXML
    private VBox rhManagerContainer; // For displaying RH and Managers
    @FXML
    private VBox sidePanel; // Side panel for navigation

    private UtilisateurCrud utilisateurCrud = new UtilisateurCrud();

    @FXML
    public void initialize() {
        setupSidePanel();
        loadStagiaires();
    }

    private void setupSidePanel() {
        Button stagiaireButton = new Button("Gestion des Stagiaires");
        stagiaireButton.setOnAction(event -> loadStagiaires());

        Button rhManagerButton = new Button("Voir RH et Manager");
        rhManagerButton.setOnAction(event -> loadRHAndManagers());

        sidePanel.getChildren().addAll(stagiaireButton, rhManagerButton);
    }

    private void loadStagiaires() {
        // Fetch all Stagiaires
        List<Utilisateur> stagiaires = utilisateurCrud.getUtilisateursByRole(Role.USER);

        // Clear the VBox before loading new results
        stagiaireContainer.getChildren().clear();
        rhManagerContainer.getChildren().clear();
        // Display the results
        for (Utilisateur stagiaire : stagiaires) {
            HBox stagiaireBox = createUserBox(stagiaire, true); // true = show CRUD buttons
            stagiaireContainer.getChildren().add(stagiaireBox);
        }
    }

    private void loadRHAndManagers() {
        // Fetch RH and Managers
        List<Utilisateur> rhManagers = utilisateurCrud.getUtilisateursByRole(Role.RH);
        rhManagers.addAll(utilisateurCrud.getUtilisateursByRole(Role.MANAGER));

        // Clear the VBox before loading new results
        stagiaireContainer.getChildren().clear();
        rhManagerContainer.getChildren().clear();

        // Display the results
        for (Utilisateur user : rhManagers) {
            HBox userBox = createUserBox(user, false); // false = show contact button only
            rhManagerContainer.getChildren().add(userBox);
        }
    }

    private HBox createUserBox(Utilisateur user, boolean showCRUDButtons) {
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

        // Add buttons based on the role
        if (showCRUDButtons) {
            // Add CRUD buttons for Stagiaires
            Button modifyButton = new Button("Modifier");
            modifyButton.setOnAction(event -> handleModifyUser(user));
            modifyButton.getStyleClass().add("modify-button");

            Button deleteButton = new Button("Supprimer");
            deleteButton.setOnAction(event -> handleDeleteUser(user));
            deleteButton.getStyleClass().add("delete-button");

            userBox.getChildren().addAll(nomLabel, prenomLabel, emailLabel, roleLabel, modifyButton, deleteButton);
        } else {
            // Add a "Contact" button for RH and Managers
            Button contactButton = new Button("Contact");
            contactButton.setOnAction(event -> handleContactUser(user));
            contactButton.getStyleClass().add("contact-button");

            userBox.getChildren().addAll(nomLabel, prenomLabel, emailLabel, roleLabel, contactButton);
        }

        return userBox;
    }

    @FXML
    private void handleSearchStagiaire() {
        String searchTerm = searchStagiaireField.getText().trim();
        if (searchTerm.isEmpty()) {
            showAlert("Veuillez entrer un terme de recherche.");
            return;
        }

        // Fetch Stagiaires matching the search term
        List<Utilisateur> stagiaires = utilisateurCrud.searchByNameOrEmail(searchTerm, Role.USER);

        // Clear the VBox before loading new results
        stagiaireContainer.getChildren().clear();

        // Display the results
        for (Utilisateur stagiaire : stagiaires) {
            HBox stagiaireBox = createUserBox(stagiaire, true); // true = show CRUD buttons
            stagiaireContainer.getChildren().add(stagiaireBox);
        }
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

        // Reload the Stagiaires
        loadStagiaires();
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
        loadStagiaires();
    }
}