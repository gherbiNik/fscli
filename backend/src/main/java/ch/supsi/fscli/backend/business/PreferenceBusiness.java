package ch.supsi.fscli.backend.business;

import ch.supsi.fscli.backend.dataAccess.IPreferenceDAO;


import java.nio.file.Path;
import java.util.Properties;

public class PreferenceBusiness implements IPreferenceBusiness{
    private static PreferenceBusiness instance;

    private IPreferenceDAO preferencesDao;
    private Properties userPreferences;

    private PreferenceBusiness() {}


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
    public void setPreference(String key, String value) {
        this.preferencesDao.setPreference(key, value);
    }

    @Override
    public Path getUserPreferencesDirectoryPath() {
        return preferencesDao.getPreferencesDirectoryPath();
    }

    @Override
    public String getCommandLineFont() {
        return preferencesDao.getCommandLineFont();
    }

    @Override
    public String getOutputAreaFont() {
        return preferencesDao.getOutputAreaFont();
    }

    @Override
    public String getLogAreaFont() {
        return preferencesDao.getLogAreaFont();
    }

    @Override
    public int getOutputAreaRow() {
        return preferencesDao.getOutputAreaRow();
    }

    @Override
    public int getLogAreaRow() {
        return preferencesDao.getLogAreaRow();
    }

    @Override
    public int getColumn() {
        return preferencesDao.getColumn();
    }
}
