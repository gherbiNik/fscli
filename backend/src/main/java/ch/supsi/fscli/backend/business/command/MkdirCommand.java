package ch.supsi.fscli.backend.business.command;

import ch.supsi.fscli.backend.business.service.FileSystemService;

public class MkdirCommand implements ICommand {

    private FileSystemService fileSystemService; // used to create a dir

    public MkdirCommand(FileSystemService fileSystemService) { // FIXME maybe to change position
        this.fileSystemService = fileSystemService;
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

    //FIXME: insert this infos in a proper location
    @Override
    public String getName() {
        return "mkdir";
    }

    @Override
    public String getSynopsis() {
        return "mkdir DIRECTORY";
    }

    @Override
    public String getDescription() {
        return "Create the DIRECTORY, if it does not already exist.";
    }
}