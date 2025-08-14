package controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import models.Mailing;
import models.Utilisateur;
import services.UtilisateurCrud;
import utils.SessionManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class ProfilMembreController extends BaseProfileController {
    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> sortComboBox;
    @FXML
    private VBox rhContainer;
    @FXML
    private Label dateTimeLabel;
    @FXML
    private TableView<RequestItem> requestsTable;

    private UtilisateurCrud utilisateurCrud = new UtilisateurCrud();
    private Utilisateur currentUser;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // For demonstration purposes (in a real app, you'd fetch this from a database)
    private static class RequestItem {
        private final String id;
        private final String type;
        private final String date;
        private final String status;

        public RequestItem(String id, String type, String date, String status) {
            this.id = id;
            this.type = type;
            this.date = date;
            this.status = status;
        }

        public String getId() { return id; }
        public String getType() { return type; }
        public String getDate() { return date; }
        public String getStatus() { return status; }
    }
    // Flag to track if password has been modified
    private boolean passwordModified = false;

    @Override
    public void initialize() {
        // First ensure the labels are available before calling the parent's initialize
        // which will trigger initializeProfileSpecifics
        super.initialize();
    }

    @Override
    protected void initializeProfileSpecifics(Utilisateur user) {
        // Store the current user
        currentUser = user;

        try {
            // Setup the form fields with user data - with null checks
            if (nomField != null) {
                nomField.setText(currentUser.getNom());
            }

            if (prenomField != null) {
                prenomField.setText(currentUser.getPrenom());
            }

            if (emailField != null) {
                emailField.setText(currentUser.getEmail());
            }

            if (passwordField != null) {
                passwordField.setText("");
                passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
                    passwordModified = !newValue.isEmpty();
                });
            }

            // Setup sorting options in the ComboBox
            if (sortComboBox != null) {
                sortComboBox.getItems().setAll("id", "nom", "prenom", "email", "roles");
                sortComboBox.setValue("nom");
            }

            // Setup the current date/time - with null check
            safeUpdateDateTime();

            // Load RH users - will handle its own null checks
            handleSearchRH();

            // Setup sample request data
            setupSampleRequests();

            // Start a timer to update date/time periodically
            if (dateTimeLabel != null) {
                startDateTimeUpdater();
            }
        } catch (Exception e) {
            System.err.println("Error in initializeProfileSpecifics: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void safeUpdateDateTime() {
        try {
            if (dateTimeLabel != null) {
                LocalDateTime now = LocalDateTime.now();
                dateTimeLabel.setText(now.format(dateFormatter));
            } else {
                System.err.println("Warning: dateTimeLabel is null in updateDateTime");
            }
        } catch (Exception e) {
            System.err.println("Error updating date/time: " + e.getMessage());
        }
    }

    private void startDateTimeUpdater() {
        Thread thread = new Thread(() -> {
            while(true) {
                Platform.runLater(this::safeUpdateDateTime);
                try {
                    Thread.sleep(60000); // Update every minute
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void setupSampleRequests() {
        try {
            if (requestsTable != null) {
                ObservableList<RequestItem> requests = FXCollections.observableArrayList(
                        new RequestItem("1", "Congé", "2025-05-01", "Approuvé"),
                        new RequestItem("2", "Formation", "2025-05-05", "En attente"),
                        new RequestItem("3", "Attestation", "2025-05-10", "Rejeté")
                );
                requestsTable.setItems(requests);

                // Make sure the table has columns
                if (requestsTable.getColumns().size() >= 5) {
                    // Setup table columns
                    TableColumn<RequestItem, String> idColumn = (TableColumn<RequestItem, String>) requestsTable.getColumns().get(0);
                    TableColumn<RequestItem, String> typeColumn = (TableColumn<RequestItem, String>) requestsTable.getColumns().get(1);
                    TableColumn<RequestItem, String> dateColumn = (TableColumn<RequestItem, String>) requestsTable.getColumns().get(2);
                    TableColumn<RequestItem, String> statusColumn = (TableColumn<RequestItem, String>) requestsTable.getColumns().get(3);
                    TableColumn<RequestItem, String> actionsColumn = (TableColumn<RequestItem, String>) requestsTable.getColumns().get(4);

                    idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getId()));
                    typeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getType()));
                    dateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDate()));

                    // Custom cell factory for status with badges
                    statusColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));
                    statusColumn.setCellFactory(column -> new TableCell<RequestItem, String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || item == null) {
                                setText(null);
                                setGraphic(null);
                            } else {
                                Label statusLabel = new Label(item);
                                statusLabel.getStyleClass().add("status-badge");

                                if (item.equalsIgnoreCase("Approuvé")) {
                                    statusLabel.getStyleClass().add("status-approved");
                                } else if (item.equalsIgnoreCase("Rejeté")) {
                                    statusLabel.getStyleClass().add("status-rejected");
                                } else {
                                    statusLabel.getStyleClass().add("status-pending");
                                }

                                setGraphic(statusLabel);
                                setText(null);
                            }
                        }
                    });

                    // Custom cell factory for action buttons
                    actionsColumn.setCellFactory(column -> new TableCell<RequestItem, String>() {
                        private final Button viewButton = new Button();

                        {
                            viewButton.getStyleClass().add("action-button");
                            viewButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.EYE));
                            viewButton.setOnAction(event -> {
                                int index = getIndex();
                                if (index >= 0 && index < getTableView().getItems().size()) {
                                    RequestItem request = getTableView().getItems().get(index);
                                    showAlert(Alert.AlertType.INFORMATION, "Détails de la demande",
                                            "Détails de la demande #" + request.getId() + " (" + request.getType() + ")");
                                }
                            });
                        }

                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                setGraphic(viewButton);
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            System.err.println("Error setting up sample requests: " + e.getMessage());
        }
    }

    @FXML
    private void handleReset() {
        if (currentUser != null) {
            if (nomField != null) nomField.setText(currentUser.getNom());
            if (prenomField != null) prenomField.setText(currentUser.getPrenom());
            if (emailField != null) emailField.setText(currentUser.getEmail());
            if (passwordField != null) passwordField.setText("");
        }
    }

    @FXML
    private void handleModifyProfile() {
        try {
            // Validate inputs
            if (nomField == null || prenomField == null || emailField == null ||
                    nomField.getText().trim().isEmpty() ||
                    prenomField.getText().trim().isEmpty() ||
                    emailField.getText().trim().isEmpty()) {

                showAlert(Alert.AlertType.ERROR, "Erreur de saisie",
                        "Veuillez remplir tous les champs obligatoires.");
                return;
            }

            // Update basic user information
            currentUser.setNom(nomField.getText().trim());
            currentUser.setPrenom(prenomField.getText().trim());
            currentUser.setEmail(emailField.getText().trim());

            // Only update password if it has been changed (not empty)
            if (passwordModified && passwordField != null) {
                currentUser.setpassword(passwordField.getText());
                System.out.println("Password has been modified to a new value");
            } else {
                // Do not modify the password in the user object if field is empty
                // The existing password hash will be preserved
                System.out.println("Password field empty, keeping original password");
            }

            // Save changes
            utilisateurCrud.modifierEntite(currentUser);

            // Update session with the modified user
            SessionManager.getInstance().setCurrentUser(currentUser);

            // Update displayed name in UI
            if (userNameLabel != null) {
                userNameLabel.setText(currentUser.getNom() + " " + currentUser.getPrenom());
            }

            // Reset the password modified flag after successful update
            passwordModified = false;

            showAlert(Alert.AlertType.INFORMATION, "Succès",
                    "Profil mis à jour avec succès !");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Une erreur est survenue lors de la modification du profil: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearchRH() {
        try {
            String searchTerm = searchField != null ? searchField.getText().trim() : "";
            List<Utilisateur> rhUsers;

            if (searchTerm.isEmpty()) {
                // Search for users with role "RH" or "ROLE_RH"
                rhUsers = utilisateurCrud.getAllUtilisateurs().stream()
                        .filter(u -> u.getroles().equals("RH") || u.getroles().equals("ROLE_RH"))
                        .collect(java.util.stream.Collectors.toList());
            } else {
                // Search by name or email for RH users
                rhUsers = utilisateurCrud.searchByNameOrEmail(searchTerm).stream()
                        .filter(u -> u.getroles().equals("RH") || u.getroles().equals("ROLE_RH"))
                        .collect(java.util.stream.Collectors.toList());
            }

            loadRHs(rhUsers);
        } catch (Exception e) {
            System.err.println("Error searching for RH: " + e.getMessage());
        }
    }

    @FXML
    private void handleSort() {
        try {
            String sortBy = sortComboBox != null ? sortComboBox.getValue() : null;

            if (sortBy == null || sortBy.isEmpty()) {
                sortBy = "nom"; // Default sort
            }

            // Sort the list based on selection
            List<Utilisateur> sortedUsers = utilisateurCrud.sortUtilisateurs(sortBy).stream()
                    .filter(u -> u.getroles().equals("RH") || u.getroles().equals("ROLE_RH"))
                    .collect(java.util.stream.Collectors.toList());

            loadRHs(sortedUsers);
        } catch (Exception e) {
            System.err.println("Error sorting RH: " + e.getMessage());
        }
    }

    private void loadRHs(List<Utilisateur> rhs) {
        try {
            if (rhContainer == null) {
                System.err.println("RH container is null, cannot load RH list");
                return;
            }

            // Clear the VBox before loading new results
            rhContainer.getChildren().clear();

            if (rhs.isEmpty()) {
                Label emptyLabel = new Label("Aucun RH trouvé");
                emptyLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                rhContainer.getChildren().add(emptyLabel);
                return;
            }

            // Display the results
            for (Utilisateur rh : rhs) {
                HBox rhCard = createRHCard(rh);
                rhContainer.getChildren().add(rhCard);
            }
        } catch (Exception e) {
            System.err.println("Error loading RH list: " + e.getMessage());
        }
    }

    private HBox createRHCard(Utilisateur rh) {
        HBox rhCard = new HBox();
        rhCard.getStyleClass().add("rh-card");
        rhCard.setAlignment(Pos.CENTER_LEFT);
        rhCard.setSpacing(15);

        // Avatar/Icon placeholder
        FontAwesomeIconView avatarIcon = new FontAwesomeIconView(FontAwesomeIcon.USER_CIRCLE);
        avatarIcon.setSize("32");
        avatarIcon.setStyle("-fx-fill: white;");

        // User info
        VBox infoBox = new VBox();
        infoBox.getStyleClass().add("rh-card-info");
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        Label nameLabel = new Label(rh.getNom() + " " + rh.getPrenom());
        nameLabel.getStyleClass().add("rh-name");

        Label emailLabel = new Label(rh.getEmail());
        emailLabel.getStyleClass().add("rh-email");

        Label roleLabel = new Label("RH");
        roleLabel.getStyleClass().add("role-badge");
        roleLabel.setStyle("-fx-padding: 2 8;");

        infoBox.getChildren().addAll(nameLabel, emailLabel, roleLabel);

        // Contact Button
        Button contactButton = new Button("Contacter");
        contactButton.getStyleClass().add("contact-button");
        contactButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ENVELOPE));
        contactButton.setOnAction(event -> handleContactRH(rh));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        rhCard.getChildren().addAll(avatarIcon, infoBox, spacer, contactButton);

        return rhCard;
    }

    private void handleContactRH(Utilisateur rh) {
        // Open a mailing dialog to contact the RH
        openMailingDialog(rh.getEmail(), rh.getNom() + " " + rh.getPrenom());
    }

    private void openMailingDialog(String recipientEmail, String recipientName) {
        try {
            // Create a dialog for sending an email
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Envoyer un message");
            dialog.setHeaderText("Message à " + recipientName);
            dialog.getDialogPane().getStyleClass().add("contact-dialog");

            // Set the button types
            ButtonType sendButtonType = new ButtonType("Envoyer", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(sendButtonType, ButtonType.CANCEL);

            // Create the content
            VBox content = new VBox(10);
            content.setPadding(new Insets(20));

            // Subject field
            Label subjectLabel = new Label("Sujet:");
            subjectLabel.setStyle("-fx-text-fill: #333333; -fx-font-weight: bold;");
            TextField subjectField = new TextField();
            subjectField.setPromptText("Entrez le sujet du message");

            // Message area
            Label messageLabel = new Label("Message:");
            messageLabel.setStyle("-fx-text-fill: #333333; -fx-font-weight: bold;");
            TextArea messageArea = new TextArea();
            messageArea.setPromptText("Rédigez votre message ici...");
            messageArea.setPrefRowCount(8);
            messageArea.setWrapText(true);

            content.getChildren().addAll(subjectLabel, subjectField, messageLabel, messageArea);
            dialog.getDialogPane().setContent(content);

            // Request focus on the subject field by default
            Platform.runLater(subjectField::requestFocus);

            // Convert the result
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == sendButtonType) {
                    if (subjectField.getText().trim().isEmpty() || messageArea.getText().trim().isEmpty()) {
                        showAlert(Alert.AlertType.WARNING, "Champs requis",
                                "Veuillez remplir tous les champs.");
                        return null;
                    }
                    return subjectField.getText() + "|||" + messageArea.getText(); // Use a separator for parsing
                }
                return null;
            });

            // Show the dialog and process the result
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(content_str -> {
                String[] parts = content_str.split("\\|\\|\\|");
                String subject = parts[0];
                String message = parts[1];

                // Send the email
                try {
                    Mailing.sendEmail(recipientEmail, subject, message);
                    showAlert(Alert.AlertType.INFORMATION, "Succès",
                            "Votre message a été envoyé avec succès à " + recipientName + ".");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur d'envoi",
                            "Impossible d'envoyer le message: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            System.err.println("Error opening mailing dialog: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Une erreur est survenue lors de l'ouverture du dialogue de messagerie: " + e.getMessage());
        }
    }
}