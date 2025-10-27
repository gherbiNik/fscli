package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.controller.PreferenceController;
import ch.supsi.fscli.frontend.util.I18nManager;
import javafx.scene.Node;
import javafx.scene.control.TextArea;

public class LogView implements UncontrolledFxView{
    private static LogView instance;

    private PreferenceController preferenceController;
    private I18nManager i18n;
    private TextArea logView;




    private LogView(){
    }
    public static LogView getInstance(PreferenceController preferenceController, I18nManager i18nManager){
        if(instance == null){
            instance = new LogView();
            instance.initialize(i18nManager, preferenceController);
        }
        return instance;
    }


    @Override
    public Node getNode() {
        return this.logView;
    }

    @Override
    public void initialize(I18nManager i18n, PreferenceController preferenceController) {
        this.preferenceController = preferenceController;
        this.i18n = i18n;
        this.logView = new TextArea();
        this.logView.setId("logView");

        // Example code
        this.logView.appendText("1This is an example log text...\n");
        this.logView.appendText("2This is an example log text...\n");
        this.logView.appendText("3This is an example log text...\n");
        this.logView.appendText("4This is an example log text...\n");
        logView.setFont(this.preferenceController.getLogAreaFont());
        logView.setPrefRowCount(this.preferenceController.getLogAreaRow());

    }

    @Override
    public void update() {
        // TODO
    }

    @Override
    public void setLocalizedText() {
        // TODO
    }
}
