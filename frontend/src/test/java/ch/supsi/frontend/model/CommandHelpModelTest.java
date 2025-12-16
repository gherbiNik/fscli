package ch.supsi.frontend.model;

import ch.supsi.fscli.backend.application.filesystem.IFileSystemApplication;
import ch.supsi.fscli.frontend.model.CommandHelpModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CommandHelpModelTest {

    @Mock
    private IFileSystemApplication fileSystemApplication;

    private CommandHelpModel model;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        model = new CommandHelpModel(fileSystemApplication);
    }

    @Test
    void testGetCommandDescriptions() {
         List<String> expectedList = Arrays.asList("help", "cd", "ls");
        when(fileSystemApplication.getCommandsHelp()).thenReturn(expectedList);

        List<String> result = model.getCommandDescriptions();

        assertEquals(expectedList, result);
        verify(fileSystemApplication, times(1)).getCommandsHelp();
    }
}