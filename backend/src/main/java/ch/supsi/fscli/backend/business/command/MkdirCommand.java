package ch.supsi.fscli.backend.business.command;

import ch.supsi.fscli.backend.business.service.FileSystemService;

public class MkdirCommand extends AbstractCommand {

    public MkdirCommand(FileSystemService fileSystemService, String name, String synopsis, String description) {
        super(fileSystemService, name, synopsis, description);
    }

    @Override
    public CommandResult execute(CommandContext context) {
        // Check if dir name is present
        if (context.getArguments() == null || context.getArguments().isEmpty()) {
            return CommandResult.error("mkdir: missing name");
        }

        String directoryName = context.getArguments().get(0);

        if (directoryName == null || directoryName.trim().isEmpty()) {
            return CommandResult.error("mkdir: invalid directory name");
        }

        try {
            fileSystemService.createDirectory(directoryName);
            return CommandResult.success("Directory '" + directoryName + "' created successfully");
        } catch (Exception e) {
            return CommandResult.error("mkdir: cannot create directory '" + directoryName + "': " + e.getMessage());
        }
    }

}