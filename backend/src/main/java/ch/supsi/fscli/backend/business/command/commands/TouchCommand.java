package ch.supsi.fscli.backend.business.command.commands;

import ch.supsi.fscli.backend.business.command.commands.validators.CommandValidator;
import ch.supsi.fscli.backend.business.command.commands.validators.RequiresArgumentsValidator;
import ch.supsi.fscli.backend.business.service.FileSystemService;

public class TouchCommand extends AbstractValidatedCommand{

    public TouchCommand(FileSystemService fileSystemService, String name, String synopsis, String description) {
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
        boolean anyFileCreated = false;

        for (String fileName : context.getArguments()) {
            if (fileName == null || fileName.trim().isEmpty()) {
                //errors.append("touch: invalid file name");
                errors.append(getName()).append(": ").append(translate("touch_invalid_filename"));
                hasErrors = true;
                continue;
            }

            try {
                fileSystemService.createFile(fileName);

                anyFileCreated = true;

                output.append(translate("touch_file_prefix"))
                        .append(fileName)
                        .append(translate("touch_created_suffix"))
                        .append("\n");
            } catch (Exception e) {
                hasErrors = true;
                //errors.append("touch: cannot create file '").append(fileName).append("': ").append(e.getMessage());
                errors.append(getName())
                        .append(": ")
                        .append(translate("cannot_create_file_prefix"))
                        .append(fileName)
                        .append(translate("touch_error_suffix"))
                        .append(e.getMessage())
                        .append("\n");

            }

        }
        if(anyFileCreated)
            fileSystemService.setDataToSave(true); // mods done (also partially)

        if(hasErrors){
            return CommandResult.error(errors.toString().trim());
        }
        return CommandResult.success(output.toString().trim());
    }
}
