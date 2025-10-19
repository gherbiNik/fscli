package ch.supsi.fscli.backend.business;

import ch.supsi.fscli.backend.dataAccess.IPreferenceDAO;

import java.nio.file.Path;
import java.util.Properties;

public class PreferenceBusiness implements IPreferenceBusiness{
    private static PreferenceBusiness instance;

    private IPreferenceDAO preferencesDao;

    private Properties userPreferences;


    public static PreferenceBusiness getInstance(IPreferenceDAO preferencesDao) {
        if (instance == null) {
            instance = new PreferenceBusiness();
            instance.initialize(preferencesDao);
        }

        return instance;
    }

    private void initialize( IPreferenceDAO preferencesDao ){
        this.preferencesDao = preferencesDao;
        this.userPreferences = preferencesDao.getPreferences();
    }

    @Override
    public String getCurrentLanguage() {
        //con getProperty in base al tag capis cosa deve restituire
        return userPreferences.getProperty("language-tag");
    }

    @Override
    public String getPreference(String key) {
        if (key == null || key.isEmpty() || userPreferences == null) {
            return null;
        }

        Object value = userPreferences.get(key);


        return (value != null) ? value.toString() : null;
    }

    @Override
    public void setPreference(String key, Object value) {
        this.preferencesDao.setPreference(key, value.toString());
    }

    @Override
    public Path getUserPreferencesDirectoryPath() {
        return preferencesDao.getPreferencesDirectoryPath();
    }
}
