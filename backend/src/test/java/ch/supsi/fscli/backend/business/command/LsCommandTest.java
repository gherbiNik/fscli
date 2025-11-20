package ch.supsi.fscli.backend.business.command;

import ch.supsi.fscli.backend.business.command.business.CommandDetails;
import ch.supsi.fscli.backend.business.command.business.CommandExecutor;
import ch.supsi.fscli.backend.business.command.business.CommandHelpContainer;
import ch.supsi.fscli.backend.business.command.business.CommandParser;
import ch.supsi.fscli.backend.business.command.commands.CommandContext;
import ch.supsi.fscli.backend.business.command.commands.CommandResult;
import ch.supsi.fscli.backend.business.command.commands.LsCommand;
import ch.supsi.fscli.backend.business.command.commands.PwdCommand;
import ch.supsi.fscli.backend.business.filesystem.FileSystem;
import ch.supsi.fscli.backend.business.service.FileSystemService;
import ch.supsi.fscli.backend.util.BackendTranslator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class LsCommandTest {
    private LsCommand lsCommand;
    private FileSystemService fileSystemService;
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
        String synopsis = m.get("ls").synopsis();
        String descr = m.get("ls").description();
        lsCommand = new LsCommand(fileSystemService, "ls", synopsis, descr);
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
        fileSystemService.createDirectory("testDirectory2");
        fileSystemService.createDirectory("testDirectory3");
        fileSystemService.createDirectory("testDirectory3/p");
        fileSystemService.createFile("testDirectory3/pluto.xml");
        fileSystemService.createFile("pippo.txt");




        List<String> arguments = new ArrayList<>();
        List<String> options = new ArrayList<>();
        CommandContext context = new CommandContext(fileSystemService.getCurrentDirectory(), arguments, options);
        CommandResult result = lsCommand.execute(context);
        System.out.println(result.getOutput());
        options.add("-i");
        context = new CommandContext(fileSystemService.getCurrentDirectory(), arguments, options);
        result = lsCommand.execute(context);
        System.out.println(result.getOutput());


        //arguments.add("testDirectory2");
        //arguments.add("testDirectory5");

        //TODO controllo caso in cui faccio ls di una cartella e questa è vuoto con 1 arg
        arguments.add("testDirectory3/p");
        //arguments.add("testDirectory3");
        context = new CommandContext(fileSystemService.getCurrentDirectory(), arguments, options);
        result = lsCommand.execute(context);

        System.out.println(result.getError());
        System.out.println(result.getOutput());
    }


}