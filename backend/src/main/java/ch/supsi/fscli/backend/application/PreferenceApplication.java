package ch.supsi.fscli.backend.application;

import ch.supsi.fscli.backend.business.preferences.IPreferenceBusiness;
import com.google.inject.Inject;
import com.google.inject.Singleton;


import java.nio.file.Path;
import java.util.Locale;

@Singleton
public class PreferenceApplication implements IPreferenceApplication{
    private final IPreferenceBusiness preferenceBusiness;

    @Inject
    public PreferenceApplication(IPreferenceBusiness preferenceBusiness) {
        this.preferenceBusiness = preferenceBusiness;
    }

    @Override
    public String getPreference(String key) {
        return this.preferenceBusiness.getPreference(key);
    }

    @Override
    public void setPreference(String key, String value) {
        this.preferenceBusiness.setPreference(key, value);
    }

    @Override
    public Path getUserPreferencesDirectoryPath() {
        return this.preferenceBusiness.getUserPreferencesDirectoryPath();
    }


    @Override
    public Locale loadLanguagePreference() {
        String langTag = this.getPreference("language-tag");

        // FIX: Gestione corretta del formato lingua_PAESE (es. it_IT, en_US)
        if (langTag != null && langTag.contains("_")) {
            String[] parts = langTag.split("_");
            if (parts.length >= 2) {
                return new Locale(parts[0], parts[1]);
            }
        }

        // Fallback per tag semplici (es. "en", "it") o formati non standard
        return new Locale(langTag);
    }

    @Override
    public void saveLanguagePreference(Locale locale) {
        String langTag = locale.getLanguage();
        this.setPreference("language-tag", langTag);
    }

    @Override
    public String getCommandLineFont() {
        return preferenceBusiness.getCommandLineFont();
    }

    @Override
    public String getLogAreaFont() {
        return preferenceBusiness.getLogAreaFont();
    }

    @Override
    public String getOutputAreaFont() {
        return preferenceBusiness.getOutputAreaFont();
    }

    @Override
    public int getOutputAreaRow() {
        return preferenceBusiness.getOutputAreaRow();
    }

    @Override
    public int getLogAreaRow() {
        return preferenceBusiness.getLogAreaRow();
    }

    @Override
    public int getColumn() {
        return preferenceBusiness.getColumn();
    }

}
