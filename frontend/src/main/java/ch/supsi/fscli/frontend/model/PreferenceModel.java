package ch.supsi.fscli.frontend.model;

import ch.supsi.fscli.backend.application.IPreferenceApplication;
import ch.supsi.fscli.backend.business.IPreferenceBusiness;

import java.nio.file.Path;

public class PreferenceModel implements IPreferenceModel{

    private static PreferenceModel instance;
    private IPreferenceApplication preferenceApplication;

    private PreferenceModel() {}

    public static PreferenceModel getInstance(IPreferenceApplication preferenceApplication) {
        if (instance == null) {
            instance = new PreferenceModel();
            instance.initialize(preferenceApplication);
        }
        return instance;
    }

    private void initialize(IPreferenceApplication preferenceApplication) {
        this.preferenceApplication = preferenceApplication;
    }

    @Override
    public void setPreferences(String key, String value) {
        preferenceApplication.setPreference(key, value);
    }

    @Override
    public String getPreferences(String key) {
        return preferenceApplication.getPreference(key);
    }

    @Override
    public Path getUserPreferencesDirectoryPath() {
        return preferenceApplication.getUserPreferencesDirectoryPath();
    }
}
