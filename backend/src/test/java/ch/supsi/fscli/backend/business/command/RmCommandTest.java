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
        arguments.add("testFile");
        Map<String, String> options = new HashMap<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();

        fileSystemService.createFile("testFile");

        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = rmCommand.execute(context);

        assertTrue(result.isSuccess());
        assertNotNull(result.getOutput());
        assertTrue(result.getOutput().contains("testFile"));
        assertTrue(result.getOutput().contains("removed successfully"));

        // Verifica che il file sia stata effettivamente rimosso
        assertNull(currentDir.getChild("testFile"));
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

        fileSystemService.createFile("file1");
        fileSystemService.createFile("file2");
        fileSystemService.createFile("file3");

        List<String> args1 = new ArrayList<>();
        args1.add("file1");
        CommandContext context1 = new CommandContext(currentDir, args1, options);
        CommandResult result1 = rmCommand.execute(context1);

        List<String> args2 = new ArrayList<>();
        args2.add("file2");
        CommandContext context2 = new CommandContext(currentDir, args2, options);
        CommandResult result2 = rmCommand.execute(context2);

        List<String> args3 = new ArrayList<>();
        args3.add("file3");
        CommandContext context3 = new CommandContext(currentDir, args3, options);
        CommandResult result3 = rmCommand.execute(context3);

        assertTrue(result1.isSuccess());
        assertTrue(result2.isSuccess());
        assertTrue(result3.isSuccess());

        assertNull(currentDir.getChild("file1"));
        assertNull(currentDir.getChild("file2"));
        assertNull(currentDir.getChild("file3"));
    }

    @Test
    void testExecute_FileNameWithSpecialCharacters() {
        // valid chars
        List<String> arguments = new ArrayList<>();
        arguments.add("my-file_123");
        Map<String, String> options = new HashMap<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        fileSystemService.createFile("my-file_123");

        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = rmCommand.execute(context);

        assertTrue(result.isSuccess());
        assertNull(currentDir.getChild("my-file_123"));
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
