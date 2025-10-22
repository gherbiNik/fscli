package ch.supsi.fscli.frontend.controller;

import ch.supsi.fscli.frontend.view.ExitView;
import javafx.application.Platform;
import javafx.stage.Stage;
import ch.supsi.fscli.frontend.MainFx;

public class ExitController implements IExitController {

    private static ExitController instance;

    private ExitController() {}

    public static ExitController getInstance() {
        if (instance == null) {
            instance = new ExitController();
        }
        instance.initialize();
        return instance;
    }

    private void initialize() {
    }

    public void quit() {

        //TODO: check if there is something to save


        Stage stageToClose = MainFx.getStageToClose();
        if (stageToClose != null) {
            stageToClose.close();
        }
    }
}