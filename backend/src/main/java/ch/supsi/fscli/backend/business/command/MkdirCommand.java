package ch.supsi.fscli.backend.business.command;

import ch.supsi.fscli.backend.business.service.FileSystemService;

public class MkdirCommand implements ICommand{

    private FileSystemService fileSystemService; // used to create a dir

    public MkdirCommand(FileSystemService fileSystemService) { // FIXME maybe to change position
        this.fileSystemService = fileSystemService;
    }

    @Override
    public CommandResult execute(CommandContext context) {
        return null;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getSynopsis() {
        return "";
    }

    @Override
    public String getDescription() {
        return "";
    }
}
