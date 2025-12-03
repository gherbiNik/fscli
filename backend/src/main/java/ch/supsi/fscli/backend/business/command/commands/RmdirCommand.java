package ch.supsi.fscli.backend.business.command.commands;

import ch.supsi.fscli.backend.business.command.commands.validators.CommandValidator;
import ch.supsi.fscli.backend.business.command.commands.validators.RequiresArgumentsValidator;
import ch.supsi.fscli.backend.business.service.FileSystemService;

// Removes an empty dir
public class RmdirCommand extends AbstractValidatedCommand{

    // used to create a dir

    public RmdirCommand(FileSystemService fileSystemService, String name, String synopsis, String description) {
        super(fileSystemService, name, synopsis, description);
    }

    @Override
    protected CommandResult executeCommand(CommandContext context) {
        StringBuilder output = new StringBuilder();
        StringBuilder errors = new StringBuilder();
        boolean hasErrors = false;

        // For each args
        for (String directoryName : context.getArguments()) {
            if (directoryName == null || directoryName.trim().isEmpty()) {
                errors.append("rmdir: invalid directory name\n");
                hasErrors = true;
                continue;
            }

            try {
                if(fileSystemService.removeDirectory(directoryName)) {
                    output.append("Directory '").append(directoryName).append("' deleted successfully\n");
                } else {
                    output.append("Directory '").append(directoryName).append("' cannot be deleted: it is not empty!\n");
                }
            } catch (Exception e) {
                errors.append("rmdir: cannot remove directory '").append(directoryName).append("': ").append(e.getMessage()).append("\n");
                hasErrors = true;
            }
        }

        if (hasErrors) {
            return CommandResult.error(errors.toString().trim());
        }
        // TODO ADD SOFT LINK CONTROLL
        return CommandResult.success(output.toString().trim());
    }

    @Override
    protected CommandValidator getValidator() {
        return new RequiresArgumentsValidator(getName());
    }

}
