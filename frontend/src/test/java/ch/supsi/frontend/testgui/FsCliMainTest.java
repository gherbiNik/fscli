package ch.supsi.frontend.testgui;

import com.sun.javafx.scene.control.ContextMenuContent;
import javafx.scene.control.MenuItem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testfx.matcher.control.TextInputControlMatchers;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class FsCliMainTest extends AbstractGUITest {

    @Test
    public void walkThrough() {
        testMainScene();
        testFileMenu();
        testEditMenu();
        testHelpMenu();
        testFileSystemCreation();
    }

    private void testMainScene() {
        step("Verifica componenti principali della scena...", () -> {
            // Verifica visibilità delle aree principali
            verifyThat("#outputView", isVisible());
            verifyThat("#logView", isVisible());

            // Verifica che le aree di testo siano inizialmente vuote
            verifyThat("#outputView", TextInputControlMatchers.hasText(""));
            verifyThat("#logView", TextInputControlMatchers.hasText(""));

            // Verifica visibilità della barra dei menu
            verifyThat("#fileMenu", isVisible());
            verifyThat("#editMenu", isVisible());
            verifyThat("#helpMenu", isVisible());

            // Verifica che la command line sia disabilitata all'avvio (se hai aggiunto l'ID "enter" al bottone)
            verifyThat("#enter", (node) -> node.isDisabled());
        });
    }

    private void testFileMenu() {
        step("Verifica Menu File...", () -> {
            // Apre il menu File
            clickOn("#fileMenu");

            // Verifica New e Open (abilitati)
            MenuItem newMenuItem = getMenuItem("#newMenuItem");
            Assertions.assertFalse(newMenuItem.isDisable());

            MenuItem openMenuItem = getMenuItem("#openMenuItem");
            Assertions.assertFalse(openMenuItem.isDisable());

            // Verifica Save e Save As (disabilitati all'avvio)
            MenuItem saveMenuItem = getMenuItem("#saveMenuItem");
            Assertions.assertTrue(saveMenuItem.isDisable());

            MenuItem saveAsMenuItem = getMenuItem("#saveAsMenuItem");
            Assertions.assertTrue(saveAsMenuItem.isDisable());

            // Verifica Exit (abilitato)
            MenuItem exitMenuItem = getMenuItem("#exitMenuItem");
            Assertions.assertFalse(exitMenuItem.isDisable());

            // Chiude il menu cliccando di nuovo
            clickOn("#fileMenu");
        });
    }

    private void testEditMenu() {
        step("Verifica Menu Edit...", () -> {
            clickOn("#editMenu");

            // Verifica voce Preferences
            MenuItem preferencesMenuItem = getMenuItem("#preferencesMenuItem");
            Assertions.assertTrue(preferencesMenuItem.isVisible());
            Assertions.assertFalse(preferencesMenuItem.isDisable());

            clickOn("#editMenu");
        });
    }

    private void testHelpMenu() {
        step("Verifica Menu Help...", () -> {
            clickOn("#helpMenu");

            // Verifica voci Help e About
            MenuItem helpMenuItem = getMenuItem("#helpMenuItem");
            Assertions.assertTrue(helpMenuItem.isVisible());

            MenuItem aboutMenuItem = getMenuItem("#aboutMenuItem");
            Assertions.assertTrue(aboutMenuItem.isVisible());

            clickOn("#helpMenu");
        });
    }

    private void testFileSystemCreation() {
        step("Test Creazione File System e attivazione interfaccia...", () -> {
            // Clicca su File -> New
            clickOn("#fileMenu");
            clickOn("#newMenuItem");

            // Verifica che ora Save e Save As siano abilitati
            clickOn("#fileMenu");
            MenuItem saveMenuItem = getMenuItem("#saveMenuItem");
            Assertions.assertFalse(saveMenuItem.isDisable(), "Il tasto Save dovrebbe essere attivo dopo aver creato un FS");

            MenuItem saveAsMenuItem = getMenuItem("#saveAsMenuItem");
            Assertions.assertFalse(saveAsMenuItem.isDisable(), "Il tasto Save As dovrebbe essere attivo dopo aver creato un FS");
            clickOn("#fileMenu"); // Chiudi menu

            // Verifica che la command line si sia attivata (verifica sul bottone Enter)
            verifyThat("#enter", (node) -> !node.isDisabled());
        });
    }

    /**
     * Metodo helper per recuperare i MenuItem dai menu aperti.
     */
    private MenuItem getMenuItem(String id) {
        return lookup(id).queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
    }
}