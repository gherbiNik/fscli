package ch.supsi.fscli.backend.business.command.commands;

import ch.supsi.fscli.backend.business.command.commands.validators.CommandValidator;
import ch.supsi.fscli.backend.business.service.FileSystemService;

/*
DESIGN PATTERN: Template
 */

abstract class AbstractValidatedCommand extends AbstractCommand {

    public AbstractValidatedCommand(FileSystemService fileSystemService, String name,
                                    String synopsis, String description) {
        super(fileSystemService, name, synopsis, description);
    }

    @Override
    public final CommandResult execute(CommandContext context) {
        // Template Method: validation + execution
        CommandValidator validator = getValidator();

        if (validator != null) {
            CommandResult validationResult = validator.validate(context);
            if (validationResult != null) {
                return validationResult; // Validation failed
            }
        }

        return executeCommand(context); // Validation passed
    }

    protected abstract CommandValidator getValidator();
    protected abstract CommandResult executeCommand(CommandContext context);
}
