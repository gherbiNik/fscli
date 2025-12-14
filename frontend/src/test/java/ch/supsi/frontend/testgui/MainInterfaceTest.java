package ch.supsi.frontend.testgui;

import ch.supsi.fscli.frontend.controller.PreferenceController;
import ch.supsi.fscli.frontend.controller.filesystem.IFileSystemController;
import ch.supsi.fscli.frontend.controller.mapper.IFsStateMapperController;
import ch.supsi.fscli.frontend.event.FileSystemCreationEvent;
import ch.supsi.fscli.frontend.event.OutputEvent;
import ch.supsi.fscli.frontend.util.I18nManager;
import ch.supsi.fscli.frontend.view.*;
import com.sun.javafx.scene.control.ContextMenuContent;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.Stage;
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

    // Viste secondarie (Mock)
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

        // Configurazione Mock di base
        lenient().when(i18nManager.getString(anyString())).thenAnswer(i -> (String) i.getArgument(0));
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

        // Layout
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
        // verify save menu item is visible but disabled
        sleep(SLEEP_INTERVAL);
        clickOn("#fileMenu");

        Assertions.assertTrue(lookup("#saveMenuItem").queryAs(ContextMenuContent.MenuItemContainer.class).getItem().isVisible());
        Assertions.assertTrue(lookup("#saveMenuItem").queryAs(ContextMenuContent.MenuItemContainer.class).getItem().isDisable());

        clickOn("#fileMenu");
    }

    private void testSaveItemsEnabled() {
        // verify save menu item is visible and enabled
        sleep(SLEEP_INTERVAL);
        clickOn("#fileMenu");
        Assertions.assertTrue(lookup("#saveMenuItem").queryAs(ContextMenuContent.MenuItemContainer.class).getItem().isVisible());
        Assertions.assertFalse(lookup("#saveMenuItem").queryAs(ContextMenuContent.MenuItemContainer.class).getItem().isDisable());

        clickOn("#fileMenu");
    }

    private void testCreateFsAndSendCommand() {
        step("Create File System, check menus, and run commands...", () -> {

            // Verify initial state (Save disabled)
            testSaveItemsDisabled();

            // Verify command line is disabled initially
            verifyThat("#inputCommand", isDisabled());

            // Create File System via Menu
            sleep(SLEEP_INTERVAL);
            clickOn("#fileMenu");
            clickOn("#newMenuItem");

            // Verify controller call
            verify(fileSystemController, times(1)).createFileSystem();

            // Simulates that the backend responds
            Platform.runLater(() -> {
                FileSystemCreationEvent event = new FileSystemCreationEvent(this);
                commandLineView.propertyChange(event); // Unlock Command Line
                menuBarView.propertyChange(event);     // Unlock Save menus
                outputView.propertyChange(new OutputEvent(this, "output", null, "File System Created.\n"));
            });
            WaitForAsyncUtils.waitForFxEvents();

            // Verify state after creation (Save enabled)
            testSaveItemsEnabled();

            // Verify command line is enabled
            verifyThat("#inputCommand", isEnabled());
            verifyThat("#outputView", TextInputControlMatchers.hasText("File System Created.\n"));

            // Enter invalid command
            sleep(SLEEP_INTERVAL);
            clickOn("#inputCommand");
            write("invalid-command");
            type(KeyCode.ENTER);

            verify(fileSystemController, times(1)).sendCommand("invalid-command");

            // Simulation: Backend responds with error
            Platform.runLater(() -> {
                outputView.propertyChange(new OutputEvent(this, "output", null, "Error: Unknown command\n"));
            });
            WaitForAsyncUtils.waitForFxEvents();

            verifyThat("#outputView", TextInputControlMatchers.hasText(
                    "File System Created.\n" +
                            "Error: Unknown command\n"
            ));

            // 'touch' command
            sleep(SLEEP_INTERVAL);
            clickOn("#inputCommand");
            write("touch myFile.txt");
            type(KeyCode.ENTER);

            verify(fileSystemController, times(1)).sendCommand("touch myFile.txt");

            // Simulation: Backend responds with success
            Platform.runLater(() -> {
                outputView.propertyChange(new OutputEvent(this, "output", null, "File myFile.txt created.\n"));
            });
            WaitForAsyncUtils.waitForFxEvents();

            // Verify final output
            verifyThat("#outputView", TextInputControlMatchers.hasText(
                    "File System Created.\n" +
                            "Error: Unknown command\n" +
                            "File myFile.txt created.\n"
            ));

            // Verify command line cleared
            verifyThat("#inputCommand", TextInputControlMatchers.hasText(""));

            // Verify Save still enabled
            testSaveItemsEnabled();
        });
    }
}