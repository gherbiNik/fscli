package ch.supsi.fscli.frontend.model;

import javafx.scene.text.Font;

import java.nio.file.Path;

public interface IPreferenceModel {
    void setPreferences(String key, String value);
    String getPreferences(String key);
    Path getUserPreferencesDirectoryPath();

    Font getCommandLineFont();
    Font getLogAreaFont();
    Font getOutputAreaFont();

    int getOutputAreaRow();
    int getLogAreaRow();
    int getColumn();
}
