package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.util.I18nManager;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class CreditsView implements ShowView {
    private static CreditsView instance;
    private Stage stage;
    private I18nManager i18nManager;

    // Declare labels as instance variables for setter access
    private Label titleLabel;
    private Label frontendVersionLabel;
    private Label frontendBuildDateLabel;
    private Label backendHeader;
    private Label backendVersionLabel;
    private Label backendBuildDateLabel;
    private Label authorLabel;

    public static CreditsView getInstance(I18nManager i18nManager) {
        if (instance == null) {
            instance = new CreditsView();
            instance.initialize(i18nManager);
        }
        return instance;
    }

    private void initialize(I18nManager i18nManager) {
        this.i18nManager = i18nManager;
        initializeUI();
    }

    private CreditsView() {
    }

    private void initializeUI() {
        stage = new Stage();
        stage.setTitle(""); // Initially empty; set by controller
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);

        VBox root = new VBox(15);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);

        titleLabel = new Label(""); // Initially empty; set by controller
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Frontend Info
        Label frontendHeader = new Label("Frontend");
        frontendHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        frontendVersionLabel = new Label(""); // Initially empty; set by controller
        frontendBuildDateLabel = new Label(""); // Initially empty; set by controller

        Separator separator = new Separator();
        separator.setPrefWidth(500);

        // Backend Info (initially set to empty; updated by controller)
        backendHeader = new Label("Backend");
        backendHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        backendVersionLabel = new Label("");
        backendBuildDateLabel = new Label("");

        Separator separator2 = new Separator();
        separator2.setPrefWidth(500);

        authorLabel = new Label(""); // Initially empty; set by controller

        root.getChildren().addAll(
                titleLabel,
                frontendHeader,
                frontendVersionLabel,
                frontendBuildDateLabel,
                separator,
                backendHeader,
                backendVersionLabel,
                backendBuildDateLabel,
                separator2,
                authorLabel
        );

        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
    }

    public void setStageTitle(String title) {
        stage.setTitle(title);
    }

    public void setAppName(String appName) {
        titleLabel.setText(appName);
    }

    public void setFrontendVersion(String version) {
        frontendVersionLabel.setText(version);
    }

    public void setFrontendBuildDate(String buildDate) {
        frontendBuildDateLabel.setText(buildDate);
    }

    public void setBackendVersion(String version) {
        backendVersionLabel.setText(version);
    }

    public void setBackendBuildDate(String buildDate) {
        backendBuildDateLabel.setText(buildDate);
    }

    public void setAuthorLabel(String authors) {
        authorLabel.setText(authors);
    }

    @Override
    public void showView() {
        stage.show();
    }
}