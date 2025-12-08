package ch.supsi.fscli.backend.business.command.commands.validators;

import ch.supsi.fscli.backend.business.command.commands.CommandContext;
import ch.supsi.fscli.backend.business.command.commands.CommandResult;

public class NoArgumentsValidator extends AbstractValidator implements CommandValidator{

    public NoArgumentsValidator(String commandName) {
        super(commandName);
    }

    // The command requires no args (ex: [clear])
    @Override
    public CommandResult validate(CommandContext context) {
        if((context.getArguments() != null) && (!context.getArguments().isEmpty()))
            return CommandResult.error(commandName + ": " + translate("invalid.noargs"));

        return null; // Validation passed
    }

}
