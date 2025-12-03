package ch.supsi.fscli.backend.business.command.commands;

/*
mv SOURCE DESTINATION
move a file/directory to a new file/directory
(therefore also acts as rename)
 */

import ch.supsi.fscli.backend.business.command.commands.validators.CommandValidator;
import ch.supsi.fscli.backend.business.command.commands.validators.ExactArgumentCountValidator;
import ch.supsi.fscli.backend.business.service.FileSystemService;

public class MvCommand extends AbstractValidatedCommand{
    public MvCommand(FileSystemService fileSystemService, String name, String synopsis, String description) {
        super(fileSystemService, name, synopsis, description);
    }

    @Override
    protected CommandValidator getValidator() {
        return new ExactArgumentCountValidator(getName(), 2);
    }

    @Override
    protected CommandResult executeCommand(CommandContext context) {
        String source = context.getArguments().get(0);
        String destination = context.getArguments().get(1);

        try {
            fileSystemService.move(source, destination);
            return CommandResult.success("Moved '" + source + "' to '" + destination + "'");
        } catch (IllegalArgumentException e){
            return CommandResult.error("mv: " + e.getMessage());
        }
        // TODO ADD SOFT LINK CONTROL
    }
}
