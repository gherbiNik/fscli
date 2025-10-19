package ch.supsi.fscli.frontend.view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

//TODO: extract translated infos and display them

public class HelpView implements ShowView{
    private static HelpView instance;

    private Stage stage = new Stage();

    public static ShowView getInstance(){
        if(instance == null){
            instance = new HelpView();
            instance.initialize();
        }

        return instance;
    }

    private void initialize() {
    }

    public HelpView() {
        initializeUI();
    }

    private void initializeUI() {
        stage = new Stage();

        stage.setTitle("Help");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label helpLabel = new Label("temp");
        root.getChildren().add(helpLabel);

        Scene scene = new Scene(root, 400, 300);
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
