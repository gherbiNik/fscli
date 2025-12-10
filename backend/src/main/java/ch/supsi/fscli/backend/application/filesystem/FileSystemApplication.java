package ch.supsi.fscli.backend.application.filesystem;

import ch.supsi.fscli.backend.application.module.BackendModule;
import ch.supsi.fscli.backend.business.command.business.CommandExecutor;
import ch.supsi.fscli.backend.business.command.business.CommandLoader;
import ch.supsi.fscli.backend.business.command.business.CommandParser;
import ch.supsi.fscli.backend.business.command.commands.ICommand;
import ch.supsi.fscli.backend.business.filesystem.FileSystem;
import ch.supsi.fscli.backend.business.filesystem.IFileSystem;
import ch.supsi.fscli.backend.business.service.FileSystemService;
import ch.supsi.fscli.backend.business.service.IFileSystemService;
import ch.supsi.fscli.backend.dataAccess.ICommandDAO;
import ch.supsi.fscli.backend.dataAccess.JsonCommandDAO;
import ch.supsi.fscli.backend.util.BackendTranslator;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import java.util.ArrayList;
import java.util.List;

public class FileSystemApplication implements IFileSystemApplication {

    private static FileSystemApplication instance;
    private IFileSystem fileSystem;
    private List<ICommand> loadedCommands;

    // Teniamo l'injector nel caso servisse ad altri
    private Injector injector;

    private FileSystemApplication() {}

    public static FileSystemApplication getInstance() {
        if (instance == null) {
            instance = new FileSystemApplication();
        }
        return instance;
    }

    @Override
    public void createFileSystem() {
        // 1. BOOTSTRAP: Accendiamo il robot con le istruzioni del modulo!
        this.injector = Guice.createInjector(new BackendModule());
        System.out.println("Guice Injector created.");

        // 2. RECUPERO AUTOMATICO
        // Chiediamo l'IFileSystem: Guice crea automaticamente a cascata
        // FileSystem -> Service -> DAO -> ecc.
        this.fileSystem = injector.getInstance(IFileSystem.class);

        // Chiediamo il CommandExecutor: Guice attiverà il Loader e userà il metodo @Provides
        // per passargli la lista dei comandi.
        CommandExecutor executor = injector.getInstance(CommandExecutor.class);

        // 3. WIRING MANUALE (Setter Injection)
        // Dobbiamo inserire l'executor nel filesystem manualmente perché FileSystem
        // non può dipendere da Executor nel costruttore (ciclo).
        if (fileSystem instanceof FileSystem) {
            ((FileSystem) fileSystem).setCommandExecutor(executor);
        }

        // 4. RECUPERO DELLA LISTA
        // Per chiedere "List<ICommand>" serve questa sintassi speciale (TypeLiteral)
        // perché in Java i generici vengono cancellati a runtime.
        // ↓ Approccio più purista ↓
        //this.loadedCommands = injector.getInstance(Key.get(new TypeLiteral<List<ICommand>>(){}));
        CommandLoader commandLoader = injector.getInstance(CommandLoader.class);
        this.loadedCommands = commandLoader.loadCommands();

        System.out.println("File System initialized (via Guice).");
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
            // Se serve ricaricare al volo, usiamo l'injector già esistente o ne creiamo uno
            if (injector == null) {
                createFileSystem(); // Questo inizializza tutto
            }
            // La logica di reload è già in createFileSystem, quindi qui siamo a posto
        }

        List<String> descriptions = new ArrayList<>();
        // Se per qualche motivo ancora null, ritorna vuoto
        if (loadedCommands == null) return descriptions;

        BackendTranslator translator = injector.getInstance(BackendTranslator.class);

        for (ICommand cmd : loadedCommands) {
            String synopsis = translator.getString(cmd.getSynopsis());
            String description = translator.getString(cmd.getDescription());
            descriptions.add(synopsis + " : " + description);
        }
        return descriptions;
    }
}