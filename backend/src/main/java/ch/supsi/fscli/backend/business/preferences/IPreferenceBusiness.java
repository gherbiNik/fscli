package ch.supsi.fscli.backend.business.preferences;



import java.nio.file.Path;

public interface IPreferenceBusiness {
    String getCurrentLanguage();

    String getPreference(String key);

    void setPreference(String key, String value);
    Path getUserPreferencesDirectoryPath();

    String getCommandLineFont();
    String getOutputAreaFont();
    String getLogAreaFont();

    int getOutputAreaRow();
    int getLogAreaRow();
    int getColumn();


}
