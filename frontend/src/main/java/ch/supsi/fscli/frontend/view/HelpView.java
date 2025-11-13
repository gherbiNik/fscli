package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.util.I18nManager;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class HelpView implements ShowView{
    private static HelpView instance;
    private I18nManager i18nManager;
    private Map<String, String> commandDescriptions;
    private Stage stage = new Stage();

    public static HelpView getInstance(I18nManager i18nManager){
        if(instance == null){
            instance = new HelpView();
            instance.initialize(i18nManager);
        }

        return instance;
    }

    private void initialize(I18nManager i18nManager) {
        this.i18nManager = i18nManager;
        initializeUI();
    }

    private HelpView() {

    }

    private void initializeUI() {
        stage = new Stage();

        stage.setTitle(i18nManager.getString("help.name"));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label titleLabel = new Label(i18nManager.getString("help.title"));
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        root.getChildren().add(titleLabel);

        // Command descriptions
        if(commandDescriptions != null ){
            for(Map.Entry<String, String> row : commandDescriptions){
                createCommandLabel(row.getKey(), row.getValue())
            }

               //TODO sintassi sbagliata? : backend mi invia l'help di quel comando
        }

        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
    }

    public void setCommandDescriptions(Map<String, String> commandDescriptions) {
        this.commandDescriptions = commandDescriptions;
    }

    private Label createCommandLabel(String command, String description) {
        Label label = new Label("• '" + command + "'  " + description);
        label.setWrapText(true);
        label.setFont(Font.font("Monospaced", 12));
        return label;
    }

    @Override
    public void showView() {
        stage.show();
    }

}