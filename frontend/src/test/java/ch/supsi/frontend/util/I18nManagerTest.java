package ch.supsi.frontend.util;

import ch.supsi.fscli.frontend.model.TranslationModel;
import ch.supsi.fscli.frontend.util.I18nManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class I18nManagerTest {

    @Mock
    private TranslationModel translationModel;

    private I18nManager i18nManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        i18nManager = new I18nManager(translationModel);
    }

    @Test
    void testSetAndGetLocale() {
        Locale locale = new Locale("it-IT");

        i18nManager.setLocale(locale);
        assertEquals(locale, i18nManager.getLocale());
    }

    @Test
    void testGetStringFallbackToModel() {
        // setLocale deve essere chiamato per inizializzare il manager
        i18nManager.setLocale(new Locale("it-IT"));

        // Verifichiamo il fallback al Model (Backend)
        when(translationModel.getString("missing.key")).thenReturn("BackendTranslation");

        String result = i18nManager.getString("missing.key");
        assertEquals("BackendTranslation", result);
    }
}