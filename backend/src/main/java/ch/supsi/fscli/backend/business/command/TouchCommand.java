package ch.supsi.fscli.backend.business.command;

import ch.supsi.fscli.backend.business.service.FileSystemService;

public class TouchCommand implements ICommand{
    private final FileSystemService fileSystemService;

    public TouchCommand(FileSystemService fileSystemService) {
        this.fileSystemService = fileSystemService;
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

    @Override
    public String getName() {
        return "touch";
    }

    @Override
    public String getSynopsis() {
        return "touch FILE";
    }

    @Override
    public String getDescription() {
        return "Create an empty text file, if it does not already exist.";
    }
}
