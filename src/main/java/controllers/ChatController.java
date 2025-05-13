package controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ChatController {
    @FXML private TextField messageField;
    @FXML private Button sendButton;
    @FXML private VBox chatArea;
    @FXML private ScrollPane scrollPane;

    private final double MESSAGE_WIDTH_PERCENTAGE = 0.7; // 70% of the chat area

    @FXML
    private void initialize() {
        // Add welcome message
        addMessage("Hello! I'm your Gemini-powered assistant. How can I help you today?", "bot");

        // Bind chat area width to scroll pane width
        chatArea.prefWidthProperty().bind(scrollPane.widthProperty().subtract(20));

        // Allow Enter key to send messages
        messageField.setOnAction(event -> handleSendMessage());

        // Make sure chatArea fills the width of the scrollPane
        scrollPane.setFitToWidth(true);
    }

    @FXML
    private void handleSendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            addMessage(message, "user");
            messageField.clear();

            // Pre-prompt to guide the conversation toward WayMate application context
            String prePrompt = "You are a chatbot that only answers questions related to the WayMate application, " +
                    "a smart car rental management system designed for Tunisia. Please stay on topic.\n";

            // Combine pre-prompt with user message
            String contextualMessage = prePrompt + "User: " + message;

            // Generate and display the bot's response
            String initialResponse = generateBotResponse(contextualMessage);
            addMessage(initialResponse, "bot");
        }
    }

    private void addMessage(String message, String sender) {
        Text text = new Text(message);
        text.setStyle("-fx-font-size: 14px;");

        TextFlow textFlow = new TextFlow(text);
        textFlow.setPadding(new Insets(5, 10, 5, 10));

        // Create a container for the message
        HBox messageContainer = new HBox();
        VBox.setMargin(messageContainer, new Insets(5, 0, 5, 0));

        // Set constraints to make sure text wraps properly
        double maxWidth = scrollPane.getWidth() * MESSAGE_WIDTH_PERCENTAGE;
        if (maxWidth <= 0) { // If width is not yet determined
            maxWidth = 300; // Default initial value
        }

        textFlow.setMaxWidth(maxWidth);
        textFlow.setPrefWidth(TextFlow.USE_COMPUTED_SIZE);

        // Ensure text flow will wrap properly
        text.wrappingWidthProperty().bind(textFlow.widthProperty().subtract(20));

        if (sender.equals("user")) {
            textFlow.setStyle("-fx-background-color: #DCF8C6; " +
                    "-fx-background-radius: 10 10 0 10; " +
                    "-fx-padding: 10;");
            messageContainer.setAlignment(Pos.CENTER_RIGHT);
        } else {
            textFlow.setStyle("-fx-background-color: #ECECEC; " +
                    "-fx-background-radius: 10 10 10 0; " +
                    "-fx-padding: 10;");
            messageContainer.setAlignment(Pos.CENTER_LEFT);
        }

        messageContainer.getChildren().add(textFlow);

        // Ensure message container spans full width
        HBox.setHgrow(messageContainer, Priority.ALWAYS);
        messageContainer.setMaxWidth(Double.MAX_VALUE);

        chatArea.getChildren().add(messageContainer);

        // Add a listener to adjust wrapping after the layout is complete
        chatArea.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            double newMaxWidth = scrollPane.getWidth() * MESSAGE_WIDTH_PERCENTAGE;
            if (newMaxWidth > 0) {
                textFlow.setMaxWidth(newMaxWidth);
            }
        });

        // Scroll to bottom
        scrollPane.applyCss();
        scrollPane.layout();
        scrollPane.setVvalue(1.0);
    }

    // API Key for Google Gemini API
    private final String GEMINI_API_KEY = "AIzaSyB2rU4iGz4w_NHlE5W1aYKx9DvR5Nbl-UI";
    private final String GEMINI_MODEL = "gemini-1.5-flash-latest"; // Supported model

    private String generateBotResponse(String userMessage) {
        // Create a new thread to call Gemini API to avoid blocking the UI
        Thread apiCallThread = new Thread(() -> {
            try {
                String response = callGeminiAPI(userMessage);
                // Update UI on JavaFX thread
                javafx.application.Platform.runLater(() -> {
                    // Remove "thinking" message if it exists
                    if (chatArea.getChildren().size() > 0) {
                        int lastIndex = chatArea.getChildren().size() - 1;
                        HBox lastMessage = (HBox) chatArea.getChildren().get(lastIndex);
                        TextFlow textFlow = (TextFlow) lastMessage.getChildren().get(0);
                        Text text = (Text) textFlow.getChildren().get(0);
                        if (text.getText().equals("Thinking...")) {
                            chatArea.getChildren().remove(lastIndex);
                        }
                    }
                    // Add the actual response
                    addMessage(response, "bot");
                });
            } catch (Exception e) {
                e.printStackTrace();
                // Handle API error on JavaFX thread
                javafx.application.Platform.runLater(() -> {
                    // Remove "thinking" message if it exists
                    if (chatArea.getChildren().size() > 0) {
                        int lastIndex = chatArea.getChildren().size() - 1;
                        HBox lastMessage = (HBox) chatArea.getChildren().get(lastIndex);
                        TextFlow textFlow = (TextFlow) lastMessage.getChildren().get(0);
                        Text text = (Text) textFlow.getChildren().get(0);
                        if (text.getText().equals("Thinking...")) {
                            chatArea.getChildren().remove(lastIndex);
                        }
                    }
                    // Add error message
                    addMessage("Sorry, I couldn't connect to Gemini. Error: " + e.getMessage(), "bot");
                });
            }
        });

        // Start the API call in background
        apiCallThread.setDaemon(true);
        apiCallThread.start();

        // Return a placeholder while waiting for the API response
        return "Thinking...";
    }

    private String callGeminiAPI(String userMessage) throws Exception {
        // Check if API key is set
        if (GEMINI_API_KEY == null || GEMINI_API_KEY.isEmpty()) {
            return "Error: Please set your Gemini API key in the code.";
        }

        String geminiEndpoint = "https://generativelanguage.googleapis.com/v1beta/models/" + GEMINI_MODEL + ":generateContent?key=" + GEMINI_API_KEY;

        System.out.println("Creating URL: " + geminiEndpoint);
        try {
            java.net.URL url = new java.net.URL(geminiEndpoint);
            System.out.println("Opening connection...");
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
            if (connection == null) {
                throw new Exception("Failed to open connection to Gemini API: connection is null");
            }
            connection.setConnectTimeout(60000); // 60 seconds timeout
            connection.setReadTimeout(60000);    // 60 seconds read timeout
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Create JSON request body for Gemini API - with proper escaping
            String escapedMessage = userMessage
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");

            String requestBody = "{\"contents\":[{\"parts\":[{\"text\":\"" + escapedMessage + "\"}]}]}";

            System.out.println("Request body: " + requestBody);

            // Send request
            try (java.io.OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
                os.flush();
            } catch (Exception e) {
                System.err.println("Error sending request: " + e.getMessage());
                e.printStackTrace();
                return "Error sending request to Gemini API: " + e.getMessage();
            }

            // Read response
            StringBuilder response = new StringBuilder();
            int responseCode = connection.getResponseCode();
            System.out.println("Response code: " + responseCode);

            if (responseCode >= 200 && responseCode < 300) {
                try (java.io.BufferedReader br = new java.io.BufferedReader(
                        new java.io.InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine);
                    }
                }
            } else {
                // Handle error response
                try (java.io.BufferedReader br = new java.io.BufferedReader(
                        new java.io.InputStreamReader(connection.getErrorStream(), "utf-8"))) {
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine);
                    }
                }
                System.err.println("API Error response: " + response.toString());
                return "API Error: " + responseCode + " - " + response.toString();
            }

            String jsonResponse = response.toString();
            System.out.println("Raw response: " + jsonResponse);

            // Extract text from the response
            String generatedText = extractTextFromGeminiResponse(jsonResponse);

            // If we couldn't parse the response, return a fallback message
            if (generatedText.isEmpty()) {
                return "I received a response but couldn't interpret it correctly. Please try again.";
            }

            return generatedText;

        } catch (java.net.MalformedURLException e) {
            System.err.println("Malformed URL: " + e.getMessage());
            throw e;
        } catch (java.net.UnknownHostException e) {
            System.err.println("DNS resolution failed: " + e.getMessage());
            throw new Exception("Cannot resolve host. Please check your internet connection or DNS settings.");
        } catch (java.net.ConnectException e) {
            System.err.println("Connection failed: " + e.getMessage());
            throw new Exception("Failed to connect to the server. Please check your network or firewall settings.");
        } catch (java.io.IOException e) {
            System.err.println("IO Exception: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // Extract text content from Gemini API response
    private String extractTextFromGeminiResponse(String jsonResponse) {
        try {
            if (jsonResponse == null || jsonResponse.isEmpty()) {
                return "Empty response received";
            }

            // Check for error message
            if (jsonResponse.contains("\"error\":")) {
                int errorMsgStart = jsonResponse.indexOf("\"message\":\"") + 11;
                int errorMsgEnd = jsonResponse.indexOf("\"", errorMsgStart);
                if (errorMsgStart > 11 && errorMsgEnd > errorMsgStart) {
                    return "API Error: " + jsonResponse.substring(errorMsgStart, errorMsgEnd);
                }
                return "API Error in response";
            }

            // Look for the text field in the structure based on the actual response format
            int textFieldStart = jsonResponse.indexOf("\"text\":");
            if (textFieldStart == -1) {
                return "No text field found in response";
            }

            // Find the opening quote of the text content
            int textStart = jsonResponse.indexOf("\"", textFieldStart + 7) + 1;
            if (textStart == 0) { // indexOf returns -1 if not found, so +1 would be 0
                return "Malformed text field in response";
            }

            // Find the closing quote, carefully handling escaped quotes
            StringBuilder textContent = new StringBuilder();
            boolean escaped = false;

            for (int i = textStart; i < jsonResponse.length(); i++) {
                char c = jsonResponse.charAt(i);

                if (escaped) {
                    if (c == 'n') textContent.append('\n');
                    else if (c == 'r') textContent.append('\r');
                    else if (c == 't') textContent.append('\t');
                    else textContent.append(c);
                    escaped = false;
                } else if (c == '\\') {
                    escaped = true;
                } else if (c == '"') {
                    // Found the end of the text content
                    break;
                } else {
                    textContent.append(c);
                }
            }

            String result = textContent.toString().trim();
            System.out.println("Extracted text: " + result);
            return result.isEmpty() ? "No text content extracted" : result;
        } catch (Exception e) {
            System.err.println("Error parsing Gemini response: " + e.getMessage());
            e.printStackTrace();
            return "Error parsing response: " + e.getMessage();
        }
    }
}
