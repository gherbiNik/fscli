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
import java.io.IOException;
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
    void setUp() {
        // 1. Mock delle dipendenze
        preferenceDAO = mock(PreferenceDAO.class);

        // Configuriamo il mock per restituire un percorso valido nella cartella temporanea
        // Simula la posizione del file preferences.properties
        when(preferenceDAO.getUserPreferencesFilePath()).thenReturn(tempDir.resolve("preferences.properties"));

        // 2. Creazione dell'istanza da testare
        service = new JacksonSaveDataService(preferenceDAO);
    }

    // Helper per creare un DTO valido senza ripetere codice
    private IFsStateDto createDummyState() {
        Map<Integer, InodeDto> inodeTable = new HashMap<>();
        Map<String, Integer> childrenUids = new HashMap<>();
        DirectoryNodeDto rootDto = new DirectoryNodeDto(0, InodeType.DIRECTORY, childrenUids, null);
        inodeTable.put(0, rootDto);
        return new FsStateDto(rootDto, 0, 1, inodeTable);
    }

    @Test
    void testSaveCreatesFileWithTimestamp() {
        IFsStateDto state = createDummyState();

        service.save(state);

        // Check that a file was created in the saves directory
        Path savesDir = Paths.get(tempDir.toString(), "saves");
        assertTrue(savesDir.toFile().exists(), "Saves directory should be created");

        File[] files = savesDir.toFile().listFiles((dir, name) -> name.startsWith("filesystemSaved_") && name.endsWith(".json"));
        assertNotNull(files);
        assertEquals(1, files.length, "Should have created exactly one save file");

        // Verifica il pattern del nome file
        assertTrue(files[0].getName().matches("filesystemSaved_\\d{4}-\\d{2}-\\d{2}_\\d{2}-\\d{2}-\\d{2}\\.json"));

        // Verifica che il servizio abbia aggiornato il file corrente
        assertEquals(files[0].getAbsolutePath(), service.getCurrentFileAbsolutePath());
    }

    @Test
    void testSaveAsCreatesFileAtSpecificLocation() {
        IFsStateDto state = createDummyState();
        File targetFile = new File(tempDir.toFile(), "custom_save.json");

        service.saveAs(state, targetFile);

        assertTrue(targetFile.exists(), "File should be created at specified location");
        assertTrue(targetFile.length() > 0, "File should not be empty");

        // Verifica che il saveAs abbia aggiornato il file corrente
        assertEquals(targetFile.getAbsolutePath(), service.getCurrentFileAbsolutePath());
    }

    @Test
    void testMultipleSavesOverwriteCurrentFile() throws InterruptedException {
        IFsStateDto state = createDummyState();

        // 1. Primo salvataggio (crea nuovo file timestampato)
        service.save(state);

        Path savesDir = Paths.get(tempDir.toString(), "saves");
        File[] filesAfterFirstSave = savesDir.toFile().listFiles((dir, name) -> name.startsWith("filesystemSaved_"));
        assertNotNull(filesAfterFirstSave);
        assertEquals(1, filesAfterFirstSave.length);
        File firstFile = filesAfterFirstSave[0];
        long lastModifiedFirst = firstFile.lastModified();

        // Piccolo sleep per garantire che il timestamp di modifica del file cambi se il sistema è veloce
        Thread.sleep(100);

        // 2. Secondo salvataggio (dovrebbe sovrascrivere lo stesso file perché currentFile è settato)
        service.save(state);

        File[] filesAfterSecondSave = savesDir.toFile().listFiles((dir, name) -> name.startsWith("filesystemSaved_"));
        assertNotNull(filesAfterSecondSave);

        // Assert: Deve esserci ancora solo 1 file
        assertEquals(1, filesAfterSecondSave.length, "Should not create a new file, but overwrite the existing one");
        assertEquals(firstFile.getName(), filesAfterSecondSave[0].getName(), "Filename should remain the same");

        // Verifica opzionale: il file è stato effettivamente toccato?
        assertTrue(filesAfterSecondSave[0].lastModified() >= lastModifiedFirst, "File should have been modified/overwritten");
    }

    @Test
    void testLoadUpdatesCurrentFile() throws IOException {
        // Scenario: Carico un file esistente, poi faccio save().
        // Deve sovrascrivere quel file, non crearne uno nuovo con timestamp.

        // Setup: Creo manualmente un file JSON valido
        File existingSave = new File(tempDir.toFile(), "existing.json");
        IFsStateDto originalState = createDummyState();
        service.saveAs(originalState, existingSave); // Uso saveAs per comodità per creare il file iniziale

        // Re-inizializzo il service per simulare un riavvio dell'applicazione (currentFile = null)
        service = new JacksonSaveDataService(preferenceDAO);

        // Act 1: Load
        IFsStateDto loadedState = service.load(existingSave.getAbsolutePath());
        assertNotNull(loadedState);

        // Assert 1: Il load deve aver impostato il currentFile
        assertEquals(existingSave.getAbsolutePath(), service.getCurrentFileAbsolutePath());

        // Act 2: Save successivo
        service.save(loadedState);

        // Assert 2: Non devono essere stati creati file timestampati nella cartella saves (se la cartella saves non esisteva nemmeno)
        // O comunque il file 'existing.json' deve essere stato aggiornato.
        Path savesDir = Paths.get(tempDir.toString(), "saves");
        if (Files.exists(savesDir)) {
            File[] timestampedFiles = savesDir.toFile().listFiles((dir, name) -> name.startsWith("filesystemSaved_"));
            if (timestampedFiles != null) {
                assertEquals(0, timestampedFiles.length, "Save after Load should NOT create a new timestamped file");
            }
        }

        assertTrue(existingSave.exists());
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

        IFsStateDto loadedState = service.load(saveFile.getAbsolutePath());

        // Assert
        assertNotNull(loadedState, "Loaded state should not be null");
        assertEquals(originalState.getCurrentDirectoryUid(), loadedState.getCurrentDirectoryUid());

        // Verify root structure
        assertNotNull(loadedState.getRoot());
        assertEquals(0, loadedState.getRoot().getUid());

        // Verify content types
        InodeDto loadedFile = loadedState.getInodeTable().get(1);
        assertInstanceOf(IFileNodeDto.class, loadedFile);

        InodeDto loadedLink = loadedState.getInodeTable().get(2);
        assertInstanceOf(ISoftLinkDto.class, loadedLink);
        assertEquals("/test.txt", ((ISoftLinkDto) loadedLink).getTargetPath());
    }
}