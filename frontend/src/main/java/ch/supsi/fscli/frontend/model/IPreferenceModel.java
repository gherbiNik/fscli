package ch.supsi.fscli.frontend.model;

import java.nio.file.Path;

public interface IPreferenceModel {
    void setPreferences(String value, Object object);
    Object getPreferences(String key);
    Path getUserPreferencesDirectoryPath();
}
