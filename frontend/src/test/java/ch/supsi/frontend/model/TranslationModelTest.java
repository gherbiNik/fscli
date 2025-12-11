package ch.supsi.frontend.model;

import ch.supsi.fscli.backend.util.BackendTranslator;
import ch.supsi.fscli.frontend.model.TranslationModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class TranslationModelTest {

    @Mock
    private BackendTranslator backendTranslator;

    private TranslationModel model;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        model = new TranslationModel(backendTranslator);
    }

    @Test
    void testGetString() {
        // FIX 3: Verifichiamo che la chiamata venga delegata al BackendTranslator
        when(backendTranslator.getString("key")).thenReturn("translatedValue");
        assertEquals("translatedValue", model.getString("key"));
    }
}