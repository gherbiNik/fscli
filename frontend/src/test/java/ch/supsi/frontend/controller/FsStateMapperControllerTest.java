package ch.supsi.frontend.controller;

import ch.supsi.fscli.frontend.controller.mapper.FsStateMapperController;
import ch.supsi.fscli.frontend.model.filesystem.IFileSystemModel;
import ch.supsi.fscli.frontend.model.mapper.IFsStateMapperModel;
import ch.supsi.fscli.frontend.view.LogView;
import ch.supsi.fscli.frontend.view.MenuBarView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.google.inject.Provider;

import java.io.File;

import static org.mockito.Mockito.*;

class FsStateMapperControllerTest {

    @Mock
    private IFsStateMapperModel fsStateMapperModel;
    @Mock
    private IFileSystemModel fileSystemModel;
    @Mock
    private LogView logView;
    @Mock
    private MenuBarView menuBarView;
    // Mockiamo il Provider, che Guice inietterà nel setter.
    @Mock
    private Provider<MenuBarView> menuBarViewProvider;

    private FsStateMapperController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        controller = new FsStateMapperController(fsStateMapperModel, fileSystemModel, logView);

        controller.setMenuBarView(menuBarViewProvider);
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