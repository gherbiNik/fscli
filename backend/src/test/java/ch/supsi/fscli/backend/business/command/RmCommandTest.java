package ch.supsi.fscli.backend.business.command;

import ch.supsi.fscli.backend.business.command.commands.AbstractValidatedCommand;
import ch.supsi.fscli.backend.business.command.commands.CommandContext;
import ch.supsi.fscli.backend.business.command.commands.CommandResult;
import ch.supsi.fscli.backend.business.command.commands.RmCommand;
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

class RmCommandTest {

    private RmCommand rmCommand;
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
        rmCommand = new RmCommand(
                fileSystemService,
                "rm",
                "rm synopsis",
                "rm description"
        );
    }

    @Test
    void testExecute_Success() {
        List<String> arguments = new ArrayList<>();
        arguments.add("fileToDelete.txt");
        List<String> options = new ArrayList<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        fileSystemService.createFile("fileToDelete.txt");

        assertNotNull(currentDir.getChild("fileToDelete.txt"));

        CommandContext context = new CommandContext(currentDir, arguments, options);
        CommandResult result = rmCommand.execute(context);

        assertTrue(result.isSuccess());
        assertNull(currentDir.getChild("fileToDelete.txt"));
    }

    @Test
    void testExecute_MissingArguments() {
        List<String> arguments = new ArrayList<>();
        List<String> options = new ArrayList<>();
        CommandContext context = new CommandContext(fileSystemService.getCurrentDirectory(), arguments, options);
        CommandResult result = rmCommand.execute(context);
        assertFalse(result.isSuccess());
    }

    @Test
    void testExecute_NullArguments() {
        List<String> options = new ArrayList<>();
        CommandContext context = new CommandContext(fileSystemService.getCurrentDirectory(), null, options);
        CommandResult result = rmCommand.execute(context);
        assertFalse(result.isSuccess());
    }

    @Test
    void testExecute_FileDoesNotExist() {
        List<String> arguments = new ArrayList<>();
        arguments.add("nonExistentFile.txt");
        List<String> options = new ArrayList<>();
        CommandContext context = new CommandContext(fileSystemService.getCurrentDirectory(), arguments, options);
        CommandResult result = rmCommand.execute(context);
        assertFalse(result.isSuccess());
    }

    @Test
    void testExecute_AttemptToDeleteDirectory() {
        List<String> arguments = new ArrayList<>();
        arguments.add("aDirectory");
        List<String> options = new ArrayList<>();
        fileSystemService.createDirectory("aDirectory");
        CommandContext context = new CommandContext(fileSystemService.getCurrentDirectory(), arguments, options);
        CommandResult result = rmCommand.execute(context);

        assertFalse(result.isSuccess());
        assertNotNull(fileSystemService.getCurrentDirectory().getChild("aDirectory"));
    }

    @Test
    @DisplayName("Testa rimozione file con path assoluto")
    void testExecute_PathResolution_Absolute() {
        fileSystemService.createDirectory("docs");
        fileSystemService.createFile("/docs/file.txt");

        Inode docsNode = fileSystem.resolveNode("/docs");
        assertNotNull(((DirectoryNode) docsNode).getChild("file.txt"));

        List<String> arguments = new ArrayList<>();
        arguments.add("/docs/file.txt");
        List<String> options = new ArrayList<>();

        CommandContext context = new CommandContext(fileSystemService.getCurrentDirectory(), arguments, options);
        CommandResult result = rmCommand.execute(context);

        assertTrue(result.isSuccess());
        assertNull(((DirectoryNode) docsNode).getChild("file.txt"));
    }

    @Test
    @DisplayName("Testa rimozione file con path relativo '..'")
    void testExecute_PathResolution_DotDot() {
        fileSystemService.createFile("file.txt");
        fileSystemService.createDirectory("docs");
        fileSystem.changeDirectory("/docs");

        assertNotNull(fileSystem.resolveNode("/file.txt"));

        List<String> arguments = new ArrayList<>();
        arguments.add("../file.txt");
        List<String> options = new ArrayList<>();

        CommandContext context = new CommandContext(fileSystemService.getCurrentDirectory(), arguments, options);
        CommandResult result = rmCommand.execute(context);

        assertTrue(result.isSuccess());
        assertNull(fileSystem.resolveNode("/file.txt"));
    }
}