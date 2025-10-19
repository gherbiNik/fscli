package ch.supsi.fscli.backend.application;

import java.nio.file.Path;

public interface IPreferenceApplication {
    Object getPreference(String key);
    void setPreference(String key, Object value);
    Path getUserPreferencesDirectoryPath();
}
