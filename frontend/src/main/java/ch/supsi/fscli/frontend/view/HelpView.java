package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.util.I18nManager;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class HelpView implements ShowView{
    private static HelpView instance;
    private I18nManager i18nManager;
    private List<String> commandDescriptions;
    private Stage stage = new Stage();
    private VBox root;

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

        root = new VBox(10);
        root.setPadding(new Insets(20));

        Label titleLabel = new Label(i18nManager.getString("help.title"));
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        root.getChildren().add(titleLabel);

        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
    }

    public void setCommandDescriptions(List<String> commandDescriptions) {
        this.commandDescriptions = commandDescriptions;
        populateCommands();
    }

    private void populateCommands() {
        // Clear existing command labels (skip the title: index 0)
        if (root.getChildren().size() > 1) {
            root.getChildren().remove(1, root.getChildren().size());
        }

        if (commandDescriptions != null) {
            for (String cmd : commandDescriptions) {
                TextFlow tf = createCommandLabel(cmd);
                root.getChildren().add(tf);
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
    public void showView() {
        stage.show();
    }

}