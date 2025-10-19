package ch.supsi.fscli.backend.dataAccess;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class PreferenceDAOTest {

    @TempDir
    Path tempDir; // JUnit automatically provides and cleans up this directory

    private PreferenceDAO preferenceDAO;

    private final String originalUserHome = System.getProperty("user.home");

    @BeforeEach
    void setUp() {
        // Temporarily set the 'user.home' property to our temp directory for this test
        System.setProperty("user.home", tempDir.toString());

        preferenceDAO = new PreferenceDAO();
    }

    @AfterEach
    void tearDown() throws Exception {
        // Reset the static 'preferences' field in PreferenceDAO to null
        // This forces the DAO to reload from the file in the next test
        Field preferencesField = PreferenceDAO.class.getDeclaredField("preferences");
        preferencesField.setAccessible(true);
        preferencesField.set(null, null);

        // It's also good practice to restore the original system property
        System.setProperty("user.home", originalUserHome);
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
    void getPreferences_ShouldLoadExistingPreferencesFromFile() throws IOException {
        // Arrange: Manually create a properties file with custom values.
        Path preferencesDirectory = tempDir.resolve(".fscli");
        Files.createDirectories(preferencesDirectory);
        Path preferencesFile = preferencesDirectory.resolve("preferences.properties");

        Properties customPrefs = new Properties();
        customPrefs.setProperty("language-tag", "en-US");
        customPrefs.setProperty("column", "100");
        try (FileOutputStream fos = new FileOutputStream(preferencesFile.toFile())) {
            customPrefs.store(fos, "Test preferences");
        }

        // Action
        Properties loadedPrefs = preferenceDAO.getPreferences();

        // Assertions
        assertEquals("en-US", loadedPrefs.getProperty("language-tag"));
        assertEquals("100", loadedPrefs.getProperty("column"));
        assertNull(loadedPrefs.getProperty("font-log-area"), "Default value should not be present unless specified.");
    }

    @Test
    void setPreference_ShouldUpdateAndSavePreferenceToFile() throws IOException {
        // Arrange: Get the initial default preferences, which also creates the file.
        preferenceDAO.getPreferences();

        // Action
        preferenceDAO.setPreference("font-log-area", "Consolas");
        System.out.println(preferenceDAO.getPreferences());

        // Assert: Read the file back from disk to verify it was saved correctly.
        Path preferencesFile = tempDir.resolve(".fscli").resolve("preferences.properties");
        Properties savedPrefs = new Properties();
        try (FileInputStream fis = new FileInputStream(preferencesFile.toFile())) {
            savedPrefs.load(fis);
        }

        assertEquals("Consolas", savedPrefs.getProperty("font-log-area"));
        assertEquals("80", savedPrefs.getProperty("column"), "Other properties should remain unchanged.");
    }
}