package ch.supsi.fscli.frontend.controller;

import ch.supsi.fscli.frontend.MainFx;
import ch.supsi.fscli.frontend.model.IExitModel;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.stage.Stage;

@Singleton
public class ExitController implements IExitController {
    private final IExitModel exitModel;

    @Inject
    public ExitController(IExitModel exitModel) {
        this.exitModel = exitModel;
    }

    @Override
    public void quit() {
        if (exitModel.isExitPossible()) {
            Stage stageToClose = MainFx.getStageToClose();
            if (stageToClose != null) {
                stageToClose.close();
            }
        }


    }
}