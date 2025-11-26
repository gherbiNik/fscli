package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.controller.PreferenceController;
import ch.supsi.fscli.frontend.util.I18nManager;
import javafx.scene.Node;
import javafx.scene.control.TextArea;

public class OutputView implements UncontrolledFxView{
    private static OutputView instance;
    private PreferenceController preferenceController;
    private I18nManager i18nManager;

    private TextArea outputView;


    private OutputView(){}

    public static OutputView getInstance(PreferenceController preferenceController,I18nManager i18n){
        if(instance == null){
            instance = new OutputView();
            instance.initialize(i18n, preferenceController);
        }
        return instance;
    }

    @Override
    public void initialize(I18nManager i18n, PreferenceController preferenceController) {
        this.preferenceController = preferenceController;
        this.i18nManager = i18n;

        this.outputView = new TextArea();
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
    public void update(String message) {
        outputView.appendText(message);
    }

    @Override
    public void setLocalizedText() {
        // FIXME
    }

    @Override
    public Node getNode() {
        return this.outputView;
    }
}
