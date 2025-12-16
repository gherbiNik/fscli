package ch.supsi.fscli.backend.dataAccess;

import ch.supsi.fscli.backend.dataAccess.preferences.PreferenceDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class PreferenceDAOTest {

    @TempDir
    Path tempDir;

    private PreferenceDAO preferenceDAO;
    private final String originalUserHome = System.getProperty("user.home");

    @BeforeEach
    void setUp() {
        // Simuliamo la home directory in una cartella temporanea
        System.setProperty("user.home", tempDir.toString());
        preferenceDAO = new PreferenceDAO();
    }

    @AfterEach
    void tearDown() {
        // Ripristiniamo la user.home originale
        if (originalUserHome != null) {
            System.setProperty("user.home", originalUserHome);
        } else {
            System.clearProperty("user.home");
        }
    }

    @Test
    void testGetPreferences_CreatesDefaults_WhenNoFileExists() {
        // Action: Prima chiamata, non esiste nulla
        Properties prefs = preferenceDAO.getPreferences();

        Path preferencesDirectory = tempDir.resolve(".fscli");
        Path preferencesFile = preferencesDirectory.resolve("preferences.properties");

        // Assert: Deve aver creato cartella e file
        assertTrue(Files.exists(preferencesDirectory), "La directory .fscli dovrebbe essere creata");
        assertTrue(Files.exists(preferencesFile), "Il file preferences.properties dovrebbe essere creato");

        // Assert: Valori di default
        assertEquals("en_US", prefs.getProperty("language-tag"));
        assertEquals("80", prefs.getProperty("column"));
    }

    @Test
    void testGetPreferences_LoadsExisting_WhenFileExists() throws IOException {
        // Arrange: Creiamo manualmente un file di preferenze valido
        Path dir = tempDir.resolve(".fscli");
        Files.createDirectories(dir);
        Path file = dir.resolve("preferences.properties");

        Properties preExisting = new Properties();
        preExisting.setProperty("language-tag", "fr_CH");
        // Dobbiamo inserire TUTTE le chiavi, altrimenti il DAO resetta ai default
        preExisting.setProperty("column", "120");
        preExisting.setProperty("output-area-row", "15");
        preExisting.setProperty("log-area-row", "7");
        preExisting.setProperty("font-command-line", "Arial");
        preExisting.setProperty("font-output-area", "Arial");
        preExisting.setProperty("font-log-area", "Arial");

        try (FileOutputStream fos = new FileOutputStream(file.toFile())) {
            preExisting.store(fos, null);
        }

        // Act: Chiamiamo getPreferences su una nuova istanza (o la stessa, la logica ricarica se null, ma qui simula avvio)
        // Nota: Il DAO carica nel costruttore o alla prima chiamata lazy. Qui è lazy.
        Properties loaded = preferenceDAO.getPreferences();

        // Assert: Deve aver letto i valori su file, non i default
        assertEquals("fr_CH", loaded.getProperty("language-tag"));
        assertEquals("120", loaded.getProperty("column"));
    }

    @Test
    void testGetPreferences_ResetsToDefault_WhenKeysAreMissing() throws IOException {
        // Arrange: Creiamo un file con chiavi mancanti
        Path dir = tempDir.resolve(".fscli");
        Files.createDirectories(dir);
        Path file = dir.resolve("preferences.properties");

        Properties incomplete = new Properties();
        incomplete.setProperty("language-tag", "de_DE");
        // Manca "column", "output-area-row", ecc.

        try (FileOutputStream fos = new FileOutputStream(file.toFile())) {
            incomplete.store(fos, null);
        }

        // Act
        Properties loaded = preferenceDAO.getPreferences();

        // Assert
        // La logica di checkFileLoadedPreferences() dice: se manca qualcosa, resetta tutto ai default.
        // Quindi mi aspetto "en_US" (default) e non "de_DE".
        assertEquals("en_US", loaded.getProperty("language-tag"));
    }

    @Test
    void testNumericGetters_ReturnDefaults_OnInvalidFormat() {
        // Arrange: Carichiamo le preferenze e poi le corrompiamo in memoria
        preferenceDAO.getPreferences();
        preferenceDAO.setPreference("output-area-row", "NotANumber");
        preferenceDAO.setPreference("log-area-row", ""); // Vuoto
        preferenceDAO.setPreference("column", "   "); // Solo spazi

        // Act & Assert
        // isValueValidNumberFormat() dovrebbe ritornare false e il getter il valore di default

        // Default output-area-row è 10
        assertEquals(10, preferenceDAO.getOutputAreaRow());

        // Default log-area-row è 5
        assertEquals(5, preferenceDAO.getLogAreaRow());

        // Default column è 80
        assertEquals(80, preferenceDAO.getColumn());
    }

    @Test
    void testNumericGetters_ValidParsing_WithTrim() {
        preferenceDAO.getPreferences();
        preferenceDAO.setPreference("output-area-row", " 25 "); // Spazi da trimmare

        assertEquals(25, preferenceDAO.getOutputAreaRow());
    }

    @Test
    void testCreatePreferencesFile_Fails_WhenDirectoryBlocked() throws IOException {
        // Arrange: Blocchiamo la creazione della directory creando un FILE con lo stesso nome
        Path dir = tempDir.resolve(".fscli");
        Files.createFile(dir); // È un file, non una directory, quindi createDirectories fallirà o non funzionerà come atteso

        // Act
        Properties prefs = preferenceDAO.getPreferences();

        // Assert
        // Il metodo dovrebbe gestire l'eccezione stampando su stderr e ritornando i default in memoria
        assertNotNull(prefs);
        assertEquals("en_US", prefs.getProperty("language-tag"));

        // Verifica che il file properties NON sia stato creato dentro il file .fscli (impossibile)
        assertFalse(Files.exists(dir.resolve("preferences.properties")));
    }

    @Test
    void testSavePreferences_HandlesIOException() throws IOException {
        // Arrange
        preferenceDAO.getPreferences(); // Crea il file
        Path dir = tempDir.resolve(".fscli");
        Path file = dir.resolve("preferences.properties");

        // Rendiamo il file di sola lettura per forzare IOException in savePreferences
        File f = file.toFile();
        boolean isReadOnlySet = f.setWritable(false);

        if (isReadOnlySet) {
            // Act & Assert
            // Verifichiamo che non lanci eccezioni (il DAO cattura IOException e stampa su stderr)
            assertDoesNotThrow(() -> preferenceDAO.setPreference("language-tag", "es_ES"));
        } else {
            System.out.println("Skipping testSavePreferences_HandlesIOException: could not set file read-only on this OS.");
        }
    }

    @Test
    void testGetFonts_Defaults() {
        // Test di copertura per i getter dei font se la property manca o è null
        preferenceDAO.getPreferences().remove("font-command-line");
        assertEquals("System", preferenceDAO.getCommandLineFont());
    }
}