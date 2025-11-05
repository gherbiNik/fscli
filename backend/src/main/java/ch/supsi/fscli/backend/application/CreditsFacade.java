package ch.supsi.fscli.backend.application;

import ch.supsi.fscli.backend.business.CreditsService;
import ch.supsi.fscli.backend.util.BackendTranslator;
import java.util.Locale;

// Description
// This class gives to the outside the translated Credits' informations

public class CreditsFacade {
    private static CreditsFacade instance;
    private CreditsService creditsService;

    private CreditsFacade() {
    }

    public static CreditsFacade getInstance(CreditsService creditsService) {
        if (instance == null) {
            instance = new CreditsFacade();
            instance.initialize(creditsService);
        }
        return instance;
    }

    private void initialize(CreditsService creditsService){
        this.creditsService = creditsService;
    }

    public String getVersionInfo(Locale locale) {
        String version = creditsService.getVersion();
        return BackendTranslator.getString("backend.version", locale, version);
    }


    public String getBuildDateInfo(Locale locale) {
        String buildDate = creditsService.getBuildDate();
        return BackendTranslator.getString("backend.buildDate", locale, buildDate);
    }


    public String getRawVersion() {
        return creditsService.getVersion();
    }

    public String getRawBuildDate() {
        return creditsService.getBuildDate();
    }
}
