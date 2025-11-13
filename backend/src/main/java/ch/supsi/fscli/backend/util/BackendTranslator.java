package ch.supsi.fscli.backend.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class BackendTranslator {
    private static final String BUNDLE_BASE_NAME = "i18n.responses";
    private static BackendTranslator instance;

    private ResourceBundle resourceBundle;
    private Locale currentLocale;

    private BackendTranslator(){}

    public static BackendTranslator getInstance(){
        if(instance == null) {
            instance = new BackendTranslator();

        }
        return instance;
    }

    public void setLocale(Locale locale) {
        try {
            resourceBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale);
            this.currentLocale = locale;
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
