package ch.supsi.fscli.backend.business.command.commands.validators;

import ch.supsi.fscli.backend.business.command.commands.CommandContext;
import ch.supsi.fscli.backend.business.command.commands.CommandResult;

public class NoArgsOrOptNullValidator extends AbstractValidator implements CommandValidator{
    public NoArgsOrOptNullValidator(String commandName) {
        super(commandName);
    }

    @Override
    public CommandResult validate(CommandContext context) {
        if (context.getArguments() == null || context.getOptions() == null) {
            return CommandResult.error("internal error: arguments or options null");
        }

        return null; // passed
    }
}
