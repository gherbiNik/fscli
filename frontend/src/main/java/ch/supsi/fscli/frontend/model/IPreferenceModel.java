package ch.supsi.fscli.frontend.model;

import java.nio.file.Path;

public interface IPreferenceModel {
    void setPreferences(String key, String value);
    String getPreferences(String key);
    Path getUserPreferencesDirectoryPath();
}
