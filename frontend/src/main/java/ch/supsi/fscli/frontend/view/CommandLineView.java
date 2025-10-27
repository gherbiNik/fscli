package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.controller.PreferenceController;
import ch.supsi.fscli.frontend.util.I18nManager;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CommandLineView implements ControlledFxView{
    private static  CommandLineView instance;


    private PreferenceController  preferenceController;
    private I18nManager  i18n;
    // FX Componenents
    private TextField commandLine;
    private Label commandLineLabel;
    private Button enter;


    private CommandLineView(){
    }
    public static CommandLineView getInstance(PreferenceController preferenceController, I18nManager i18nManager){
        if(instance == null){
            instance = new CommandLineView();
            instance.initalize(preferenceController, i18nManager);

        }
        return instance;
    }

    private void initalize(PreferenceController preferenceController, I18nManager i18nManager) {
        this.preferenceController = preferenceController;
        this.i18n = i18nManager;
        this.enter = new Button();
        this.enter.setId("enter");

        this.commandLineLabel = new Label();
        this.commandLine = new TextField();
        this.commandLine.setFont(this.preferenceController.getCommandLineFont());
        this.commandLine.setPrefColumnCount(this.preferenceController.getColumn());
        setLocalizedText();
    }

    public Label getLabel(){
        return this.commandLineLabel;
    }

    public Button getButton(){
        return this.enter;
    }

    @Override
    public Node getNode() {
        return this.commandLine;
    }

    @Override
    public void update() {
        //TODO
    }

    @Override
    public void setLocalizedText() {
        enter.setText(i18n.getString("commandLine.enter"));
        commandLineLabel.setText(i18n.getString("commandLine.command"));

    }
}
