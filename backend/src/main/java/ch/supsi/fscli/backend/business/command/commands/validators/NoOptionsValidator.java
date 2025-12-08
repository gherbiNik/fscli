package ch.supsi.fscli.backend.business.command.commands.validators;

import ch.supsi.fscli.backend.business.command.commands.CommandContext;
import ch.supsi.fscli.backend.business.command.commands.CommandResult;
import ch.supsi.fscli.backend.util.BackendTranslator;

public class NoOptionsValidator extends AbstractValidator implements CommandValidator{
    public NoOptionsValidator(String commandName) {
        super(commandName);

    }

    @Override
    public CommandResult validate(CommandContext context) {
        if((context.getOptions() != null) && (!context.getOptions().isEmpty()))
            return CommandResult.error(commandName + ": " + translate("invalid.noopt"));

        return null; // passed
    }
}
