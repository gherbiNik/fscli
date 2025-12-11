package ch.supsi.fscli.frontend.controller;

import ch.supsi.fscli.frontend.MainFx;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.stage.Stage;

@Singleton
public class ExitController implements IExitController {

    @Inject
    public ExitController() {
        // In futuro qui potrai iniettare IFileSystemController per controllare i salvataggi!
    }

    @Override
    public void quit() {
        //TODO: check if there is something to save

        Stage stageToClose = MainFx.getStageToClose();
        if (stageToClose != null) {
            stageToClose.close();
        }
    }
}