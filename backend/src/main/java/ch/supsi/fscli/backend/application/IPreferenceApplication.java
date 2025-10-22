package ch.supsi.fscli.backend.application;

import java.nio.file.Path;
import java.util.Locale;

public interface IPreferenceApplication {
    String getPreference(String key);
    void setPreference(String key, String value);
    Path getUserPreferencesDirectoryPath();

    Locale loadLanguagePreference();
    void saveLanguagePreference(Locale locale);
}
