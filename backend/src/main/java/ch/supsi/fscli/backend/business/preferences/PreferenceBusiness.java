package ch.supsi.fscli.backend.business.preferences;

import ch.supsi.fscli.backend.dataAccess.preferences.IPreferenceDAO;
import com.google.inject.Inject;
import com.google.inject.Singleton;


import java.nio.file.Path;
import java.util.Properties;

@Singleton
public class PreferenceBusiness implements IPreferenceBusiness{
    private final IPreferenceDAO preferencesDao;
    private final Properties userPreferences;

    @Inject
    public PreferenceBusiness(IPreferenceDAO preferencesDao) {
        this.preferencesDao = preferencesDao;
        // Carichiamo le preferenze subito alla creazione del service.
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
