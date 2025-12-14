package ch.supsi.fscli.backend.business.command;

import ch.supsi.fscli.backend.business.command.commands.*;
import ch.supsi.fscli.backend.business.command.commands.validators.AbstractValidator;
import ch.supsi.fscli.backend.business.filesystem.FileSystem;
import ch.supsi.fscli.backend.business.filesystem.Inode;
import ch.supsi.fscli.backend.business.service.FileSystemService;
import ch.supsi.fscli.backend.business.service.IFileSystemService;
import ch.supsi.fscli.backend.util.BackendTranslator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class CdCommandTest {
    // 1. Niente più static!
    private FileSystem fileSystem;
    private IFileSystemService fileSystemService;
    private CdCommand cdCommand;
    private LnCommand lnCommand;

    @BeforeEach // 2. Eseguiamo prima di OGNI test
    public void setUp() {
        // 3. Manual Injection per isolare il test
        BackendTranslator backendTranslator = new BackendTranslator();
        backendTranslator.setLocaleDefault(Locale.US);

        // Configurazione legacy per i validatori statici
        AbstractValidatedCommand.setTranslator(backendTranslator);
        AbstractValidator.setTranslator(backendTranslator);

        fileSystem = new FileSystem();
        fileSystem.create();
        fileSystemService = new FileSystemService(fileSystem, backendTranslator);

        // Non serve più il setter del translator sul service se lo passiamo nel costruttore
        // fileSystemService.setTranslator(backendTranslator);

        cdCommand = new CdCommand(
                fileSystemService,
                "cd",
                "cd [DIR]",
                "Change the current directory"
        );

        lnCommand = new LnCommand(fileSystemService, "ln", "ln usage", "desc");

        // 4. Ricostruiamo la struttura pulita per ogni test
        fileSystemService.createDirectory("home");
        fileSystemService.createDirectory("home/user");
        fileSystemService.createDirectory("home/user/documents");
        fileSystemService.createDirectory("tmp");
        fileSystemService.createFile("home/test.txt");

        // Assicuriamoci di partire dalla root
        fileSystem.changeDirectory("/");
    }

    @Test
    public void testExecute_WithNoArguments_ShouldReturnError() {
        CommandContext context = new CommandContext(fileSystem.getCurrentDirectory()
                , Collections.emptyList(), Collections.emptyList());
        CommandResult result = cdCommand.execute(context);
        assertFalse(result.isSuccess());
    }

    @Test
    public void testExecute_WithTooManyArguments_ShouldReturnError() {
        CommandContext context = new CommandContext(fileSystem.getCurrentDirectory(), Arrays.asList("home", "tmp")
                , Collections.emptyList());

        CommandResult result = cdCommand.execute(context);

        assertFalse(result.isSuccess());
    }

    @Test
    public void testExecute_WithValidDirectory() {
        CommandContext context = new CommandContext(
                fileSystemService.getCurrentDirectory(),
                List.of("/home"), Collections.emptyList()
        );
        CommandResult result = cdCommand.execute(context);

        assertTrue(result.isSuccess());
        assertEquals("", result.getOutput());
        assertEquals("/home", fileSystemService.getCurrentDirectoryAbsolutePath());
    }

    @Test
    public void testExecute_WithAbsolutePath_ShouldChangeDirectory() {
        CommandContext context = new CommandContext(
                fileSystemService.getCurrentDirectory(),
                List.of("/home/user"), Collections.emptyList()
        );
        CommandResult result = cdCommand.execute(context);

        assertTrue(result.isSuccess());
        assertEquals("/home/user", fileSystemService.getCurrentDirectoryAbsolutePath());
    }

    @Test
    public void testExecute_WithRelativePath_ShouldChangeDirectory() {
        // Prima vai in home
        fileSystemService.changeDirectory("/home");

        CommandContext context = new CommandContext(
                fileSystemService.getCurrentDirectory(),
                List.of("user/documents"), Collections.emptyList() // path relativo corretto senza slash iniziale
        );

        CommandResult result = cdCommand.execute(context);

        assertTrue(result.isSuccess());
        assertEquals("/home/user/documents", fileSystemService.getCurrentDirectoryAbsolutePath());
    }

    @Test
    public void testExecute_WithParentDirectory_ShouldChangeDirectory() {
        fileSystemService.changeDirectory("/home/user/documents");

        CommandContext context = new CommandContext(
                fileSystemService.getCurrentDirectory(),
                List.of(".."), Collections.emptyList()
        );

        CommandResult result = cdCommand.execute(context);

        assertTrue(result.isSuccess());
        assertEquals("/home/user", fileSystemService.getCurrentDirectoryAbsolutePath());
    }

    @Test
    public void testExecute_WithCurrentDirectory_ShouldStayInSameDirectory() {
        fileSystemService.changeDirectory("/home");

        CommandContext context = new CommandContext(
                fileSystemService.getCurrentDirectory(),
                List.of("."), Collections.emptyList()
        );
        CommandResult result = cdCommand.execute(context);

        assertTrue(result.isSuccess());
        assertEquals("/home", fileSystemService.getCurrentDirectoryAbsolutePath());
    }

    @Test
    public void testExecute_WithNonExistentDirectory_ShouldReturnError() {
        fileSystemService.changeDirectory("/home");

        CommandContext context = new CommandContext(
                fileSystemService.getCurrentDirectory(),
                List.of("aaaaa"), Collections.emptyList()
        );

        CommandResult result = cdCommand.execute(context);

        assertFalse(result.isSuccess());

        context = new CommandContext(
                fileSystemService.getCurrentDirectory(),
                List.of("../bbbb"), Collections.emptyList()
        );
        result = cdCommand.execute(context);

        assertFalse(result.isSuccess());
    }

    @Test
    public void testExecute_WithRootDirectory_ShouldChangeToRoot() {
        fileSystemService.changeDirectory("/home");

        CommandContext context = new CommandContext(
                fileSystemService.getCurrentDirectory(),
                List.of("/"), Collections.emptyList()
        );

        CommandResult result = cdCommand.execute(context);

        assertTrue(result.isSuccess());
        assertEquals("/", fileSystemService.getCurrentDirectoryAbsolutePath());
    }

    @Test
    public void testExecute_WithFilePath_ShouldReturnError() {
        CommandContext context = new CommandContext(
                fileSystemService.getCurrentDirectory(),
                List.of("/home/test.txt"), Collections.emptyList()
        );

        CommandResult result = cdCommand.execute(context);

        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("cd linkToDir: Deve entrare nella directory reale")
    void testCdThroughSoftLink() {
        fileSystemService.changeDirectory("/");

        // 1. Crea link
        createSoftLink("home", "linkTohome");

        // 2. Esegui: cd linkTohome
        List<String> args = List.of("linkTohome");
        CommandContext ctx = new CommandContext(fileSystemService.getCurrentDirectory(), args, Collections.emptyList());

        CommandResult result = cdCommand.execute(ctx);

        assertTrue(result.isSuccess(), "Il comando cd su un soft link deve funzionare");

        // 3. Verifica: Siamo finiti in /home?
        assertEquals("/home", fileSystemService.getCurrentDirectoryAbsolutePath());

        // Verifica Inode
        Inode realDocs = fileSystemService.getInode("/home");
        assertEquals(realDocs, fileSystemService.getCurrentDirectory());
    }

    @Test
    @DisplayName("cd linkToDocs/subdir: Navigazione composta tramite link")
    void testCdThroughSoftLinkNested() {
        fileSystemService.changeDirectory("/");
        // Crea link: myDocs -> home
        createSoftLink("home", "myDocs");

        // Esegui: cd myDocs/user
        List<String> args = List.of("myDocs/user");
        CommandContext ctx = new CommandContext(fileSystemService.getCurrentDirectory(), args, Collections.emptyList());

        CommandResult result = cdCommand.execute(ctx);

        assertTrue(result.isSuccess());
        assertEquals("/home/user", fileSystemService.getCurrentDirectoryAbsolutePath());
    }

    @Test
    @DisplayName("cd brokenLink: Deve fallire")
    void testCdBrokenLink() {
        // Crea link a file inesistente
        createSoftLink("nowhere", "brokenLink");

        // Esegui: cd brokenLink
        List<String> args = List.of("brokenLink");
        CommandContext ctx = new CommandContext(fileSystemService.getCurrentDirectory(), args, Collections.emptyList());

        CommandResult result = cdCommand.execute(ctx);

        assertFalse(result.isSuccess());
    }

    // --- Helper per creare link rapidamente nei test ---
    private void createSoftLink(String source, String dest) {
        List<String> args = List.of(source, dest);
        List<String> opts = List.of("-s");
        CommandContext ctx = new CommandContext(fileSystemService.getCurrentDirectory(), args, opts);
        lnCommand.execute(ctx);
    }
}