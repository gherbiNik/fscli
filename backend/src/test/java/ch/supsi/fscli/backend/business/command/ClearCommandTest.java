package ch.supsi.fscli.backend.business.command;

import ch.supsi.fscli.backend.business.command.commands.*;
import ch.supsi.fscli.backend.business.filesystem.FileSystem;
import ch.supsi.fscli.backend.business.service.FileSystemService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ClearCommandTest {
    private static FileSystem fileSystem;
    private static FileSystemService fileSystemService;
    private static ClearCommand clearCommand;

    @BeforeAll
    public static void setUp() {
        fileSystem = FileSystem.getInstance();
        fileSystemService = FileSystemService.getInstance(fileSystem);
        clearCommand = new ClearCommand(
                fileSystemService,
                "clear",
                "clear",
                "Clear the terminal screen"
        );
    }

    @Test
    public void testExecute_WithNoArgumentsAndNoOptions_ShouldSucceed() {
        CommandContext context = new CommandContext(
                fileSystem.getCurrentDirectory(),
                Collections.emptyList(),
                Collections.emptyList()
        );
        CommandResult result = clearCommand.execute(context);

        assertTrue(result.isSuccess());
        assertEquals("Perform Clear", result.getOutput());
    }

    @Test
    public void testExecute_WithArguments_ShouldReturnError() {
        CommandContext context = new CommandContext(
                fileSystem.getCurrentDirectory(),
                List.of("a"),
                Collections.emptyList()
        );
        CommandResult result = clearCommand.execute(context);

        assertFalse(result.isSuccess());
        assertEquals("clear: no args needed", result.getError());
    }



    @Test
    public void testExecute_WithOptions_ShouldReturnError() {
        CommandContext context = new CommandContext(
                fileSystem.getCurrentDirectory(),
                Collections.emptyList(),
                List.of("-a")
        );
        CommandResult result = clearCommand.execute(context);

        assertFalse(result.isSuccess());
        assertEquals("clear: no options needed", result.getError());
    }




}