package ch.supsi.fscli.frontend.model;

import ch.supsi.fscli.backend.application.CommandHelpApplication;
import ch.supsi.fscli.frontend.util.I18nManager;


import java.util.Map;

public class CommandHelpModel {
    private CommandHelpApplication commandHelpApplication;
    private static CommandHelpModel instance;
    private I18nManager i18n;

    private CommandHelpModel() {
    }

    public static CommandHelpModel getInstance(CommandHelpApplication commandHelpApplication, I18nManager i18n) {
        if (instance == null) {
            instance = new CommandHelpModel();
            instance.initialize(commandHelpApplication, i18n);
        }
        return instance;
    }

    private void initialize(CommandHelpApplication commandHelpApplication, I18nManager i18n) {
        this.commandHelpApplication = commandHelpApplication;
        this.i18n = i18n;
    }

    public Map<String, String> getCommandDescriptions() {
        return commandHelpApplication.getCommandDescriptions(i18n.getLocale());
    }


}
