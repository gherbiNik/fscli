package ch.supsi.fscli.backend.business.command;

import ch.supsi.fscli.backend.business.command.commands.RmCommand;
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

class RmCommandTest {

    private RmCommand rmCommand;
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

        String synopsis = m.get("rm").synopsis();
        String descr = m.get("rm").description();
        rmCommand = new RmCommand(fileSystemService, "rm", synopsis, descr);
    }



    @Test
    void testExecute_Success() {
        List<String> arguments = new ArrayList<>();
        arguments.add("fileToDelete.txt");
        List<String> options = new ArrayList<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        fileSystemService.createFile("fileToDelete.txt");

        assertNotNull(currentDir.getChild("fileToDelete.txt"));

        CommandContext context = new CommandContext(currentDir, arguments, options);
        CommandResult result = rmCommand.execute(context);

        assertTrue(result.isSuccess());
        assertNotNull(result.getOutput());
        assertTrue(result.getOutput().contains("fileToDelete.txt"));
        assertTrue(result.getOutput().contains("deleted successfully"));

        assertNull(currentDir.getChild("fileToDelete.txt"));
    }

    @Test
    void testExecute_MissingArguments() {
        List<String> arguments = new ArrayList<>();
        List<String> options = new ArrayList<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = rmCommand.execute(context);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
        assertTrue(result.getError().contains("missing"));
    }

    @Test
    void testExecute_NullArguments() {
        List<String> options = new ArrayList<>();
        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, null, options);

        CommandResult result = rmCommand.execute(context);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
    }

    @Test
    void testExecute_EmptyFileName() {
        List<String> arguments = new ArrayList<>();
        arguments.add("");
        List<String> options = new ArrayList<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = rmCommand.execute(context);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
        assertTrue(result.getError().contains("invalid") || result.getError().contains("empty"));
    }

    @Test
    void testExecute_WhitespaceFileName() {
        List<String> arguments = new ArrayList<>();
        arguments.add("   ");
        List<String> options = new ArrayList<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = rmCommand.execute(context);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
    }

    @Test
    void testExecute_FileDoesNotExist() {
        List<String> arguments = new ArrayList<>();
        arguments.add("nonExistentFile.txt");
        List<String> options = new ArrayList<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = rmCommand.execute(context);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
        assertTrue(result.getError().contains("cannot remove") ||
                result.getError().contains("does not exist"));
    }

    @Test
    void testExecute_AttemptToDeleteDirectory() {
        List<String> arguments = new ArrayList<>();
        arguments.add("aDirectory");
        List<String> options = new ArrayList<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        fileSystemService.createDirectory("aDirectory");

        assertNotNull(currentDir.getChild("aDirectory"));

        CommandContext context = new CommandContext(currentDir, arguments, options);
        CommandResult result = rmCommand.execute(context);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
        assertTrue(result.getError().contains("is a directory"));

        assertNotNull(currentDir.getChild("aDirectory"));
    }

    @Test
    void testExecute_MultipleFiles() {
        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        List<String> options = new ArrayList<>();

        fileSystemService.createFile("file1.txt");
        fileSystemService.createFile("file2.txt");
        fileSystemService.createFile("file3.txt");

        List<String> args1 = new ArrayList<>();
        args1.add("file1.txt");
        CommandContext context1 = new CommandContext(currentDir, args1, options);
        CommandResult result1 = rmCommand.execute(context1);

        List<String> args2 = new ArrayList<>();
        args2.add("file2.txt");
        CommandContext context2 = new CommandContext(currentDir, args2, options);
        CommandResult result2 = rmCommand.execute(context2);

        List<String> args3 = new ArrayList<>();
        args3.add("file3.txt");
        CommandContext context3 = new CommandContext(currentDir, args3, options);
        CommandResult result3 = rmCommand.execute(context3);

        assertTrue(result1.isSuccess());
        assertTrue(result2.isSuccess());
        assertTrue(result3.isSuccess());

        assertNull(currentDir.getChild("file1.txt"));
        assertNull(currentDir.getChild("file2.txt"));
        assertNull(currentDir.getChild("file3.txt"));
    }

    @Test
    void testExecute_FileNameWithSpecialCharacters() {
        List<String> arguments = new ArrayList<>();
        arguments.add("my-file_123.txt");
        List<String> options = new ArrayList<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        fileSystemService.createFile("my-file_123.txt");

        CommandContext context = new CommandContext(currentDir, arguments, options);
        CommandResult result = rmCommand.execute(context);

        assertTrue(result.isSuccess());
        assertNull(currentDir.getChild("my-file_123.txt"));
    }

    @Test
    void testExecute_NullFileName() {
        List<String> arguments = new ArrayList<>();
        arguments.add(null);
        List<String> options = new ArrayList<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = rmCommand.execute(context);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
    }
}