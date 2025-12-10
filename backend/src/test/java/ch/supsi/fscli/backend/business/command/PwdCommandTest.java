package ch.supsi.fscli.backend.business.command;

import ch.supsi.fscli.backend.business.command.commands.*;
import ch.supsi.fscli.backend.business.command.commands.validators.AbstractValidator;
import ch.supsi.fscli.backend.business.filesystem.FileSystem;
import ch.supsi.fscli.backend.business.service.FileSystemService;
import ch.supsi.fscli.backend.business.service.IFileSystemService;
import ch.supsi.fscli.backend.util.BackendTranslator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class PwdCommandTest {
    private PwdCommand pwdCommand;
    private IFileSystemService fileSystemService;
    private FileSystem fileSystem;

    @BeforeEach
    void setUp() {
        // 1. Setup Manuale delle dipendenze
        BackendTranslator translator = new BackendTranslator();
        translator.setLocaleDefault(Locale.US);

        AbstractValidatedCommand.setTranslator(translator);
        AbstractValidator.setTranslator(translator);

        // 2. Creazione istanze fresche
        fileSystem = new FileSystem();
        fileSystemService = new FileSystemService(fileSystem, translator);

        // 3. Creazione del comando con stringhe dirette (niente CommandHelpContainer)
        pwdCommand = new PwdCommand(
                fileSystemService,
                "pwd",
                "pwd synopsis",
                "pwd description"
        );
    }

    @Test
    public void testExecute_success() {
        fileSystemService.createDirectory("testDirectory1");
        fileSystemService.createDirectory("testDirectory1/testDirectory2");
        fileSystemService.createDirectory("testDirectory1/testDirectory2/testDirectory3");
        fileSystem.changeDirectory("testDirectory1/testDirectory2/testDirectory3");


        List<String> arguments = new ArrayList<>();
        List<String> options = new ArrayList<>();
        CommandContext context = new CommandContext(fileSystemService.getCurrentDirectory(), arguments, options);
        CommandResult result = pwdCommand.execute(context);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testExecute_rootDirectory() {
        List<String> arguments = new ArrayList<>();
        List<String> options = new ArrayList<>();
        CommandContext context = new CommandContext(fileSystemService.getCurrentDirectory(), arguments, options);
        CommandResult result = pwdCommand.execute(context);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testExecute_singleDirectory() {
        fileSystemService.createDirectory("singleDir");
        fileSystem.changeDirectory("singleDir");

        List<String> arguments = new ArrayList<>();
        List<String> options = new ArrayList<>();
        CommandContext context = new CommandContext(fileSystemService.getCurrentDirectory(), arguments, options);
        CommandResult result = pwdCommand.execute(context);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testExecute_withArguments_shouldFail() {
        List<String> arguments = new ArrayList<>();
        arguments.add("someArgument");
        List<String> options = new ArrayList<>();
        CommandContext context = new CommandContext(fileSystemService.getCurrentDirectory(), arguments, options);
        CommandResult result = pwdCommand.execute(context);

        assertFalse(result.isSuccess());
    }

    @Test
    public void testExecute_withMultipleArguments_shouldFail() {
        List<String> arguments = new ArrayList<>();
        arguments.add("arg1");
        arguments.add("arg2");
        arguments.add("arg3");
        List<String> options = new ArrayList<>();
        CommandContext context = new CommandContext(fileSystemService.getCurrentDirectory(), arguments, options);
        CommandResult result = pwdCommand.execute(context);

        assertFalse(result.isSuccess());
    }

    @Test
    public void testExecute_withOptions() {
        fileSystemService.createDirectory("testDir");
        fileSystem.changeDirectory("testDir");

        List<String> arguments = new ArrayList<>();
        List<String> options = new ArrayList<>();
        options.add("-L");
        CommandContext context = new CommandContext(fileSystemService.getCurrentDirectory(), arguments, options);
        CommandResult result = pwdCommand.execute(context);

        assertFalse(result.isSuccess());
    }

    @Test
    public void testExecute_deepNestedPath() {
        fileSystemService.createDirectory("level1");
        fileSystem.changeDirectory("level1");
        fileSystemService.createDirectory("level2");
        fileSystem.changeDirectory("level2");
        fileSystemService.createDirectory("level3");
        fileSystem.changeDirectory("level3");
        fileSystemService.createDirectory("level4");
        fileSystem.changeDirectory("level4");
        fileSystemService.createDirectory("level5");
        fileSystem.changeDirectory("level5");

        List<String> arguments = new ArrayList<>();
        List<String> options = new ArrayList<>();
        CommandContext context = new CommandContext(fileSystemService.getCurrentDirectory(), arguments, options);
        CommandResult result = pwdCommand.execute(context);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testExecute_nullArguments() {
        List<String> options = new ArrayList<>();
        CommandContext context = new CommandContext(fileSystemService.getCurrentDirectory(), null, options);
        CommandResult result = pwdCommand.execute(context);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testExecute_emptyArguments() {
        List<String> arguments = new ArrayList<>();
        List<String> options = new ArrayList<>();
        CommandContext context = new CommandContext(fileSystemService.getCurrentDirectory(), arguments, options);
        CommandResult result = pwdCommand.execute(context);

        assertTrue(result.isSuccess());
    }
}