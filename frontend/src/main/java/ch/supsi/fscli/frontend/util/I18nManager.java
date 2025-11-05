package ch.supsi.fscli.frontend.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18nManager {
    private static I18nManager instance;
    private static final String BUNDLE_BASE_NAME = "i18n.labels";

    private ResourceBundle resourceBundle;
    private Locale currentLocale;

    private I18nManager(){}

    public static I18nManager getInstance() {
        if(instance == null)
            instance = new I18nManager();

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

    public Locale getCurrentLocale() {
        return currentLocale != null ? currentLocale : Locale.getDefault();
    }

    public String getString(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (Exception e) {
            System.err.println("Key not found inside the frontend bundle: " + key);

            return "!" + key + "!";
        }
    }
}