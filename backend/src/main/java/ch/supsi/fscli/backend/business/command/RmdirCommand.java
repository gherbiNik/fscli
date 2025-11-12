package ch.supsi.fscli.backend.business.command;

import ch.supsi.fscli.backend.business.service.FileSystemService;

// Removes an empty dir
public class RmdirCommand implements ICommand{
    private FileSystemService fileSystemService; // used to create a dir

    public RmdirCommand(FileSystemService fileSystemService) { // FIXME maybe to change position
        this.fileSystemService = fileSystemService;
    }

    @Override
    public CommandResult execute(CommandContext context) {
        if (context.getArguments() == null || context.getArguments().isEmpty()) {
            return CommandResult.error("rmdir: missing name");
        }

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

        return CommandResult.success(output.toString().trim());
    }

    @Override
    public String getName() {
        return "rmdir";
    }

    @Override
    public String getSynopsis() {
        return "rmdir DIRECTORY";
    }

    @Override
    public String getDescription() {
        return "";
    }
}
