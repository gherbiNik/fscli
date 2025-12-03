package ch.supsi.fscli.backend.business.command.commands.validators;

/*
    DESIGN PATTERN: Chain of responsibility
 */

import ch.supsi.fscli.backend.business.command.commands.CommandContext;
import ch.supsi.fscli.backend.business.command.commands.CommandResult;

public interface CommandValidator {
    CommandResult validate(CommandContext context);

    default CommandValidator and(CommandValidator next){
        return context -> {
            CommandResult result = this.validate(context);
            return result != null ? result : next.validate(context);
        };
    }
}
