package ch.supsi.fscli.backend.dataAccess;

import ch.supsi.fscli.backend.business.dto.*;
import ch.supsi.fscli.backend.business.filesystem.InodeType;
import ch.supsi.fscli.backend.dataAccess.filesystem.JacksonSaveDataService;
import ch.supsi.fscli.backend.dataAccess.filesystem.NoFilesystemSavedEx;
import ch.supsi.fscli.backend.dataAccess.preferences.PreferenceDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JacksonSaveTest {

    @TempDir
    Path tempDir;

    private PreferenceDAO preferenceDAO;
    private JacksonSaveDataService service;

    @BeforeEach
    void setUp() throws Exception {
        // 1. Mock delle dipendenze
        preferenceDAO = mock(PreferenceDAO.class);

        // Configuriamo il mock per restituire un percorso valido nella cartella temporanea
        when(preferenceDAO.getUserPreferencesFilePath()).thenReturn(tempDir.resolve("preferences.properties"));

        // 2. Creazione dell'istanza da testare (Manual Injection)
        // Niente più reflection o getInstance!
        service = new JacksonSaveDataService(preferenceDAO);
    }

    @Test
    void testSaveCreatesFileWithTimestamp() {
        Map<Integer, InodeDto> inodeTable = new HashMap<>();
        Map<String, Integer> childrenUids = new HashMap<>();

        DirectoryNodeDto rootDto = new DirectoryNodeDto(0, InodeType.DIRECTORY, childrenUids, null);
        inodeTable.put(0, rootDto);

        IFsStateDto state = new FsStateDto(rootDto, 0, 1, inodeTable);

        service.save(state);

        //  Check that a file was created in the saves directory
        Path savesDir = Paths.get(tempDir.toString(), "saves");
        assertTrue(savesDir.toFile().exists(), "Saves directory should be created");

        File[] files = savesDir.toFile().listFiles((dir, name) -> name.startsWith("filesystemSaved_") && name.endsWith(".json"));
        assertNotNull(files);
        assertEquals(1, files.length, "Should have created one save file");
        assertTrue(files[0].getName().matches("filesystemSaved_\\d{4}-\\d{2}-\\d{2}_\\d{2}-\\d{2}-\\d{2}\\.json"));
    }

    @Test
    void testSaveAsCreatesFileAtSpecificLocation() {
        Map<Integer, InodeDto> inodeTable = new HashMap<>();
        Map<String, Integer> childrenUids = new HashMap<>();

        DirectoryNodeDto rootDto = new DirectoryNodeDto(0, InodeType.DIRECTORY, childrenUids, null);
        inodeTable.put(0, rootDto);

        IFsStateDto state = new FsStateDto(rootDto, 0, 1, inodeTable);
        File targetFile = new File(tempDir.toFile(), "custom_save.json");

        service.saveAs(state, targetFile);

        assertTrue(targetFile.exists(), "File should be created at specified location");
        assertTrue(targetFile.length() > 0, "File should not be empty");
    }

    @Test
    void testLoadThrowsExceptionForNonExistentFile() {
        String nonExistentFile = tempDir.resolve("nonexistent.json").toString();

        assertThrows(NoFilesystemSavedEx.class, () -> {
            service.load(nonExistentFile);
        }, "Should throw NoFilesystemSavedEx for non-existent file");
    }

    @Test
    void testSaveAndLoadRoundTrip() {
        Map<Integer, InodeDto> inodeTable = new HashMap<>();
        Map<String, Integer> rootChildren = new HashMap<>();

        // Root directory
        DirectoryNodeDto rootDto = new DirectoryNodeDto(0, InodeType.DIRECTORY, rootChildren, null);
        inodeTable.put(0, rootDto);

        // File node
        FileNodeDto fileDto = new FileNodeDto(1, InodeType.FILE);
        inodeTable.put(1, fileDto);
        rootChildren.put("test.txt", 1);

        // Soft link
        SoftLinkDto linkDto = new SoftLinkDto(2, InodeType.SOFTLINK, "/test.txt");
        inodeTable.put(2, linkDto);
        rootChildren.put("link", 2);

        IFsStateDto originalState = new FsStateDto(rootDto, 0, 3, inodeTable);

        File saveFile = new File(tempDir.toFile(), "test_save.json");

        // Act: Save and then load
        service.saveAs(originalState, saveFile);

        // Debug: Print the saved JSON
        try {
            String content = new String(Files.readAllBytes(saveFile.toPath()));
            System.out.println("Saved JSON:\n" + content);
        } catch (Exception e) {
            e.printStackTrace();
        }

        IFsStateDto loadedState = service.load(saveFile.getAbsolutePath());
        System.out.println(saveFile.getAbsolutePath());

        // Assert
        assertNotNull(loadedState, "Loaded state should not be null");
        assertEquals(originalState.getCurrentDirectoryUid(), loadedState.getCurrentDirectoryUid());
        assertEquals(originalState.getNextInodeId(), loadedState.getNextInodeId());
        assertEquals(originalState.getInodeTable().size(), loadedState.getInodeTable().size());

        // Verify root
        assertNotNull(loadedState.getRoot());
        assertEquals(0, loadedState.getRoot().getUid());

        // Verify file exists in inode table
        assertTrue(loadedState.getInodeTable().containsKey(1));
        InodeDto loadedFile = loadedState.getInodeTable().get(1);
        assertTrue(loadedFile instanceof IFileNodeDto);
        assertEquals(InodeType.FILE, loadedFile.getType());

        // Verify soft link
        assertTrue(loadedState.getInodeTable().containsKey(2));
        InodeDto loadedLink = loadedState.getInodeTable().get(2);
        assertTrue(loadedLink instanceof ISoftLinkDto);
        assertEquals("/test.txt", ((ISoftLinkDto) loadedLink).getTargetPath());
    }

    @Test
    void testMultipleSavesCreateMultipleFiles() throws InterruptedException {
        Map<Integer, InodeDto> inodeTable = new HashMap<>();
        Map<String, Integer> childrenUids = new HashMap<>();

        DirectoryNodeDto rootDto = new DirectoryNodeDto(0, InodeType.DIRECTORY, childrenUids, null);
        inodeTable.put(0, rootDto);

        IFsStateDto state = new FsStateDto(rootDto, 0, 1, inodeTable);

        //Save twice with a small delay to ensure different timestamps
        service.save(state);
        Thread.sleep(1100);
        service.save(state);

        Path savesDir = Paths.get(tempDir.toString(), "saves");
        File[] files = savesDir.toFile().listFiles((dir, name) -> name.startsWith("filesystemSaved_") && name.endsWith(".json"));
        assertNotNull(files);
        assertEquals(2, files.length, "Should have created two save files");
    }
}