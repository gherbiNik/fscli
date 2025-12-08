package ch.supsi.fscli.backend.business.command;

import ch.supsi.fscli.backend.business.command.business.CommandDetails;
import ch.supsi.fscli.backend.business.command.business.CommandExecutor;
import ch.supsi.fscli.backend.business.command.business.CommandHelpContainer;
import ch.supsi.fscli.backend.business.command.business.CommandParser;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PwdCommandTest {
    private PwdCommand pwdCommand;
    private IFileSystemService fileSystemService;
    private FileSystem fileSystem;
    private CommandHelpContainer commandHelpContainer;

    @BeforeEach
    void setUp() {
        resetSingleton(CommandExecutor.class);
        resetSingleton(CommandHelpContainer.class);
        resetSingleton(FileSystemService.class);
        resetSingleton(BackendTranslator.class);
        resetSingleton(CommandParser.class);
        resetSingleton(FileSystem.class);

        fileSystem = FileSystem.getInstance();
        fileSystemService = FileSystemService.getInstance(fileSystem);

        BackendTranslator translator = BackendTranslator.getInstance();
        translator.setLocaleDefault(Locale.US);

        commandHelpContainer = CommandHelpContainer.getInstance(translator);

        Map<String, CommandDetails> m = commandHelpContainer.getCommandDetailsMap();
        String synopsis = m.get("pwd").synopsis();
        String descr = m.get("pwd").description();
        pwdCommand = new PwdCommand(fileSystemService, "pwd", synopsis, descr);
        AbstractValidatedCommand.setTranslator(BackendTranslator.getInstance());
        AbstractValidator.setTranslator(BackendTranslator.getInstance());

    }

    private void resetSingleton(Class<?> aClass) {
        try {
            java.lang.reflect.Field instance = aClass.getDeclaredField("instance");
            instance.setAccessible(true);
            instance.set(null, null);
        } catch (Exception e) {
            fail("Could not reset singleton for: " + aClass.getName());
        }
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