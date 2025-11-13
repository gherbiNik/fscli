package ch.supsi.fscli.backend.business.command;

import ch.supsi.fscli.backend.business.service.FileSystemService;

public abstract class AbstractCommand  implements ICommand{
    private final String name;
    private final String synopsis;
    private final String description;
    protected final FileSystemService fileSystemService;

    public AbstractCommand(FileSystemService fileSystemService, String name, String synopsis, String description) {
        this.fileSystemService = fileSystemService;
        this.name = name;
        this.synopsis = synopsis;
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSynopsis() {
        return synopsis;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
