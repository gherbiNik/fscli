package ch.supsi.frontend.controller;

import ch.supsi.fscli.frontend.controller.PreferenceController;
import ch.supsi.fscli.frontend.model.IPreferenceModel;
import javafx.scene.text.Font;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class PreferenceControllerTest {

    @Mock
    private IPreferenceModel preferenceModel;

    private PreferenceController preferenceController;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        resetSingleton(PreferenceController.class, "instance");
        preferenceController = PreferenceController.getInstance(preferenceModel);
    }

    private void resetSingleton(Class<?> clazz, String fieldName) throws Exception {
        Field instance = clazz.getDeclaredField(fieldName);
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    void testSetPreferences() {
        preferenceController.setPreferences("key", "value");
        verify(preferenceModel).setPreferences("key", "value");
    }

    @Test
    void testGetPreferences() {
        when(preferenceModel.getPreferences("key")).thenReturn("value");
        String result = preferenceController.getPreferences("key");
        assertEquals("value", result);
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
        preferenceController.savePreferences(
                "en-US", "80", "10", "5",
                "Arial", "Verdana", "Courier"
        );
        verify(preferenceModel).savePreferences(
                "en-US", "80", "10", "5",
                "Arial", "Verdana", "Courier"
        );
    }
}