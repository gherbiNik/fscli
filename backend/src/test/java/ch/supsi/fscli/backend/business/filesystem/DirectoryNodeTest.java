package ch.supsi.fscli.backend.business.filesystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DirectoryNodeTest {

    private DirectoryNode root;
    private DirectoryNode subDir;

    @BeforeEach
    void setUp() {
        // Niente più reflection! DirectoryNode è un oggetto semplice e isolato.
        root = new DirectoryNode(null);
        subDir = new DirectoryNode(root);
        root.addChild("subDir", subDir);
    }

    @Test
    @DisplayName("Should add child to directory")
    void testAddChildren() {
        FileNode file = new FileNode(subDir);
        subDir.addChild("test.txt", file);
        assertNotNull(subDir.getChild("test.txt"));
    }

    @Test
    @DisplayName("Should handle multiple children")
    void testMultipleChildren() {
        FileNode file1 = new FileNode(subDir);
        FileNode file2 = new FileNode(subDir);
        DirectoryNode childDir = new DirectoryNode(subDir);

        subDir.addChild("f1.txt", file1);
        subDir.addChild("f2.txt", file2);
        subDir.addChild("documents", childDir);

        assertNotNull(subDir.getChild("f1.txt"));
        assertNotNull(subDir.getChild("f2.txt"));
        assertNotNull(subDir.getChild("documents"));
        assertEquals(3+2, subDir.getNumChild()); // considero . e ..
    }

    @Test
    @DisplayName("Root directory should have null parent")
    void testRootParent() {
        DirectoryNode localRoot = new DirectoryNode(null);
        assertNull(localRoot.getParent());
    }

    @Test
    @DisplayName("Get child names returns correct set")
    void testGetChildNames() {
        FileNode file1 = new FileNode(subDir);
        DirectoryNode childDir = new DirectoryNode(subDir);
        subDir.addChild("f1.txt", file1);
        subDir.addChild("documents", childDir);

        var names = subDir.getChildNames();
        assertEquals(2+2, names.size()); // considero . e ..
        assertTrue(names.contains("f1.txt"));
        assertTrue(names.contains("documents"));
    }
}