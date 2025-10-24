package ch.supsi.fscli.frontend.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18nManager {
    private static final I18nManager instance = new I18nManager();
    private static final String BUNDLE_BASE_NAME = "i18n.labels";

    private ResourceBundle resourceBundle;

    private I18nManager(){}

    public static I18nManager getInstance() { return instance;}

    public void setLocale(Locale locale) {
        try {
            resourceBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale);
        } catch (Exception e) {
            System.err.println("Could not load resource bundle for locale: " + locale + ". Falling back to default.");
            resourceBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, Locale.ROOT);
        }
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
