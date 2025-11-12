package ch.supsi.fscli.backend.business.filesystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DirectoryNodeTest {

    private DirectoryNode root;
    private DirectoryNode subDir;

    @BeforeEach
    void setUp() {
        root = new DirectoryNode(null);
        subDir = new DirectoryNode(root);
        root.addChildren("subDir",subDir);
    }

    @Test
    @DisplayName("Should add child to directory")
    void testAddChildren() {
        FileNode file = new FileNode(subDir);
        subDir.addChildren("test.txt", file);
        assertTrue(root.toString().contains("test.txt"));
    }

    @Test
    @DisplayName("Should handle multiple children")
    void testMultipleChildren() {
        FileNode file1 = new FileNode(subDir);
        FileNode file2 = new FileNode(subDir);
        DirectoryNode childDir = new DirectoryNode(subDir);

        subDir.addChildren("file1.txt", file1);
        subDir.addChildren("file2.txt", file2);
        subDir.addChildren("documents", childDir);

        String dirString = subDir.toString();
        assertTrue(dirString.contains("file1.txt"));
        assertTrue(dirString.contains("file2.txt"));
        assertTrue(dirString.contains("documents"));
    }

    @Test
    @DisplayName("Root directory should have null parent")
    void testRootParent() {
        DirectoryNode root = new DirectoryNode(null);
        assertNotNull(root);
    }
}
