package ch.supsi.fscli.backend.business.command.commands;

import ch.supsi.fscli.backend.business.command.commands.validators.CommandValidator;
import ch.supsi.fscli.backend.business.command.commands.validators.RequiresArgumentsValidator;
import ch.supsi.fscli.backend.business.service.FileSystemService;
import ch.supsi.fscli.backend.business.service.IFileSystemService;

// Removes an empty dir
public class RmdirCommand extends AbstractValidatedCommand{

    // used to create a dir

    public RmdirCommand(IFileSystemService fileSystemService, String name, String synopsis, String description) {
        super(fileSystemService, name, synopsis, description);
    }

    @Override
    protected CommandResult executeCommand(CommandContext context) {
        StringBuilder output = new StringBuilder();
        StringBuilder errors = new StringBuilder();
        boolean hasErrors = false;
        boolean any = false;


        // For each args
        for (String directoryName : context.getArguments()) {
            if (directoryName == null || directoryName.trim().isEmpty()) {
                //errors.append("rmdir: invalid directory name\n");
                errors.append(getName())
                        .append(": ")
                        .append(translate("rmdir_invalid_dirname"))
                        .append("\n");
                hasErrors = true;
                continue;
            }

            try {
                if(fileSystemService.removeDirectory(directoryName)) {
                    //output.append("Directory '").append(directoryName).append("' deleted successfully\n");
                    any = true;
                    output.append("\n");
//                    output.append(translate("rmdir_dir_prefix"))
//                            .append(directoryName)
//                            .append(translate("rmdir_deleted_suffix"))
//                            .append("\n");
                } else {
                    //output.append("Directory '").append(directoryName).append("' cannot be deleted: it is not empty!\n");
                    output.append(translate("rmdir_dir_prefix"))
                            .append(directoryName)
                            .append(translate("rmdir_not_empty_suffix"))
                            .append("\n");
                }
            } catch (Exception e) {
                //errors.append("rmdir: cannot remove directory '").append(directoryName).append("': ").append(e.getMessage()).append("\n");
                errors.append(getName())
                        .append(": ")
                        .append(translate("cannot_remove_dir_prefix"))
                        .append(directoryName)
                        .append(translate("rmdir_error_suffix")) // "': "
                        .append(e.getMessage())
                        .append("\n");
                hasErrors = true;
            }
        }

        if(any)
            fileSystemService.setDataToSave(true);

        if (hasErrors) {
            return CommandResult.error(errors.toString().trim());
        }
        return CommandResult.success(output.toString().trim());
    }

    @Override
    protected CommandValidator getValidator() {
        return new RequiresArgumentsValidator(getName());
    }

}
