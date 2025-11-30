package ch.supsi.fscli.backend.business.filesystem;

import ch.supsi.fscli.backend.business.command.commands.CommandContext;
import ch.supsi.fscli.backend.business.command.commands.LnCommand;
import ch.supsi.fscli.backend.business.service.FileSystemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileSystemTest {

    private FileSystem fileSystem;
    private FileSystemService fileSystemService;
    private DirectoryNode root;
    private DirectoryNode home;
    private DirectoryNode user;
    private DirectoryNode docs;
    private FileNode fileTxt;

    private LnCommand lnCommand;

    @BeforeEach
    void setUp() {
        // Reset dei singleton dipendenti
        resetSingleton(FileSystemService.class);
        resetSingleton(FileSystem.class);

        // Setup del FileSystem
        fileSystem = FileSystem.getInstance();
        fileSystemService = FileSystemService.getInstance(fileSystem);
        root = fileSystem.getRoot();

        // Crea una struttura di test: /home/user/docs e /home/user/file.txt
        fileSystemService.createDirectory("home");
        home = (DirectoryNode) root.getChild("home");

        // Usiamo changeDirectory per testare anche il CWD
        fileSystem.changeDirectory("/home");

        fileSystemService.createDirectory("user");
        user = (DirectoryNode) home.getChild("user");

        fileSystem.changeDirectory("/home/user");

        fileSystemService.createDirectory("docs");
        docs = (DirectoryNode) user.getChild("docs");

        fileSystemService.createFile("file.txt");
        fileTxt = (FileNode) user.getChild("file.txt");

        // Torna alla root per i test
        fileSystem.changeDirectory("/");
    }

    private void resetSingleton(Class<?> aClass) {
        try {
            Field instance = aClass.getDeclaredField("instance");
            instance.setAccessible(true);
            instance.set(null, null);
        } catch (Exception e) {
            fail("Could not reset singleton for: " + aClass.getName());
        }
    }

    @Test
    @DisplayName("FileSystem should be singleton")
    void testSingleton() {
        FileSystem fs1 = FileSystem.getInstance();
        assertSame(fs1, fileSystem);
    }

    @Test
    @DisplayName("Current directory should be root initially")
    void testInitialCurrentDirectory() {
        assertSame(fileSystem.getRoot(), fileSystem.getCurrentDirectory());
    }

    @Test
    @DisplayName("Risolve percorsi assoluti")
    void testResolveAbsolute() {
        assertSame(home, fileSystem.resolveNode("/home"));
        assertSame(user, fileSystem.resolveNode("/home/user"));
        assertSame(docs, fileSystem.resolveNode("/home/user/docs"));
        assertSame(fileTxt, fileSystem.resolveNode("/home/user/file.txt"));
    }

    @Test
    @DisplayName("Risolve percorsi relativi")
    void testResolveRelative() {
        fileSystem.changeDirectory("/home");
        assertSame(user, fileSystem.resolveNode("user"));
        assertSame(docs, fileSystem.resolveNode("user/docs"));
    }

    @Test
    @DisplayName("Risolve '..'")
    void testResolveDotDot() {
        fileSystem.changeDirectory("/home/user/docs");
        assertSame(user, fileSystem.resolveNode(".."));
        assertSame(home, fileSystem.resolveNode("../.."));
        assertSame(root, fileSystem.resolveNode("../../.."));
        // Non si può andare oltre la root
        assertSame(root, fileSystem.resolveNode("../../../.."));
    }

    @Test
    @DisplayName("Risolve '.'")
    void testResolveDot() {
        fileSystem.changeDirectory("/home/user");
        assertSame(user, fileSystem.resolveNode("."));
        assertSame(docs, fileSystem.resolveNode("./docs"));
    }

    @Test
    @DisplayName("Risolve percorsi complessi e ridondanti")
    void testResolveComplex() {
        fileSystem.changeDirectory("/home/user/docs");
        // /home/user/docs/.././file.txt -> /home/user/file.txt
        assertSame(fileTxt, fileSystem.resolveNode(".././file.txt"));
    }

    @Test
    @DisplayName("Restituisce null per percorsi non validi")
    void testResolveNotFound() {
        assertNull(fileSystem.resolveNode("/home/nonEsiste"));
        assertNull(fileSystem.resolveNode("/home/user/file.txt/altro"));
    }

    @Test
    @DisplayName("changeDirectory funziona e lancia eccezione")
    void testChangeDirectory() {
        assertDoesNotThrow(() -> fileSystem.changeDirectory("/home/user"));
        assertSame(user, fileSystem.getCurrentDirectory());

        assertThrows(IllegalArgumentException.class, () -> fileSystem.changeDirectory("/nonEsiste"));
    }

    @Test
    @DisplayName("followLink: Deve risolvere una catena di link")
    void testFollowChainOfLinks() {
        // documents <- link1 <- link2
        fileSystemService.createDirectory("documents");
        Inode documents = fileSystemService.getInode("documents");
        lnCommand = new LnCommand(fileSystemService, "ln", "ln usage", "desc");

        createSoftLink("documents", "link1");
        createSoftLink("link1", "link2");

        Inode link2 = fileSystemService.getInode("link2");
        assertNotNull(link2);

        // Testiamo il metodo del service direttamente
        Inode resolved = fileSystemService.followLink(link2);

        assertNotNull(resolved);
        assertTrue(resolved.isDirectory());
        assertEquals(documents.getUid(), resolved.getUid());
    }

    private void createSoftLink(String source, String dest) {
        List<String> args = List.of(source, dest);
        List<String> opts = List.of("-s");
        CommandContext ctx = new CommandContext(fileSystemService.getCurrentDirectory(), args, opts);
        lnCommand.execute(ctx);
    }
}