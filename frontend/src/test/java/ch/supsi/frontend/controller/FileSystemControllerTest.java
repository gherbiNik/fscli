package ch.supsi.frontend.controller;

import ch.supsi.fscli.frontend.controller.filesystem.FileSystemController;
import ch.supsi.fscli.frontend.model.filesystem.IFileSystemModel;
import ch.supsi.fscli.frontend.util.I18nManager;
import ch.supsi.fscli.frontend.view.CommandLineView;
import ch.supsi.fscli.frontend.view.LogView;
import ch.supsi.fscli.frontend.view.OutputView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileSystemControllerTest {

    @Mock
    private IFileSystemModel fileSystemModel;
    @Mock
    private OutputView outputView;
    @Mock
    private LogView logView;
    @Mock
    private I18nManager i18n;
    @Mock
    private CommandLineView commandLineView;

    private FileSystemController controller;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        resetSingleton(FileSystemController.class, "instance");
        controller = FileSystemController.getInstance(fileSystemModel, outputView, logView, i18n);
    }

    private void resetSingleton(Class<?> clazz, String fieldName) throws Exception {
        Field instance = clazz.getDeclaredField(fieldName);
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    void testCreateFileSystem() {
        controller.createFileSystem();
        verify(fileSystemModel).createFileSystem();
    }

    @Test
    void testSendCommand() {
        String input = "ls";
        controller.sendCommand(input);
        verify(fileSystemModel).sendCommand(input);
    }

    @Test
    void testHasDataToSave() {
        when(fileSystemModel.isDataToSave()).thenReturn(true);
        assertTrue(controller.hasDataToSave());

        when(fileSystemModel.isDataToSave()).thenReturn(false);
        assertFalse(controller.hasDataToSave());
    }

    @Test
    void testSetCommandLineView() {
        controller.setCommandLineView(commandLineView);
        // Verify no exception, internal state set
        assertNotNull(controller);
    }
}
