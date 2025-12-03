package ch.supsi.fscli.backend.business.command.commands;

import ch.supsi.fscli.backend.business.command.commands.validators.CommandValidator;
import ch.supsi.fscli.backend.business.command.commands.validators.RequiresArgumentsValidator;
import ch.supsi.fscli.backend.business.service.FileSystemService;

public class RmCommand extends AbstractValidatedCommand {

    public RmCommand(FileSystemService fileSystemService, String name, String synopsis, String description) {
        super(fileSystemService, name, synopsis, description);
    }

    @Override
    protected CommandValidator getValidator() {
        return new RequiresArgumentsValidator(getName());
    }

    @Override
    protected CommandResult executeCommand(CommandContext context) {
        StringBuilder output = new StringBuilder();
        StringBuilder errors = new StringBuilder();
        boolean hasErrors = false;

        for (String fileName : context.getArguments()) {
            if (fileName == null || fileName.trim().isEmpty()) {
                errors.append("rm: invalid file name\n");
                hasErrors = true;
                continue;
            }

            try {
                fileSystemService.removeFile(fileName);
                output.append("File '").append(fileName).append("' deleted successfully\n");
            } catch (Exception e) {
                errors.append("rm: cannot remove file '").append(fileName).append("': ").append(e.getMessage()).append("\n");
                hasErrors = true;
            }
        }

        if (hasErrors) {
            return CommandResult.error(errors.toString().trim());
        }


        return CommandResult.success(output.toString().trim());
    }
}
