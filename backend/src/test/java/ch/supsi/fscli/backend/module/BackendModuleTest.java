package ch.supsi.fscli.backend.module;

import ch.supsi.fscli.backend.business.command.business.CommandLoader;
import ch.supsi.fscli.backend.business.command.commands.ICommand;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BackendModuleTest {

    @TempDir
    Path tempDir;

    private final String originalUserHome = System.getProperty("user.home");

    @BeforeEach
    void setUp() {
        // Impostiamo una user.home temporanea.
        // Questo è CRUCIALE perché il modulo creerà PreferenceBusiness -> PreferenceDAO
        // e non vogliamo che PreferenceDAO scriva nella tua vera home directory durante il test.
        System.setProperty("user.home", tempDir.toString());
    }

    @AfterEach
    void tearDown() {
        if (originalUserHome != null) {
            System.setProperty("user.home", originalUserHome);
        } else {
            System.clearProperty("user.home");
        }
    }

    @Test
    void testConfigure_BindingsAreValid() {
        // Questo test verifica che il metodo configure() venga eseguito e che Guice riesca a validare i collegamenti.
        // Creando l'injector, Guice istanzia i Singleton "Eager" (come BackendTranslator).
        // Se mancano file di properties o altro, qui potrebbe fallire
        assertDoesNotThrow(() -> {
            Injector injector = Guice.createInjector(new BackendModule());
            assertNotNull(injector);
        }, "La creazione dell'injector non dovrebbe lanciare eccezioni");
    }

    @Test
    void testProvideCommandList() {
        // Testiamo specificamente il metodo @Provides
        BackendModule module = new BackendModule();
        CommandLoader mockLoader = Mockito.mock(CommandLoader.class);

        // Configuriamo il mock
        when(mockLoader.loadCommands()).thenReturn(Collections.emptyList());

        // Chiamiamo il metodo
        List<ICommand> result = module.provideCommandList(mockLoader);

        // Verifiche
        assertNotNull(result);
        verify(mockLoader).loadCommands();
    }
}