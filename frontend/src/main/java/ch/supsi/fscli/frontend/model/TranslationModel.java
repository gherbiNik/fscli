package ch.supsi.fscli.frontend.model;


import ch.supsi.fscli.backend.application.TranslationApplication;

public class TranslationModel implements ITranslationModel {
    private TranslationApplication translation;
    private static TranslationModel instance;

    private TranslationModel() {
    }

    public static TranslationModel getInstance(TranslationApplication translation){
        if (instance == null) {
            instance = new TranslationModel();
            instance.initialize(translation);
        }
        return instance;
    }

    private void initialize(TranslationApplication translation){
        this.translation = translation;
    }

    public String getString(String key) {
        return translation.getString(key);
    }

}