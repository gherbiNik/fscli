package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.controller.IPreferenceController; // Usiamo l'interfaccia!
import ch.supsi.fscli.frontend.event.*;
import ch.supsi.fscli.frontend.util.I18nManager;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.scene.Node;
import javafx.scene.control.TextArea;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

@Singleton
public class LogView implements ViewComponent, PropertyChangeListener {

    private final IPreferenceController preferenceController;
    private final I18nManager i18n;
    private TextArea logView;

    @Inject
    public LogView(I18nManager i18n, IPreferenceController preferenceController){
        this.preferenceController = preferenceController;
        this.i18n = i18n;
        createLayout();
    }

    public void createLayout() {
        this.logView = new TextArea();
        this.logView.setEditable(false);
        this.logView.setId("logView");

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
        if (evt instanceof FileSystemCreationEvent) {
            log(i18n.getString("log.fsCreated"));
        }
        if (evt instanceof PreferenceSavedEvent) {
            log(i18n.getString("log.preferenceSaved"));
        }

        if (evt instanceof FileSystemSaved)
            log(i18n.getString("log.fileSaved") + evt.getNewValue());

        if (evt instanceof FileSystemSavedAs)
            log(i18n.getString("log.fileSavedAs") + evt.getNewValue());

        if (evt instanceof FileSystemOpenEvent)
            log(i18n.getString("log.fileOpen") + evt.getNewValue());
    }
}