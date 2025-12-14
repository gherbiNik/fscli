package ch.supsi.frontend.model;

import ch.supsi.fscli.backend.application.filesystem.IFileSystemApplication;
import ch.supsi.fscli.frontend.event.ExitAbortedEvent;
import ch.supsi.fscli.frontend.model.ExitModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.beans.PropertyChangeListener;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ExitModelTest {

    @Mock
    private IFileSystemApplication application;

    @Mock
    private PropertyChangeListener listener;

    private ExitModel exitModel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        exitModel = new ExitModel(application);
        exitModel.addPropertyChangeListener(listener);
    }

    @Test
    void testIsExitPossible_FileSystemNotCreated() {
        // FS non creato, dati da salvare false
        when(application.isFileSystemCreated()).thenReturn(false);
        when(application.isDataToSave()).thenReturn(false);


        boolean result = exitModel.isExitPossible();


        assertTrue(result, "L'uscita dovrebbe essere possibile se il FS non è creato");
        verify(listener, never()).propertyChange(any());
    }

    @Test
    void testIsExitPossible_FileSystemCreated_NoDataToSave() {
        // FS creato, ma tutto salvato
        when(application.isFileSystemCreated()).thenReturn(true);
        when(application.isDataToSave()).thenReturn(false);

        // Act
        boolean result = exitModel.isExitPossible();

        // Assert
        assertTrue(result, "L'uscita dovrebbe essere possibile se non ci sono dati da salvare");
        verify(listener, never()).propertyChange(any());
    }

    @Test
    void testIsExitPossible_FileSystemCreated_DataToSave() {
        // Arrange: FS creato E dati non salvati -> Uscita bloccata
        when(application.isFileSystemCreated()).thenReturn(true);
        when(application.isDataToSave()).thenReturn(true);

        // Act
        boolean result = exitModel.isExitPossible();

        // Assert
        assertFalse(result, "L'uscita NON dovrebbe essere possibile se ci sono dati non salvati");

        // Verifica che venga lanciato l'evento ExitAbortedEvent
        ArgumentCaptor<ExitAbortedEvent> captor = ArgumentCaptor.forClass(ExitAbortedEvent.class);
        verify(listener).propertyChange(captor.capture());

        // Verifica che la source dell'evento sia il model stesso
        assertTrue(captor.getValue().getSource() instanceof ExitModel);
    }
}