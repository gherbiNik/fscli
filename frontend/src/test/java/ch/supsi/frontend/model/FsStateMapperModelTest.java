package ch.supsi.frontend.model;

import ch.supsi.fscli.backend.application.filesystem.IFileSystemApplication;
import ch.supsi.fscli.backend.application.mapper.IFsStateMapperApplication;
import ch.supsi.fscli.frontend.event.FileSystemOpenEvent;
import ch.supsi.fscli.frontend.event.FileSystemSaved;
import ch.supsi.fscli.frontend.event.FileSystemSavedAs;
import ch.supsi.fscli.frontend.model.mapper.FsStateMapperModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.beans.PropertyChangeListener;
import java.io.File;
// Rimuoviamo l'import java.lang.reflect.Field;

import static org.mockito.Mockito.verify;

class FsStateMapperModelTest {

    @Mock
    private IFsStateMapperApplication mapperApplication;
    @Mock
    private PropertyChangeListener listener;
    @Mock
    private IFileSystemApplication fileSystemApplication;

    private FsStateMapperModel model;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        model = new FsStateMapperModel(mapperApplication, fileSystemApplication);

        // Il wiring del listener resta manuale
        model.addPropertyChangeListener(listener);
    }

    @Test
    void testSave() {
        model.save();
        verify(mapperApplication).toDTO();

        ArgumentCaptor<FileSystemSaved> captor = ArgumentCaptor.forClass(FileSystemSaved.class);
        verify(listener).propertyChange(captor.capture());

    }

    @Test
    void testOpen() {
        String filename = "data.json";
        model.open(filename);
        verify(mapperApplication).fromDTO(filename);

        ArgumentCaptor<FileSystemOpenEvent> captor = ArgumentCaptor.forClass(FileSystemOpenEvent.class);
        verify(listener).propertyChange(captor.capture());

    }

    @Test
    void testSaveAs() {
        File file = new File("data.json");
        model.saveAs(file);
        verify(mapperApplication).toDTOas(file);

        ArgumentCaptor<FileSystemSavedAs> captor = ArgumentCaptor.forClass(FileSystemSavedAs.class);
        verify(listener).propertyChange(captor.capture());
    }
}