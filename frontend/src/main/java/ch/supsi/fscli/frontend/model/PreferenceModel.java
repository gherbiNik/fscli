package ch.supsi.fscli.frontend.model;

import ch.supsi.fscli.backend.business.IPreferenceBusiness;

public class PreferenceModel implements IPreferenceModel{

    private static PreferenceModel instance;
    private IPreferenceBusiness preferenceBusiness;

    public static PreferenceModel getInstance() {
        if (instance == null) {
            instance = new PreferenceModel();
            instance.initialize();
        }
        return instance;
    }

    private void initialize() {

    }
}
