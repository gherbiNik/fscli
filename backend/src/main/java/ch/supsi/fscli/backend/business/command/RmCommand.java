package ch.supsi.fscli.backend.business.command;

import ch.supsi.fscli.backend.business.service.FileSystemService;

public class RmCommand extends AbstractCommand{

    public RmCommand(FileSystemService fileSystemService, String name, String synopsis, String description) {
        super(fileSystemService, name, synopsis, description);
    }

    @Override
    public CommandResult execute(CommandContext context) {
        if (context.getArguments() == null || context.getArguments().isEmpty()) {
            return CommandResult.error("rm: missing file name");
        }

        String fileName = context.getArguments().get(0);

        if (fileName == null || fileName.trim().isEmpty()) {
            return CommandResult.error("rm: invalid file name");
        }

        try {
            fileSystemService.removeFile(fileName);
            return CommandResult.success("File '" + fileName + "' removed successfully");
        } catch (Exception e) {
            return CommandResult.error("rm cannot remove file '" + fileName + "': " + e.getMessage());
        }
    }


}
