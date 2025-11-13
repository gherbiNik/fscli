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
/*
class RmdirCommandTest {

    private RmdirCommand rmdirCommand;
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
        rmdirCommand = new RmdirCommand(fileSystemService);
    }

    @Test
    void testGetName() {
        assertEquals("rmdir", rmdirCommand.getName());
    }

    @Test
    void testGetSynopsis() {
        assertEquals("rmdir DIRECTORY", rmdirCommand.getSynopsis());
    }



    @Test
    void testExecute_Success() {
        fileSystemService.createDirectory("testDir");

        List<String> arguments = new ArrayList<>();
        arguments.add("testDir");
        Map<String, String> options = new HashMap<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = rmdirCommand.execute(context);

        assertTrue(result.isSuccess());
        assertNotNull(result.getOutput());
        assertTrue(result.getOutput().contains("testDir"));
        assertTrue(result.getOutput().contains("deleted successfully"));

        // Verifica che la directory sia stata effettivamente rimossa
        assertNull(currentDir.getChild("testDir"));
    }

    @Test
    void testExecute_MissingArguments() {
        List<String> arguments = new ArrayList<>();
        Map<String, String> options = new HashMap<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = rmdirCommand.execute(context);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
        assertTrue(result.getError().contains("missing"));
    }

    @Test
    void testExecute_NullArguments() {
        Map<String, String> options = new HashMap<>();
        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, null, options);

        CommandResult result = rmdirCommand.execute(context);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
    }

    @Test
    void testExecute_EmptyDirectoryName() {
        List<String> arguments = new ArrayList<>();
        arguments.add("");
        Map<String, String> options = new HashMap<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = rmdirCommand.execute(context);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
        assertTrue(result.getError().contains("invalid"));
    }

    @Test
    void testExecute_WhitespaceDirectoryName() {
        List<String> arguments = new ArrayList<>();
        arguments.add("   ");
        Map<String, String> options = new HashMap<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = rmdirCommand.execute(context);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
    }

    @Test
    void testExecute_DirectoryDoesNotExist() {
        List<String> arguments = new ArrayList<>();
        arguments.add("nonExistentDir");
        Map<String, String> options = new HashMap<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = rmdirCommand.execute(context);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
        assertTrue(result.getError().contains("cannot remove") ||
                result.getError().contains("does not exist"));
    }

    @Test
    void testExecute_DirectoryNotEmpty() {
        fileSystemService.createDirectory("parentDir");

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        DirectoryNode parentDir = (DirectoryNode) currentDir.getChild("parentDir");
        DirectoryNode childDir = new DirectoryNode(parentDir);
        parentDir.addChild("childDir", childDir);

        List<String> arguments = new ArrayList<>();
        arguments.add("parentDir");
        Map<String, String> options = new HashMap<>();

        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = rmdirCommand.execute(context);

        assertTrue(result.isSuccess());
        assertNotNull(result.getOutput());
        assertTrue(result.getOutput().contains("cannot be deleted") ||
                result.getOutput().contains("not empty"));

        assertNotNull(currentDir.getChild("parentDir"));
    }

    @Test
    void testExecute_MultipleDirectories() {
        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        Map<String, String> options = new HashMap<>();

        fileSystemService.createDirectory("dir1");
        fileSystemService.createDirectory("dir2");
        fileSystemService.createDirectory("dir3");

        List<String> args1 = new ArrayList<>();
        args1.add("dir1");
        CommandContext context1 = new CommandContext(currentDir, args1, options);
        CommandResult result1 = rmdirCommand.execute(context1);

        List<String> args2 = new ArrayList<>();
        args2.add("dir2");
        CommandContext context2 = new CommandContext(currentDir, args2, options);
        CommandResult result2 = rmdirCommand.execute(context2);

        List<String> args3 = new ArrayList<>();
        args3.add("dir3");
        CommandContext context3 = new CommandContext(currentDir, args3, options);
        CommandResult result3 = rmdirCommand.execute(context3);

        assertTrue(result1.isSuccess());
        assertTrue(result2.isSuccess());
        assertTrue(result3.isSuccess());

        assertNull(currentDir.getChild("dir1"));
        assertNull(currentDir.getChild("dir2"));
        assertNull(currentDir.getChild("dir3"));
    }

    @Test
    void testExecute_DirectoryNameWithSpecialCharacters() {
        fileSystemService.createDirectory("my-directory_123");

        List<String> arguments = new ArrayList<>();
        arguments.add("my-directory_123");
        Map<String, String> options = new HashMap<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = rmdirCommand.execute(context);

        assertTrue(result.isSuccess());
        assertNull(currentDir.getChild("my-directory_123"));
    }

    @Test
    void testExecute_NullDirectoryName() {
        List<String> arguments = new ArrayList<>();
        arguments.add(null);
        Map<String, String> options = new HashMap<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = rmdirCommand.execute(context);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
    }
    @Test
    void testExecute_MultipleArgumentsAtOnce() {
        // Crea 3 directory
        fileSystemService.createDirectory("dir1");
        fileSystemService.createDirectory("dir2");
        fileSystemService.createDirectory("dir3");

        List<String> arguments = new ArrayList<>();
        arguments.add("dir1");
        arguments.add("dir2");
        arguments.add("dir3");
        Map<String, String> options = new HashMap<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = rmdirCommand.execute(context);

        assertTrue(result.isSuccess());
        assertNotNull(result.getOutput());
        assertTrue(result.getOutput().contains("dir1"));
        assertTrue(result.getOutput().contains("dir2"));
        assertTrue(result.getOutput().contains("dir3"));

        assertNull(currentDir.getChild("dir1"));
        assertNull(currentDir.getChild("dir2"));
        assertNull(currentDir.getChild("dir3"));
    }
}

 */