package ch.supsi.fscli.backend.business.command;

import ch.supsi.fscli.backend.business.command.business.CommandDetails;
import ch.supsi.fscli.backend.business.command.business.CommandExecutor;
import ch.supsi.fscli.backend.business.command.business.CommandHelpContainer;
import ch.supsi.fscli.backend.business.command.business.CommandParser;
import ch.supsi.fscli.backend.business.command.commands.*;
import ch.supsi.fscli.backend.business.command.commands.validators.AbstractValidator;
import ch.supsi.fscli.backend.business.filesystem.FileSystem;
import ch.supsi.fscli.backend.business.filesystem.Inode;
import ch.supsi.fscli.backend.business.service.FileSystemService;
import ch.supsi.fscli.backend.business.service.IFileSystemService;
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
    private IFileSystemService fileSystemService;
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
    @DisplayName("ln source target: Crea un hard link con nuovo nome")
    void testCreateHardLinkNewName() {
        fileSystemService.createFile("source.txt");
        List<String> args = List.of("source.txt", "hardlink.txt");
        CommandContext ctx = new CommandContext(fileSystemService.getCurrentDirectory(), args, Collections.emptyList());

        CommandResult result = lnCommand.execute(ctx);

        assertTrue(result.isSuccess());

        Inode source = fileSystemService.getInode("source.txt");
        Inode link = fileSystemService.getInode("hardlink.txt");
        //System.out.println(fileSystem);
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
    }

    @Test
    @DisplayName("ln source dir: Crea link dentro la directory con lo stesso nome")
    void testCreateHardLinkIntoDirectory() {
        // Comando: ln source.txt myDir
        // Risultato atteso: dir/source.txt
        fileSystemService.createFile("source.txt");
        fileSystemService.createDirectory("dir");
        List<String> args = List.of("source.txt", "dir");
        CommandContext ctx = new CommandContext(fileSystemService.getCurrentDirectory(), args, Collections.emptyList());

        CommandResult result = lnCommand.execute(ctx);

        assertTrue(result.isSuccess());

        Inode source = fileSystemService.getInode("source.txt");
        Inode linkInside = fileSystemService.getInode("dir/source.txt");

        //System.out.println(fileSystem);

        assertNotNull(linkInside, "Il file dovrebbe essere stato creato dentro dir");
        assertEquals(source.getUid(), linkInside.getUid());
    }

    @Test
    @DisplayName("ln -s source target: Crea un soft link")
    void testCreateSoftLink() {
        fileSystemService.createFile("source.txt");
        List<String> args = List.of("source.txt", "softlink");
        List<String> opts = List.of("-s");
        CommandContext ctx = new CommandContext(fileSystemService.getCurrentDirectory(), args, opts);

        CommandResult result = lnCommand.execute(ctx);

        assertTrue(result.isSuccess());
        Inode link = fileSystemService.getInode("softlink");
        assertNotNull(link);
        //System.out.println(fileSystem);
        // Verifica che sia un tipo diverso (SOFTLINK) o comunque un nuovo oggetto con UID diverso
        assertNotEquals(fileSystemService.getInode("source.txt").getUid(), link.getUid());
    }

    @Test
    @DisplayName("ln -s source target: Crea un soft link con percorso assoluto")
    void testCreateSoftLinkAbsolutePath() {
        fileSystemService.createDirectory("dir");
        fileSystemService.createDirectory("dir/dir");
        fileSystemService.createDirectory("dir/dir/dir");

        fileSystemService.createFile("dir/dir/dir/source.txt");
        List<String> args = List.of("/dir/dir/dir/source.txt", "softlink");
        List<String> opts = List.of("-s");
        CommandContext ctx = new CommandContext(fileSystemService.getCurrentDirectory(), args, opts);

        CommandResult result = lnCommand.execute(ctx);

        assertTrue(result.isSuccess());
        Inode link = fileSystemService.getInode("softlink");
        assertNotNull(link);
        System.out.println(fileSystem);
        // Verifica che sia un tipo diverso (SOFTLINK) o comunque un nuovo oggetto con UID diverso
        assertNotEquals(fileSystemService.getInode("/dir/dir/dir/source.txt").getUid(), link.getUid());
    }

    @Test
    @DisplayName("ln -s source target: Crea un soft link of copmposed path")
    void testCreateSoftLinkOfComposedPath() {
        fileSystemService.createDirectory("dir");
        fileSystemService.createFile("dir/source.txt");
        List<String> args = List.of("dir/source.txt", "softlink");
        List<String> opts = List.of("-s");
        CommandContext ctx = new CommandContext(fileSystemService.getCurrentDirectory(), args, opts);

        CommandResult result = lnCommand.execute(ctx);

        assertTrue(result.isSuccess());
        Inode link = fileSystemService.getInode("softlink");
        assertNotNull(link);
        //System.out.println(fileSystem);
        // Verifica che sia un tipo diverso (SOFTLINK) o comunque un nuovo oggetto con UID diverso
        assertNotEquals(fileSystemService.getInode("dir/source.txt").getUid(), link.getUid());
    }

    @Test
    @DisplayName("ln (no args): Errore argomenti mancanti")
    void testMissingArguments() {
        CommandContext ctx = new CommandContext(fileSystemService.getCurrentDirectory(), Collections.emptyList(), Collections.emptyList());
        CommandResult result = lnCommand.execute(ctx);

        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("usage"));
    }

    @Test
    @DisplayName("ln -z source target: Errore opzione illegale")
    void testIllegalOption() {
        fileSystemService.createFile("source.txt");
        List<String> args = List.of("source.txt", "link");
        List<String> opts = List.of("-z"); // Opzione non valida
        CommandContext ctx = new CommandContext(fileSystemService.getCurrentDirectory(), args, opts);

        CommandResult result = lnCommand.execute(ctx);

        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("ln ghost.txt target: Errore source inesistente")
    void testSourceNotFound() {
        List<String> args = List.of("ghost.txt", "link");
        CommandContext ctx = new CommandContext(fileSystemService.getCurrentDirectory(), args, Collections.emptyList());

        CommandResult result = lnCommand.execute(ctx);

        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("ln source existingFile: Errore file destinazione esistente")
    void testDestinationFileExists() {
        fileSystemService.createFile("source.txt");
        fileSystemService.createFile("exists.txt");
        List<String> args = List.of("source.txt", "exists.txt");
        CommandContext ctx = new CommandContext(fileSystemService.getCurrentDirectory(), args, Collections.emptyList());

        CommandResult result = lnCommand.execute(ctx);

        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("ln source myDir: Errore se dentro myDir esiste già il file")
    void testDestinationFileInDirectoryExists() {
        fileSystemService.createFile("source.txt");
        fileSystemService.createDirectory("dir");
        fileSystemService.createFile("dir/source.txt");

        // Provo a fare ln source.txt myDir (che proverebbe a creare myDir/source.txt)
        List<String> args = List.of("source.txt", "dir");
        CommandContext ctx = new CommandContext(fileSystemService.getCurrentDirectory(), args, Collections.emptyList());

        CommandResult result = lnCommand.execute(ctx);

        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("ln source fakeDir/link: Errore parent folder destinazione inesistente")
    void testDestinationParentMissing() {
        fileSystemService.createFile("source.txt");
        List<String> args = List.of("source.txt", "fakeDir/link.txt");
        CommandContext ctx = new CommandContext(fileSystemService.getCurrentDirectory(), args, Collections.emptyList());

        CommandResult result = lnCommand.execute(ctx);

        assertFalse(result.isSuccess());
    }




}
