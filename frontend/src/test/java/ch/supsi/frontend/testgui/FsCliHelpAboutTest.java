package ch.supsi.frontend.testgui;

import org.junit.jupiter.api.Test;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;

import static org.testfx.api.FxAssert.verifyThat;


public class FsCliHelpAboutTest extends AbstractGUITest {

    @Test
    public void walkThrough() {
        testHelp();
        testAbout();
    }

    private void testHelp() {
        step("help...", () -> {
            // open menu
            sleep(SLEEP_INTERVAL);
            clickOn("#helpMenu");

            sleep(SLEEP_INTERVAL);
            clickOn("#helpMenuItem");

            verifyThat("#helpPopup", NodeMatchers.isVisible());
            verifyThat("#messageLabel", NodeMatchers.isVisible());

            sleep(SLEEP_INTERVAL);
            clickOn("#helpPopupOkButton");
        });
    }

    private void testAbout() {
        step("about...", () -> {
            // open menu
            sleep(SLEEP_INTERVAL);
            clickOn("#helpMenu");

            sleep(SLEEP_INTERVAL);
            clickOn("#aboutMenuItem");

            verifyThat("#aboutPopup", NodeMatchers.isVisible());
            verifyThat("#messageLabel", NodeMatchers.isVisible());

            sleep(SLEEP_INTERVAL);
            clickOn("#aboutPopupOkButton");
        });
    }

}
