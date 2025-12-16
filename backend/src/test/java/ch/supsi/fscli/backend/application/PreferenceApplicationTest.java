package ch.supsi.fscli.backend.application;

import ch.supsi.fscli.backend.business.preferences.IPreferenceBusiness;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PreferenceApplicationTest {

    @Mock
    private IPreferenceBusiness preferenceBusiness;

    @InjectMocks
    private PreferenceApplication preferenceApplication;

    @Test
    void testGetPreference() {
        String key = "myKey";
        String expectedValue = "myValue";
        when(preferenceBusiness.getPreference(key)).thenReturn(expectedValue);

        String result = preferenceApplication.getPreference(key);

        assertEquals(expectedValue, result);
        verify(preferenceBusiness).getPreference(key);
    }

    @Test
    void testSetPreference() {
        String key = "key";
        String value = "value";
        preferenceApplication.setPreference(key, value);
        verify(preferenceBusiness).setPreference(key, value);
    }

    @Test
    void testGetUserPreferencesDirectoryPath() {
        Path mockPath = Path.of("some/path");
        when(preferenceBusiness.getUserPreferencesDirectoryPath()).thenReturn(mockPath);

        assertEquals(mockPath, preferenceApplication.getUserPreferencesDirectoryPath());
        verify(preferenceBusiness).getUserPreferencesDirectoryPath();
    }

    @Test
    void testLoadLanguagePreference() {
        // Simuliamo che nel file di configurazione ci sia "en"
        when(preferenceBusiness.getPreference("language-tag")).thenReturn("en");

        Locale result = preferenceApplication.loadLanguagePreference();

        // Verifichiamo che venga convertito correttamente in un oggetto Locale
        assertEquals(new Locale("en"), result);
        assertEquals("en", result.getLanguage());
    }

    @Test
    void testSaveLanguagePreference() {
        // Passiamo un Locale (es. ITALIANO)
        Locale locale = Locale.ITALIAN;

        preferenceApplication.saveLanguagePreference(locale);

        // Verifichiamo che al business layer venga passata la stringa "it"
        verify(preferenceBusiness).setPreference("language-tag", "it");
    }

    @Test
    void testGettersDelegation() {
        // Test cumulativo per i getter semplici di delega
        when(preferenceBusiness.getCommandLineFont()).thenReturn("Arial");
        when(preferenceBusiness.getLogAreaFont()).thenReturn("Times");
        when(preferenceBusiness.getOutputAreaFont()).thenReturn("Verdana");
        when(preferenceBusiness.getOutputAreaRow()).thenReturn(10);
        when(preferenceBusiness.getLogAreaRow()).thenReturn(5);
        when(preferenceBusiness.getColumn()).thenReturn(80);

        assertEquals("Arial", preferenceApplication.getCommandLineFont());
        assertEquals("Times", preferenceApplication.getLogAreaFont());
        assertEquals("Verdana", preferenceApplication.getOutputAreaFont());
        assertEquals(10, preferenceApplication.getOutputAreaRow());
        assertEquals(5, preferenceApplication.getLogAreaRow());
        assertEquals(80, preferenceApplication.getColumn());
    }
}