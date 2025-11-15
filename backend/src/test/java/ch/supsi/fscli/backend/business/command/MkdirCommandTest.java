package ch.supsi.fscli.backend.business.command;


import ch.supsi.fscli.backend.business.command.business.CommandDetails;
import ch.supsi.fscli.backend.business.command.business.CommandHelpContainer;
import ch.supsi.fscli.backend.business.command.commands.CommandContext;
import ch.supsi.fscli.backend.business.command.commands.CommandResult;
import ch.supsi.fscli.backend.business.command.commands.MkdirCommand;
import ch.supsi.fscli.backend.business.filesystem.DirectoryNode;
import ch.supsi.fscli.backend.business.filesystem.FileSystem;
import ch.supsi.fscli.backend.business.service.FileSystemService;
import ch.supsi.fscli.backend.util.BackendTranslator;
import org.junit.jupiter.api.BeforeEach;
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
        try {
            java.lang.reflect.Field instance = FileSystem.class.getDeclaredField("instance");
            instance.setAccessible(true);
            instance.set(null, null);
        } catch (Exception e) {
            fail("Could not reset singleton");
        }

        BackendTranslator translator = BackendTranslator.getInstance();
        translator.setLocaleDefault(Locale.US);
        commandHelpContainer = CommandHelpContainer.getInstance(translator);
        fileSystem = FileSystem.getInstance();
        fileSystemService = FileSystemService.getInstance(fileSystem);

        Map<String, CommandDetails> m = commandHelpContainer.getCommandDetailsMap();
        String synopsis = m.get("mkdir").synopsis();
        String descr = m.get("mkdir").description();
        mkdirCommand = new MkdirCommand(fileSystemService, "mkdir", synopsis, descr);
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
        assertNotNull(result.getOutput());
        assertTrue(result.getOutput().contains("testDir"));
        assertTrue(result.getOutput().contains("created successfully"));

        // Verifica che la directory sia stata effettivamente creata
        assertNotNull(currentDir.getChild("testDir"));
    }

    @Test
    void testExecute_MissingArguments() {
        List<String> arguments = new ArrayList<>();
        List<String> options = new ArrayList<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = mkdirCommand.execute(context);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
        assertTrue(result.getError().contains("missing"));
    }

    @Test
    void testExecute_NullArguments() {
        List<String> options = new ArrayList<>();
        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, null, options);

        CommandResult result = mkdirCommand.execute(context);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
    }

    @Test
    void testExecute_EmptyDirectoryName() {
        List<String> arguments = new ArrayList<>();
        arguments.add("");
        List<String> options = new ArrayList<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = mkdirCommand.execute(context);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
        assertTrue(result.getError().contains("invalid") || result.getError().contains("empty"));
    }

    @Test
    void testExecute_WhitespaceDirectoryName() {
        List<String> arguments = new ArrayList<>();
        arguments.add("   ");
        List<String> options = new ArrayList<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = mkdirCommand.execute(context);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
    }

    @Test
    void testExecute_DirectoryAlreadyExists() {
        List<String> arguments = new ArrayList<>();
        arguments.add("existingDir");
        List<String> options = new ArrayList<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();

        fileSystemService.createDirectory("existingDir");

        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = mkdirCommand.execute(context);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
        assertTrue(result.getError().contains("cannot create") ||
                result.getError().contains("already exists"));
    }

    @Test
    void testExecute_MultipleDirectories() {
        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        List<String> options = new ArrayList<>();

        List<String> args1 = new ArrayList<>();
        args1.add("dir1");
        CommandContext context1 = new CommandContext(currentDir, args1, options);
        CommandResult result1 = mkdirCommand.execute(context1);

        List<String> args2 = new ArrayList<>();
        args2.add("dir2");
        CommandContext context2 = new CommandContext(currentDir, args2, options);
        CommandResult result2 = mkdirCommand.execute(context2);

        List<String> args3 = new ArrayList<>();
        args3.add("dir3");
        CommandContext context3 = new CommandContext(currentDir, args3, options);
        CommandResult result3 = mkdirCommand.execute(context3);

        assertTrue(result1.isSuccess());
        assertTrue(result2.isSuccess());
        assertTrue(result3.isSuccess());

        assertNotNull(currentDir.getChild("dir1"));
        assertNotNull(currentDir.getChild("dir2"));
        assertNotNull(currentDir.getChild("dir3"));
    }

    @Test
    void testExecute_DirectoryNameWithSpecialCharacters() {
        // valid chars
        List<String> arguments = new ArrayList<>();
        arguments.add("my-directory_123");
        List<String> options = new ArrayList<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = mkdirCommand.execute(context);

        assertTrue(result.isSuccess());
        assertNotNull(currentDir.getChild("my-directory_123"));
    }

    @Test
    void testExecute_NullDirectoryName() {
        // Arrange
        List<String> arguments = new ArrayList<>();
        arguments.add(null);
        List<String> options = new ArrayList<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = mkdirCommand.execute(context);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
    }
}
