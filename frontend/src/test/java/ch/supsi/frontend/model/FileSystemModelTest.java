package ch.supsi.frontend.model;

import ch.supsi.fscli.backend.application.filesystem.IFileSystemApplication;
import ch.supsi.fscli.frontend.event.ClearEvent;
import ch.supsi.fscli.frontend.event.FileSystemCreationEvent;
import ch.supsi.fscli.frontend.event.FileSystemToSaved;
import ch.supsi.fscli.frontend.event.OutputEvent;
import ch.supsi.fscli.frontend.model.filesystem.FileSystemModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.beans.PropertyChangeListener;
// Rimosso import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class FileSystemModelTest {

    @Mock
    private IFileSystemApplication fileSystemApplication;
    @Mock
    private PropertyChangeListener listener;

    private FileSystemModel model;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        model = new FileSystemModel(fileSystemApplication);

        // Il wiring del listener resta manuale, perché non è gestito da Guice
        model.addPropertyChangeListener(listener);
    }


    @Test
    void testCreateFileSystem() {
        model.createFileSystem();
        verify(fileSystemApplication).createFileSystem();

        ArgumentCaptor<FileSystemCreationEvent> captor = ArgumentCaptor.forClass(FileSystemCreationEvent.class);
        verify(listener).propertyChange(captor.capture());
    }

    @Test
    void testSendCommandSuccess() {
        when(fileSystemApplication.sendCommand(anyString())).thenReturn("Success output");

        String result = model.sendCommand("ls");

        assertEquals("Success output", result);

        // Verify FileSystemToSaved event (since result has no ERROR-)
        ArgumentCaptor<FileSystemToSaved> saveCaptor = ArgumentCaptor.forClass(FileSystemToSaved.class);
        verify(listener, atLeastOnce()).propertyChange(saveCaptor.capture());


        // Verify OutputEvent
        ArgumentCaptor<OutputEvent> outputCaptor = ArgumentCaptor.forClass(OutputEvent.class);
        verify(listener, atLeastOnce()).propertyChange(outputCaptor.capture());

    }

    @Test
    void testSendCommandError() {
        when(fileSystemApplication.sendCommand(anyString())).thenReturn("ERROR-Command failed");

        String result = model.sendCommand("badCmd");

        assertEquals("Command failed", result); // ERROR- is stripped

        // Should NOT fire FileSystemToSaved
        verify(listener, never()).propertyChange(any(FileSystemToSaved.class));

        // Should fire OutputEvent
        ArgumentCaptor<OutputEvent> outputCaptor = ArgumentCaptor.forClass(OutputEvent.class);
        verify(listener).propertyChange(outputCaptor.capture());
        assertTrue(((String)outputCaptor.getValue().getNewValue()).contains("Command failed"));
    }

    @Test
    void testSendCommandClear() {
        when(fileSystemApplication.sendCommand("clear")).thenReturn("Perform Clear");

        model.sendCommand("clear");

        // Should fire ClearEvent
        ArgumentCaptor<ClearEvent> captor = ArgumentCaptor.forClass(ClearEvent.class);
        verify(listener, atLeastOnce()).propertyChange(captor.capture());

    }

    @Test
    void testDataToSave() {
        when(fileSystemApplication.isDataToSave()).thenReturn(true);
        assertTrue(model.isDataToSave());

        model.setDataToSave(false);
        verify(fileSystemApplication).setDataToSave(false);
    }
}