package ch.supsi.fscli.frontend.view;


import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class CreditsView implements ShowView {
    private static CreditsView instance;
    private Stage stage;

    public static CreditsView getInstance() {
        if (instance == null) {
            instance = new CreditsView();
        }
        return instance;
    }

    private CreditsView() {
        initializeUI();
    }

    private void initializeUI() {
        stage = new Stage();

        stage.setTitle("Crediti");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);

        VBox root = new VBox(15);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("temp");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label appNameLabel = new Label("Name");
        appNameLabel.setStyle("-fx-font-size: 14px;");

        Label versionLabel = new Label("Version");

        Label authorLabel = new Label("dev by");


        root.getChildren().addAll(
                titleLabel,
                appNameLabel,
                versionLabel,
                authorLabel
        );

        Scene scene = new Scene(root, 400, 250);
        stage.setScene(scene);
    }

    @Override
    public void showView() {
        stage.show();
    }

    public void closeView() {
        stage.close();
    }
}
