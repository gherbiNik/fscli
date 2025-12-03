package ch.supsi.fscli.backend.business.command.commands.validators;

import ch.supsi.fscli.backend.util.BackendTranslator;

public abstract class AbstractValidator {
    protected final String commandName;

    private static BackendTranslator i18n;

    public static void setTranslator(BackendTranslator translator) {
        i18n = translator;
    }

    protected String translate(String key) {
        return i18n.getString(key);
    }

    public AbstractValidator(String commandName) {
        this.commandName = commandName;
    }
}
