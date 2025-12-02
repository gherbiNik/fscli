package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.backend.application.filesystem.FileSystemApplication;
import ch.supsi.fscli.frontend.controller.PreferenceController;
import ch.supsi.fscli.frontend.controller.filesystem.IFileSystemController;
import ch.supsi.fscli.frontend.event.FileSystemCreationEvent;
import ch.supsi.fscli.frontend.event.FileSystemOpenEvent;
import ch.supsi.fscli.frontend.util.I18nManager;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class CommandLineView implements ViewComponent, PropertyChangeListener {
    private PreferenceController  preferenceController;
    private I18nManager  i18n;
    // FX Componenents
    private TextField commandLine;
    private Label commandLineLabel;
    private Button enter;

    private IFileSystemController fileSystemController;

    public CommandLineView(IFileSystemController fileSystemController, PreferenceController preferenceController, I18nManager i18nManager){
        this.fileSystemController = fileSystemController;
        this.preferenceController = preferenceController;
        this.i18n = i18nManager;
        createLayout();
    }


    private void createLayout() {

        this.enter = new Button();
        this.enter.setId("enter");


        this.commandLineLabel = new Label();
        this.commandLine = new TextField();

        // Disabilito di default
        this.setDisable(true);

        this.commandLine.setFont(this.preferenceController.getCommandLineFont());
        this.commandLine.setPrefColumnCount(this.preferenceController.getColumn());
        setLocalizedText();

        this.enter.setOnAction(actionEvent -> {
                fileSystemController.sendCommand(commandLine.getText());
                commandLine.clear();

            }
        );

        this.commandLine.setOnAction(event -> this.enter.fire());
    }

    public void setDisable(boolean b) {
        this.commandLine.setDisable(b);
        this.enter.setDisable(b);
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


    public void setLocalizedText() {
        enter.setText(i18n.getString("commandLine.enter"));
        commandLineLabel.setText(i18n.getString("commandLine.command"));

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt instanceof FileSystemCreationEvent) {
            setDisable(false);
        }
        if (evt instanceof FileSystemOpenEvent)
            setDisable(false);


    }
}
