package ch.supsi.fscli.backend.application.filesystem;

import ch.supsi.fscli.backend.business.command.business.CommandExecutor;
import ch.supsi.fscli.backend.business.command.business.CommandLoader;
import ch.supsi.fscli.backend.business.command.business.CommandParser;
import ch.supsi.fscli.backend.business.command.commands.ICommand;
import ch.supsi.fscli.backend.business.filesystem.FileSystem;
import ch.supsi.fscli.backend.business.filesystem.IFileSystem;
import ch.supsi.fscli.backend.business.service.FileSystemService;
import ch.supsi.fscli.backend.dataAccess.ICommandDAO;
import ch.supsi.fscli.backend.dataAccess.JsonCommandDAO;
import ch.supsi.fscli.backend.util.BackendTranslator;

import java.util.ArrayList;
import java.util.List;

public class FileSystemApplication implements IFileSystemApplication {

    private static FileSystemApplication instance;
    private IFileSystem fileSystem;
    private List<ICommand> loadedCommands;

    private FileSystemApplication() {}

    public static FileSystemApplication getInstance() {
        if (instance == null) {
            instance = new FileSystemApplication();
        }
        return instance;
    }

    @Override
    public void createFileSystem() {
        // 1. Inizializziamo il FileSystem
        fileSystem = FileSystem.getInstance();
        System.out.println("File System initialized.");

        // 2. Inizializziamo il Service
        FileSystemService fsService = FileSystemService.getInstance((FileSystem) fileSystem);

        // 3. Prepariamo il DAO e il Loader
        ICommandDAO commandDAO = new JsonCommandDAO("commands.json");
        CommandLoader loader = new CommandLoader(commandDAO, fsService);

        // 4. Carichiamo i comandi dal JSON tramite Reflection
        System.out.println("Loading commands...");
        this.loadedCommands = loader.loadCommands();

        // 5. Inizializziamo il CommandExecutor con la lista di comandi caricati
        CommandParser parser = CommandParser.getInstance();
        CommandExecutor executor = CommandExecutor.getInstance(fsService, parser, this.loadedCommands);

        // 6. Ora che l'Executor è pronto e pieno di comandi, lo diamo al FileSystem
        ((FileSystem) fileSystem).setCommandExecutor(executor);

        System.out.println("Commands loaded: " + this.loadedCommands.size());
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
        // FIX: Se i comandi non sono ancora stati caricati (perché non ho fatto "Nuovo"),
        // li carichiamo ora per poter mostrare l'Help.
        if (loadedCommands == null) {
            // 1. Recuperiamo le dipendenze minime necessarie
            FileSystem fs = FileSystem.getInstance();
            FileSystemService fsService = FileSystemService.getInstance(fs);
            ICommandDAO commandDAO = new JsonCommandDAO("commands.json");
            CommandLoader loader = new CommandLoader(commandDAO, fsService);

            // 2. Carichiamo i comandi
            this.loadedCommands = loader.loadCommands();
        }

        List<String> descriptions = new ArrayList<>();
        // Se per qualche motivo ancora null, ritorna vuoto
        if (loadedCommands == null) return descriptions;

        BackendTranslator translator = BackendTranslator.getInstance();

        for (ICommand cmd : loadedCommands) {
            String synopsis = translator.getString(cmd.getSynopsis());
            String description = translator.getString(cmd.getDescription());
            descriptions.add(synopsis + " : " + description);
        }
        return descriptions;
    }
}