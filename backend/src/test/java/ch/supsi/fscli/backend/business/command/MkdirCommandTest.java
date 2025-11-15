package ch.supsi.fscli.backend.business.command;

import ch.supsi.fscli.backend.business.command.business.CommandDetails;
import ch.supsi.fscli.backend.business.command.business.CommandHelpContainer;
import ch.supsi.fscli.backend.business.command.commands.CommandContext;
import ch.supsi.fscli.backend.business.command.commands.CommandResult;
import ch.supsi.fscli.backend.business.command.commands.MkdirCommand;
import ch.supsi.fscli.backend.business.filesystem.DirectoryNode;
import ch.supsi.fscli.backend.business.filesystem.FileSystem;
import ch.supsi.fscli.backend.business.filesystem.Inode;
import ch.supsi.fscli.backend.business.service.FileSystemService;
import ch.supsi.fscli.backend.util.BackendTranslator;
import ch.supsi.fscli.backend.business.command.business.CommandExecutor;
import ch.supsi.fscli.backend.business.command.business.CommandParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MkdirCommandTest {

    private MkdirCommand mkdirCommand;
    private FileSystemService fileSystemService;
    private FileSystem fileSystem;
    private CommandHelpContainer commandHelpContainer;

    @BeforeEach
    void setUp() {
        // Reset di tutti i singleton
        resetSingleton(CommandExecutor.class);
        resetSingleton(CommandHelpContainer.class);
        resetSingleton(FileSystemService.class);
        resetSingleton(BackendTranslator.class);
        resetSingleton(CommandParser.class);
        resetSingleton(FileSystem.class);

        // Inizializza dipendenze
        fileSystem = FileSystem.getInstance();
        fileSystemService = FileSystemService.getInstance(fileSystem);

        BackendTranslator translator = BackendTranslator.getInstance();
        translator.setLocaleDefault(Locale.US);

        commandHelpContainer = CommandHelpContainer.getInstance(translator);

        // Inizializza il comando specifico
        Map<String, CommandDetails> m = commandHelpContainer.getCommandDetailsMap();
        String synopsis = m.get("mkdir").synopsis();
        String descr = m.get("mkdir").description();
        mkdirCommand = new MkdirCommand(fileSystemService, "mkdir", synopsis, descr);
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
    void testExecute_Success() {
        List<String> arguments = new ArrayList<>();
        arguments.add("testDir");
        List<String> options = new ArrayList<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = mkdirCommand.execute(context);

        assertTrue(result.isSuccess());
        assertNotNull(currentDir.getChild("testDir"));
    }

    @Test
    void testExecute_MissingArguments() {
        List<String> arguments = new ArrayList<>();
        List<String> options = new ArrayList<>();
        CommandContext context = new CommandContext(fileSystemService.getCurrentDirectory(), arguments, options);
        CommandResult result = mkdirCommand.execute(context);
        assertFalse(result.isSuccess());
    }

    @Test
    void testExecute_NullArguments() {
        List<String> options = new ArrayList<>();
        CommandContext context = new CommandContext(fileSystemService.getCurrentDirectory(), null, options);
        CommandResult result = mkdirCommand.execute(context);
        assertFalse(result.isSuccess());
    }

    @Test
    void testExecute_EmptyDirectoryName() {
        List<String> arguments = new ArrayList<>();
        arguments.add("");
        List<String> options = new ArrayList<>();
        CommandContext context = new CommandContext(fileSystemService.getCurrentDirectory(), arguments, options);
        CommandResult result = mkdirCommand.execute(context);
        assertFalse(result.isSuccess());
    }

    @Test
    void testExecute_DirectoryAlreadyExists() {
        List<String> arguments = new ArrayList<>();
        arguments.add("existingDir");
        List<String> options = new ArrayList<>();
        fileSystemService.createDirectory("existingDir");

        CommandContext context = new CommandContext(fileSystemService.getCurrentDirectory(), arguments, options);
        CommandResult result = mkdirCommand.execute(context);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("Testa creazione con path assoluto (/)")
    void testExecute_PathResolution_Absolute() {
        fileSystemService.createDirectory("docs");

        List<String> arguments = new ArrayList<>();
        arguments.add("/docs/newDir");
        List<String> options = new ArrayList<>();

        CommandContext context = new CommandContext(fileSystemService.getCurrentDirectory(), arguments, options);
        CommandResult result = mkdirCommand.execute(context);

        assertTrue(result.isSuccess());
        Inode docsNode = fileSystem.resolveNode("/docs");
        assertTrue(docsNode instanceof DirectoryNode);
        assertNotNull(((DirectoryNode) docsNode).getChild("newDir"));
    }

    @Test
    @DisplayName("Testa creazione con path relativo '..'")
    void testExecute_PathResolution_DotDot() {
        fileSystemService.createDirectory("docs");
        fileSystem.changeDirectory("/docs");

        List<String> arguments = new ArrayList<>();
        arguments.add("../otherDir");
        List<String> options = new ArrayList<>();

        CommandContext context = new CommandContext(fileSystemService.getCurrentDirectory(), arguments, options);
        CommandResult result = mkdirCommand.execute(context);

        assertTrue(result.isSuccess());
        assertNotNull(fileSystem.resolveNode("/otherDir"));
    }
}