package ch.supsi.fscli.frontend.model;


import ch.supsi.fscli.backend.application.CreditsFacade;
import java.util.Locale;

public class CreditsBackendModel {
    private  CreditsFacade creditsFacade;
    private static CreditsBackendModel instance;

    public CreditsBackendModel() {
    }

    public static CreditsBackendModel getInstance(CreditsFacade creditsFacade){
        if (instance == null) {
            instance = new CreditsBackendModel();
            instance.initialize(creditsFacade);
        }
        return instance;
    }

    private void initialize(CreditsFacade creditsFacade){
        this.creditsFacade = creditsFacade;
    }

    public String getBackendName(Locale locale) {
        return creditsFacade.getBackendName(locale);
    }

    public String getBackendVersion(Locale locale) {
        return creditsFacade.getVersionInfo(locale);
    }

    public String getBackendBuildDate(Locale locale) {
        return creditsFacade.getBuildDateInfo(locale);
    }
}