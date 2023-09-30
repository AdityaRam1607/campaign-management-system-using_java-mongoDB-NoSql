package application;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.ArrayList;

public class Campaign extends Application {
	  public static void main(String[] args) {
	        launch(args);
	  }
    @Override
    public void start(Stage primaryStage) throws Exception {

        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase database = mongoClient.getDatabase("campaigns");
        MongoCollection<Document> collection = database.getCollection("campaigns");

        
        VBox vBox = new VBox();


        Label titleLabel = new Label("Campaign Management System");
        titleLabel.setStyle("-fx-font-size: 24pt;");


        ComboBox<String> optionsComboBox = new ComboBox<>();
        optionsComboBox.getItems().addAll("Create a new campaign", "View all campaigns", "Update a campaign", "Delete a campaign");
        optionsComboBox.setValue("Select an option");


        GridPane inputGridPane = new GridPane();
        inputGridPane.setHgap(10);
        inputGridPane.setVgap(10);


        Label nameLabel = new Label("Name:");
        TextField nameTextField = new TextField();
        Label descriptionLabel = new Label("Description:");
        TextField descriptionTextField = new TextField();
        Label startDateLabel = new Label("Start Date (YYYY-MM-DD):");
        TextField startDateTextField = new TextField();
        Label endDateLabel = new Label("End Date (YYYY-MM-DD):");
        TextField endDateTextField = new TextField();
        Label budgetLabel = new Label("Budget:");
        TextField budgetTextField = new TextField();
        Label statusLabel = new Label("Status:");
        TextField statusTextField = new TextField();


        inputGridPane.addRow(0, nameLabel, nameTextField);
        inputGridPane.addRow(1, descriptionLabel, descriptionTextField);
        inputGridPane.addRow(2, startDateLabel, startDateTextField);
        inputGridPane.addRow(3, endDateLabel, endDateTextField);
        inputGridPane.addRow(4, budgetLabel, budgetTextField);
        inputGridPane.addRow(5, statusLabel, statusTextField);


        Button submitButton = new Button("Submit");


        vBox.getChildren().addAll(titleLabel, optionsComboBox, inputGridPane, submitButton);

        // Create a Scene and set it on the Stage
        Scene scene = new Scene(vBox, 500, 400);
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();


		optionsComboBox.setOnAction(e -> {
            String selectedOption = optionsComboBox.getValue();
            inputGridPane.setVisible(false);
            switch (selectedOption) {
                case "Create a new campaign":
                    inputGridPane.setVisible(true);
                    break;
                case "View all campaigns":
                    MongoCursor<Document> cursor = collection.find().iterator();
                    ArrayList<String> campaigns = new ArrayList<>();
                    while (cursor.hasNext()) {
                        Document doc = cursor.next();
                        campaigns.add(doc.toJson());
                    }
                    TextArea campaignsTextArea = new TextArea(String.join("\n\n", campaigns));
                    campaignsTextArea.setEditable(false);
                    vBox.getChildren().add(campaignsTextArea);
                    break;
                case "Update a campaign":
                    TextField updateNameTextField = new TextField();
                    Label updateNameLabel =new Label("Name of campaign to update:");
                    Button updateButton = new Button("Update");
                    // Add the input fields to the GridPane
                    inputGridPane.getChildren().clear();
                    inputGridPane.addRow(0, updateNameLabel, updateNameTextField);
                    inputGridPane.addRow(1, nameLabel, nameTextField);
                    inputGridPane.addRow(2, descriptionLabel, descriptionTextField);
                    inputGridPane.addRow(3, startDateLabel, startDateTextField);
                    inputGridPane.addRow(4, endDateLabel, endDateTextField);
                    inputGridPane.addRow(5, budgetLabel, budgetTextField);
                    inputGridPane.addRow(6, statusLabel, statusTextField);
                    inputGridPane.addRow(7, updateButton);

                    updateButton.setOnAction(event -> {
                        String name = updateNameTextField.getText();
                        Document query = new Document("name", name);
                        Document update = new Document("$set", new Document("name", nameTextField.getText())
                                .append("description", descriptionTextField.getText())
                                .append("start_date", startDateTextField.getText())
                                .append("end_date", endDateTextField.getText())
                                .append("budget", budgetTextField.getText())
                                .append("status", statusTextField.getText()));
                        collection.updateOne(query, update);
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Campaign updated successfully!");
                        alert.showAndWait();
                    });

                    inputGridPane.setVisible(true);
                    break;
                case "Delete a campaign":
                    TextField deleteNameTextField = new TextField();
                    Label deleteNameLabel = new Label("Name of campaign to delete:");
                    Button deleteButton = new Button("Delete");


                    inputGridPane.getChildren().clear();
                    inputGridPane.addRow(0, deleteNameLabel, deleteNameTextField);
                    inputGridPane.addRow(1, deleteButton);

                    deleteButton.setOnAction(event -> {
                        String name = deleteNameTextField.getText();
                        Document query = new Document("name", name);
                        collection.deleteOne(query);
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Campaign deleted successfully!");
                        alert.showAndWait();
                    });

                    inputGridPane.setVisible(true);
                    break;
            }
        });


		submitButton.setOnAction(e -> {
            Document document = new Document("name", nameTextField.getText())
                    .append("description", descriptionTextField.getText())
                    .append("start_date", startDateTextField.getText())
                    .append("end_date", endDateTextField.getText())
                    .append("budget", budgetTextField.getText())
                    .append("status", statusTextField.getText());
            collection.insertOne(document);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Campaign added successfully!");
            alert.showAndWait();
            nameTextField.clear();
            descriptionTextField.clear();
            startDateTextField.clear();
            endDateTextField.clear();
            budgetTextField.clear();
            statusTextField.clear();
        });
    }
}
