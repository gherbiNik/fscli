package ch.supsi.fscli.backend.dataAccess;

import java.nio.file.Path;
import java.util.Properties;

public interface IPreferenceDAO {
    Properties getPreferences();

    void setPreference(String key, String value);

    Path getPreferencesDirectoryPath();

    Path getUserPreferencesFilePath();
}
