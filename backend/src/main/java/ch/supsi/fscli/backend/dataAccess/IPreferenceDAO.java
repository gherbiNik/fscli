package ch.supsi.fscli.backend.dataAccess;

import javafx.scene.text.Font;

import java.nio.file.Path;
import java.util.Properties;

public interface IPreferenceDAO {
    Properties getPreferences();

    void setPreference(String key, String value);

    Path getPreferencesDirectoryPath();

    Path getUserPreferencesFilePath();

    Font getCommandLineFont();
    Font getOutputAreaFont();
    Font getLogAreaFont();
    int getOutputAreaRow();
    int getLogAreaRow();
    int getColumn();
}
