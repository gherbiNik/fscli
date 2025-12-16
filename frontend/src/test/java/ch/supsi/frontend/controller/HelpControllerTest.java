package ch.supsi.frontend.controller;

import ch.supsi.fscli.frontend.controller.HelpController;
import ch.supsi.fscli.frontend.model.ICommandHelpModel;
import ch.supsi.fscli.frontend.view.HelpView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

class HelpControllerTest {

    @Mock
    private HelpView helpView;

    @Mock
    private ICommandHelpModel commandHelpModel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConstructionUpdatesView() {
        List<String> mockDescriptions = Arrays.asList("cmd1: desc1", "cmd2: desc2");
        when(commandHelpModel.getCommandDescriptions()).thenReturn(mockDescriptions);

        // L'updateView viene chiamato nel costruttore
        new HelpController(helpView, commandHelpModel);

        verify(commandHelpModel, times(1)).getCommandDescriptions();
        verify(helpView, times(1)).setCommandDescriptions(mockDescriptions);
    }
}