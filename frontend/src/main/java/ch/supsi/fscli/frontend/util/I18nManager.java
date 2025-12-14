package ch.supsi.fscli.frontend.util;

import ch.supsi.fscli.frontend.model.ITranslationModel;
import ch.supsi.fscli.frontend.model.TranslationModel;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Locale;
import java.util.ResourceBundle;

@Singleton
public class I18nManager {
    private static final String BUNDLE_BASE_NAME = "i18n.labels";
    private final ITranslationModel translationModel;

    private ResourceBundle resourceBundle;
    private Locale locale;

    @Inject
    public I18nManager(ITranslationModel translationModel) {
        this.translationModel = translationModel;
    }

    public void setLocale(Locale locale) {
        try {
            resourceBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale);
            this.locale = locale;
        } catch (Exception e) {
            System.err.println("Could not load resource bundle for locale: " + locale + ". Falling back to default.");
            resourceBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, Locale.ROOT);
        }
    }

    public Locale getLocale() {
        return locale;
    }

    public String getString(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (Exception e) { // search in the backend
            return translationModel.getString(key);
        }
    }
}