package ch.supsi.fscli.backend.business.command.commands;

import ch.supsi.fscli.backend.business.service.FileSystemService;

public class TouchCommand extends AbstractCommand{

    public TouchCommand(FileSystemService fileSystemService, String name, String synopsis, String description) {
        super(fileSystemService, name, synopsis, description);
    }

    @Override
    public CommandResult execute(CommandContext context) {
        if (context.getArguments() == null || context.getArguments().isEmpty()) {
            return CommandResult.error("touch: missing file name");
        }

        String fileName = context.getArguments().get(0);

        if (fileName == null || fileName.trim().isEmpty()) {
            return CommandResult.error("touch: invalid file name");
        }

        try {
            fileSystemService.createFile(fileName);
            return CommandResult.success("File '" + fileName + "' created successfully");
        } catch (Exception e) {
            return CommandResult.error("touch cannot create file '" + fileName + "': " + e.getMessage());
        }
    }

}
