package ch.supsi.frontend.testgui;

import ch.supsi.fscli.frontend.controller.IPreferenceController;
import ch.supsi.fscli.frontend.util.I18nManager;
import ch.supsi.fscli.frontend.view.PreferenceView;
import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class PreferenceViewTest {

    @Mock
    private IPreferenceController controller;

    @Mock
    private I18nManager i18nManager;

    private PreferenceView preferenceView;

    @Start
    public void start(Stage stage) throws InterruptedException {
        MockitoAnnotations.openMocks(this);

        lenient().when(i18nManager.getString(any())).thenAnswer(invocation -> "LB_" + invocation.getArgument(0));

        lenient().when(controller.getPreferences("language-tag")).thenReturn("en-US");
        lenient().when(controller.getPreferences("column")).thenReturn("80");
        lenient().when(controller.getPreferences("output-area-row")).thenReturn("10");
        lenient().when(controller.getPreferences("log-area-row")).thenReturn("5");
        lenient().when(controller.getPreferences("font-command-line")).thenReturn("Arial");
        lenient().when(controller.getPreferences("font-output-area")).thenReturn("Arial");
        lenient().when(controller.getPreferences("font-log-area")).thenReturn("Arial");

        // Avvio interfaccia
        Platform.runLater(() -> {
            preferenceView = new PreferenceView(controller, i18nManager);
            preferenceView.show();
        });

        WaitForAsyncUtils.waitForFxEvents();

        Thread.sleep(2000);
    }

    @AfterEach
    void tearDown() throws TimeoutException {
        FxToolkit.hideStage();
        if (preferenceView != null) {
            Platform.runLater(() -> preferenceView.closeView());
        }
    }

    @Test
    void testInitialValuesLoaded(FxRobot robot) {
        verifySpinnerValue(robot, "#columnsSpinner", 80);
        verifySpinnerValue(robot, "#outputLinesSpinner", 10);

        ComboBox<String> langBox = robot.lookup("#languageComboBox").queryComboBox();
        assertEquals("en-US", langBox.getValue());
    }

    @Test
    void testSaveAction(FxRobot robot) {
        robot.clickOn("#languageComboBox").clickOn("it-IT"); // Seleziona italiano

        robot.interact(() -> {
            Spinner<Integer> s = robot.lookup("#columnsSpinner").query();
            s.getValueFactory().setValue(90);
        });

        robot.clickOn("#saveButton");

        WaitForAsyncUtils.waitForFxEvents();

        verify(controller, times(1)).savePreferences(
                eq("it-IT"),
                eq("90"),
                eq("10"),
                eq("5"),
                eq("Arial"),
                eq("Arial"),
                eq("Arial")
        );
    }

    @Test
    void testCancelAction(FxRobot robot) {
        robot.clickOn("#cancelButton");
        WaitForAsyncUtils.waitForFxEvents();

        verify(controller, never()).savePreferences(any(), any(), any(), any(), any(), any(), any());
    }

    private void verifySpinnerValue(FxRobot robot, String cssId, int expected) {
        Spinner<Integer> spinner = robot.lookup(cssId).query();
        assertEquals(expected, spinner.getValue());
    }
}