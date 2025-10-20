package ch.supsi.fscli.backend.application;

import java.nio.file.Path;

public interface IPreferenceApplication {
    String getPreference(String key);
    void setPreference(String key, String value);
    Path getUserPreferencesDirectoryPath();
}
