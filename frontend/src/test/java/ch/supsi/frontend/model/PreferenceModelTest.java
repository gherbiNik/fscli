package ch.supsi.frontend.model;

import ch.supsi.fscli.backend.application.IPreferenceApplication;
import ch.supsi.fscli.frontend.event.PreferenceSavedEvent;
import ch.supsi.fscli.frontend.model.PreferenceModel;
import javafx.scene.text.Font;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.beans.PropertyChangeListener;
// Rimuoviamo l'import java.lang.reflect.Field;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PreferenceModelTest {

    @Mock
    private IPreferenceApplication preferenceApplication;
    @Mock
    private PropertyChangeListener propertyChangeListener;

    private PreferenceModel preferenceModel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        preferenceModel = new PreferenceModel(preferenceApplication);

        // Il wiring del listener resta manuale
        preferenceModel.addPropertyChangeListener(propertyChangeListener);
    }

    @Test
    void testDelegationToApplication() {
        when(preferenceApplication.getPreference("k")).thenReturn("v");
        assertEquals("v", preferenceModel.getPreferences("k"));

        preferenceModel.setPreferences("k2", "v2");
        verify(preferenceApplication).setPreference("k2", "v2");

        Path path = Path.of("test/path");
        when(preferenceApplication.getUserPreferencesDirectoryPath()).thenReturn(path);
        assertEquals(path, preferenceModel.getUserPreferencesDirectoryPath());
    }

    @Test
    void testFontRetrieval() {
        // Recuperiamo il nome del font di default del sistema su cui sta girando il test.
        // In CI questo sarà probabilmente "System Regular", su Windows "Segoe UI", ecc.
        String safeFont = Font.getDefault().getName();

        // Istruiamo il mock per restituire questo nome "sicuro"
        when(preferenceApplication.getCommandLineFont()).thenReturn(safeFont);
        when(preferenceApplication.getOutputAreaFont()).thenReturn(safeFont);
        when(preferenceApplication.getLogAreaFont()).thenReturn(safeFont);

        // Testiamo
        Font cmdFont = preferenceModel.getCommandLineFont();
        assertEquals(safeFont, cmdFont.getName());

        Font outFont = preferenceModel.getOutputAreaFont();
        assertEquals(safeFont, outFont.getName());

        Font logFont = preferenceModel.getLogAreaFont();
        assertEquals(safeFont, logFont.getName());
    }

    @Test
    void testNumericPreferences() {
        when(preferenceApplication.getOutputAreaRow()).thenReturn(10);
        when(preferenceApplication.getLogAreaRow()).thenReturn(5);
        when(preferenceApplication.getColumn()).thenReturn(80);

        assertEquals(10, preferenceModel.getOutputAreaRow());
        assertEquals(5, preferenceModel.getLogAreaRow());
        assertEquals(80, preferenceModel.getColumn());
    }

    @Test
    void testSavePreferencesFiresEvent() {
        preferenceModel.savePreferences(
                "en", "100", "20", "15",
                "F1", "F2", "F3"
        );

        verify(preferenceApplication).setPreference("language-tag", "en");
        verify(preferenceApplication).setPreference("column", "100");
        verify(preferenceApplication).setPreference("output-area-row", "20");
        verify(preferenceApplication).setPreference("log-area-row", "15");
        verify(preferenceApplication).setPreference("font-command-line", "F1");
        verify(preferenceApplication).setPreference("font-output-area", "F2");
        verify(preferenceApplication).setPreference("font-log-area", "F3");

        ArgumentCaptor<PreferenceSavedEvent> captor = ArgumentCaptor.forClass(PreferenceSavedEvent.class);
        verify(propertyChangeListener).propertyChange(captor.capture());
    }
}