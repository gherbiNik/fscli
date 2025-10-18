package ch.supsi.fscli.frontend.controller;

import ch.supsi.fscli.frontend.model.IPreferenceModel;

public class PreferenceController implements IPreferenceController {

    private static PreferenceController instance;
    private IPreferenceModel preferenceModel;

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


}
