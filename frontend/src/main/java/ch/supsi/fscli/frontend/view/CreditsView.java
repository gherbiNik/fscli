package ch.supsi.fscli.frontend.view;


import ch.supsi.fscli.frontend.util.I18nManager;
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
    private I18nManager i18nManager;

    public static CreditsView getInstance(I18nManager i18nManager) {
        if (instance == null) {
            instance = new CreditsView();
            instance.initialize(i18nManager);
        }
        return instance;
    }

    private void initialize(I18nManager i18nManager){
        this.i18nManager = i18nManager;
        initializeUI();
    }

    private CreditsView() {

    }

    private void initializeUI() {
        stage = new Stage();

        stage.setTitle(i18nManager.getString("credits.name"));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);

        VBox root = new VBox(15);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);

        Label titleLabel = new Label(i18nManager.getString("credits.appname"));
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label appNameLabel = new Label(i18nManager.getString("credits.version"));
        appNameLabel.setStyle("-fx-font-size: 14px;");

        Label versionLabel = new Label(i18nManager.getString("app.buildDate"));

        Label authorLabel = new Label(i18nManager.getString("credits.devteam"));


        root.getChildren().addAll(
                titleLabel,
                appNameLabel,
                versionLabel,
                authorLabel
        );

        Scene scene = new Scene(root, 600, 300);
        stage.setScene(scene);
    }

    @Override
    public void showView() {
        stage.show();
    }

}
