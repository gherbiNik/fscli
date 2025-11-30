package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.controller.PreferenceController;
import ch.supsi.fscli.frontend.event.ClearEvent;
import ch.supsi.fscli.frontend.event.OutputEvent;
import ch.supsi.fscli.frontend.util.I18nManager;
import javafx.scene.Node;
import javafx.scene.control.TextArea;

import javax.swing.text.View;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class OutputView implements ViewComponent, PropertyChangeListener {

    private PreferenceController preferenceController;
    private I18nManager i18nManager;

    private TextArea outputView;


    public OutputView(I18nManager i18n, PreferenceController preferenceController){
        this.preferenceController = preferenceController;
        this.i18nManager = i18n;
        createLayout();
    }

    public void createLayout() {


        this.outputView = new TextArea();
        this.outputView.setEditable(false);
        this.outputView.setId("outputView");

        // EXAMPLE TEXT
        this.outputView.appendText("1This is an example output text...\n");
        this.outputView.appendText("2This is an example output text...\n");
        this.outputView.appendText("3This is an example output text...\n");
        this.outputView.appendText("4This is an example output text...\n");
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
        }
    }

}
