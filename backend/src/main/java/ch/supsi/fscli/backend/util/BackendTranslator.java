package ch.supsi.fscli.backend.util;

import com.google.inject.Singleton;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

@Singleton
public class BackendTranslator {
    private static final String BUNDLE_BASE_NAME = "i18n.responses";
    private static final String BUNDLE_BASE_NAME_TEST = "i18n.test";

    private ResourceBundle resourceBundle;
    private Locale currentLocale;

    public BackendTranslator(){}

    /* Testing purpose */
    public void setLocaleDefault(Locale locale){
        try {
            resourceBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME_TEST, locale);
            if (!resourceBundle.getLocale().getLanguage().equals(locale.getLanguage())) {
                System.err.println("No matching language bundle for: " + locale + ". Falling back to root.");
                resourceBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME_TEST, Locale.ROOT);
                this.currentLocale = Locale.ROOT;
            } else {
                this.currentLocale = locale;
            }
        } catch (Exception e) {
            System.err.println("Could not load resource bundle for locale: " + locale + ". Falling back to default.");
            resourceBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME_TEST, Locale.ROOT);
            this.currentLocale = Locale.ROOT;
        }
    }

    public void setLocale(Locale locale) {
        try {
            resourceBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale);
            if (!resourceBundle.getLocale().getLanguage().equals(locale.getLanguage())) {
                System.err.println("No matching language bundle for: " + locale + ". Falling back to root.");
                resourceBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, Locale.ROOT);
                this.currentLocale = Locale.ROOT;
            } else {
                this.currentLocale = locale;
            }
        } catch (Exception e) {
            System.err.println("Could not load resource bundle for locale: " + locale + ". Falling back to default.");
            resourceBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, Locale.ROOT);
            this.currentLocale = Locale.ROOT;
        }
    }

    public String getString(String key) {
        try {
            System.out.println(resourceBundle.getString(key));
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
