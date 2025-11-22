package ch.supsi.fscli.backend.business.command;

import ch.supsi.fscli.backend.business.command.business.CommandDetails;
import ch.supsi.fscli.backend.business.command.business.CommandExecutor;
import ch.supsi.fscli.backend.business.command.business.CommandHelpContainer;
import ch.supsi.fscli.backend.business.command.business.CommandParser;
import ch.supsi.fscli.backend.business.command.commands.CommandContext;
import ch.supsi.fscli.backend.business.command.commands.CommandResult;
import ch.supsi.fscli.backend.business.command.commands.LsCommand;
import ch.supsi.fscli.backend.business.filesystem.FileSystem;
import ch.supsi.fscli.backend.business.service.FileSystemService;
import ch.supsi.fscli.backend.util.BackendTranslator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class LsCommandTest {
    private LsCommand lsCommand;
    private FileSystemService fileSystemService;
    private FileSystem fileSystem;
    private CommandHelpContainer commandHelpContainer;

    @BeforeEach
    void setUp() {
        resetSingleton(CommandExecutor.class);
        resetSingleton(CommandHelpContainer.class);
        resetSingleton(FileSystemService.class);
        resetSingleton(BackendTranslator.class);
        resetSingleton(CommandParser.class);
        resetSingleton(FileSystem.class);

        fileSystem = FileSystem.getInstance();
        fileSystemService = FileSystemService.getInstance(fileSystem);

        BackendTranslator translator = BackendTranslator.getInstance();
        translator.setLocaleDefault(Locale.US);

        commandHelpContainer = CommandHelpContainer.getInstance(translator);

        Map<String, CommandDetails> m = commandHelpContainer.getCommandDetailsMap();
        String synopsis = m.get("ls").synopsis();
        String descr = m.get("ls").description();
        lsCommand = new LsCommand(fileSystemService, "ls", synopsis, descr);
        createSimFS();
    }

    private void createSimFS() {
        fileSystemService.createFile("file1.txt");
        fileSystemService.createDirectory("folder1");
        fileSystemService.createFile("folder1/subfile.txt");
    }

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
    @DisplayName("ls (senza argomenti): deve mostrare il contenuto della root")
    void testLsCurrentDirectory() {
        CommandContext ctx = new CommandContext(fileSystemService.getCurrentDirectory(),Collections.emptyList(), Collections.emptyList() );

        CommandResult result = lsCommand.execute(ctx);

        assertTrue(result.isSuccess());
        String output = result.getOutput();

        // Deve contenere i nomi dei figli diretti della root
        assertTrue(output.contains("file1.txt"));
        assertTrue(output.contains("folder1"));
        // NON deve contenere i file nelle sottocartelle
        assertFalse(output.contains("subfile.txt"));
    }

    @Test
    @DisplayName("ls -i: deve mostrare gli UID")
    void testLsWithInodeOption() {
        CommandContext ctx = new CommandContext(fileSystemService.getCurrentDirectory(), Collections.emptyList(), List.of("-i"));

        CommandResult result = lsCommand.execute(ctx);

        assertTrue(result.isSuccess());
        String output = result.getOutput();

        assertEquals(fileSystemService.getInode("file1.txt").getUid()+" file1.txt "+fileSystemService.getInode("folder1").getUid()+" folder1", output);

    }

    @Test
    @DisplayName("ls [file]: deve stampare solo il nome del file")
    void testLsSingleFile() {
        CommandContext ctx = new CommandContext(fileSystemService.getCurrentDirectory(), List.of("file1.txt"), Collections.emptyList());

        CommandResult result = lsCommand.execute(ctx);

        assertTrue(result.isSuccess());
        // L'output deve essere esattamente il nome del file (più eventuale newline)
        assertEquals("file1.txt", result.getOutput().trim());
    }

    @Test
    @DisplayName("ls [cartella]: deve stampare il contenuto della cartella")
    void testLsSpecificFolder() {
        CommandContext ctx = new CommandContext(fileSystemService.getCurrentDirectory(), List.of("folder1"), Collections.emptyList());

        CommandResult result = lsCommand.execute(ctx);

        assertTrue(result.isSuccess());
        // Deve mostrare il contenuto di folder1
        assertTrue(result.getOutput().contains("subfile.txt"));
        // Non deve mostrare il contenuto della root
        assertFalse(result.getOutput().contains("file1.txt"));
    }

    @Test
    @DisplayName("ls path/annidato/file: deve risolvere il path e stampare il file")
    void testLsNestedPathFile() {
        // Caso richiesto: "ls pippo/pippo.txt" -> qui "folder1/subfile.txt"
        String path = "folder1/subfile.txt";
        CommandContext ctx = new CommandContext(fileSystemService.getCurrentDirectory(),List.of(path), Collections.emptyList());

        CommandResult result = lsCommand.execute(ctx);

        assertTrue(result.isSuccess(), "Il comando dovrebbe riuscire a risolvere path annidati");
        // Quando fai ls su un file specifico con path, stampa il path passato
        assertEquals("folder1/subfile.txt", result.getOutput().trim());
    }

    @Test
    @DisplayName("ls path/annidato/file con -i: deve stampare inode e path")
    void testLsNestedPathFileWithInode() {
        String path = "folder1/subfile.txt";
        CommandContext ctx = new CommandContext(fileSystemService.getCurrentDirectory() ,List.of(path), List.of("-i"));

        CommandResult result = lsCommand.execute(ctx);

        assertTrue(result.isSuccess());
        String output = result.getOutput();

        assertTrue(output.contains("folder1/subfile.txt"));
        //assertTrue(output.contains(String.valueOf(fileInFolder.getUid())));
    }

    @Test
    @DisplayName("ls nonEsistente: deve ritornare errore")
    void testLsNonExistent() {
        CommandContext ctx = new CommandContext(fileSystemService.getCurrentDirectory(), List.of("fantasma.txt"), Collections.emptyList());

        CommandResult result = lsCommand.execute(ctx);

        // Verifica che NON sia successo (o che sia partial error)
        // Adatta in base a come gestisci l'errore puro in CommandResult
        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("No such file or directory"));
    }

    @Test
    @DisplayName("ls multiplo (misto): successo parziale")
    void testLsMixedExistAndNonExist() {
        // ls file1.txt fantasma.txt
        CommandContext ctx = new CommandContext(fileSystemService.getCurrentDirectory(), List.of("file1.txt", "fantasma.txt"), Collections.emptyList());

        CommandResult result = lsCommand.execute(ctx);

        // Dovrebbe essere un successo parziale (dipende dalla tua implementazione di CommandResult)
        // L'output deve contenere il file esistente
        assertTrue(result.getOutput().contains("file1.txt"));
        // L'errore deve contenere il file mancante
        assertTrue(result.getError().contains("fantasma.txt"));
    }

    @Test
    @DisplayName("ls multiplo cartelle: deve stampare header")
    void testLsMultipleFolders() {
        // Creiamo un'altra cartella per testare il multi-folder
        fileSystemService.createDirectory("folder2");

        CommandContext ctx = new CommandContext(fileSystemService.getCurrentDirectory(), List.of("folder1", "folder2"), Collections.emptyList());

        CommandResult result = lsCommand.execute(ctx);

        assertTrue(result.isSuccess());
        String output = result.getOutput();

        // Verifica la presenza degli "header" (i nomi delle cartelle seguiti da due punti)
        assertTrue(output.contains("folder1:"));
        assertTrue(output.contains("folder2:"));
    }


}