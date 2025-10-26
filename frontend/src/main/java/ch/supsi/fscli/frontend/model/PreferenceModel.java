package ch.supsi.fscli.frontend.model;

import ch.supsi.fscli.backend.application.IPreferenceApplication;
import javafx.scene.text.Font;

import java.nio.file.Path;

public class PreferenceModel implements IPreferenceModel{

    private static PreferenceModel instance;
    private IPreferenceApplication preferenceApplication;

    private static final double DEFAULT_FONT_SIZE = Font.getDefault().getSize();

    private PreferenceModel() {}

    public static PreferenceModel getInstance(IPreferenceApplication preferenceApplication) {
        if (instance == null) {
            instance = new PreferenceModel();
            instance.initialize(preferenceApplication);
        }
        return instance;
    }

    private void initialize(IPreferenceApplication preferenceApplication) {
        this.preferenceApplication = preferenceApplication;
    }

    @Override
    public void setPreferences(String key, String value) {
        preferenceApplication.setPreference(key, value);
    }

    @Override
    public String getPreferences(String key) {
        return preferenceApplication.getPreference(key);
    }

    @Override
    public Path getUserPreferencesDirectoryPath() {
        return preferenceApplication.getUserPreferencesDirectoryPath();
    }

    @Override
    public Font getCommandLineFont() {
        return new Font(preferenceApplication.getCommandLineFont(), DEFAULT_FONT_SIZE);
    }

    @Override
    public Font getLogAreaFont() {
        return new Font(preferenceApplication.getLogAreaFont(), DEFAULT_FONT_SIZE);
    }

    @Override
    public Font getOutputAreaFont() {
        return new Font(preferenceApplication.getOutputAreaFont(), DEFAULT_FONT_SIZE);
    }

    @Override
    public int getOutputAreaRow() {
        return preferenceApplication.getOutputAreaRow();
    }

    @Override
    public int getLogAreaRow() {
        return preferenceApplication.getLogAreaRow();
    }

    @Override
    public int getColumn() {
        return preferenceApplication.getColumn();
    }
}
