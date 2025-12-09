package ch.supsi.fscli.backend.business.command.commands.validators;

import ch.supsi.fscli.backend.business.command.commands.CommandContext;
import ch.supsi.fscli.backend.business.command.commands.CommandResult;

public class RequiresAtLeastArgumentsValidator extends AbstractValidator implements CommandValidator{
    private final int expectedCount;

    public RequiresAtLeastArgumentsValidator(String commandName, int expectedCount) {
        super(commandName);
        this.expectedCount = expectedCount;
    }

    @Override
    public CommandResult validate(CommandContext context) {

        if (context.getArguments().size() < expectedCount) {
            return CommandResult.error(commandName + ": " + translate("invalid.missingarguments"));
        }
        return null; // Validation passed
    }
}
