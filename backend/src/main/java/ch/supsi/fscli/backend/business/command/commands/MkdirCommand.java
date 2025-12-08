package ch.supsi.fscli.backend.business.command.commands;

import ch.supsi.fscli.backend.business.command.commands.validators.CommandValidator;
import ch.supsi.fscli.backend.business.command.commands.validators.RequiresArgumentsValidator;
import ch.supsi.fscli.backend.business.service.FileSystemService;
import ch.supsi.fscli.backend.business.service.IFileSystemService;

public class MkdirCommand extends AbstractValidatedCommand {

    public MkdirCommand(IFileSystemService fileSystemService, String name, String synopsis, String description) {
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
        boolean any = false; // any changes

        for (String directoryName : context.getArguments()) {
            if (directoryName == null || directoryName.trim().isEmpty()) {
                errors.append(getName()).append(": ").append(translate("invalid_directory_name"));
                hasErrors = true;
                continue;
            }

            try {
                fileSystemService.createDirectory(directoryName);
                any = true;

                output.append(translate("directory_created_prefix"))
                        .append(directoryName)
                        .append(translate("directory_created_suffix"))
                        .append("\n");
            } catch (Exception e) {
                hasErrors = true;
                errors.append(getName())
                        .append(": ")
                        .append(translate("cannot_create_dir_prefix"))
                        .append(directoryName)
                        .append(translate("dir_error_suffix"))
                        .append(e.getMessage())
                        .append("\n");
            }

        }
        if(any)
            fileSystemService.setDataToSave(true);

        if(hasErrors){
            return CommandResult.error(errors.toString().trim());
        }
        return CommandResult.success(output.toString().trim());
    }
}