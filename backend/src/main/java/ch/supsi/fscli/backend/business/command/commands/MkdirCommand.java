package ch.supsi.fscli.backend.business.command.commands;

import ch.supsi.fscli.backend.business.service.IFileSystemService;

public class MkdirCommand extends AbstractCommand {

    public MkdirCommand(IFileSystemService fileSystemService, String name, String synopsis, String description) {
        super(fileSystemService, name, synopsis, description);
    }

    @Override
    public CommandResult execute(CommandContext context) {
        // Check if dir name is present
        if (context.getArguments() == null || context.getArguments().isEmpty()) {
            return CommandResult.error("mkdir: missing name");
        }

        StringBuilder output = new StringBuilder();
        StringBuilder errors = new StringBuilder();
        boolean hasErrors = false;

        for (String directoryName : context.getArguments()) {
            if (directoryName == null || directoryName.trim().isEmpty()) {
                errors.append("mkdir: invalid directory name");
                hasErrors = true;
                continue;
            }

            try {
                fileSystemService.createDirectory(directoryName);
                output.append("Directory '").append(directoryName).append("' created successfully");
            } catch (Exception e) {
                hasErrors = true;
                errors.append("mkdir: cannot create directory '").append(directoryName).append("': ").append(e.getMessage());
            }

        }
        if(hasErrors){
            return CommandResult.error(errors.toString().trim());
        }
        return CommandResult.success(output.toString().trim());
    }
}