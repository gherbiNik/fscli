package ch.supsi.fscli.backend.util;

import com.google.inject.Singleton;
import java.util.Locale;
import java.util.ResourceBundle;

@Singleton
public class BackendTranslator {
    private static final String BUNDLE_BASE_NAME = "i18n.responses";

    // Definiamo una costante per il fallback sicuro
    private static final Locale DEFAULT_FALLBACK_LOCALE = Locale.US;

    private ResourceBundle resourceBundle;
    private Locale currentLocale;

    public BackendTranslator(){}

    /* Testing purpose */
    public void setLocaleDefault(Locale locale){
        try {
            resourceBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale);
            // Se la lingua caricata non corrisponde a quella richiesta, usa il fallback
            if (!resourceBundle.getLocale().getLanguage().equals(locale.getLanguage())) {
                System.err.println("No matching language bundle for: " + locale + ". Falling back to default (US).");
                resourceBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, DEFAULT_FALLBACK_LOCALE);
                this.currentLocale = DEFAULT_FALLBACK_LOCALE;
            } else {
                this.currentLocale = locale;
            }
        } catch (Exception e) {
            System.err.println("Could not load resource bundle for locale: " + locale + ". Falling back to default (US).");
            // Qui il fallback è sicuro perché sappiamo che test_en_US esiste
            resourceBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, DEFAULT_FALLBACK_LOCALE);
            this.currentLocale = DEFAULT_FALLBACK_LOCALE;
        }
    }

    public void setLocale(Locale locale) {
        try {
            resourceBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale);
            if (!resourceBundle.getLocale().getLanguage().equals(locale.getLanguage())) {
                System.err.println("No matching language bundle for: " + locale + ". Falling back to default (US).");
                resourceBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, DEFAULT_FALLBACK_LOCALE);
                this.currentLocale = DEFAULT_FALLBACK_LOCALE;
            } else {
                this.currentLocale = locale;
            }
        } catch (Exception e) {
            System.err.println("Could not load resource bundle for locale: " + locale + ". Falling back to default (US).");
            resourceBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, DEFAULT_FALLBACK_LOCALE);
            this.currentLocale = DEFAULT_FALLBACK_LOCALE;
        }
    }

    public String getString(String key) {
        try {
            // Nota: Rimuoverei il System.out.println in produzione per pulizia, ma per ora ok
            return resourceBundle.getString(key);
        } catch (Exception e) {
            System.err.println("Key not found inside the frontend bundle: " + key);
            return "!" + key + "!";
        }
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }
}