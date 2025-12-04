package ch.supsi.fscli.frontend.model;

import ch.supsi.fscli.backend.application.filesystem.IFileSystemApplication;
import java.util.List;

public class CommandHelpModel implements ICommandHelpModel {
    private static CommandHelpModel instance;
    private IFileSystemApplication fileSystemApplication;

    private CommandHelpModel() {}

    // Singleton che richiede l'istanza dell'applicazione backend
    public static CommandHelpModel getInstance(IFileSystemApplication fileSystemApplication) {
        if (instance == null) {
            instance = new CommandHelpModel();
            instance.initialize(fileSystemApplication);
        }
        return instance;
    }

    private void initialize(IFileSystemApplication fileSystemApplication) {
        this.fileSystemApplication = fileSystemApplication;
    }

    @Override
    public List<String> getCommandDescriptions() {
        return fileSystemApplication.getCommandsHelp();
    }
}