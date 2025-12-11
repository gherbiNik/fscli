package ch.supsi.fscli.frontend;

import ch.supsi.fscli.backend.application.IPreferenceApplication;
import ch.supsi.fscli.backend.business.command.commands.AbstractValidatedCommand;
import ch.supsi.fscli.backend.business.command.commands.validators.AbstractValidator;
import ch.supsi.fscli.backend.business.filesystem.FileSystem;
import ch.supsi.fscli.backend.util.BackendTranslator;
import ch.supsi.fscli.frontend.model.filesystem.FileSystemModel;
import ch.supsi.fscli.frontend.model.PreferenceModel;
import ch.supsi.fscli.frontend.model.mapper.FsStateMapperModel;
import ch.supsi.fscli.frontend.module.FrontendModule;
import ch.supsi.fscli.frontend.util.I18nManager;
import ch.supsi.fscli.frontend.view.*;
import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Locale;

public class MainFx extends Application {
    private static final int PREF_INSETS_SIZE = 7;
    private static final int PREF_COMMAND_SPACER_WIDTH = 11;
    private static Stage stageToClose;

    private CommandLineView commandLineView;
    private OutputView outputView;
    private LogView logView;
    private ExitView exitView;

    // Costruttore vuoto richiesto da JavaFX
    public MainFx() {
        // La logica di costruzione è in start(Stage)
    }

    public static Stage getStageToClose() {
        return stageToClose;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stageToClose = primaryStage;

        // 1. COMPOSITION ROOT: Crea l'Injector usando il FrontendModule
        Injector injector = Guice.createInjector(new FrontendModule());

        // 2. SETUP INIZIALE (Lingua)
        // Recuperiamo i componenti essenziali per l'inizializzazione
        IPreferenceApplication preferenceApplication = injector.getInstance(IPreferenceApplication.class);
        I18nManager i18n = injector.getInstance(I18nManager.class);
        BackendTranslator backendTranslator = injector.getInstance(BackendTranslator.class);

        Locale loadedLocale = preferenceApplication.loadLanguagePreference();

        // Configura i traduttori
        backendTranslator.setLocale(loadedLocale);
        i18n.setLocale(loadedLocale);

        System.out.println("Application started with language: " + loadedLocale.getLanguage());

        // 3. FETCH COMPONENTI PRINCIPALI
        // Guice assembla e ci restituisce tutti i componenti (Model, Controller e View)
        // Campi per le View principali (necessari per il layout e i listener)
        MenuBarView menuBarView = injector.getInstance(MenuBarView.class);
        this.commandLineView = injector.getInstance(CommandLineView.class);
        this.outputView = injector.getInstance(OutputView.class);
        this.logView = injector.getInstance(LogView.class);
        this.exitView = injector.getInstance(ExitView.class);

        // Fetch i Model per collegare i listener
        FileSystemModel fileSystemModel = injector.getInstance(FileSystemModel.class);
        PreferenceModel preferenceModel = injector.getInstance(PreferenceModel.class);
        FsStateMapperModel fsStateMapperModel = injector.getInstance(FsStateMapperModel.class);

        injector.getInstance(ch.supsi.fscli.frontend.controller.HelpController.class);
        injector.getInstance(ch.supsi.fscli.frontend.controller.CreditsController.class);

        // 4. WIRING MANUALE DEI LISTENER (Pattern Observer/MVC)
        // Nonostante Guice, questo passaggio è necessario per collegare gli Observable (Model)
        // agli Observer (View/Controller).
        fileSystemModel.addPropertyChangeListener(commandLineView);
        fileSystemModel.addPropertyChangeListener(logView);
        fileSystemModel.addPropertyChangeListener(outputView);
        fileSystemModel.addPropertyChangeListener(menuBarView);

        preferenceModel.addPropertyChangeListener(logView);

        fsStateMapperModel.addPropertyChangeListener(menuBarView);
        fsStateMapperModel.addPropertyChangeListener(logView);
        fsStateMapperModel.addPropertyChangeListener(commandLineView);

        // 5. SETUP STATICHE BACKEND (Legacy)
        // Mantenute perché la struttura backend le richiede ancora.
        AbstractValidator.setTranslator(backendTranslator);
        AbstractValidatedCommand.setTranslator(backendTranslator);
        FileSystem.setTranslator(backendTranslator);

        // 6. LAYOUT JAVA FX

        // horizontal box to hold the command line
        HBox commandLinePane = new HBox();
        commandLinePane.setAlignment(Pos.BASELINE_LEFT);
        commandLinePane.setPadding(new Insets(PREF_INSETS_SIZE));

        Region spacer1 = new Region();
        spacer1.setPrefWidth(PREF_COMMAND_SPACER_WIDTH);

        Region spacer2 = new Region();
        spacer2.setPrefWidth(PREF_COMMAND_SPACER_WIDTH);

        commandLinePane.getChildren().add(this.commandLineView.getLabel());
        commandLinePane.getChildren().add(spacer1);
        commandLinePane.getChildren().add(this.commandLineView.getNode());
        commandLinePane.getChildren().add(spacer2);
        commandLinePane.getChildren().add(this.commandLineView.getButton());

        // vertical pane to hold the menu bar and the command line
        VBox top = new VBox(
                menuBarView.getNode(),
                commandLinePane
        );

        // scroll pane to hold the output view
        ScrollPane centerPane = new ScrollPane();
        centerPane.setFitToHeight(true);
        centerPane.setFitToWidth(true);
        centerPane.setPadding(new Insets(PREF_INSETS_SIZE));
        centerPane.setContent(this.outputView.getNode());

        // scroll pane to hold log view
        ScrollPane bottomPane = new ScrollPane();
        bottomPane.setFitToHeight(true);
        bottomPane.setFitToWidth(true);
        bottomPane.setPadding(new Insets(PREF_INSETS_SIZE));
        bottomPane.setContent(this.logView.getNode());

        // root pane
        BorderPane rootPane = new BorderPane();
        rootPane.setTop(top);
        rootPane.setCenter(centerPane);
        rootPane.setBottom(bottomPane);

        // scene
        Scene mainScene = new Scene(rootPane);

        // put the scene onto the primary stage
        String applicationTitle = i18n.getString("app.title");
        primaryStage.setTitle(applicationTitle);
        primaryStage.setResizable(true);
        primaryStage.setScene(mainScene);

        // on close
        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            exitView.show();
        });

        // show the primary stage
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}