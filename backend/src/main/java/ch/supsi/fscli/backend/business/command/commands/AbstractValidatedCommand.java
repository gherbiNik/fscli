package ch.supsi.fscli.backend.business.command.commands;

import ch.supsi.fscli.backend.business.command.commands.validators.CommandValidator;
import ch.supsi.fscli.backend.business.service.FileSystemService;
import ch.supsi.fscli.backend.business.service.IFileSystemService;
import ch.supsi.fscli.backend.util.BackendTranslator;

/*
DESIGN PATTERN: Template
 */

public abstract class AbstractValidatedCommand extends AbstractCommand {
    private static BackendTranslator i18n;

    public AbstractValidatedCommand(IFileSystemService fileSystemService, String name,
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


    public static void setTranslator(BackendTranslator translator) {
        i18n = translator;
    }

    protected String translate(String key) {// To show messages when commands are executed
        return i18n.getString(key);
    }

    protected abstract CommandValidator getValidator();
    protected abstract CommandResult executeCommand(CommandContext context);
}
