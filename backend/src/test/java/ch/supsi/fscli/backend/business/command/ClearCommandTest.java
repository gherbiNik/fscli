package ch.supsi.fscli.backend.business.command;

import ch.supsi.fscli.backend.business.command.commands.*;
import ch.supsi.fscli.backend.business.command.commands.validators.AbstractValidator;
import ch.supsi.fscli.backend.business.filesystem.FileSystem;
import ch.supsi.fscli.backend.business.service.FileSystemService;
import ch.supsi.fscli.backend.business.service.IFileSystemService;
import ch.supsi.fscli.backend.util.BackendTranslator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class ClearCommandTest {
    // 1. Niente più static
    private FileSystem fileSystem;
    private IFileSystemService fileSystemService;
    private ClearCommand clearCommand;

    @BeforeEach // 2. Eseguiamo prima di OGNI test
    public void setUp() {
        // 3. Manual Injection
        BackendTranslator backendTranslator = new BackendTranslator();
        backendTranslator.setLocaleDefault(Locale.US);

        AbstractValidatedCommand.setTranslator(backendTranslator);
        AbstractValidator.setTranslator(backendTranslator);

        fileSystem = new FileSystem();
        // Iniettiamo le dipendenze nel costruttore
        fileSystemService = new FileSystemService(fileSystem, backendTranslator);

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
    }
}