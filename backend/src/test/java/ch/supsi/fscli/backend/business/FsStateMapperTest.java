package ch.supsi.fscli.backend.business;


import ch.supsi.fscli.backend.business.dto.FsStateMapper;
import ch.supsi.fscli.backend.business.dto.IFsStateDto;
import ch.supsi.fscli.backend.business.dto.IFsStateMapper;
import ch.supsi.fscli.backend.business.filesystem.*;
import ch.supsi.fscli.backend.business.service.ISaveDataService;
import ch.supsi.fscli.backend.business.service.SaveDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FsStateMapperTest {

    @TempDir
    File tempDir;

    private FileSystem fileSystem;
    private ISaveDataService saveDataService;
    private IFsStateMapper mapper;

    @BeforeEach
    void setUp() throws Exception {
        // 1. Reset Inode counter
        // Questo rimane necessario perché Inode ha una logica statica interna per gli ID
        Field idCounterField = Inode.class.getDeclaredField("idCounter");
        idCounterField.setAccessible(true);
        idCounterField.set(null, 0);

        // 2. Creiamo le istanze pulite (Niente più reflection sui singleton!)
        fileSystem = new FileSystem();

        // Mockiamo l'interfaccia, è più pulito
        saveDataService = mock(ISaveDataService.class);

        // 3. Injection manuale nel costruttore
        mapper = new FsStateMapper(saveDataService, fileSystem);
    }

    @Test
    void testSerializationAndDeserialization() throws Exception {
        // Arrange: Create a file system structure
        DirectoryNode root = fileSystem.getRoot();

        // Create /home directory
        DirectoryNode home = new DirectoryNode(root);
        root.addChild("home", home);

        // Create /home/user directory
        DirectoryNode user = new DirectoryNode(home);
        home.addChild("user", user);

        // Create /home/user/file.txt
        FileNode file = new FileNode(user);
        user.addChild("file.txt", file);

        // Create /home/link -> /home/user/file.txt
        SoftLink link = new SoftLink("/home/user/file.txt");
        home.addChild("link", link);

        // Change to /home/user
        fileSystem.changeDirectory("/home/user");

        // Capture the serialized state
        final IFsStateDto[] capturedState = new IFsStateDto[1];
        doAnswer(invocation -> {
            capturedState[0] = invocation.getArgument(0);
            return null;
        }).when(saveDataService).save(any(IFsStateDto.class));

        when(saveDataService.load(anyString())).thenAnswer(invocation -> capturedState[0]);

        // Act: Serialize
        mapper.toDTO();

        // Verify serialization was called
        verify(saveDataService, times(1)).save(any(IFsStateDto.class));
        assertNotNull(capturedState[0], "State should be captured");

        // Verify DTO structure
        IFsStateDto state = capturedState[0];
        assertNotNull(state.getRoot());
        assertEquals(user.getUid(), state.getCurrentDirectoryUid());
        assertTrue(state.getInodeTable().size() >= 4);

        // --- SIMULIAMO IL RIAVVIO ---

        // Reset file system for deserialization test
        // Molto più semplice ora: basta crearne uno nuovo!
        fileSystem = new FileSystem();

        // Reinitialize mapper with new file system
        mapper = new FsStateMapper(saveDataService, fileSystem);

        // Act: Deserialize
        mapper.fromDTO("test.json");

        // Assert: Verify reconstructed file system
        DirectoryNode reconstructedRoot = fileSystem.getRoot();
        assertNotNull(reconstructedRoot);

        // Verify /home exists
        Inode homeNode = reconstructedRoot.getChild("home");
        assertNotNull(homeNode);
        assertTrue(homeNode instanceof DirectoryNode);

        // Verify /home/user exists
        DirectoryNode homeDir = (DirectoryNode) homeNode;
        Inode userNode = homeDir.getChild("user");
        assertNotNull(userNode);
        assertTrue(userNode instanceof DirectoryNode);

        // Verify /home/user/file.txt exists
        DirectoryNode userDir = (DirectoryNode) userNode;
        Inode fileNode = userDir.getChild("file.txt");
        assertNotNull(fileNode);
        assertTrue(fileNode instanceof FileNode);

        // Verify /home/link exists and has correct path
        Inode linkNode = homeDir.getChild("link");
        assertNotNull(linkNode);
        assertTrue(linkNode instanceof SoftLink);
        assertEquals("/home/user/file.txt", ((SoftLink) linkNode).getTargetPath());

        // Verify current directory is restored correctly
        DirectoryNode currentDir = fileSystem.getCurrentDirectory();
        assertEquals(userDir.getUid(), currentDir.getUid());

        // Verify parent relationships
        assertEquals(homeDir.getUid(), userDir.getParent().getUid());
        assertEquals(reconstructedRoot.getUid(), homeDir.getParent().getUid());
        assertNull(reconstructedRoot.getParent());
    }

    @Test
    void testSerializationToFile() {
        // Arrange
        DirectoryNode root = fileSystem.getRoot();
        FileNode file = new FileNode(root);
        root.addChild("test.txt", file);

        File outputFile = new File(tempDir, "filesystem.json");

        // Configure mock to do nothing (void method)
        doNothing().when(saveDataService).saveAs(any(IFsStateDto.class), any(File.class));

        // Act
        mapper.toDTOas(outputFile);

        // Assert - verify the method was called with correct parameters
        ArgumentCaptor<IFsStateDto> stateCaptor = ArgumentCaptor.forClass(IFsStateDto.class);
        verify(saveDataService, times(1)).saveAs(stateCaptor.capture(), eq(outputFile));

        // Verify the captured state
        IFsStateDto state = stateCaptor.getValue();
        assertNotNull(state, "State should be captured");
        assertNotNull(state.getRoot());
        assertTrue(state.getInodeTable().containsKey(file.getUid()));
    }

    @Test
    void testEmptyFileSystemSerialization() throws Exception {
        DirectoryNode root = fileSystem.getRoot();
        int rootUid = root.getUid();

        doNothing().when(saveDataService).save(any(IFsStateDto.class));

        mapper.toDTO();

        // Using ArgumentCaptor to capture the state of the class
        ArgumentCaptor<IFsStateDto> stateCaptor = ArgumentCaptor.forClass(IFsStateDto.class);
        verify(saveDataService, times(1)).save(stateCaptor.capture());

        // Verify the captured state
        IFsStateDto state = stateCaptor.getValue();
        assertNotNull(state);
        assertNotNull(state.getRoot());
        assertEquals(rootUid, state.getRoot().getUid());
        assertEquals(rootUid, state.getCurrentDirectoryUid());
        assertEquals(1, state.getInodeTable().size(), "Should contain only root directory");
        assertTrue(state.getInodeTable().containsKey(rootUid), "Inode table should contain root");
    }
}