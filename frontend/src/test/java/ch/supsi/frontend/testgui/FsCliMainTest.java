package ch.supsi.frontend.testgui;

import com.sun.javafx.scene.control.ContextMenuContent;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll; // Import added
import org.junit.jupiter.api.Test;
import org.testfx.matcher.control.TextInputControlMatchers;

import java.util.Locale; // Import added

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class FsCliMainTest extends AbstractGUITest {

    @BeforeAll
    public static void setupSpec() {
        Locale.setDefault(Locale.ENGLISH);
    }
    @Test
    public void walkThrough() {
        testMainScene();
        testFileMenu();
        testEditMenu();
        testHelpMenu();
        testFileSystemCreation();
        testExitAborted();
    }

    private void testMainScene() {
        step("Verifica componenti principali della scena...", () -> {
            verifyThat("#outputView", isVisible());
            verifyThat("#logView", isVisible());
            verifyThat("#outputView", TextInputControlMatchers.hasText(""));
            verifyThat("#logView", TextInputControlMatchers.hasText(""));
            verifyThat("#fileMenu", isVisible());
            verifyThat("#editMenu", isVisible());
            verifyThat("#helpMenu", isVisible());
            verifyThat("#enter", (node) -> node.isDisabled());
        });
    }

    private void testFileMenu() {
        step("Verifica Menu File...", () -> {
            clickOn("#fileMenu");
            MenuItem newMenuItem = getMenuItem("#newMenuItem");
            Assertions.assertFalse(newMenuItem.isDisable());
            MenuItem openMenuItem = getMenuItem("#openMenuItem");
            Assertions.assertFalse(openMenuItem.isDisable());
            MenuItem saveMenuItem = getMenuItem("#saveMenuItem");
            Assertions.assertTrue(saveMenuItem.isDisable());
            MenuItem saveAsMenuItem = getMenuItem("#saveAsMenuItem");
            Assertions.assertTrue(saveAsMenuItem.isDisable());
            MenuItem exitMenuItem = getMenuItem("#exitMenuItem");
            Assertions.assertFalse(exitMenuItem.isDisable());
            clickOn("#fileMenu");
        });
    }

    private void testEditMenu() {
        step("Verifica Menu Edit...", () -> {
            clickOn("#editMenu");
            MenuItem preferencesMenuItem = getMenuItem("#preferencesMenuItem");
            Assertions.assertTrue(preferencesMenuItem.isVisible());
            Assertions.assertFalse(preferencesMenuItem.isDisable());
            clickOn("#editMenu");
        });
    }

    private void testHelpMenu() {
        step("Verifica Menu Help...", () -> {
            clickOn("#helpMenu");
            MenuItem helpMenuItem = getMenuItem("#helpMenuItem");
            Assertions.assertTrue(helpMenuItem.isVisible());
            MenuItem aboutMenuItem = getMenuItem("#aboutMenuItem");
            Assertions.assertTrue(aboutMenuItem.isVisible());
            clickOn("#helpMenu");
        });
    }

    private void testFileSystemCreation() {
        step("Test Creazione File System e attivazione interfaccia...", () -> {
            clickOn("#fileMenu");
            clickOn("#newMenuItem");
            sleep(SLEEP_INTERVAL);
            clickOn("#fileMenu");
            MenuItem saveMenuItem = getMenuItem("#saveMenuItem");
            Assertions.assertFalse(saveMenuItem.isDisable(), "Il tasto Save dovrebbe essere attivo dopo aver creato un FS");
            MenuItem saveAsMenuItem = getMenuItem("#saveAsMenuItem");
            Assertions.assertFalse(saveAsMenuItem.isDisable(), "Il tasto Save As dovrebbe essere attivo dopo aver creato un FS");
            clickOn("#fileMenu");
            verifyThat("#enter", (node) -> !node.isDisabled());
        });
    }

    private void testExitAborted() {
        step("Test Uscita Abortita (Exit was aborted)...", () -> {
            clickOn("#inputCommand");
            write("touch abc");
            type(KeyCode.ENTER);

            // Wait for backend processing
            sleep(SLEEP_INTERVAL);

            clickOn("#fileMenu");
            clickOn("#exitMenuItem");

            verifyThat("#outputView", (javafx.scene.control.TextArea area) ->
                    area.getText().contains("Exit aborted: unsaved changes detected.")
            );
        });
    }

    private MenuItem getMenuItem(String id) {
        return lookup(id).queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
    }
}