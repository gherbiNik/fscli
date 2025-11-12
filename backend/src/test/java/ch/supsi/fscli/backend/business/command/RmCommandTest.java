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

public class RmCommandTest {
    private RmCommand rmCommand;
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
        rmCommand = new RmCommand(fileSystemService);
    }

    @Test
    void testGetName() {
        assertEquals("rm", rmCommand.getName());
    }

    @Test
    void testGetSynopsis() {
        assertEquals("rm FILE", rmCommand.getSynopsis());
    }

    @Test
    void testGetDescription() {
        assertNotNull(rmCommand.getDescription());
        assertFalse(rmCommand.getDescription().isEmpty());
    }

    @Test
    void testExecute_Success() {
        List<String> arguments = new ArrayList<>();
        arguments.add("testRmFile");
        Map<String, String> options = new HashMap<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();

        fileSystemService.createFile("testRmFile");

        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = rmCommand.execute(context);

        assertTrue(result.isSuccess());
        assertNotNull(result.getOutput());
        assertTrue(result.getOutput().contains("testRmFile"));
        assertTrue(result.getOutput().contains("removed successfully"));

        // Verifica che il file sia stata effettivamente rimosso
        assertNull(currentDir.getChild("testRmFile"));
    }

    @Test
    void testExecute_MissingArguments() {
        List<String> arguments = new ArrayList<>();
        Map<String, String> options = new HashMap<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = rmCommand.execute(context);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
        assertTrue(result.getError().contains("missing"));
    }

    @Test
    void testExecute_NullArguments() {
        Map<String, String> options = new HashMap<>();
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
        Map<String, String> options = new HashMap<>();

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
        Map<String, String> options = new HashMap<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = rmCommand.execute(context);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
    }

    @Test
    void testExecute_FileDoesNotExists() {
        List<String> arguments = new ArrayList<>();
        arguments.add("existingFile");
        Map<String, String> options = new HashMap<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();

        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = rmCommand.execute(context);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
        assertTrue(result.getError().contains("cannot remove") ||
                result.getError().contains("does not exists"));
    }

    @Test
    void testExecute_MultipleFiles() {
        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        Map<String, String> options = new HashMap<>();

        fileSystemService.createFile("f1");
        fileSystemService.createFile("f2");
        fileSystemService.createFile("f3");

        List<String> args1 = new ArrayList<>();
        args1.add("f1");
        CommandContext context1 = new CommandContext(currentDir, args1, options);
        CommandResult result1 = rmCommand.execute(context1);

        List<String> args2 = new ArrayList<>();
        args2.add("f2");
        CommandContext context2 = new CommandContext(currentDir, args2, options);
        CommandResult result2 = rmCommand.execute(context2);

        List<String> args3 = new ArrayList<>();
        args3.add("f3");
        CommandContext context3 = new CommandContext(currentDir, args3, options);
        CommandResult result3 = rmCommand.execute(context3);

        assertTrue(result1.isSuccess());
        assertTrue(result2.isSuccess());
        assertTrue(result3.isSuccess());

        assertNull(currentDir.getChild("f1"));
        assertNull(currentDir.getChild("f2"));
        assertNull(currentDir.getChild("f3"));
    }

    @Test
    void testExecute_FileNameWithSpecialCharacters() {
        // valid chars
        List<String> arguments = new ArrayList<>();
        arguments.add("my-file_1234");
        Map<String, String> options = new HashMap<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        fileSystemService.createFile("my-file_1234");

        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = rmCommand.execute(context);

        assertTrue(result.isSuccess());
        assertNull(currentDir.getChild("my-file_1234"));
    }

    @Test
    void testExecute_NullFileName() {
        // Arrange
        List<String> arguments = new ArrayList<>();
        arguments.add(null);
        Map<String, String> options = new HashMap<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = rmCommand.execute(context);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
    }
}
