package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.controller.IPreferenceController; // Usiamo l'interfaccia!
import ch.supsi.fscli.frontend.event.ClearEvent;
import ch.supsi.fscli.frontend.event.FileSystemCreationEvent;
import ch.supsi.fscli.frontend.event.OutputEvent;
import ch.supsi.fscli.frontend.util.I18nManager;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.scene.Node;
import javafx.scene.control.TextArea;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

@Singleton
public class OutputView implements ViewComponent, PropertyChangeListener {

    private final IPreferenceController preferenceController;
    private final I18nManager i18nManager;
    private TextArea outputView;

    @Inject
    public OutputView(I18nManager i18n, IPreferenceController preferenceController){
        this.preferenceController = preferenceController;
        this.i18nManager = i18n;
        createLayout();
    }

    public void createLayout() {
        this.outputView = new TextArea();
        this.outputView.setEditable(false);
        this.outputView.setId("outputView");

        this.outputView.setPrefRowCount(this.preferenceController.getOutputAreaRow());
        outputView.setFont(this.preferenceController.getOutputAreaFont());
        outputView.setWrapText(false);
    }

    public void clear() {
        this.outputView.clear();
    }

    @Override
    public Node getNode() {
        return this.outputView;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt instanceof OutputEvent) {
            String text = (String) evt.getNewValue();
            this.outputView.appendText(text);
        } else if (evt instanceof ClearEvent) {
            this.outputView.clear();
        } else if(evt instanceof FileSystemCreationEvent){
            clear();
        }
    }
}