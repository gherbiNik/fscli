package ch.supsi.fscli.frontend.controller;

import ch.supsi.fscli.frontend.model.IPreferenceModel;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.scene.text.Font;

@Singleton
public class PreferenceController implements IPreferenceController {
    private final IPreferenceModel preferenceModel;

    @Inject
    public PreferenceController(IPreferenceModel preferenceModel) {
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

    @Override
    public void savePreferences(String languageComboBox, String columnsSpinner, String outputLinesSpinner, String logLinesSpinner, String commandLineFontComboBox, String outputAreaFontComboBox, String logAreaFontComboBox) {
        preferenceModel.savePreferences(languageComboBox, columnsSpinner, outputLinesSpinner, logLinesSpinner, commandLineFontComboBox, outputAreaFontComboBox, logAreaFontComboBox);

    }


}
