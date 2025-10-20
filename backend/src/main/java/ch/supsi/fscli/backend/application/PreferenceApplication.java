package ch.supsi.fscli.backend.application;

import ch.supsi.fscli.backend.business.IPreferenceBusiness;

import java.nio.file.Path;

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

}
