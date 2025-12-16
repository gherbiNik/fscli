package ch.supsi.frontend.testgui;

import ch.supsi.fscli.frontend.controller.PreferenceController;
import ch.supsi.fscli.frontend.controller.filesystem.IFileSystemController;
import ch.supsi.fscli.frontend.controller.mapper.IFsStateMapperController;
import ch.supsi.fscli.frontend.event.ExitAbortedEvent;
import ch.supsi.fscli.frontend.event.FileSystemCreationEvent;
import ch.supsi.fscli.frontend.event.FileSystemSaved;
import ch.supsi.fscli.frontend.event.OutputEvent;
import ch.supsi.fscli.frontend.util.I18nManager;
import ch.supsi.fscli.frontend.view.*;
import com.sun.javafx.scene.control.ContextMenuContent;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.matcher.control.TextInputControlMatchers;
import org.testfx.util.WaitForAsyncUtils;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.*;

@ExtendWith(MockitoExtension.class)
public class MainInterfaceTest extends AbstractGUITest {

    @Mock private IFileSystemController fileSystemController;
    @Mock private PreferenceController preferenceController;
    @Mock private I18nManager i18nManager;
    @Mock private IFsStateMapperController fsStateMapperController;

    // Viste secondarie
    @Mock private ExitView exitView;
    @Mock private CreditsView creditsView;
    @Mock private OpenView openView;
    @Mock private SaveAsView saveAsView;
    @Mock private HelpView helpView;
    @Mock private PreferenceView preferenceView;

    // Viste Reali
    private CommandLineView commandLineView;
    private OutputView outputView;
    private LogView logView;
    private MenuBarView menuBarView;

    @Override
    public void start(Stage stage) throws Exception {
        MockitoAnnotations.openMocks(this);
        this.primaryStage = stage;

        // --- MOCK I18N ---
        lenient().when(i18nManager.getString(anyString())).thenAnswer(invocation -> {
            String key = invocation.getArgument(0);
            switch (key) {
                // Menu Keys
                case "menu.file": return "File";
                case "menu.file.new": return "New";
                case "menu.file.open": return "Open";
                case "menu.file.save": return "Save";
                case "menu.file.save_as": return "Save As";
                case "menu.file.exit": return "Exit";
                case "menu.edit": return "Edit";
                case "menu.edit.preferences": return "Preferences";
                case "menu.help": return "Help";
                case "menu.help.help": return "Help";
                case "menu.help.about": return "About";

                // Other Keys
                case "commandLine.enter": return "Enter";
                case "commandLine.command": return "Command:";
                case "log.exitAbort": return "Exit Aborted";
                case "log.fileSaved": return "File Saved: ";

                default: return key;
            }
        });

        // Mock Preferenze
        lenient().when(preferenceController.getCommandLineFont()).thenReturn(Font.getDefault());
        lenient().when(preferenceController.getColumn()).thenReturn(80);
        lenient().when(preferenceController.getOutputAreaFont()).thenReturn(Font.getDefault());
        lenient().when(preferenceController.getOutputAreaRow()).thenReturn(10);
        lenient().when(preferenceController.getLogAreaFont()).thenReturn(Font.getDefault());
        lenient().when(preferenceController.getLogAreaRow()).thenReturn(5);

        // Creazione Viste
        commandLineView = new CommandLineView(fileSystemController, preferenceController, i18nManager);
        outputView = new OutputView(i18nManager, preferenceController);
        logView = new LogView(i18nManager, preferenceController);
        menuBarView = new MenuBarView(
                i18nManager, exitView, creditsView, openView, saveAsView,
                helpView, preferenceView, fileSystemController, fsStateMapperController
        );

        commandLineView.setDisable(true);

        // Layout Manuale
        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(menuBarView.getNode());

        SplitPane centerSplit = new SplitPane();
        centerSplit.setOrientation(javafx.geometry.Orientation.VERTICAL);
        centerSplit.getItems().addAll(outputView.getNode(), logView.getNode());
        mainLayout.setCenter(centerSplit);

        HBox bottomBox = new HBox(10);
        HBox.setHgrow(commandLineView.getNode(), Priority.ALWAYS);
        bottomBox.getChildren().addAll(commandLineView.getLabel(), commandLineView.getNode(), commandLineView.getButton());
        mainLayout.setBottom(bottomBox);

        Scene scene = new Scene(mainLayout, 800, 600);
        stage.setScene(scene);
        stage.show();
        stage.toFront();
    }

    @Test
    public void walkThrough() {
        testCreateFsAndSendCommand();
    }

    private void testSaveItemsDisabled() {
        sleep(SLEEP_INTERVAL);
        clickOn("#fileMenu");
        Assertions.assertTrue(lookup("#saveMenuItem").queryAs(ContextMenuContent.MenuItemContainer.class).getItem().isVisible());
        Assertions.assertTrue(lookup("#saveMenuItem").queryAs(ContextMenuContent.MenuItemContainer.class).getItem().isDisable());
        clickOn("#fileMenu");
    }

