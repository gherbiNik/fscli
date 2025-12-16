package ch.supsi.fscli.backend.business.command.commands;

import ch.supsi.fscli.backend.business.command.commands.validators.CommandValidator;
import ch.supsi.fscli.backend.business.command.commands.validators.RequiresArgumentsValidator;
import ch.supsi.fscli.backend.business.service.FileSystemService;
import ch.supsi.fscli.backend.business.service.IFileSystemService;

public class RmCommand extends AbstractValidatedCommand {

    public RmCommand(IFileSystemService fileSystemService, String name, String synopsis, String description) {
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
        boolean any = false;

        for (String fileName : context.getArguments()) {
            if (fileName == null || fileName.trim().isEmpty()) {
                //errors.append("rm: invalid file name\n");
                errors.append(getName())
                        .append(": ")
                        .append(translate("invalid_filename"))
                        .append("\n");
                hasErrors = true;
                continue;
            }

            try {
                fileSystemService.removeFile(fileName);

                any = true;
                output.append("\n");
//                output.append(translate("rm_deleted_prefix"))
//                        .append(fileName)
//                        .append(translate("rm_deleted_suffix"))
//                        .append("\n");
            } catch (Exception e) {
                //errors.append("rm: cannot remove file '").append(fileName).append("': ").append(e.getMessage()).append("\n");
                errors.append(getName())
                        .append(": ")
                        .append(translate("cannot_remove_file_prefix"))
                        .append(fileName)
                        .append(translate("rm_error_suffix")) // Contiene "': "
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
}
