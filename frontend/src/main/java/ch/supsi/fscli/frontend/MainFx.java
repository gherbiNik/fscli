package ch.supsi.fscli.frontend;

import ch.supsi.fscli.backend.application.PreferenceApplication;
import ch.supsi.fscli.backend.application.filesystem.FileSystemApplication;
import ch.supsi.fscli.backend.business.PreferenceBusiness;
import ch.supsi.fscli.backend.dataAccess.PreferenceDAO;
import ch.supsi.fscli.frontend.controller.ExitController;
import ch.supsi.fscli.frontend.controller.PreferenceController;
import ch.supsi.fscli.frontend.controller.filesystem.FileSystemController;
import ch.supsi.fscli.frontend.model.PreferenceModel;
import ch.supsi.fscli.frontend.model.filesystem.FileSystemModel;
import ch.supsi.fscli.frontend.util.I18nManager;
import ch.supsi.fscli.frontend.view.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Locale;

public class MainFx extends Application {
    private static final int PREF_INSETS_SIZE = 7;
    private static final int PREF_COMMAND_SPACER_WIDTH = 11;

    private final String applicationTitle;
    private static Stage stageToClose;


    private final MenuBarView menuBarView;
    private final CommandLineView commandLineView;
    private final OutputView outputView;
    private final LogView logView;
    private final PreferenceView preferenceView;
    private final PreferenceController preferenceController;
    private final PreferenceModel preferenceModel;
    private final PreferenceApplication preferenceApplication;
    private final PreferenceDAO preferenceDAO;
    private final PreferenceBusiness preferenceBusiness;
    private final HelpView helpView;
    private final CreditsView creditsView;
    private final ExitView exitView;
    private final ExitController exitController;
    private final FileSystemController fileSystemController;
    private final FileSystemModel  fileSystemModel;
    private final FileSystemApplication fileSystemApplication;

    public MainFx() {

        // DAO
        this.preferenceDAO = PreferenceDAO.getInstance();

        // BUSINESS
        this.preferenceBusiness = PreferenceBusiness.getInstance(preferenceDAO);

        // APPLICATION
        this.preferenceApplication = PreferenceApplication.getInstance(preferenceBusiness);
        this.fileSystemApplication = FileSystemApplication.getInstance();

        // --- I18N INITIALIZATION ---
        Locale loadedLocale = this.preferenceApplication.loadLanguagePreference();
        I18nManager i18n = I18nManager.getInstance();
        i18n.setLocale(loadedLocale);

        // MODEL
        this.preferenceModel = PreferenceModel.getInstance(preferenceApplication);
        this.fileSystemModel = FileSystemModel.getInstance(fileSystemApplication);

        // CONTROLLER
        this.preferenceController = PreferenceController.getInstance(preferenceModel);
        // LOG VIEW (to be encapsulated properly)
        this.logView = LogView.getInstance(preferenceController, i18n);
        this.fileSystemController = FileSystemController.getInstance(fileSystemModel,logView, i18n);
        this.exitController = ExitController.getInstance();



        // VIEW
        this.preferenceView = PreferenceView.getInstance(preferenceController, i18n);
        this.helpView = HelpView.getInstance(i18n);
        this.creditsView = CreditsView.getInstance(i18n);
        this.exitView = ExitView.getInstance(exitController, i18n);
        this.menuBarView = MenuBarView.getInstance(i18n, exitView, creditsView, helpView, preferenceView, fileSystemController);



        System.out.println("Application started with language: " + loadedLocale.getLanguage());

        this.applicationTitle = i18n.getString("app.title");


        // COMMAND LINE
        this.commandLineView = CommandLineView.getInstance(preferenceController, i18n);


        // OUTPUT VIEW (to be encapsulated properly)
        this.outputView = OutputView.getInstance(preferenceController, i18n);




    }

    public static Stage getStageToClose() {
        return stageToClose;
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        stageToClose = primaryStage;

        // command line
        //this.commandLine.setPrefColumnCount(COMMAND_LINE_PREF_COLUMN_COUNT);

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
                this.menuBarView.getNode(),
                commandLinePane
        );

        // output view
        //this.outputView.setPrefRowCount(PREF_OUTPUT_VIEW_ROW_COUNT);
        //this.outputView.setEditable(false);

        // scroll pane to hold the output view
        ScrollPane centerPane = new ScrollPane();
        centerPane.setFitToHeight(true);
        centerPane.setFitToWidth(true);
        centerPane.setPadding(new Insets(PREF_INSETS_SIZE));
        centerPane.setContent(this.outputView.getNode());

        // log view
        //this.logView.setPrefRowCount(PREF_LOG_VIEW_ROW_COUNT);
        //this.logView.setEditable(false);

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
        primaryStage.setTitle(this.applicationTitle);
        primaryStage.setResizable(true);
        primaryStage.setScene(mainScene);

        // on close
        primaryStage.setOnCloseRequest(e -> {
            // send a command to the ApplicationExitController
            // to handle to exit process...
            //
            // for new we just close the app directly
            e.consume();
            exitView.showView();
            //primaryStage.close();
        });

        // show the primary stage
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}
