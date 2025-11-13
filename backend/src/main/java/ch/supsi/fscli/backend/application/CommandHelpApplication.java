package ch.supsi.fscli.backend.application;

import ch.supsi.fscli.backend.business.command.CommandHelpContainer;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CommandHelpApplication implements ICommandHelpApplication {
    private static CommandHelpApplication instance;
    private CommandHelpContainer container;

    private CommandHelpApplication() {
    }

    public static CommandHelpApplication getInstance(CommandHelpContainer container) {
        if (instance == null) {
            instance = new CommandHelpApplication();
            instance.initialize(container);
        }
        return instance;
    }

    private void initialize(CommandHelpContainer container){
        this.container = container;
    }

    @Override
    public List<String> getCommandDescriptions(Locale locale) {
        return container.getCommandDescriptions();
    }
}