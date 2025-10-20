package ch.supsi.fscli.frontend.controller;

import ch.supsi.fscli.frontend.view.ExitView;
import javafx.application.Platform;

public class ExitController implements IExitController {

    private static ExitController instance;
    private ExitView exitView;

    private ExitController() {}

    public static ExitController getInstance(ExitView exitView) {
        if (instance == null) {
            instance = new ExitController();
        }
        instance.initialize(exitView);
        return instance;
    }

    private void initialize(ExitView exitView) {
        this.exitView = exitView;
        setupEventHandlers();
    }

    private void setupEventHandlers() {
        exitView.getConfirmButton().setOnAction(e -> handleConfirm());
        exitView.getCancelButton().setOnAction(e -> handleCancel());
    }

    private void handleConfirm() {
        exitView.closeView();
        Platform.exit();
    }

    private void handleCancel() {
        exitView.closeView();
    }

    @Override
    public void showExitDialog() {
        exitView.showView();
    }
}