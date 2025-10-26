package ch.supsi.fscli.backend.business;

import javafx.scene.text.Font;

import java.nio.file.Path;

public interface IPreferenceBusiness {
    String getCurrentLanguage();

    String getPreference(String key);

    void setPreference(String key, String value);
    Path getUserPreferencesDirectoryPath();

    Font getCommandLineFont();
    Font getOutputAreaFont();
    Font getLogAreaFont();

    int getOutputAreaRow();
    int getLogAreaRow();
    int getColumn();


}
