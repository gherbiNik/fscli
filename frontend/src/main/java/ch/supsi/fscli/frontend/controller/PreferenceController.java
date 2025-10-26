package ch.supsi.fscli.frontend.controller;

import ch.supsi.fscli.frontend.model.IPreferenceModel;
import javafx.scene.text.Font;

public class PreferenceController implements IPreferenceController {

    private static PreferenceController instance;
    private IPreferenceModel preferenceModel;

    private PreferenceController() {}

    public static PreferenceController getInstance(IPreferenceModel preferenceModel) {
        if (instance == null) {
            instance = new PreferenceController();
            instance.initialize(preferenceModel);
        }
        return instance;
    }

    private void initialize(IPreferenceModel preferenceModel) {
        this.preferenceModel = preferenceModel;
    }

    @Override
    public void setPreferences(String key, String value) {
        this.preferenceModel.setPreferences(key, value);
    }

    @Override
    public String getPreferences(String value) {
        return this.preferenceModel.getPreferences(value);
    }

    @Override
    public Font getCommandLineFont() {
        return preferenceModel.getCommandLineFont();
    }

    @Override
    public Font getOutputAreaFont() {
        return preferenceModel.getOutputAreaFont();
    }

    @Override
    public Font getLogAreaFont() {
        return preferenceModel.getLogAreaFont();
    }

    @Override
    public int getOutputAreaRow() {
        return preferenceModel.getOutputAreaRow();
    }

    @Override
    public int getLogAreaRow() {
        return preferenceModel.getLogAreaRow();
    }

    @Override
    public int getColumn() {
        return preferenceModel.getColumn();
    }


}
