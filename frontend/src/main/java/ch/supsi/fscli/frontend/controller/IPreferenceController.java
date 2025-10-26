package ch.supsi.fscli.frontend.controller;

import javafx.scene.text.Font;

public interface IPreferenceController {
    void setPreferences(String key, String value);
    String getPreferences(String value);

    Font getCommandLineFont();
    Font getOutputAreaFont();
    Font getLogAreaFont();

    int getOutputAreaRow();
    int getLogAreaRow();
    int getColumn();
}
