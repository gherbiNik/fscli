package ch.supsi.fscli.backend.business.command;

import ch.supsi.fscli.backend.business.command.commands.AbstractValidatedCommand;
import ch.supsi.fscli.backend.business.command.commands.CommandContext;
import ch.supsi.fscli.backend.business.command.commands.CommandResult;
import ch.supsi.fscli.backend.business.command.commands.MkdirCommand;
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
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class MkdirCommandTest {

    private MkdirCommand mkdirCommand;
    private IFileSystemService fileSystemService;
    private FileSystem fileSystem;

    @BeforeEach
    void setUp() {
        // 1. Manual Injection
        BackendTranslator translator = new BackendTranslator();
        translator.setLocaleDefault(Locale.US);

        AbstractValidatedCommand.setTranslator(translator);
        AbstractValidator.setTranslator(translator);

        // 2. Creazione istanze
        fileSystem = new FileSystem();
        fileSystemService = new FileSystemService(fileSystem, translator);

        // 3. Creazione comando
        mkdirCommand = new MkdirCommand(
                fileSystemService,
                "mkdir",
                "mkdir synopsis",
                "mkdir description"
        );
    }

    @Test
    void testExecute_Success() {
        List<String> arguments = new ArrayList<>();
        arguments.add("testDir");
        List<String> options = new ArrayList<>();

        DirectoryNode currentDir = fileSystemService.getCurrentDirectory();
        CommandContext context = new CommandContext(currentDir, arguments, options);

        CommandResult result = mkdirCommand.execute(context);

        assertTrue(result.isSuccess());
        assertNotNull(currentDir.getChild("testDir"));
    }

    @Test
    void testExecute_MissingArguments() {
        List<String> arguments = new ArrayList<>();
        List<String> options = new ArrayList<>();
        CommandContext context = new CommandContext(fileSystemService.getCurrentDirectory(), arguments, options);
        CommandResult result = mkdirCommand.execute(context);
        assertFalse(result.isSuccess());
    }

    @Test
    void testExecute_NullArguments() {
        List<String> options = new ArrayList<>();
        CommandContext context = new CommandContext(fileSystemService.getCurrentDirectory(), null, options);
        CommandResult result = mkdirCommand.execute(context);
        assertFalse(result.isSuccess());
    }

    @Test
    void testExecute_EmptyDirectoryName() {
        List<String> arguments = new ArrayList<>();
        arguments.add("");
        List<String> options = new ArrayList<>();
        CommandContext context = new CommandContext(fileSystemService.getCurrentDirectory(), arguments, options);
        CommandResult result = mkdirCommand.execute(context);
        assertFalse(result.isSuccess());
    }

    @Test
    void testExecute_DirectoryAlreadyExists() {
        List<String> arguments = new ArrayList<>();
        arguments.add("existingDir");
        List<String> options = new ArrayList<>();
        fileSystemService.createDirectory("existingDir");

        CommandContext context = new CommandContext(fileSystemService.getCurrentDirectory(), arguments, options);
        CommandResult result = mkdirCommand.execute(context);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("Testa creazione con path assoluto (/)")
    void testExecute_PathResolution_Absolute() {
        fileSystemService.createDirectory("docs");

        List<String> arguments = new ArrayList<>();
        arguments.add("/docs/newDir");
        List<String> options = new ArrayList<>();

        CommandContext context = new CommandContext(fileSystemService.getCurrentDirectory(), arguments, options);
        CommandResult result = mkdirCommand.execute(context);

        assertTrue(result.isSuccess());
        Inode docsNode = fileSystem.resolveNode("/docs");
        assertInstanceOf(DirectoryNode.class, docsNode);
        assertNotNull(((DirectoryNode) docsNode).getChild("newDir"));
    }

    @Test
    @DisplayName("Testa creazione con path relativo '..'")
    void testExecute_PathResolution_DotDot() {
        fileSystemService.createDirectory("docs");
        fileSystem.changeDirectory("/docs");

        List<String> arguments = new ArrayList<>();
        arguments.add("../otherDir");
        List<String> options = new ArrayList<>();

        CommandContext context = new CommandContext(fileSystemService.getCurrentDirectory(), arguments, options);
        CommandResult result = mkdirCommand.execute(context);

        assertTrue(result.isSuccess());
        assertNotNull(fileSystem.resolveNode("/otherDir"));
    }
}