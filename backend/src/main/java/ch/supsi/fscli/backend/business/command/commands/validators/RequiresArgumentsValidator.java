package ch.supsi.fscli.backend.business.command.commands.validators;

import ch.supsi.fscli.backend.business.command.commands.CommandContext;
import ch.supsi.fscli.backend.business.command.commands.CommandResult;

public class RequiresArgumentsValidator extends AbstractValidator implements CommandValidator{
    public RequiresArgumentsValidator(String commandName) {
        super(commandName);
    }

    @Override
    public CommandResult validate(CommandContext context) {
        if (context.getArguments() == null || context.getArguments().isEmpty()) {
            return CommandResult.error(commandName + ": " + translate("invalid.missingarguments"));
        }
        return null; // Validation passed
    }
}
