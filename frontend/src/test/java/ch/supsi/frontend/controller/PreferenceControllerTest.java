package ch.supsi.frontend.controller;

import ch.supsi.fscli.frontend.controller.PreferenceController;
import ch.supsi.fscli.frontend.model.IPreferenceModel;
import javafx.scene.text.Font;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PreferenceControllerTest {

    @Mock
    private IPreferenceModel preferenceModel;

    private PreferenceController preferenceController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        preferenceController = new PreferenceController(preferenceModel);
    }


    @Test
    void testSetPreferences() {
        preferenceController.setPreferences("key", "value");
        verify(preferenceModel).setPreferences("key", "value");
    }

    @Test
    void testGetPreferences() {
        String key = "key";
        String expectedValue = "value";
        when(preferenceModel.getPreferences(key)).thenReturn(expectedValue);
        String result = preferenceController.getPreferences(key);
        assertEquals(expectedValue, result);
    }

    @Test
    void testGetFontsAndDimensions() {
        Font mockFont = Font.font("Arial", 12);
        when(preferenceModel.getCommandLineFont()).thenReturn(mockFont);
        when(preferenceModel.getOutputAreaFont()).thenReturn(mockFont);
        when(preferenceModel.getLogAreaFont()).thenReturn(mockFont);
        when(preferenceModel.getOutputAreaRow()).thenReturn(10);
        when(preferenceModel.getLogAreaRow()).thenReturn(5);
        when(preferenceModel.getColumn()).thenReturn(80);

        assertEquals(mockFont, preferenceController.getCommandLineFont());
        assertEquals(mockFont, preferenceController.getOutputAreaFont());
        assertEquals(mockFont, preferenceController.getLogAreaFont());
        assertEquals(10, preferenceController.getOutputAreaRow());
        assertEquals(5, preferenceController.getLogAreaRow());
        assertEquals(80, preferenceController.getColumn());
    }

    @Test
    void testSavePreferences() {
        // Parametri di test
        String lang = "en-US";
        String col = "80";
        String outRow = "10";
        String logRow = "5";
        String fontCL = "Arial";
        String fontOut = "Verdana";
        String fontLog = "Courier";

        preferenceController.savePreferences(
                lang, col, outRow, logRow,
                fontCL, fontOut, fontLog
        );

        verify(preferenceModel).savePreferences(
                lang, col, outRow, logRow,
                fontCL, fontOut, fontLog
        );
    }
}