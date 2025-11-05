package ch.supsi.fscli.backend.application;

import ch.supsi.fscli.backend.util.BackendTranslator;
import java.util.Locale;

// Description
// This class gives to the outside the translated Credits' informations

public class TranslationApplication implements ITranslationApplication {
    private static TranslationApplication instance;
    private BackendTranslator translator;

    private TranslationApplication() {
    }

    public static TranslationApplication getInstance(BackendTranslator translator) {
        if (instance == null) {
            instance = new TranslationApplication();
            instance.initialize(translator);
        }
        return instance;
    }

    private void initialize(BackendTranslator translator){
        this.translator = translator;
    }

    @Override
    public String getString(String key) {
        return translator.getString(key);
    }

}
