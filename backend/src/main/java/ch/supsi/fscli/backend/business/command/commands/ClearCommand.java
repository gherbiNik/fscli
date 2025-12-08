package ch.supsi.fscli.backend.business.command.commands;

import ch.supsi.fscli.backend.business.command.commands.validators.CommandValidator;
import ch.supsi.fscli.backend.business.command.commands.validators.NoArgumentsValidator;
import ch.supsi.fscli.backend.business.command.commands.validators.NoOptionsValidator;
import ch.supsi.fscli.backend.business.service.FileSystemService;

public class ClearCommand extends AbstractValidatedCommand{
    public ClearCommand(FileSystemService fileSystemService, String name, String synopsis, String description) {
        super(fileSystemService, name, synopsis, description);
    }

    @Override
    protected CommandValidator getValidator() {
        return new NoArgumentsValidator(getName())
                .and(new NoOptionsValidator(getName()));
    }

    @Override
    protected CommandResult executeCommand(CommandContext context) {
        System.out.println("WDJNRNFREFNRENIERNNWIENENIJFNIRENJIEWRNI");
        return CommandResult.success("Perform Clear");
    }
}
