package ch.supsi.fscli.backend.business.command;

import ch.supsi.fscli.backend.application.CommandHelpApplication;
import ch.supsi.fscli.backend.business.command.business.CommandHelpContainer;
import ch.supsi.fscli.backend.business.command.commands.CdCommand;
import ch.supsi.fscli.backend.business.command.commands.CommandContext;
import ch.supsi.fscli.backend.business.command.commands.CommandResult;
import ch.supsi.fscli.backend.business.filesystem.FileSystem;
import ch.supsi.fscli.backend.business.service.FileSystemService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CdCommandTest {
    private static FileSystem fileSystem;
    private static FileSystemService fileSystemService;
    private static CdCommand cdCommand;

    @BeforeAll
    public static void setUp() {
        fileSystem = FileSystem.getInstance();
        fileSystemService = FileSystemService.getInstance(fileSystem);
        cdCommand = new CdCommand(
                fileSystemService,
                "cd",
                "cd [DIR]",
                "Change the current directory"
        );

        // Crea una struttura di directory per i test
        fileSystemService.createDirectory("home");
        fileSystemService.createDirectory("home/user");
        fileSystemService.createDirectory("home/user/documents");
        fileSystemService.createDirectory("tmp");
        fileSystemService.createFile("home/test.txt");
    }

    @Test
    public void testExecute_WithNoArguments_ShouldReturnError() {
        CommandContext context = new CommandContext(fileSystem.getCurrentDirectory()
                , Collections.emptyList(), Collections.emptyList());
        CommandResult result = cdCommand.execute(context);
        assertFalse(result.isSuccess());
        assertEquals("cd: missing arguments", result.getError());

    }

    @Test
    public void testExecute_WithTooManyArguments_ShouldReturnError() {
        CommandContext context = new CommandContext(fileSystem.getCurrentDirectory(), Arrays.asList("home", "tmp")
        , Collections.emptyList());

        CommandResult result = cdCommand.execute(context);

        assertFalse(result.isSuccess());
        assertEquals("cd: too many arguments", result.getError());
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
                List.of("/home/user/documents"), Collections.emptyList()
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


}
