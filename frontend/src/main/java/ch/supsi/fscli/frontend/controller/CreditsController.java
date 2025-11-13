package ch.supsi.fscli.frontend.controller;


import ch.supsi.fscli.frontend.model.TranslationModel;
import ch.supsi.fscli.frontend.view.CreditsView;
import ch.supsi.fscli.backend.application.TranslationApplication;
import ch.supsi.fscli.frontend.util.I18nManager;
import java.util.Locale;

public class CreditsController {
    private static CreditsController instance;
    private CreditsView view;
    private I18nManager i18nManager;

    private CreditsController() {
    }

    public static CreditsController getInstance(I18nManager i18nManager, CreditsView view) {
        if (instance == null) {
            instance = new CreditsController();
            instance.initialize(i18nManager, view);
        }
        return instance;
    }

    private void initialize(I18nManager i18nManager, CreditsView view) {
        this.i18nManager = i18nManager;
        this.view = view;
        updateView();
    }

    private void updateView() {

        // Set all translatable elements via setters
        view.setStageTitle(i18nManager.getString("credits.name"));
        view.setAppName(i18nManager.getString("credits.appname"));

        // Frontend info
        view.setFrontendVersion(i18nManager.getString("credits.version"));
        view.setFrontendBuildDate(i18nManager.getString("app.buildDate"));

        // Backend info from model
        view.setBackendVersion(i18nManager.getString("backend.version"));
        view.setBackendBuildDate(i18nManager.getString("backend.buildDate"));

        // Authors
        view.setAuthorLabel(i18nManager.getString("credits.devteam"));
    }
}