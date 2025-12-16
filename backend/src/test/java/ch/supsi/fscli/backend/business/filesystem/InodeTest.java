package ch.supsi.fscli.backend.business.filesystem;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

public class InodeTest {

    private DirectoryNode parent;

    @BeforeEach
    void setUp() {
        parent = new DirectoryNode(null);
    }

    @Test
    @DisplayName("Root directory should have null parent")
    void testFileNodeCreation() {
        FileNode file = new FileNode();
        assertNotNull(file);
        assertTrue(file.toString().contains("FILE"));
    }

    @Test
    @DisplayName("Root directory should have null parent")
    void testDirectoryNodeCreation() {
        DirectoryNode dir = new DirectoryNode(parent);
        assertNotNull(dir);
        assertTrue(dir.toString().contains("DIRECTORY"));
    }

    @Test
    @DisplayName("Root directory should have null parent")
    void testUniqueUid() {
        FileNode file1 = new FileNode();
        FileNode file2 = new FileNode();
        assertNotEquals(file1.toString(), file2.toString());
    }
}
