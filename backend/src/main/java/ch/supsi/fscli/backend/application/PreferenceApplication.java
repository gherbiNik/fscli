package ch.supsi.fscli.backend.application;

import ch.supsi.fscli.backend.business.IPreferenceBusiness;
import javafx.scene.text.Font;

import java.nio.file.Path;
import java.util.Locale;

public class PreferenceApplication implements IPreferenceApplication{
    private static  PreferenceApplication instance;

    private IPreferenceBusiness preferenceBusiness;

    private PreferenceApplication() {}

    public static PreferenceApplication getInstance(IPreferenceBusiness preferenceBusiness) {
        if (instance == null) {
            instance = new PreferenceApplication();
            instance.initialize(preferenceBusiness);
        }
        return instance;
    }

    private void initialize(IPreferenceBusiness preferenceBusiness) {
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

        return new Locale(langTag);
    }

    @Override
    public void saveLanguagePreference(Locale locale) {
        String langTag = locale.getLanguage();
        this.setPreference("language-tag", langTag);
    }

    @Override
    public Font getCommandLineFont() {
        return preferenceBusiness.getCommandLineFont();
    }

    @Override
    public Font getLogAreaFont() {
        return preferenceBusiness.getLogAreaFont();
    }

    @Override
    public Font getOutputAreaFont() {
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
