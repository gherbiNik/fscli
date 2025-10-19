package ch.supsi.fscli.frontend.controller;

public interface IPreferenceController {
    void setPreferences(String value, Object object);
    Object getPreferences(String value);
}
