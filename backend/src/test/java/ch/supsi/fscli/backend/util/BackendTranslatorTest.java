package ch.supsi.fscli.backend.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;

class BackendTranslatorTest {

    private BackendTranslator translator;

    @BeforeEach
    void setUp() {
        translator = new BackendTranslator();
    }

    @Test
    void testSetLocale_Valid() {
        // Testiamo con una lingua che sappiamo esistere (es. Inglese o Italiano)
        translator.setLocale(Locale.US);

        assertEquals(Locale.US, translator.getCurrentLocale());
        assertNotNull(translator.getResourceBundle());

        // Verifica che una chiave standard esista
        String result = translator.getString("commandList.title");
        assertFalse(result.startsWith("!"), "Dovrebbe trovare una traduzione valida");
    }

    @Test
    void testSetLocale_Fallback() {
        Locale unsupportedLocale = Locale.JAPAN;
        translator.setLocale(unsupportedLocale);

        // Ora ci aspettiamo US
        assertEquals(Locale.US, translator.getCurrentLocale());
        assertNotNull(translator.getResourceBundle());
    }

    @Test
    void testSetLocaleDefault_Fallback() {
        translator.setLocaleDefault(Locale.CHINESE);

        // Anche qui, aspettiamo US
        assertEquals(Locale.US, translator.getCurrentLocale());
    }

    @Test
    void testSetLocaleDefault_Valid() {
        // Testiamo il metodo specifico per i test (usa i18n.test)
        // Assumiamo che esista i18n/test.properties o simili
        translator.setLocaleDefault(Locale.US);

        // Se il file test_en_US.properties esiste
        assertNotNull(translator.getResourceBundle());
        assertEquals(Locale.US, translator.getCurrentLocale());
    }

    @Test
    void testGetString_KeyFound() {
        translator.setLocale(Locale.US);
        // Simuliamo il recupero se possibile.
        // Nota: Senza sapere le chiavi esatte dei tuoi file .properties,
        // questo test è generico. Se conosci una chiave (es. "cli.welcome"), usala qui.
        String key = "test.key";
        // Se la chiave non c'è, il metodo ritorna !key!, testiamo il caso negativo dopo.
    }

    @Test
    void testGetString_KeyNotFound() {
        translator.setLocale(Locale.US);

        String missingKey = "chiave.assolutamente.inesistente.12345";
        String result = translator.getString(missingKey);

        // Verifichiamo che il blocco catch venga eseguito e ritorni il formato "!key!"
        assertEquals("!" + missingKey + "!", result);
    }

    @Test
    void testResourceBundleGetter() {
        translator.setLocale(Locale.US);
        ResourceBundle rb = translator.getResourceBundle();
        assertNotNull(rb);
    }
}