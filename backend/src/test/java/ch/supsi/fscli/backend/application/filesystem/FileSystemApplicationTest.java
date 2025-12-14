package ch.supsi.fscli.backend.application.filesystem;

import ch.supsi.fscli.backend.business.command.business.CommandExecutor;
import ch.supsi.fscli.backend.business.command.business.CommandLoader;
import ch.supsi.fscli.backend.business.filesystem.IFileSystem;
import ch.supsi.fscli.backend.util.BackendTranslator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FileSystemApplicationTest {

    @Mock private IFileSystem fileSystem;
    @Mock private CommandExecutor commandExecutor;
    @Mock private CommandLoader commandLoader;
    @Mock private BackendTranslator backendTranslator;

    private FileSystemApplication app;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Utilizziamo la Constructor Injection con i 4 mock
        app = new FileSystemApplication(fileSystem, commandExecutor, commandLoader, backendTranslator);
    }

    @Test
    @DisplayName("Should create file system")
    void testCreateFileSystem() {
        // Verifica che createFileSystem deleghi correttamente al metodo create() del Business Layer
        assertDoesNotThrow(app::createFileSystem);
        verify(fileSystem).create();
    }

    @Test
    @DisplayName("Created file system should be accessible")
    void testFileSystemAccessibility() {

        // Verifichiamo il funzionamento della delega valida: isDataToSave()
        when(fileSystem.isDataToSave()).thenReturn(false);
        assertFalse(app.isDataToSave());

        // Verifichiamo la creazione
        app.createFileSystem();

        // Verifichiamo che la delega al reset sia avvenuta
        verify(fileSystem).create();

        // Eseguiamo un'ultima verifica di delega:
        when(fileSystem.isDataToSave()).thenReturn(true);
        assertTrue(app.isDataToSave());
    }
}