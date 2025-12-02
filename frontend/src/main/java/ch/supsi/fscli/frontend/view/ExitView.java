package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.controller.ExitController;
import ch.supsi.fscli.frontend.util.I18nManager;
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
    private I18nManager i18nManager;

    private Stage stage = new Stage();
    private Button confirmButton;
    private Button cancelButton;
    private ExitController exitController;

    public ExitView(ExitController exitController, I18nManager i18nManager) {
        this.exitController = exitController;
        this.i18nManager = i18nManager;
        createLayout();
    }

    private void createLayout() {
        stage = new Stage();

        stage.setTitle(i18nManager.getString("confirmation.exit.windowname"));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        //TODO: check if there is something to save

        Label messageLabel = new Label(i18nManager.getString("confirmation.exit.ask"));
        messageLabel.setStyle("-fx-font-size: 14px;");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        //confirmation.exit.confirm="Confirm"
        //confirmation.exit.cancel="Cancel"

        confirmButton = new Button(i18nManager.getString("confirmation.exit.confirm"));
        confirmButton.setPrefWidth(80);

        cancelButton = new Button(i18nManager.getString("confirmation.exit.cancel"));
        cancelButton.setPrefWidth(80);

        setupEventHandlers();

        buttonBox.getChildren().addAll(confirmButton, cancelButton);

        root.getChildren().addAll(messageLabel, buttonBox);

        Scene scene = new Scene(root, 300, 150);
        stage.setScene(scene);
    }

    private void setupEventHandlers() {
        confirmButton.setOnAction(e -> {
            stage.close();
            exitController.quit();
        });

        cancelButton.setOnAction(e -> {
            stage.close();
        });
    }

    @Override
    public void show() {
        stage.showAndWait();
    }

}