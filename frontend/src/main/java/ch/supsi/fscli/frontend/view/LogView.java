package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.controller.PreferenceController;
import ch.supsi.fscli.frontend.event.*;
import ch.supsi.fscli.frontend.util.I18nManager;
import javafx.scene.Node;
import javafx.scene.control.TextArea;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class LogView implements ViewComponent, PropertyChangeListener {

    private PreferenceController preferenceController;
    private I18nManager i18n;
    private TextArea logView;




    public LogView(I18nManager i18n, PreferenceController preferenceController){
        this.preferenceController = preferenceController;
        this.i18n = i18n;
        createLayout();
    }

    public void createLayout() {

        this.logView = new TextArea();
        this.logView.setId("logView");

            //Example code
//        this.logView.appendText("1This is an example log text...\n");
//        this.logView.appendText("2This is an example log text...\n");
//        this.logView.appendText("3This is an example log text...\n");
//        this.logView.appendText("4This is an example log text...\n");
        logView.setFont(this.preferenceController.getLogAreaFont());
        logView.setPrefRowCount(this.preferenceController.getLogAreaRow());

    }

    @Override
    public Node getNode() {
        return this.logView;
    }

    public void log(String message) {
        logView.appendText(message + "\n");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // La LogView ascolta eventi diversi, es. ""
        if (evt instanceof FileSystemCreationEvent) {
            log(i18n.getString("log.fsCreated"));
        }
        if (evt instanceof PreferenceSavedEvent) {
            log(i18n.getString("log.preferenceSaved"));
        }

        if (evt instanceof FileSystemSaved)
            log("DA TRADURRE: fs salvato");

        if (evt instanceof FileSystemSavedAs)
            log("DA TRADURRE: fs salvato come...");

        if (evt instanceof FileSystemOpenEvent)
            log("DA TRADURRE: fs open");



    }

}
