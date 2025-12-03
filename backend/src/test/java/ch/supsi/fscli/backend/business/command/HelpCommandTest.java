package ch.supsi.fscli.backend.business.command;


import ch.supsi.fscli.backend.business.command.business.CommandDetails;
import ch.supsi.fscli.backend.business.command.business.CommandHelpContainer;
import ch.supsi.fscli.backend.business.command.commands.CommandContext;
import ch.supsi.fscli.backend.business.command.commands.CommandResult;
import ch.supsi.fscli.backend.business.command.commands.HelpCommand;
import ch.supsi.fscli.backend.business.filesystem.FileSystem;
import ch.supsi.fscli.backend.business.service.FileSystemService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HelpCommandTest {
    private static FileSystem fileSystem;
    private static FileSystemService fileSystemService;
    private static CommandHelpContainer mockContainer;
    private static HelpCommand helpCommand;

    @BeforeAll
    public static void setUp() {
        fileSystem = FileSystem.getInstance();
        fileSystemService = FileSystemService.getInstance(fileSystem);
        mockContainer = mock(CommandHelpContainer.class);

        helpCommand = new HelpCommand(
                fileSystemService,
                "help",
                "help",
                "Display available commands"
        );
    }

    @Test
    public void testExecute_WithNoArgumentsAndNoOptions_ShouldSucceed() {
        Map<String, CommandDetails> commands = new HashMap<>();
        commands.put("cd", new CommandDetails("cd [DIR]", "Change directory"));
        commands.put("ls", new CommandDetails("ls [OPTIONS]", "List files"));

        when(mockContainer.getCommandDetailsMap()).thenReturn(commands);

        CommandContext context = new CommandContext(
                fileSystem.getCurrentDirectory(),
                Collections.emptyList(),
                Collections.emptyList()
        );
        CommandResult result = helpCommand.execute(context);

        assertTrue(result.isSuccess());
        assertTrue(result.getOutput().contains("Available Commands List:"));
        assertTrue(result.getOutput().contains("cd : cd [DIR]"));
        assertTrue(result.getOutput().contains("ls : ls [OPTIONS]"));
    }

    @Test
    public void testExecute_WithArguments_ShouldReturnError() {
        CommandContext context = new CommandContext(
                fileSystem.getCurrentDirectory(),
                List.of("somearg"),
                Collections.emptyList()
        );
        CommandResult result = helpCommand.execute(context);

        assertFalse(result.isSuccess());
        assertEquals("help: no args needed", result.getError());
    }

    @Test
    public void testExecute_WithOptions_ShouldReturnError() {
        CommandContext context = new CommandContext(
                fileSystem.getCurrentDirectory(),
                Collections.emptyList(),
                List.of("-a")
        );
        CommandResult result = helpCommand.execute(context);

        assertFalse(result.isSuccess());
        assertEquals("help: no options needed", result.getError());
    }

    @Test
    public void testExecute_WithEmptyCommandMap_ShouldReturnError() {
        when(mockContainer.getCommandDetailsMap()).thenReturn(Collections.emptyMap());

        CommandContext context = new CommandContext(
                fileSystem.getCurrentDirectory(),
                Collections.emptyList(),
                Collections.emptyList()
        );
        CommandResult result = helpCommand.execute(context);

        assertFalse(result.isSuccess());
        assertEquals("help: no commands available", result.getError());
    }

    @Test
    public void testExecute_WithNullCommandMap_ShouldReturnError() {
        when(mockContainer.getCommandDetailsMap()).thenReturn(null);

        CommandContext context = new CommandContext(
                fileSystem.getCurrentDirectory(),
                Collections.emptyList(),
                Collections.emptyList()
        );
        CommandResult result = helpCommand.execute(context);

        assertFalse(result.isSuccess());
        assertEquals("help: error occurred while reading commands", result.getError());
    }
}
