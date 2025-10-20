package ch.supsi.fscli.backend.business;

import java.nio.file.Path;

public interface IPreferenceBusiness {
    String getCurrentLanguage();

    String getPreference(String key);

    void setPreference(String key, String value);
    Path getUserPreferencesDirectoryPath();
}
