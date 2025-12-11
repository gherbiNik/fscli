package ch.supsi.fscli.backend.application.filesystem;

import ch.supsi.fscli.backend.business.command.business.CommandExecutor;
import ch.supsi.fscli.backend.business.command.business.CommandLoader;
import ch.supsi.fscli.backend.business.command.commands.ICommand;
import ch.supsi.fscli.backend.business.filesystem.FileSystem;
import ch.supsi.fscli.backend.business.filesystem.IFileSystem;
import ch.supsi.fscli.backend.util.BackendTranslator;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class FileSystemApplication implements IFileSystemApplication {

    private final IFileSystem fileSystem;
    private final BackendTranslator translator;
    private List<ICommand> loadedCommands;

    // Richiediamo tutti i componenti necessari all'avvio:
    @Inject
    public FileSystemApplication(IFileSystem fileSystem, CommandExecutor executor, CommandLoader commandLoader, BackendTranslator translator) {
        this.fileSystem = fileSystem;
        this.translator = translator;

        // 1. WIRING MANUALE (Setter Injection): necessario per rompere il ciclo tra FileSystem e Executor.
        if (fileSystem instanceof FileSystem) {
            ((FileSystem) fileSystem).setCommandExecutor(executor);
        }

        // 2. CARICAMENTO COMANDI: li carichiamo una volta sola nel costruttore
        this.loadedCommands = commandLoader.loadCommands();
        System.out.println("File System initialized (via Guice).");
        System.out.println("Commands loaded: " + this.loadedCommands.size());
    }


    @Override
    public void createFileSystem() {
        // Deleghiamo semplicemente l'azione di reset al Business Layer (IFileSystem)
        fileSystem.create();
        System.out.println("File system state reset (delegated to Business Layer).");
    }

    @Override
    public String sendCommand(String userInput) {
        return fileSystem.executeCommand(userInput);
    }

    @Override
    public boolean isDataToSave() {
        return fileSystem.isDataToSave();
    }

    @Override
    public void setDataToSave(boolean dataToSave) {
        fileSystem.setDataToSave(dataToSave);
    }

    @Override
    public boolean isFileSystemCreated() {
        return fileSystem!=null;
    }

    @Override
    public List<String> getCommandsHelp() {
        List<String> descriptions = new ArrayList<>();
        if (loadedCommands == null) return descriptions; // Dovrebbe essere impossibile ora

        for (ICommand cmd : loadedCommands) {
            String synopsis = translator.getString(cmd.getSynopsis());
            String description = translator.getString(cmd.getDescription());
            descriptions.add(synopsis + " : " + description);
        }
        return descriptions;
    }
}