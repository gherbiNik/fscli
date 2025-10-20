package ch.supsi.fscli.frontend.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ExitView implements ShowView {
    private static ExitView instance;

    private Stage stage = new Stage();
    private Button confirmButton;
    private Button cancelButton;

    public static ExitView getInstance() {
        if (instance == null) {
            instance = new ExitView();
            instance.initialize();
        }

        return instance;
    }

    private void initialize() {
    }

    private ExitView() {
        initializeUI();
    }

    private void initializeUI() {
        stage = new Stage();

        stage.setTitle("Exit");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        //TODO: check if there is something to save

        Label messageLabel = new Label("Are you sure you want to exit?");
        messageLabel.setStyle("-fx-font-size: 14px;");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        confirmButton = new Button("Yes");
        confirmButton.setPrefWidth(80);

        cancelButton = new Button("No");
        cancelButton.setPrefWidth(80);

        buttonBox.getChildren().addAll(confirmButton, cancelButton);

        root.getChildren().addAll(messageLabel, buttonBox);

        Scene scene = new Scene(root, 300, 150);
        stage.setScene(scene);
    }

    @Override
    public void showView() {
        stage.showAndWait();
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public Button getConfirmButton() {
        return confirmButton;
    }

    public void closeView() {
        stage.close();
    }
}