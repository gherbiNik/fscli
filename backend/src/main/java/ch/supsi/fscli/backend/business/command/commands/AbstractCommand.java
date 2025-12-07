package ch.supsi.fscli.backend.business.command.commands;

import ch.supsi.fscli.backend.business.service.IFileSystemService;

// At the moment of creation of the command, we insert the info: name - synopis and descr from the .properties file.
// we can use the CommandHelpContainer to get the infos
// for this purpose we use its field: """ Map<String, CommandDetails > commandDetailsMap """

public abstract class AbstractCommand  implements ICommand{
    private final String name;
    private final String synopsis;
    private final String description;
    protected final IFileSystemService fileSystemService;

    public AbstractCommand(IFileSystemService fileSystemService, String name, String synopsis, String description) {
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
