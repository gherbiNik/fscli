package ch.supsi.fscli.backend.business.command;

import ch.supsi.fscli.backend.business.command.commands.*;
import ch.supsi.fscli.backend.business.command.commands.validators.AbstractValidator;
import ch.supsi.fscli.backend.business.filesystem.FileSystem;
import ch.supsi.fscli.backend.business.filesystem.Inode;
import ch.supsi.fscli.backend.business.service.FileSystemService;
import ch.supsi.fscli.backend.business.service.IFileSystemService;
import ch.supsi.fscli.backend.util.BackendTranslator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MvCommandTest {
    private static FileSystem fileSystem;
    private static IFileSystemService fileSystemService;
    private static MvCommand mvCommand;

    @BeforeAll
    public static void setUp() {
        fileSystem = FileSystem.getInstance();
        fileSystemService = FileSystemService.getInstance(fileSystem);
        mvCommand = new MvCommand(
                fileSystemService,
                "mv",
                "mv [SOURCE] [DESTINATION]",
                "Move or rename a file or directory"
        );
        AbstractValidatedCommand.setTranslator(BackendTranslator.getInstance());
        AbstractValidator.setTranslator(BackendTranslator.getInstance());
        fileSystemService.setTranslator(BackendTranslator.getInstance());
    }

    @BeforeEach
    public void resetFileSystem() {
        // Reset to root directory before each test
        fileSystemService.changeDirectory("/");

        // Clean up existing structure recursively
        cleanupDirectory("home");
        cleanupDirectory("tmp");

        // Create fresh test directory structure
        fileSystemService.createDirectory("home");
        fileSystemService.createDirectory("home/user");
        fileSystemService.createDirectory("home/user/documents");
        fileSystemService.createDirectory("tmp");
        fileSystemService.createFile("home/test.txt");
        fileSystemService.createFile("home/user/file1.txt");
        fileSystemService.createFile("home/user/file2.txt");
    }

    private void cleanupDirectory(String dirName) {
        try {
            Inode node = fileSystemService.getInode(dirName);
            if (node != null) {
                if (node.isDirectory()) {
                    // Remove all children first
                    var children = fileSystemService.getChildInodeTable(dirName);
                    if (children != null) {
                        for (String childName : children.keySet().toArray(new String[0])) {
                            if (childName.equals(".") || childName.equals("..")) {
                                continue;
                            }
                            cleanupDirectory(dirName + "/" + childName);
                        }
                    }
                    fileSystemService.removeDirectory(dirName);
                } else {
                    fileSystemService.removeFile(dirName);
                }
            }
        } catch (Exception ignored) {
        }
    }

    @Test
    public void testExecute_WithNoArguments_ShouldReturnError() {
        CommandContext context = new CommandContext(
                fileSystem.getCurrentDirectory(),
                Collections.emptyList(),
                Collections.emptyList()
        );
        CommandResult result = mvCommand.execute(context);

        assertFalse(result.isSuccess());
    }

    @Test
    public void testExecute_WithOneArgument_ShouldReturnError() {
        CommandContext context = new CommandContext(
                fileSystem.getCurrentDirectory(),
                List.of("home/test.txt"),
                Collections.emptyList()
        );
        CommandResult result = mvCommand.execute(context);

        assertFalse(result.isSuccess());
    }


    @Test
    public void testExecute_MoveFileToDirectory_ShouldSucceed() {
        CommandContext context = new CommandContext(
                fileSystemService.getCurrentDirectory(),
                List.of("home/test.txt", "tmp"),
                Collections.emptyList()
        );
        CommandResult result = mvCommand.execute(context);

        assertTrue(result.isSuccess());

        // Verify file no longer exists in source
        assertNull(fileSystemService.getInode("home/test.txt"));

        // Verify file exists in destination
        assertNotNull(fileSystemService.getInode("tmp/test.txt"));
    }

    @Test
    public void testExecute_RenameFile_ShouldSucceed() {
        CommandContext context = new CommandContext(
                fileSystemService.getCurrentDirectory(),
                List.of("home/test.txt", "home/renamed.txt"),
                Collections.emptyList()
        );
        CommandResult result = mvCommand.execute(context);

        assertTrue(result.isSuccess());

        // Verify old name doesn't exist
        assertNull(fileSystemService.getInode("home/test.txt"));

        // Verify new name exists
        assertNotNull(fileSystemService.getInode("home/renamed.txt"));
    }

    @Test
    public void testExecute_MoveDirectoryToDirectory_ShouldSucceed() {
        CommandContext context = new CommandContext(
                fileSystemService.getCurrentDirectory(),
                List.of("home/user/documents", "tmp"),
                Collections.emptyList()
        );
        CommandResult result = mvCommand.execute(context);

        assertTrue(result.isSuccess());

        // Verify directory no longer exists in source
        assertNull(fileSystemService.getInode("home/user/documents"));

        // Verify directory exists in destination
        assertNotNull(fileSystemService.getInode("tmp/documents"));
    }

    @Test
    public void testExecute_RenameDirectory_ShouldSucceed() {
        CommandContext context = new CommandContext(
                fileSystemService.getCurrentDirectory(),
                List.of("home/user", "home/username"),
                Collections.emptyList()
        );
        CommandResult result = mvCommand.execute(context);

        assertTrue(result.isSuccess());

        // Verify old name doesn't exist
        assertNull(fileSystemService.getInode("home/user"));

        // Verify new name exists with children
        assertNotNull(fileSystemService.getInode("home/username"));
        assertNotNull(fileSystemService.getInode("home/username/documents"));
        assertNotNull(fileSystemService.getInode("home/username/file1.txt"));
    }

    @Test
    public void testExecute_WithRelativePath_ShouldSucceed() {
        fileSystemService.changeDirectory("/home");

        CommandContext context = new CommandContext(
                fileSystemService.getCurrentDirectory(),
                List.of("test.txt", "user"),
                Collections.emptyList()
        );
        CommandResult result = mvCommand.execute(context);

        assertTrue(result.isSuccess());

        // Verify file moved
        assertNull(fileSystemService.getInode("/home/test.txt"));
        assertNotNull(fileSystemService.getInode("/home/user/test.txt"));
    }

    @Test
    public void testExecute_MoveToNonExistentDirectory_ShouldReturnError() {
        CommandContext context = new CommandContext(
                fileSystemService.getCurrentDirectory(),
                List.of("home/test.txt", "nonexistent/test.txt"),
                Collections.emptyList()
        );
        CommandResult result = mvCommand.execute(context);

        assertFalse(result.isSuccess());

    }

    @Test
    public void testExecute_MoveNonExistentSource_ShouldReturnError() {
        CommandContext context = new CommandContext(
                fileSystemService.getCurrentDirectory(),
                List.of("home/nonexistent.txt", "tmp"),
                Collections.emptyList()
        );
        CommandResult result = mvCommand.execute(context);

        assertFalse(result.isSuccess());
    }

    @Test
    public void testExecute_MoveMultipleFilesInSequence_ShouldSucceed() {
        // Move first file
        CommandContext context1 = new CommandContext(
                fileSystemService.getCurrentDirectory(),
                List.of("home/user/file1.txt", "tmp"),
                Collections.emptyList()
        );
        CommandResult result1 = mvCommand.execute(context1);
        assertTrue(result1.isSuccess());

        // Move second file
        CommandContext context2 = new CommandContext(
                fileSystemService.getCurrentDirectory(),
                List.of("home/user/file2.txt", "tmp"),
                Collections.emptyList()
        );
        CommandResult result2 = mvCommand.execute(context2);
        assertTrue(result2.isSuccess());

        // Verify both files are in tmp
        assertNotNull(fileSystemService.getInode("tmp/file1.txt"));
        assertNotNull(fileSystemService.getInode("tmp/file2.txt"));
        assertNull(fileSystemService.getInode("home/user/file1.txt"));
        assertNull(fileSystemService.getInode("home/user/file2.txt"));
    }

    @Test
    public void testExecute_MoveWithAbsolutePaths_ShouldSucceed() {
        CommandContext context = new CommandContext(
                fileSystemService.getCurrentDirectory(),
                List.of("/home/test.txt", "/tmp/test.txt"),
                Collections.emptyList()
        );
        CommandResult result = mvCommand.execute(context);

        assertTrue(result.isSuccess());
        assertNull(fileSystemService.getInode("/home/test.txt"));
        assertNotNull(fileSystemService.getInode("/tmp/test.txt"));
    }

    @Test
    @DisplayName("mv source linkToDir: Dovrebbe spostare il file DENTRO la directory puntata")
    void testMoveIntoSymlinkDirectory() {
        // Setup: crea /tmp/realDir e un link /home/link -> /tmp/realDir
        fileSystemService.createDirectory("tmp/realDir");

        // Creazione manuale link per il test (o usare lnCommand se disponibile)
        List<String> args = List.of("/tmp/realDir", "home/linkToDir");
        List<String> opts = List.of("-s");
        LnCommand ln = new LnCommand(fileSystemService, "ln", "", "");
        ln.execute(new CommandContext(fileSystemService.getCurrentDirectory(), args, opts));

        // Esegui: mv home/test.txt home/linkToDir
        CommandContext context = new CommandContext(
                fileSystemService.getCurrentDirectory(),
                List.of("home/test.txt", "home/linkToDir"),
                Collections.emptyList()
        );
        CommandResult result = mvCommand.execute(context);

        assertTrue(result.isSuccess());
        // Il file non deve più essere in home
        assertNull(fileSystemService.getInode("home/test.txt"));
        // Il file deve essere dentro la directory reale puntata dal link
        assertNotNull(fileSystemService.getInode("tmp/realDir/test.txt"));
    }

    @Test
    @DisplayName("mv source existingFile: Dovrebbe sovrascrivere il file esistente")
    void testOverwriteExistingFile() {
        // file1 e file2 esistono
        CommandContext context = new CommandContext(
                fileSystemService.getCurrentDirectory(),
                List.of("home/user/file1.txt", "home/user/file2.txt"),
                Collections.emptyList()
        );
        CommandResult result = mvCommand.execute(context);

        assertTrue(result.isSuccess());

        // file1 sparito (spostato)
        assertNull(fileSystemService.getInode("home/user/file1.txt"));
        // file2 esiste (è diventato il vecchio file1)
        assertNotNull(fileSystemService.getInode("home/user/file2.txt"));
    }

    @Test
    @DisplayName("mv file1 file2 file3 dir: Sposta multipli file in una directory")
    void testMoveMultipleFilesToDirectory() {
        // Setup: crea cartella destinazione e 3 file
        fileSystemService.createDirectory("destDir");
        fileSystemService.createFile("f1.txt");
        fileSystemService.createFile("f2.txt");
        fileSystemService.createFile("f3.txt");

        // Esegui: mv f1.txt f2.txt f3.txt destDir
        List<String> args = List.of("f1.txt", "f2.txt", "f3.txt", "destDir");
        CommandContext context = new CommandContext(
                fileSystemService.getCurrentDirectory(),
                args,
                Collections.emptyList()
        );

        CommandResult result = mvCommand.execute(context);

        // TODO nik controlla validator
        assertTrue(result.isSuccess());

        // Verifica che i file siano spariti dalla root
        assertNull(fileSystemService.getInode("f1.txt"));
        assertNull(fileSystemService.getInode("f2.txt"));
        assertNull(fileSystemService.getInode("f3.txt"));

        // Verifica che siano dentro destDir
        assertNotNull(fileSystemService.getInode("destDir/f1.txt"));
        assertNotNull(fileSystemService.getInode("destDir/f2.txt"));
        assertNotNull(fileSystemService.getInode("destDir/f3.txt"));
    }

    @Test
    @DisplayName("mv f1 f2 fileDest: Errore se sposto più file verso un file")
    void testMoveMultipleFilesToFileError() {
        fileSystemService.createFile("f12.txt");
        fileSystemService.createFile("f22.txt");
        fileSystemService.createFile("notADir.txt");

        // Esegui: mv f1 f2 notADir.txt
        List<String> args = List.of("f12.txt", "f22.txt", "notADir.txt");
        CommandContext context = new CommandContext(
                fileSystemService.getCurrentDirectory(),
                args,
                Collections.emptyList()
        );

        CommandResult result = mvCommand.execute(context);

        // Deve fallire perché notADir.txt non è una directory
        assertFalse(result.isSuccess());
    }
}