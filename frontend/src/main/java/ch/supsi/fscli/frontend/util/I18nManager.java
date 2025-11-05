package ch.supsi.fscli.frontend.util;

import ch.supsi.fscli.frontend.model.TranslationModel;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18nManager {
    private static I18nManager instance;
    private static final String BUNDLE_BASE_NAME = "i18n.labels";
    private TranslationModel translationModel;

    private ResourceBundle resourceBundle;

    private I18nManager(){}

    public static I18nManager getInstance(TranslationModel translationModel) {
        if(instance == null){
            instance = new I18nManager();
            instance.initialize(translationModel);
        }
        return instance;
    }

    private void initialize(TranslationModel translationModel)
    {
        this.translationModel = translationModel;
    }

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
        } catch (Exception e) { // search in the backend
            return translationModel.getString(key);
        }
    }
}