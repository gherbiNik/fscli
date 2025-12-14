package ch.supsi.fscli.backend.business;

import ch.supsi.fscli.backend.business.command.business.CommandExecutor;
import ch.supsi.fscli.backend.business.command.business.CommandParser;
import ch.supsi.fscli.backend.business.command.commands.*;
import ch.supsi.fscli.backend.business.command.commands.validators.AbstractValidator;
import ch.supsi.fscli.backend.business.filesystem.FileSystem;
import ch.supsi.fscli.backend.business.service.FileSystemService;
import ch.supsi.fscli.backend.util.BackendTranslator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CommandExecutorTest {

    private CommandExecutor commandExecutor;
    private FileSystemService fileSystemService;
    private FileSystem fileSystem;

    @BeforeEach
    void setUp() {
        // 1. Creiamo le dipendenze base manualmente (Manual Injection)
        // Non servono resetSingleton perché ogni test avrà le sue nuove istanze!
        BackendTranslator translator = new BackendTranslator();
        translator.setLocaleDefault(Locale.US);

        // Configuriamo i componenti statici legacy (se non ancora refactorizzati)
        AbstractValidatedCommand.setTranslator(translator);
        AbstractValidator.setTranslator(translator);

        // 2. Creiamo il "mondo" (FileSystem e Service)
        fileSystem = new FileSystem();
        fileSystem.create();
        fileSystemService = new FileSystemService(fileSystem, translator);

        CommandParser parser = new CommandParser();

        // 3. Prepariamo la lista dei comandi per il test
        List<ICommand> commands = new ArrayList<>();
        // Possiamo passare stringhe fittizie per synopsis/desc, non influenzano la logica del test
        commands.add(new RmCommand(fileSystemService, "rm", "rm [FILE]...", "remove files"));

        // 4. Creiamo l'Executor iniettando le dipendenze nel costruttore
        commandExecutor = new CommandExecutor(fileSystemService, parser, commands);
    }

    @Test
    @DisplayName("L'espansione '*' funziona con il comando 'rm'")
    void testWildcardExpansionWithRm() {
        // Setup: Crea file nella root
        fileSystemService.createFile("file1.txt");
        fileSystemService.createFile("file2.txt");
        fileSystemService.createDirectory("dir1");

        assertNotNull(fileSystem.resolveNode("/file1.txt"));
        assertNotNull(fileSystem.resolveNode("/file2.txt"));
        assertNotNull(fileSystem.resolveNode("/dir1"));

        // Esecuzione: "rm *"
        // Il comando 'rm' riceverà ["file1.txt", "file2.txt", "dir1"]
        // Rimuoverà i file e fallirà sulla directory
        CommandResult result = commandExecutor.execute("rm *");

        // Assert: Il comando fallisce (correttamente) sulla directory
        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
        //assertTrue(result.getError().contains("is a directory"));
        System.out.println(fileSystem);
        // Verifica che l'espansione abbia funzionato e i file siano stati rimossi
        assertNull(fileSystem.resolveNode("/file1.txt"));
        assertNull(fileSystem.resolveNode("/file2.txt"), "file2.txt should have been removed");
        assertNotNull(fileSystem.resolveNode("/dir1"), "dir1 should NOT have been removed");
    }

    @Test
    @DisplayName("L'espansione '*' non espande 'file*.txt'")
    void testWildcardNoPartialMatch() {
        fileSystemService.createFile("fileA.txt");

        // Esecuzione: rm riceverà ["*.txt"]
        CommandResult result = commandExecutor.execute("rm *.txt");

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
        //System.out.println(result.getError());

        // Il file originale non è stato toccato
        assertNotNull(fileSystem.resolveNode("/fileA.txt"));
    }

    @Test
    @DisplayName("L'espansione '*' in directory vuota non passa argomenti")
    void testWildcardExpansionEmptyDir() {
        // Esecuzione: rm riceverà []
        CommandResult result = commandExecutor.execute("rm *");

        // rm si lamenterà (correttamente) che mancano argomenti
        assertFalse(result.isSuccess());
        assertNotNull(result.getError());

    }
}