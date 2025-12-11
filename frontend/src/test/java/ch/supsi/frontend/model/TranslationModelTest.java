package ch.supsi.frontend.model;

import ch.supsi.fscli.frontend.model.TranslationModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class TranslationModelTest {

    @Mock
    private TranslationApplication translationApplication;

    private TranslationModel model;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        resetSingleton(TranslationModel.class, "instance");
        model = TranslationModel.getInstance(translationApplication);
    }

    private void resetSingleton(Class<?> clazz, String fieldName) throws Exception {
        Field instance = clazz.getDeclaredField(fieldName);
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    void testGetString() {
        when(translationApplication.getString("key")).thenReturn("translatedValue");
        assertEquals("translatedValue", model.getString("key"));
    }
}