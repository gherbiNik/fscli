package ch.supsi.frontend.testgui;

import ch.supsi.fscli.frontend.util.I18nManager;
import ch.supsi.fscli.frontend.view.CreditsView;
import ch.supsi.fscli.frontend.view.HelpView;
import javafx.application.Platform;
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
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.util.WaitForAsyncUtils;

import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.testfx.api.FxAssert.verifyThat;

@ExtendWith(ApplicationExtension.class)
public class FsCliHelpAboutTest {

    @Mock
    private I18nManager i18nManager;

    private HelpView helpView;
    private CreditsView creditsView;

    @Start
    public void start(Stage stage) {
        MockitoAnnotations.openMocks(this);

        lenient().when(i18nManager.getString(anyString())).thenAnswer(invocation -> "MOCK_" + invocation.getArgument(0));
    }

    @AfterEach
    void tearDown() throws TimeoutException {
        FxToolkit.hideStage();
    }

    @Test
    public void testHelp(FxRobot robot) {
        Platform.runLater(() -> {
            helpView = new HelpView(i18nManager);
            helpView.setCommandDescriptions(Arrays.asList("ls: List files", "cd: Change directory", "mkdir: Create directory"));
            helpView.show();
        });

        WaitForAsyncUtils.waitForFxEvents();

        verifyThat("#helpPopup", NodeMatchers.isVisible());
        verifyThat("#messageLabel", NodeMatchers.isVisible());

        verifyThat("#helpPopupOkButton", NodeMatchers.isVisible());

        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        robot.clickOn("#helpPopupOkButton");
    }

    @Test
    public void testAbout(FxRobot robot) {
        Platform.runLater(() -> {
            creditsView = new CreditsView(i18nManager);
            creditsView.setAppName("File System CLI");
            creditsView.setFrontendVersion("1.0.0");
            creditsView.setFrontendBuildDate("2025");
            creditsView.setBackendVersion("2.1.0");
            creditsView.setBackendBuildDate("2025");
            creditsView.setAuthorLabel("Studenti SUPSI");

            creditsView.show();
        });

        WaitForAsyncUtils.waitForFxEvents();

        verifyThat("#aboutPopup", NodeMatchers.isVisible());
        verifyThat("#messageLabel", NodeMatchers.isVisible());

        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        robot.clickOn("#aboutPopupOkButton");
    }
}