package ch.supsi.fscli.frontend.model;


import ch.supsi.fscli.backend.util.BackendTranslator;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class TranslationModel implements ITranslationModel {
    private final BackendTranslator backendTranslator;

    @Inject
    public TranslationModel(BackendTranslator backendTranslator) {
        this.backendTranslator = backendTranslator;
    }

    public String getString(String key) {
        return backendTranslator.getString(key);
    }
}