package ch.supsi.frontend.controller;

import ch.supsi.fscli.frontend.controller.ExitController;
import ch.supsi.fscli.frontend.model.IExitModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class ExitControllerTest {

    @Mock
    private IExitModel exitModel;

    private ExitController exitController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        exitController = new ExitController(exitModel);
    }

    @Test
    void testQuit_ExitNotPossible() {
        when(exitModel.isExitPossible()).thenReturn(false);
        exitController.quit();
        verify(exitModel, times(1)).isExitPossible();
    }

    @Test
    void testQuit_ExitPossible() {
        when(exitModel.isExitPossible()).thenReturn(true);
        exitController.quit();
        verify(exitModel, times(1)).isExitPossible();
    }
}