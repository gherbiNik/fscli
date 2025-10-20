package ch.supsi.fscli.frontend.controller;

public interface IPreferenceController {
    void setPreferences(String key, String value);
    String getPreferences(String value);
}
