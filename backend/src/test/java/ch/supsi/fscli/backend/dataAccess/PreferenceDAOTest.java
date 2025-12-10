package ch.supsi.fscli.backend.dataAccess;

import ch.supsi.fscli.backend.dataAccess.preferences.PreferenceDAO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class PreferenceDAOTest {

    @TempDir
    Path tempDir;

    private PreferenceDAO preferenceDAO;
    // Salviamo la user.home originale per ripristinarla alla fine (buona pratica!)
    private final String originalUserHome = System.getProperty("user.home");

    @BeforeEach
    void setUp() {
        // 1. Impostiamo l'ambiente (System Property) PRIMA di creare l'oggetto.
        // PreferenceDAO legge "user.home" quando viene istanziato.
        System.setProperty("user.home", tempDir.toString());

        // 2. Creazione semplice (Manual Injection dell'ambiente tramite System props)
        preferenceDAO = new PreferenceDAO();
    }

    @AfterEach
    void tearDown() {
        // 3. Ripristino dell'ambiente pulito
        if (originalUserHome != null) {
            System.setProperty("user.home", originalUserHome);
        } else {
            System.clearProperty("user.home");
        }
    }

    // VIA resetSingletonState() -> Non serve più!

    @Test
    void getPreferences_ShouldCreateFilesAndReturnDefaults_WhenNoneExist() {
        // Action
        Properties prefs = preferenceDAO.getPreferences();

        // Assertions
        Path preferencesDirectory = tempDir.resolve(".fscli");
        Path preferencesFile = preferencesDirectory.resolve("preferences.properties");

        assertTrue(Files.exists(preferencesDirectory), "Preferences directory should be created.");
        assertTrue(Files.exists(preferencesFile), "Preferences file should be created.");

        assertEquals("it-IT", prefs.getProperty("language-tag"));
        assertEquals("80", prefs.getProperty("column"));
    }

    @Test
    void getPreferencesAndSetPreferencesTest() {
        // Setup iniziale
        preferenceDAO.getPreferences(); // Assicura che il file sia creato

        // Modifica preferenze
        preferenceDAO.setPreference("language-tag", "en-US");
        preferenceDAO.setPreference("column", "100");

        // Verifica su file
        try (FileInputStream fis = new FileInputStream(preferenceDAO.getUserPreferencesFilePath().toFile())) {
            Properties preferences = new Properties();
            preferences.load(fis);

            // Verifichiamo che le proprietà nel file siano quelle modificate
            assertEquals("en-US", preferences.getProperty("language-tag"));
            assertEquals("100", preferences.getProperty("column"));

        } catch (IOException e) {
            fail("Failed to read preferences file: " + e.getMessage());
        }
    }
}