package ch.supsi.fscli.backend.business.command;

import ch.supsi.fscli.backend.business.command.commands.AbstractValidatedCommand;
import ch.supsi.fscli.backend.business.command.commands.CommandContext;
import ch.supsi.fscli.backend.business.command.commands.CommandResult;
import ch.supsi.fscli.backend.business.command.commands.RmdirCommand;
import ch.supsi.fscli.backend.business.command.commands.validators.AbstractValidator;
import ch.supsi.fscli.backend.business.filesystem.DirectoryNode;
import ch.supsi.fscli.backend.business.filesystem.FileSystem;
import ch.supsi.fscli.backend.business.filesystem.Inode;
import ch.supsi.fscli.backend.business.service.FileSystemService;
import ch.supsi.fscli.backend.business.service.IFileSystemService;
import ch.supsi.fscli.backend.util.BackendTranslator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class RmdirCommandTest {

    private RmdirCommand rmdirCommand;
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

        // 3. Creazione del comando
        rmdirCommand = new RmdirCommand(
                fileSystemService,
                "rmdir",
                "rmdir synopsis",
                "rmdir description"
        );
    }

    @Test
    void testExecute_Success() {
        fileSystemService.createDirectory("testDir");

        List<String> arguments = new ArrayList<>();
        arguments.add("testDir");
        List<String> options = new ArrayList<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = rmdirCommand.execute(context);

        assertTrue(result.isSuccess());
        assertNull(currentDir.getChild("testDir"));
    }

    @Test
    void testExecute_MissingArguments() {
        List<String> arguments = new ArrayList<>();
        List<String> options = new ArrayList<>();
        CommandContext context = new CommandContext(fileSystemService.getCurrentDirectory(), arguments, options);
        CommandResult result = rmdirCommand.execute(context);
        assertFalse(result.isSuccess());
    }

    @Test
    void testExecute_NullArguments() {
        List<String> options = new ArrayList<>();
        CommandContext context = new CommandContext(fileSystemService.getCurrentDirectory(), null, options);
        CommandResult result = rmdirCommand.execute(context);
        assertFalse(result.isSuccess());
    }

    @Test
    void testExecute_DirectoryDoesNotExist() {
        List<String> arguments = new ArrayList<>();
        arguments.add("nonExistentDir");
        List<String> options = new ArrayList<>();
        CommandContext context = new CommandContext(fileSystemService.getCurrentDirectory(), arguments, options);
        CommandResult result = rmdirCommand.execute(context);
        assertFalse(result.isSuccess());
    }

    @Test
    void testExecute_DirectoryNotEmpty() {
        fileSystemService.createDirectory("parentDir");
        fileSystemService.createFile("/parentDir/file.txt");

        List<String> arguments = new ArrayList<>();
        arguments.add("parentDir");
        List<String> options = new ArrayList<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, arguments, options);
        CommandResult result = rmdirCommand.execute(context);

        // Nota: Qui mantengo la tua logica originale dove sembra che il comando
        // ritorni successo ma non cancelli la cartella se non è vuota.
        // Normalmente rmdir dovrebbe fallire (isSuccess() == false) se non vuota.
        assertTrue(result.isSuccess());
        assertNotNull(currentDir.getChild("parentDir"));
    }

    @Test
    @DisplayName("Testa rimozione dir con path assoluto")
    void testExecute_PathResolution_Absolute() {
        fileSystemService.createDirectory("docs");
        fileSystemService.createDirectory("/docs/dirB");

        Inode docsNode = fileSystem.resolveNode("/docs");
        assertNotNull(((DirectoryNode) docsNode).getChild("dirB"));

        List<String> arguments = new ArrayList<>();
        arguments.add("/docs/dirB");
        List<String> options = new ArrayList<>();

        CommandContext context = new CommandContext(fileSystemService.getCurrentDirectory(), arguments, options);
        CommandResult result = rmdirCommand.execute(context);

        assertTrue(result.isSuccess());
        assertNull(((DirectoryNode) docsNode).getChild("dirB"));
    }

    @Test
    @DisplayName("Testa rimozione dir con path relativo '..'")
    void testExecute_PathResolution_DotDot() {
        fileSystemService.createDirectory("dirA");
        fileSystemService.createDirectory("docs");
        fileSystem.changeDirectory("/docs");

        assertNotNull(fileSystem.resolveNode("/dirA"));

        List<String> arguments = new ArrayList<>();
        arguments.add("../dirA");
        List<String> options = new ArrayList<>();

        CommandContext context = new CommandContext(fileSystemService.getCurrentDirectory(), arguments, options);
        CommandResult result = rmdirCommand.execute(context);

        assertTrue(result.isSuccess());
        assertNull(fileSystem.resolveNode("/dirA"));
    }
}