package ch.supsi.fscli.backend.business.command;

import ch.supsi.fscli.backend.business.command.commands.TouchCommand;

import ch.supsi.fscli.backend.business.command.business.CommandDetails;
import ch.supsi.fscli.backend.business.command.business.CommandHelpContainer;
import ch.supsi.fscli.backend.business.command.commands.CommandContext;
import ch.supsi.fscli.backend.business.command.commands.CommandResult;
import ch.supsi.fscli.backend.business.filesystem.DirectoryNode;
import ch.supsi.fscli.backend.business.filesystem.FileSystem;
import ch.supsi.fscli.backend.business.service.FileSystemService;
import ch.supsi.fscli.backend.util.BackendTranslator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TouchCommandTest {

    private TouchCommand touchCommand;
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

        String synopsis = m.get("touch").synopsis();
        String descr = m.get("touch").description();
        touchCommand = new TouchCommand(fileSystemService, "touch", synopsis, descr);
    }



    @Test
    void testExecute_Success() {
        List<String> arguments = new ArrayList<>();
        arguments.add("testFile.txt");
        List<String> options = new ArrayList<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = touchCommand.execute(context);

        assertTrue(result.isSuccess());
        assertNotNull(result.getOutput());
        assertTrue(result.getOutput().contains("testFile.txt"));
        assertTrue(result.getOutput().contains("created successfully"));

        assertNotNull(currentDir.getChild("testFile.txt"));
    }

    @Test
    void testExecute_MissingArguments() {
        List<String> arguments = new ArrayList<>();
        List<String> options = new ArrayList<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = touchCommand.execute(context);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
        assertTrue(result.getError().contains("missing"));
    }

    @Test
    void testExecute_NullArguments() {
        List<String> options = new ArrayList<>();
        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, null, options);

        CommandResult result = touchCommand.execute(context);

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

        CommandResult result = touchCommand.execute(context);

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

        CommandResult result = touchCommand.execute(context);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
    }

    @Test
    void testExecute_DirectoryAlreadyExists() {
        List<String> arguments = new ArrayList<>();
        arguments.add("existingDir"); // Nome che andrà in conflitto
        List<String> options = new ArrayList<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();

        fileSystemService.createDirectory("existingDir");

        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = touchCommand.execute(context);

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
        args1.add("file1.txt");
        CommandContext context1 = new CommandContext(currentDir, args1, options);
        CommandResult result1 = touchCommand.execute(context1);

        List<String> args2 = new ArrayList<>();
        args2.add("file2.txt");
        CommandContext context2 = new CommandContext(currentDir, args2, options);
        CommandResult result2 = touchCommand.execute(context2);

        List<String> args3 = new ArrayList<>();
        args3.add("file3.txt");
        CommandContext context3 = new CommandContext(currentDir, args3, options);
        CommandResult result3 = touchCommand.execute(context3);

        assertTrue(result1.isSuccess());
        assertTrue(result2.isSuccess());
        assertTrue(result3.isSuccess());

        assertNotNull(currentDir.getChild("file1.txt"));
        assertNotNull(currentDir.getChild("file2.txt"));
        assertNotNull(currentDir.getChild("file3.txt"));
    }

    @Test
    void testExecute_DirectoryNameWithSpecialCharacters() {
        List<String> arguments = new ArrayList<>();
        arguments.add("my-file_123.txt");
        List<String> options = new ArrayList<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = touchCommand.execute(context);

        assertTrue(result.isSuccess());
        assertNotNull(currentDir.getChild("my-file_123.txt"));
    }

    @Test
    void testExecute_NullDirectoryName() {
        List<String> arguments = new ArrayList<>();
        arguments.add(null);
        List<String> options = new ArrayList<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = touchCommand.execute(context);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
    }
}