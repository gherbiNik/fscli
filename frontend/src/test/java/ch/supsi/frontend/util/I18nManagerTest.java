package ch.supsi.frontend.util;

import ch.supsi.fscli.frontend.model.TranslationModel;
import ch.supsi.fscli.frontend.util.I18nManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class I18nManagerTest {

    @Mock
    private TranslationModel translationModel;

    private I18nManager i18nManager;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        resetSingleton(I18nManager.class, "instance");
        i18nManager = I18nManager.getInstance(translationModel);
    }

    private void resetSingleton(Class<?> clazz, String fieldName) throws Exception {
        Field instance = clazz.getDeclaredField(fieldName);
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    void testSetAndGetLocale() {
        Locale locale = new Locale("it-IT");

        i18nManager.setLocale(locale);
        assertEquals(locale, i18nManager.getLocale());
    }

    @Test
    void testGetStringFallbackToModel() {
        // Assuming the resource bundle does not have "missing.key"
        // setLocale must be called to load a bundle, using root for safety in test env
        i18nManager.setLocale(new Locale("it-IT"));

        when(translationModel.getString("missing.key")).thenReturn("BackendTranslation");

        String result = i18nManager.getString("missing.key");
        assertEquals("BackendTranslation", result);
    }

    @Test
    void testSingleton() {
        I18nManager instance2 = I18nManager.getInstance(translationModel);
        assertEquals(i18nManager, instance2);
    }
}
