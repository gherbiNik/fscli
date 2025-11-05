package ch.supsi.fscli.frontend.controller;


import ch.supsi.fscli.frontend.model.CreditsBackendModel;
import ch.supsi.fscli.frontend.view.CreditsView;
import ch.supsi.fscli.backend.application.CreditsFacade;
import ch.supsi.fscli.frontend.util.I18nManager;
import java.util.Locale;

public class CreditsController {
    private static CreditsController instance;
    private CreditsBackendModel model;
    private CreditsView view;
    private I18nManager i18nManager;

    private CreditsController() {
    }

    public static CreditsController getInstance(I18nManager i18nManager, CreditsFacade creditsFacade, CreditsBackendModel model, CreditsView view) {
        if (instance == null) {
            instance = new CreditsController();
            instance.initialize(i18nManager, creditsFacade, model, view);
        }
        return instance;
    }

    private void initialize(I18nManager i18nManager, CreditsFacade creditsFacade, CreditsBackendModel model, CreditsView view) {
        this.i18nManager = i18nManager;
        this.model = model;
        this.view = view;
        updateView();
    }

    public void showCredits() {
        updateView();
        view.showView();
    }

    private void updateView() {
        Locale locale = i18nManager.getCurrentLocale();

        // Set all translatable elements via setters
        view.setStageTitle(i18nManager.getString("credits.name"));
        view.setAppName(i18nManager.getString("credits.appname"));

        // Frontend info
        view.setFrontendVersion(i18nManager.getString("credits.version"));
        view.setFrontendBuildDate(i18nManager.getString("app.buildDate"));

        // Backend info from model
        view.setBackendVersion(model.getBackendVersion(locale));
        view.setBackendBuildDate(model.getBackendBuildDate(locale));

        // Authors
        view.setAuthorLabel(i18nManager.getString("credits.devteam"));
    }
}