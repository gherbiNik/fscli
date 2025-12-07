package ch.supsi.frontend.controller;

import ch.supsi.fscli.frontend.controller.mapper.FsStateMapperController;
import ch.supsi.fscli.frontend.model.filesystem.FileSystemModel;
import ch.supsi.fscli.frontend.model.mapper.IFsStateMapperModel;
import ch.supsi.fscli.frontend.view.LogView;
import ch.supsi.fscli.frontend.view.MenuBarView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.lang.reflect.Field;

import static org.mockito.Mockito.*;

class FsStateMapperControllerTest {

    @Mock
    private IFsStateMapperModel fsStateMapperModel;
    @Mock
    private FileSystemModel fileSystemModel;
    @Mock
    private LogView logView;
    @Mock
    private MenuBarView menuBarView;

    private FsStateMapperController controller;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        resetSingleton(FsStateMapperController.class, "instance");
        controller = FsStateMapperController.getInstance(fsStateMapperModel, fileSystemModel);
        controller.initialize(logView, menuBarView);
    }

    private void resetSingleton(Class<?> clazz, String fieldName) throws Exception {
        Field instance = clazz.getDeclaredField(fieldName);
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    void testSave() {
        controller.save();
        verify(fsStateMapperModel).save();
        verify(fileSystemModel).setDataToSave(false);
    }

    @Test
    void testOpen() {
        String fileName = "test.json";
        controller.open(fileName);
        verify(fsStateMapperModel).open(fileName);
        verify(fileSystemModel).setDataToSave(true);
    }

    @Test
    void testSaveAs() {
        File file = new File("test.json");
        controller.saveAs(file);
        verify(fsStateMapperModel).saveAs(file);
        verify(fileSystemModel).setDataToSave(false);
    }
}