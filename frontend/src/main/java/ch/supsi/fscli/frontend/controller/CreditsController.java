package ch.supsi.fscli.frontend.controller;


import ch.supsi.fscli.frontend.view.CreditsView;
import ch.supsi.fscli.frontend.util.I18nManager;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class CreditsController {
    private final CreditsView creditsView;
    private final I18nManager i18nManager;

    @Inject
    public CreditsController(I18nManager i18nManager, CreditsView creditsView) {
        this.i18nManager = i18nManager;
        this.creditsView = creditsView;
        updateView();
    }

    private void updateView() {
        // Set all translatable elements via setters
        creditsView.setStageTitle(i18nManager.getString("credits.name"));
        creditsView.setAppName(i18nManager.getString("credits.appname"));

        // Frontend info
        creditsView.setFrontendVersion(i18nManager.getString("credits.version"));
        creditsView.setFrontendBuildDate(i18nManager.getString("app.buildDate"));

        // Backend info from model
        creditsView.setBackendVersion(i18nManager.getString("backend.version"));
        creditsView.setBackendBuildDate(i18nManager.getString("backend.buildDate"));

        // Authors
        creditsView.setAuthorLabel(i18nManager.getString("credits.devteam"));
    }
}