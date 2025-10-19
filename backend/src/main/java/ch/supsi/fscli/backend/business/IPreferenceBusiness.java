package ch.supsi.fscli.backend.business;

import java.nio.file.Path;

public interface IPreferenceBusiness {
    String getCurrentLanguage();

    Object getPreference(String key);

    void setPreference(String key, Object value);
    Path getUserPreferencesDirectoryPath();
}
