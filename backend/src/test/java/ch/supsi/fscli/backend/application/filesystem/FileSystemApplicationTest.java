package ch.supsi.fscli.backend.application.filesystem;

import ch.supsi.fscli.backend.business.filesystem.FileSystem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

public class FileSystemApplicationTest {

    @BeforeEach
    void setUp() {
        // Reset solo del Singleton "root" (l'Application)
        try {
            java.lang.reflect.Field fsAppInstance = FileSystemApplication.class.getDeclaredField("instance");
            fsAppInstance.setAccessible(true);
            fsAppInstance.set(null, null);
        } catch (Exception e) {
            fail("Could not reset singletons");
        }
    }

    @Test
    @DisplayName("FileSystemApplication should be singleton")
    void testSingleton() {
        FileSystemApplication app1 = FileSystemApplication.getInstance();
        FileSystemApplication app2 = FileSystemApplication.getInstance();
        assertSame(app1, app2);
    }

    @Test
    @DisplayName("Should create file system")
    void testCreateFileSystem() {
        FileSystemApplication app = FileSystemApplication.getInstance();
        assertDoesNotThrow(app::createFileSystem);
    }

    @Test
    @DisplayName("Created file system should be accessible")
    void testFileSystemAccessibility() {
        FileSystemApplication app = FileSystemApplication.getInstance();

        // Prima della creazione deve essere false
        assertFalse(app.isFileSystemCreated());

        app.createFileSystem();

        // Dopo la creazione deve essere true
        assertTrue(app.isFileSystemCreated());

    }
}