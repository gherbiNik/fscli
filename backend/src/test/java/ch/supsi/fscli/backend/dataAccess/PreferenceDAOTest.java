package ch.supsi.fscli.backend.dataAccess;

import ch.supsi.fscli.backend.dataAccess.preferences.PreferenceDAO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.lang.reflect.Field;
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
    void setUp() throws Exception {
        preferenceDAO = PreferenceDAO.getInstance();

        // Essendo un singleton va cambiato ogni volta la userHomeDirectory con la cartella temporanea
        // così non modifico il vero file
        Field preferencesField = PreferenceDAO.class.getDeclaredField("userHomeDirectory");
        preferencesField.setAccessible(true);
        preferencesField.set(null, tempDir.toString());
    }

    @AfterEach
    void tearDown() throws Exception {
        resetSingletonState();
        System.setProperty("user.home", originalUserHome);
    }

    private void resetSingletonState() throws Exception {
        // Resetta l'istanza del DAO
        Field instanceField = PreferenceDAO.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        // Resetta le preferenze caricate
        Field preferencesField = PreferenceDAO.class.getDeclaredField("preferences");
        preferencesField.setAccessible(true);
        preferencesField.set(preferenceDAO, null);

    }

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

        Properties newPreferences = preferenceDAO.getPreferences();
        // Cloniamo per sicurezza, anche se getPreferences() dovrebbe caricare
        Properties originalPreferences = (Properties) newPreferences.clone();


        preferenceDAO.setPreference("language-tag", "en-US");
        preferenceDAO.setPreference("column", "100");

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