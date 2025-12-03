package ch.supsi.fscli.backend.business.command.commands;

import ch.supsi.fscli.backend.business.command.commands.validators.CommandValidator;
import ch.supsi.fscli.backend.business.command.commands.validators.ExactArgumentCountValidator;
import ch.supsi.fscli.backend.business.command.commands.validators.NoOptionsValidator;
import ch.supsi.fscli.backend.business.command.commands.validators.RequiresArgumentsValidator;
import ch.supsi.fscli.backend.business.service.FileSystemService;

public class CdCommand extends AbstractValidatedCommand{

    public CdCommand(FileSystemService fileSystemService, String name, String synopsis, String description) {
        super(fileSystemService, name, synopsis, description);
    }

    @Override
    protected CommandValidator getValidator() {
        return new RequiresArgumentsValidator(getName())
                .and(new ExactArgumentCountValidator(getName(), 1))
                .and(new NoOptionsValidator(getName()));
    }

    // cd [DIR]
    @Override
    protected CommandResult executeCommand(CommandContext context) {
        String newDirPath = context.getArguments().get(0);
        try {
            fileSystemService.changeDirectory(newDirPath);
        } catch (IllegalArgumentException e){
            return CommandResult.error("Error: " + e.getMessage());
        }


        return CommandResult.success(""); // Nothing should be notified
    }
}
