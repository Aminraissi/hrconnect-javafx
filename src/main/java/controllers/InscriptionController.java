package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import models.Utilisateur;
import models.enums.Role;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.UtilisateurCrud;


public class InscriptionController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private RadioButton rbmembre;

    @FXML
    private RadioButton rbemploye;

    @FXML
    private RadioButton rbrh;

    @FXML
    private RadioButton rbmanager;

    @FXML
    private RadioButton rbadmin;

    @FXML
    private TextField tfcin;

    @FXML
    private TextField tfemail;

    @FXML
    private PasswordField tfmdp;

    @FXML
    private TextField tfnom;

    @FXML
    private TextField tfnum_tel;

    @FXML
    private TextField tfprenom;

    @FXML
    private Button btn_annul;

    @FXML
    private Button btn_inscri;

    @FXML
    private Hyperlink hyperlink;

    private TableView<Utilisateur> tableView;
    private List<Utilisateur> registeredUsers;
    private Stage primaryStage;

    @FXML
    private TextField tfshowpassword;

    @FXML
    private CheckBox show;

    @FXML
    private Label passwordStrengthLabel;

    // Create a ToggleGroup for the radio buttons
    private ToggleGroup roleToggleGroup;

    public void setRegisteredUsers(List<Utilisateur> registeredUsers) {
        this.registeredUsers = registeredUsers;
    }

    public void setTableView(TableView<Utilisateur> tableView) {
        this.tableView = tableView;
    }

    public void setHomePage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    void initialize() {
        // Create a new ToggleGroup for role selection
        roleToggleGroup = new ToggleGroup();

        // Add all radio buttons to the same ToggleGroup
        rbmembre.setToggleGroup(roleToggleGroup);
        rbemploye.setToggleGroup(roleToggleGroup);
        rbrh.setToggleGroup(roleToggleGroup);
        rbmanager.setToggleGroup(roleToggleGroup);
        rbadmin.setToggleGroup(roleToggleGroup);

        // Set default selection
        rbmembre.setSelected(true);

        // Password visibility toggle
        show.setOnAction(event -> handleShowPassCheckboxClick());

        tfshowpassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if (show.isSelected()) {
                tfmdp.setText(newValue);
            }
        });

        tfmdp.textProperty().addListener((observable, oldValue, newValue) -> {
            if (show.isSelected()) {
                tfshowpassword.setText(newValue);
            }

            // Vérifier la force du mot de passe et mettre à jour le label en conséquence
            String password = newValue;
            if (password.length() < 8 || !password.matches(".*[A-Z].*")) {
                // Si le mot de passe ne répond pas aux critères de force, afficher un message en rouge
                passwordStrengthLabel.setText("Mot de passe faible!");
                passwordStrengthLabel.setStyle("-fx-text-fill: red;");
            } else {
                // Si le mot de passe est fort, afficher un message en vert
                passwordStrengthLabel.setText("Mot de passe fort.");
                passwordStrengthLabel.setStyle("-fx-text-fill: green;");
            }
        });

        hyperlink.setOnAction(event -> {
            try {
                // Chargez la page d'authentification depuis le fichier FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/authentification.fxml"));
                Parent root = loader.load();

                // Créez une nouvelle scène et un nouveau stage
                Stage stage = new Stage();
                stage.setScene(new Scene(root));

                // Affichez la nouvelle scène
                stage.show();

                // Fermez la fenêtre actuelle
                ((Stage) hyperlink.getScene().getWindow()).close();
            } catch (IOException e) {
                e.printStackTrace(); // Gérez l'exception selon vos besoins
            }
        });
    }

    @FXML
    void savePerson(ActionEvent event) {
        if (tfcin.getText().isEmpty() || tfnum_tel.getText().isEmpty() || tfnom.getText().isEmpty() ||
                tfprenom.getText().isEmpty() || tfemail.getText().isEmpty() || tfmdp.getText().isEmpty()) {
            // Afficher un message d'erreur si les champs sont vides
            Alert alert = new Alert(Alert.AlertType.ERROR, "Veuillez remplir tous les champs.", ButtonType.OK);
            alert.show();
            return;
        }

        if (!validerCIN(tfcin.getText())) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Le CIN doit avoir 8 chiffres et commencer par 0 ou 1.", ButtonType.OK);
            alert.show();
            return;
        }

        if (!validerFormatEmail(tfemail.getText())) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "L'adresse email n'est pas dans un format valide.", ButtonType.OK);
            alert.show();
            return;
        }

        String password = tfmdp.getText();
        if (password.length() < 8 || password.equals(password.toLowerCase())) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Le mot de passe doit contenir au moins 8 caractères et au moins une majuscule.", ButtonType.OK);
            alert.show();
            return;
        }

        if (!validerNumeroTelephone(tfnum_tel.getText())) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Le numéro de téléphone doit être composé de 8 chiffres.", ButtonType.OK);
            alert.show();
            return;
        }

        // Get the selected radio button and its role
        RadioButton selectedRadioButton = (RadioButton) roleToggleGroup.getSelectedToggle();
        String selectedRole;

        if (selectedRadioButton == rbadmin) {
            selectedRole = "[\"ROLE_ADMIN\"]";
        } else if (selectedRadioButton == rbrh) {
            selectedRole = "[\"ROLE_RH\"]";
        } else if (selectedRadioButton == rbemploye) {
            selectedRole = "[\"ROLE_EMPLOYE\"]";
        } else if (selectedRadioButton == rbmanager) {
            selectedRole = "[\"ROLE_MANAGER\"]";
        } else {
            // Default to USER/Stagiaire
            selectedRole = "[\"ROLE_USER\"]";
        }

        // Create the user with the selected role
        Utilisateur p = new Utilisateur(
                Integer.parseInt(tfcin.getText()),
                tfnum_tel.getText(),
                tfnom.getText(),
                tfprenom.getText(),
                tfemail.getText(),
                tfmdp.getText(),
                selectedRole
        );

        // Add user to the database
        UtilisateurCrud uc = new UtilisateurCrud();
        if (!verifierChampsUtilisateur(p)) {
            // Afficher un message d'erreur si les champs ne sont pas valides
            Alert alert = new Alert(Alert.AlertType.ERROR, "Veuillez remplir tous les champs correctement.", ButtonType.OK);
            alert.show();
            return;
        }

        if (uc.utilisateurExisteDeja(tfcin.getText(), tfemail.getText())) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Cet utilisateur existe déjà.", ButtonType.OK);
            alert.show();
            return;
        }

        // Ajouter l'utilisateur uniquement si les vérifications sont réussies
        uc.ajouterEntite(p);
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Utilisateur ajouté", ButtonType.OK);
        alert.showAndWait();

        // Redirect to the appropriate profile page based on the role
        redirectToProfile(selectedRole);
    }

    private void redirectToProfile(String role) {
        try {
            String fxmlFileName;
            switch (role) {
                case "[\"ROLE_ADMIN\"]":
                    fxmlFileName = "/ProfilAdmin.fxml";
                    break;
                case "[\"ROLE_USER\"]":
                    fxmlFileName = "/ProfilMembre.fxml";
                    break;
                case "[\"ROLE_RH\"]":
                    fxmlFileName = "/ProfilRh.fxml";
                    break;
                case "[\"ROLE_EMPLOYE\"]":
                    fxmlFileName = "/ProfilEmploye.fxml";
                    break;
                case "[\"ROLE_MANAGER\"]":
                    fxmlFileName = "/ProfilManager.fxml";
                    break;
                default:
                    throw new IllegalArgumentException("Rôle non reconnu: " + role);
            }

            // Rest of the method remains the same...
            // Load the FXML file for the user's profile
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Parent root = loader.load();

            // Create a new scene and stage
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Profil " + role);
            stage.show();

            // Close the current registration window
            Stage currentStage = (Stage) btn_inscri.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur lors de la redirection: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }

    private boolean validerCIN(String cin) {
        String regex = "^[01]\\d{7}$";
        return cin.matches(regex);
    }

    private boolean verifierChampsUtilisateur(Utilisateur utilisateur) {
        // Ajoutez vos vérifications de champs ici
        // Par exemple, vérifiez si les champs ne sont pas vides
        return !utilisateur.getNom().isEmpty() &&
                !utilisateur.getPrenom().isEmpty() &&
                !utilisateur.getEmail().isEmpty() &&
                !utilisateur.getpassword().isEmpty();
    }

    private boolean validerFormatEmail(String email) {
        String regex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";
        return email.matches(regex);
    }

    private boolean validerNumeroTelephone(String numeroTelephone) {
        // Ajuster selon le format spécifique du pays (Tunisie)
        String regex = "^\\d{8}$";
        return numeroTelephone.matches(regex);
    }

    @FXML
    void annuler(ActionEvent event) {
        // Obtenez la scène à partir du bouton
        Scene scene = btn_annul.getScene();
        if (scene != null) {
            // Obtenez la fenêtre à partir de la scène
            Stage stage = (Stage) scene.getWindow();
            if (stage != null) {
                // Fermez la fenêtre
                stage.close();
            }
        }
    }

    @FXML
    void handleShowPassCheckboxClick() {
        if (show.isSelected()) {
            // Afficher le mot de passe en clair
            tfshowpassword.setText(tfmdp.getText());
            tfshowpassword.setVisible(true);
            tfmdp.setVisible(false);
        } else {
            // Masquer le mot de passe en clair et montrer le PasswordField à nouveau
            tfmdp.setText(tfshowpassword.getText());
            tfmdp.setVisible(true);
            tfshowpassword.setVisible(false);
        }
    }

    @FXML
    private void redirectToAuthPage() {
        try {
            // Load the authentication FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/authentification.fxml"));
            Parent authParent = loader.load();

            // Create a new scene
            Scene authScene = new Scene(authParent);

            // Get the stage information
            Stage stage = new Stage();
            stage.setTitle("Authentification");
            stage.setScene(authScene);

            // Close the current registration window
            Stage currentStage = (Stage) hyperlink.getScene().getWindow();
            currentStage.close();

            // Show the authentication window
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}