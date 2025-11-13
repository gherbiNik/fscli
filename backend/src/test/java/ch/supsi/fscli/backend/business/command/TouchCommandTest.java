package ch.supsi.fscli.backend.business.command;

import ch.supsi.fscli.backend.business.filesystem.DirectoryNode;
import ch.supsi.fscli.backend.business.filesystem.FileSystem;
import ch.supsi.fscli.backend.business.service.FileSystemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
/*
public class TouchCommandTest {
    private TouchCommand touchCommand;
    private FileSystemService fileSystemService;
    private FileSystem fileSystem;

    @BeforeEach
    void setUp() {
        try {
            java.lang.reflect.Field instance = FileSystem.class.getDeclaredField("instance");
            instance.setAccessible(true);
            instance.set(null, null);
        } catch (Exception e) {
            fail("Could not reset singleton");
        }
        fileSystem = FileSystem.getInstance();
        fileSystemService = FileSystemService.getInstance(fileSystem);
        touchCommand = new TouchCommand(fileSystemService);
    }

    @Test
    void testGetName() {
        assertEquals("touch", touchCommand.getName());
    }

    @Test
    void testGetSynopsis() {
        assertEquals("touch FILE", touchCommand.getSynopsis());
    }

    @Test
    void testGetDescription() {
        assertNotNull(touchCommand.getDescription());
        assertFalse(touchCommand.getDescription().isEmpty());
    }

    @Test
    void testExecute_Success() {
        List<String> arguments = new ArrayList<>();
        arguments.add("testFile");
        Map<String, String> options = new HashMap<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = touchCommand.execute(context);

        assertTrue(result.isSuccess());
        assertNotNull(result.getOutput());
        assertTrue(result.getOutput().contains("testFile"));
        assertTrue(result.getOutput().contains("created successfully"));

        // Verifica che il file sia stato effettivamente creato
        assertNotNull(currentDir.getChild("testFile"));
    }

    @Test
    void testExecute_MissingArguments() {
        List<String> arguments = new ArrayList<>();
        Map<String, String> options = new HashMap<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = touchCommand.execute(context);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
        assertTrue(result.getError().contains("missing"));
    }

    @Test
    void testExecute_NullArguments() {
        Map<String, String> options = new HashMap<>();
        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, null, options);

        CommandResult result = touchCommand.execute(context);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
    }

    @Test
    void testExecute_EmptyFileName() {
        List<String> arguments = new ArrayList<>();
        arguments.add("");
        Map<String, String> options = new HashMap<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = touchCommand.execute(context);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
        assertTrue(result.getError().contains("invalid") || result.getError().contains("empty"));
    }

    @Test
    void testExecute_WhitespaceFileName() {
        List<String> arguments = new ArrayList<>();
        arguments.add("   ");
        Map<String, String> options = new HashMap<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = touchCommand.execute(context);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
    }

    @Test
    void testExecute_FileAlreadyExists() {
        List<String> arguments = new ArrayList<>();
        arguments.add("existingFile");
        Map<String, String> options = new HashMap<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();

        fileSystemService.createDirectory("existingFile");

        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = touchCommand.execute(context);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
        assertTrue(result.getError().contains("cannot create") ||
                result.getError().contains("already exists"));
    }

    @Test
    void testExecute_MultipleFiles() {
        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        Map<String, String> options = new HashMap<>();

        List<String> args1 = new ArrayList<>();
        args1.add("file1");
        CommandContext context1 = new CommandContext(currentDir, args1, options);
        CommandResult result1 = touchCommand.execute(context1);

        List<String> args2 = new ArrayList<>();
        args2.add("file2");
        CommandContext context2 = new CommandContext(currentDir, args2, options);
        CommandResult result2 = touchCommand.execute(context2);

        List<String> args3 = new ArrayList<>();
        args3.add("file3");
        CommandContext context3 = new CommandContext(currentDir, args3, options);
        CommandResult result3 = touchCommand.execute(context3);

        assertTrue(result1.isSuccess());
        assertTrue(result2.isSuccess());
        assertTrue(result3.isSuccess());

        assertNotNull(currentDir.getChild("file1"));
        assertNotNull(currentDir.getChild("file2"));
        assertNotNull(currentDir.getChild("file3"));
    }

    @Test
    void testExecute_FileNameWithSpecialCharacters() {
        // valid chars
        List<String> arguments = new ArrayList<>();
        arguments.add("my-file_123");
        Map<String, String> options = new HashMap<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = touchCommand.execute(context);

        assertTrue(result.isSuccess());
        assertNotNull(currentDir.getChild("my-file_123"));
    }

    @Test
    void testExecute_NullFileName() {
        // Arrange
        List<String> arguments = new ArrayList<>();
        arguments.add(null);
        Map<String, String> options = new HashMap<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = touchCommand.execute(context);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
    }
}

 */
