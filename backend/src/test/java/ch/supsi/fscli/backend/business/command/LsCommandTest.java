package ch.supsi.fscli.backend.business.command;

import ch.supsi.fscli.backend.business.command.commands.AbstractValidatedCommand;
import ch.supsi.fscli.backend.business.command.commands.CommandContext;
import ch.supsi.fscli.backend.business.command.commands.CommandResult;
import ch.supsi.fscli.backend.business.command.commands.LsCommand;
import ch.supsi.fscli.backend.business.command.commands.validators.AbstractValidator;
import ch.supsi.fscli.backend.business.filesystem.FileSystem;
import ch.supsi.fscli.backend.business.service.FileSystemService;
import ch.supsi.fscli.backend.business.service.IFileSystemService;
import ch.supsi.fscli.backend.util.BackendTranslator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class LsCommandTest {
    // 1. Niente più static
    private LsCommand lsCommand;
    private IFileSystemService fileSystemService;
    private FileSystem fileSystem;

    @BeforeEach // 2. Eseguiamo prima di OGNI test
    void setUp() {
        // 3. Manual Injection e Setup Ambiente
        BackendTranslator translator = new BackendTranslator();
        translator.setLocaleDefault(Locale.US);

        AbstractValidatedCommand.setTranslator(translator);
        AbstractValidator.setTranslator(translator);

        fileSystem = new FileSystem();
        fileSystemService = new FileSystemService(fileSystem, translator);

        // Passiamo direttamente le stringhe (o chiavi) necessarie
        lsCommand = new LsCommand(
                fileSystemService,
                "ls",
                "ls synopsis",
                "ls description"
        );

        createSimFS();
    }

    private void createSimFS() {
        fileSystemService.createFile("file1.txt");
        fileSystemService.createDirectory("folder1");
        fileSystemService.createFile("folder1/subfile.txt");
    }

    @Test
    @DisplayName("ls (senza argomenti): deve mostrare il contenuto della root")
    void testLsCurrentDirectory() {
        CommandContext ctx = new CommandContext(fileSystemService.getCurrentDirectory(), Collections.emptyList(), Collections.emptyList());

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

        // Nota: Costruiamo la stringa attesa dinamicamente per evitare problemi se gli ID cambiano
        String expectedPart1 = fileSystemService.getInode("file1.txt").getUid() + " file1.txt";
        String expectedPart2 = fileSystemService.getInode("folder1").getUid() + " folder1";

        assertTrue(output.contains(expectedPart1));
        assertTrue(output.contains(expectedPart2));
    }

    @Test
    @DisplayName("ls -z (opzione non valida): deve ritornare errore 'illegal option'")
    void testLsIllegalOption() {
        // Setup: passiamo un'opzione non supportata "-z"
        CommandContext ctx = new CommandContext(fileSystemService.getCurrentDirectory(), Collections.emptyList(), List.of("-i","-z"));

        CommandResult result = lsCommand.execute(ctx);

        // Verifica
        assertFalse(result.isSuccess());
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
    }

    @Test
    @DisplayName("ls nonEsistente: deve ritornare errore")
    void testLsNonExistent() {
        CommandContext ctx = new CommandContext(fileSystemService.getCurrentDirectory(), List.of("fantasma.txt"), Collections.emptyList());

        CommandResult result = lsCommand.execute(ctx);

        // Verifica che NON sia successo
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("ls multiplo (misto): successo parziale")
    void testLsMixedExistAndNonExist() {
        // ls file1.txt fantasma.txt
        CommandContext ctx = new CommandContext(fileSystemService.getCurrentDirectory(), List.of("file1.txt", "fantasma.txt"), Collections.emptyList());

        CommandResult result = lsCommand.execute(ctx);

        // Dovrebbe essere un successo parziale
        // L'output deve contenere il file esistente
        assertTrue(result.getOutput().contains("file1.txt"));
        // L'errore deve contenere il file mancante (o il path)
        // Nota: usiamo contains perché il messaggio d'errore potrebbe variare leggermente
        assertTrue(result.getError().contains("fantasma.txt") || result.getError().contains("cannot access"));
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