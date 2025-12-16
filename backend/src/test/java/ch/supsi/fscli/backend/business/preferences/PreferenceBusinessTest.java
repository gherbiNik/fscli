package ch.supsi.fscli.backend.business.preferences;

import ch.supsi.fscli.backend.dataAccess.preferences.IPreferenceDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PreferenceBusinessTest {

    @Mock
    private IPreferenceDAO preferenceDAOMock;

    private PreferenceBusiness preferenceBusiness;
    private Properties mockProperties;

    @BeforeEach
    void setUp() {
        // Prepariamo le properties simulate che il DAO restituirà
        mockProperties = new Properties();
        mockProperties.setProperty("language-tag", "en_US");
        mockProperties.setProperty("font-command-line", "Arial");
        mockProperties.setProperty("output-area-row", "15");

        // Importante: PreferenceBusiness chiama getPreferences() nel costruttore.
        // Dobbiamo configurare il mock PRIMA di istanziare la classe sotto test.
        when(preferenceDAOMock.getPreferences()).thenReturn(mockProperties);

        preferenceBusiness = new PreferenceBusiness(preferenceDAOMock);
    }

    @Test
    void testGetCurrentLanguage() {
        assertEquals("en_US", preferenceBusiness.getCurrentLanguage());
    }

    @Test
    void testGetPreference_ExistingKey() {
        String value = preferenceBusiness.getPreference("language-tag");
        assertEquals("en_US", value);
    }

    @Test
    void testGetPreference_NonExistingKey() {
        String value = preferenceBusiness.getPreference("non-existing-key");
        assertNull(value);
    }

    @Test
    void testGetPreference_NullOrEmptyKey() {
        assertNull(preferenceBusiness.getPreference(null));
        assertNull(preferenceBusiness.getPreference(""));
    }

    @Test
    void testSetPreference() {
        String key = "new-key";
        String value = "new-value";

        preferenceBusiness.setPreference(key, value);

        // Verifichiamo che il metodo del DAO sia stato chiamato correttamente
        verify(preferenceDAOMock, times(1)).setPreference(key, value);
    }

    @Test
    void testGetUserPreferencesDirectoryPath() {
        Path mockPath = Path.of("/tmp/.fscli");
        when(preferenceDAOMock.getPreferencesDirectoryPath()).thenReturn(mockPath);

        assertEquals(mockPath, preferenceBusiness.getUserPreferencesDirectoryPath());
    }

    @Test
    void testGetMethodsDelegation() {
        // Testiamo che i metodi getter deleghino correttamente al DAO
        when(preferenceDAOMock.getCommandLineFont()).thenReturn("Consolas");
        when(preferenceDAOMock.getOutputAreaFont()).thenReturn("Courier");
        when(preferenceDAOMock.getLogAreaFont()).thenReturn("Times");
        when(preferenceDAOMock.getOutputAreaRow()).thenReturn(20);
        when(preferenceDAOMock.getLogAreaRow()).thenReturn(10);
        when(preferenceDAOMock.getColumn()).thenReturn(120);

        assertEquals("Consolas", preferenceBusiness.getCommandLineFont());
        assertEquals("Courier", preferenceBusiness.getOutputAreaFont());
        assertEquals("Times", preferenceBusiness.getLogAreaFont());
        assertEquals(20, preferenceBusiness.getOutputAreaRow());
        assertEquals(10, preferenceBusiness.getLogAreaRow());
        assertEquals(120, preferenceBusiness.getColumn());
    }
}