package ch.supsi.fscli.backend.business.command.commands;

import ch.supsi.fscli.backend.business.command.commands.validators.CommandValidator;
import ch.supsi.fscli.backend.business.command.commands.validators.NoArgumentsValidator;
import ch.supsi.fscli.backend.business.command.commands.validators.NoOptionsValidator;
import ch.supsi.fscli.backend.business.service.IFileSystemService;

public class PwdCommand extends AbstractValidatedCommand{


    public PwdCommand(IFileSystemService fileSystemService, String name, String synopsis, String description) {
        super(fileSystemService, name, synopsis, description);
    }

    @Override
    protected CommandValidator getValidator() {
        return new NoArgumentsValidator(getName())
                .and(new NoOptionsValidator(getName()));
    }

    @Override
    protected CommandResult executeCommand(CommandContext context) {
        return CommandResult.success(translate("root") + ": " + fileSystemService.getCurrentDirectoryAbsolutePath() + "\n");
    }
}