    private void testSaveItemsEnabled() {
        sleep(SLEEP_INTERVAL);
        clickOn("#fileMenu");
        Assertions.assertTrue(lookup("#saveMenuItem").queryAs(ContextMenuContent.MenuItemContainer.class).getItem().isVisible());
        Assertions.assertFalse(lookup("#saveMenuItem").queryAs(ContextMenuContent.MenuItemContainer.class).getItem().isDisable());
        clickOn("#fileMenu");
    }

    private void testCreateFsAndSendCommand() {
        step("Create File System, check menus, run commands, try exit...", () -> {

            // --- VERIFICA STATO INIZIALE ---
            testSaveItemsDisabled();
            verifyThat("#inputCommand", isDisabled());

            // Verify Read-Only areas
            TextArea outNode = lookup("#outputView").query();
            TextArea logNode = lookup("#logView").query();
            Assertions.assertFalse(outNode.isEditable());
            Assertions.assertFalse(logNode.isEditable());


            // --- CREAZIONE FILE SYSTEM ---
            sleep(SLEEP_INTERVAL);
            clickOn("#fileMenu");
            clickOn("#newMenuItem");

            verify(fileSystemController, times(1)).createFileSystem();

            // Simulazione Risposta Backend
            Platform.runLater(() -> {
                FileSystemCreationEvent fsEvent = new FileSystemCreationEvent(this);
                commandLineView.propertyChange(fsEvent);
                menuBarView.propertyChange(fsEvent);
                outputView.propertyChange(new OutputEvent(this, "output", null, "File System Created.\n"));
            });

            WaitForAsyncUtils.waitForFxEvents();

            // --- VERIFICA POST-CREAZIONE ---
            testSaveItemsEnabled();
            verifyThat("#inputCommand", isEnabled());
            verifyThat("#outputView", TextInputControlMatchers.hasText("File System Created.\n"));


            // --- TEST COMANDI ---
            // Comando invalido
            sleep(SLEEP_INTERVAL);
            clickOn("#inputCommand");
            write("invalid-command");
            type(KeyCode.ENTER);

            verify(fileSystemController, times(1)).sendCommand("invalid-command");

            Platform.runLater(() -> {
                outputView.propertyChange(new OutputEvent(this, "output", null, "Error: Unknown command\n"));
            });

            WaitForAsyncUtils.waitForFxEvents();
            verifyThat("#outputView", TextInputControlMatchers.hasText(
                    "File System Created.\n" + "Error: Unknown command\n"
            ));

            // touch
            sleep(SLEEP_INTERVAL);
            clickOn("#inputCommand");
            write("touch myFile.txt");
            type(KeyCode.ENTER);

            verify(fileSystemController, times(1)).sendCommand("touch myFile.txt");

            Platform.runLater(() -> {
                outputView.propertyChange(new OutputEvent(this, "output", null, ""));
            });

            WaitForAsyncUtils.waitForFxEvents();

            // Check Output Finale
            verifyThat("#outputView", TextInputControlMatchers.hasText(
                    "File System Created.\n" +
                            "Error: Unknown command\n"
            ));

            verifyThat("#inputCommand", TextInputControlMatchers.hasText(""));


            // --- TEST EXIT ABORT  ---
            // Proviamo ad uscire
            sleep(SLEEP_INTERVAL);
            clickOn("#fileMenu");
            clickOn("#exitMenuItem");

            // Verifica che la ExitView sia stata richiamata
            verify(exitView, times(1)).show();

            // Simuliamo che l'uscita venga abortita
            // Il LogView dovrebbe ascoltare ExitAbortedEvent e scrivere nel log
            Platform.runLater(() -> {
                ExitAbortedEvent abortEvent = new ExitAbortedEvent(this);
                logView.propertyChange(abortEvent);
            });

            WaitForAsyncUtils.waitForFxEvents();

            // Verifica che il log contenga il messaggio di abort
            verifyThat("#logView", TextInputControlMatchers.hasText(
                    Matchers.containsString("Exit Aborted")
            ));

            sleep(3000);

            // --- SALVATAGGIO ED USCITA ---
            clickOn("#fileMenu");
            clickOn("#saveMenuItem");

            // Verifica chiamata al controller
            verify(fsStateMapperController, times(1)).save();

            //  Simula risposta Backend
            Platform.runLater(() -> {
                FileSystemSaved savedEvent = new FileSystemSaved(this, "saved", null, "/home/user/myFile.txt");

                // Aggiorna Menu e Log
                menuBarView.propertyChange(savedEvent);
                logView.propertyChange(savedEvent);
            });

            WaitForAsyncUtils.waitForFxEvents();

            // Verifica Log e Stato Menu
            verifyThat("#logView", TextInputControlMatchers.hasText(
                    Matchers.containsString("File Saved: /home/user/myFile.txt")
            ));

            // Verifica che il menu Save sia tornato disabilitato (perché abbiamo appena salvato)
            testSaveItemsDisabled();

            // Riprova ad Uscire (File -> Exit)
            clickOn("#fileMenu");
            clickOn("#exitMenuItem");

            // Verifica che la ExitView sia stata chiamata di nuovo
            // questa sarebbe la seconda volta
            verify(exitView, times(2)).show();
            sleep(3000);
        });
    }
}