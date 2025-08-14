package com.melocode.hrreclam;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

public class ChatBotController {

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    @FXML
    private TextArea chatArea;

    @FXML
    private Button closeButton; // Only keep if it's in FXML

    @FXML
    public void initialize() {
        chatArea.setEditable(false);
        chatArea.appendText("Chatbot: Hello! How can I assist you?\n");

        // These should already be set in FXML, but keeping them just in case
        sendButton.setOnAction(event -> sendMessage());
        if (closeButton != null) { // Avoid NullPointerException
            closeButton.setOnAction(event -> closeChat());
        }
    }

    @FXML // Ensure JavaFX can find this method
    public void sendMessage() {
        String userMessage = userInput.getText().trim();
        if (!userMessage.isEmpty()) {
            chatArea.appendText("You: " + userMessage + "\n");
            String botResponse = getChatbotResponse(userMessage);
            chatArea.appendText("Chatbot: " + botResponse + "\n");
            userInput.clear();
        }
    }

    private String getChatbotResponse(String message) {
        switch (message.toLowerCase()) {
            case "hello":
                return "Hi there!";
            case "how are you?":
                return "I'm fine, thank you!";
            default:
                return "I'm here to assist you.";
        }
    }

    @FXML // Make sure this is public if called from FXML
    public void closeChat() {
        if (closeButton != null) {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        }
    }
}
