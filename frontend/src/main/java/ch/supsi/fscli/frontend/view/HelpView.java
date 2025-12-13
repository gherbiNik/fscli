package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.util.I18nManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class HelpView implements ShowView{
    private I18nManager i18nManager;
    private List<String> commandDescriptions;
    private Stage stage = new Stage();
    private VBox root;
    private VBox commandsContainer;

    public HelpView(I18nManager i18nManager) {
        this.i18nManager = i18nManager;
        createLayout();
    }

    private void createLayout() {
        stage = new Stage();

        stage.setTitle(i18nManager.getString("help.name"));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);


        root = new VBox(10);
        root.setId("helpPopup");
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_LEFT);

        Label titleLabel = new Label(i18nManager.getString("help.title"));
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        titleLabel.setId("messageLabel");

        commandsContainer = new VBox(5);

        Button okButton = new Button("OK");
        okButton.setPrefWidth(100);
        okButton.setOnAction(event -> stage.close());
        okButton.setId("helpPopupOkButton");

        root.getChildren().add(titleLabel);
        root.getChildren().add(commandsContainer);

        VBox buttonContainer = new VBox(okButton);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets(20, 0, 0, 0));

        root.getChildren().add(buttonContainer);

        Scene scene = new Scene(root, 600, 450);
        stage.setScene(scene);
    }

    public void setCommandDescriptions(List<String> commandDescriptions) {
        this.commandDescriptions = commandDescriptions;
        populateCommands();
    }

    private void populateCommands() {
        commandsContainer.getChildren().clear();

        if (commandDescriptions != null) {
            for (String cmd : commandDescriptions) {
                TextFlow tf = createCommandLabel(cmd);
                commandsContainer.getChildren().add(tf);
            }
        }
    }

    private TextFlow createCommandLabel(String description) {
        Text bullet = new Text("• ");
        bullet.setFont(Font.font("Monospaced", FontWeight.BOLD, 12));

        Text rest = new Text(description);
        rest.setFont(Font.font("Monospaced", 12));

        TextFlow tf = new TextFlow(bullet, rest);
        tf.setPrefWidth(580);
        return tf;
    }

    @Override
    public void show() {
        stage.show();
    }
}