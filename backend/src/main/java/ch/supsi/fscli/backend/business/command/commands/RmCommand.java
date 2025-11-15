package ch.supsi.fscli.backend.business.command.commands;

import ch.supsi.fscli.backend.business.service.FileSystemService;

public class RmCommand extends AbstractCommand {

    public RmCommand(FileSystemService fileSystemService, String name, String synopsis, String description) {
        super(fileSystemService, name, synopsis, description);
    }

    @Override
    public CommandResult execute(CommandContext context) {
        if (context.getArguments() == null || context.getArguments().isEmpty()) {
            return CommandResult.error("rm: missing arguments");
        }

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
