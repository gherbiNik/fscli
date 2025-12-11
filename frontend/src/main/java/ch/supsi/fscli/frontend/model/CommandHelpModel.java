package ch.supsi.fscli.frontend.model;

import ch.supsi.fscli.backend.application.filesystem.IFileSystemApplication;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.List;

@Singleton
public class CommandHelpModel implements ICommandHelpModel {
    private static CommandHelpModel instance;
    private final IFileSystemApplication fileSystemApplication;

    @Inject
    public CommandHelpModel(IFileSystemApplication fileSystemApplication) {
        this.fileSystemApplication = fileSystemApplication;
    }

    @Override
    public List<String> getCommandDescriptions() {
        return fileSystemApplication.getCommandsHelp();
    }
}