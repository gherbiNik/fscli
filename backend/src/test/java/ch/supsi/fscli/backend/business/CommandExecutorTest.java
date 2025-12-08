package ch.supsi.fscli.backend.business;

import ch.supsi.fscli.backend.business.command.business.CommandDetails;
import ch.supsi.fscli.backend.business.command.business.CommandExecutor;
import ch.supsi.fscli.backend.business.command.business.CommandHelpContainer;
import ch.supsi.fscli.backend.business.command.business.CommandParser;
import ch.supsi.fscli.backend.business.command.commands.*;
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
        // Reset di tutti i singleton per un test di integrazione pulito
        resetSingleton(CommandExecutor.class);
        resetSingleton(CommandHelpContainer.class);
        resetSingleton(FileSystemService.class);
        resetSingleton(BackendTranslator.class);
        resetSingleton(CommandParser.class);
        resetSingleton(FileSystem.class);

        // Inizializza le dipendenze
        BackendTranslator translator = BackendTranslator.getInstance();
        translator.setLocaleDefault(Locale.US);
        CommandHelpContainer container = CommandHelpContainer.getInstance(translator);

        fileSystem = FileSystem.getInstance();
        fileSystemService = FileSystemService.getInstance(fileSystem);
        CommandParser parser = CommandParser.getInstance();

        // Per testare l'executor, dobbiamo fornirgli comandi reali
        List<ICommand> commands = new ArrayList<>();
        Map<String, CommandDetails> m = container.getCommandDetailsMap();

        commands.add(new RmCommand(fileSystemService, "rm", m.get("rm").synopsis(), m.get("rm").description()));
        // Resettiamo CommandExecutor perché FileSystem.getInstance() ne ha creato uno vuoto
        // che impedirebbe l'inizializzazione corretta con la nostra lista 'commands'.
        resetSingleton(CommandExecutor.class);
        commandExecutor = CommandExecutor.getInstance(fileSystemService, parser, commands);
    }

    /**
     * Helper utility per resettare un campo 'instance' statico e privato.
     */
    private void resetSingleton(Class<?> aClass) {
        try {
            java.lang.reflect.Field instance = aClass.getDeclaredField("instance");
            instance.setAccessible(true);
            instance.set(null, null);
        } catch (Exception e) {
            fail("Could not reset singleton for: " + aClass.getName());
        }
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
        assertTrue(result.getError().contains("does not exist"));
        assertTrue(result.getError().contains("*.txt"));

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