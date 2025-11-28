package ch.supsi.fscli.backend.business.command;

import ch.supsi.fscli.backend.business.command.business.CommandDetails;
import ch.supsi.fscli.backend.business.command.business.CommandExecutor;
import ch.supsi.fscli.backend.business.command.business.CommandHelpContainer;
import ch.supsi.fscli.backend.business.command.business.CommandParser;
import ch.supsi.fscli.backend.business.command.commands.CommandContext;
import ch.supsi.fscli.backend.business.command.commands.CommandResult;
import ch.supsi.fscli.backend.business.command.commands.LnCommand;
import ch.supsi.fscli.backend.business.command.commands.LsCommand;
import ch.supsi.fscli.backend.business.filesystem.FileSystem;
import ch.supsi.fscli.backend.business.filesystem.Inode;
import ch.supsi.fscli.backend.business.service.FileSystemService;
import ch.supsi.fscli.backend.util.BackendTranslator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class LnCommandTest {
    private LnCommand lnCommand;
    private FileSystemService fileSystemService;
    private FileSystem fileSystem;
    private CommandHelpContainer commandHelpContainer;

    @BeforeEach
    void setUp() {
        // 1. Reset dei Singleton per garantire un ambiente pulito
        resetSingleton(CommandExecutor.class);
        resetSingleton(CommandHelpContainer.class);
        resetSingleton(FileSystemService.class);
        resetSingleton(BackendTranslator.class);
        resetSingleton(CommandParser.class);
        resetSingleton(FileSystem.class);

        // 2. Inizializzazione dipendenze reali
        fileSystem = FileSystem.getInstance();
        fileSystemService = FileSystemService.getInstance(fileSystem);

        BackendTranslator translator = BackendTranslator.getInstance();
        translator.setLocaleDefault(Locale.US);

        commandHelpContainer = CommandHelpContainer.getInstance(translator);


        Map<String, CommandDetails> m = commandHelpContainer.getCommandDetailsMap();
        String synopsis = m.get("ln").synopsis();
        String descr = m.get("ln").description();

        lnCommand = new LnCommand(fileSystemService, "ln", synopsis, descr);


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
    @DisplayName("ln source target: Crea un hard link con nuovo nome")
    void testCreateHardLinkNewName() {
        fileSystemService.createFile("source.txt");
        List<String> args = List.of("source.txt", "hardlink.txt");
        CommandContext ctx = new CommandContext(fileSystemService.getCurrentDirectory(), args, Collections.emptyList());

        CommandResult result = lnCommand.execute(ctx);

        assertTrue(result.isSuccess());

        Inode source = fileSystemService.getInode("source.txt");
        Inode link = fileSystemService.getInode("hardlink.txt");
        System.out.println(fileSystem);
        assertNotNull(link);
        // Verifica cruciale per Hard Link: devono avere lo stesso UID (stesso oggetto Inode)
        assertEquals(source.getUid(), link.getUid());
    }

    @Test
    @DisplayName("ln source target: non crea hard link di una directory")
    void testCreateHardLinkOfDirectory() {
        fileSystemService.createDirectory("dir");
        List<String> args = List.of("dir", "hardlink.txt");
        CommandContext ctx = new CommandContext(fileSystemService.getCurrentDirectory(), args, Collections.emptyList());

        CommandResult result = lnCommand.execute(ctx);

        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("hard link not allowed for directory"));
    }


}
