package ch.supsi.fscli.frontend;

import ch.supsi.fscli.backend.application.PreferenceApplication;
import ch.supsi.fscli.backend.business.PreferenceBusiness;
import ch.supsi.fscli.backend.dataAccess.PreferenceDAO;
import ch.supsi.fscli.frontend.controller.ExitController;
import ch.supsi.fscli.frontend.controller.PreferenceController;
import ch.supsi.fscli.frontend.model.PreferenceModel;
import ch.supsi.fscli.frontend.util.I18nManager;
import ch.supsi.fscli.frontend.view.CreditsView;
import ch.supsi.fscli.frontend.view.ExitView;
import ch.supsi.fscli.frontend.view.HelpView;
import ch.supsi.fscli.frontend.view.PreferenceView;
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
    private static final int COMMAND_LINE_PREF_COLUMN_COUNT = 72;
    private static final int PREF_OUTPUT_VIEW_ROW_COUNT = 25;
    private static final int PREF_LOG_VIEW_ROW_COUNT = 5;

    private final String applicationTitle;
    private final MenuBar menuBar;
    private final Menu fileMenu;
    private final Menu editMenu;
    private final Menu helpMenu;
    private final Label commandLineLabel;
    private final Button enter;
    private final TextField commandLine;
    private final TextArea outputView;
    private final TextArea logView;
    private static Stage stageToClose;

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

    public MainFx() {
        // DAO
        this.preferenceDAO = PreferenceDAO.getInstance();

        // BUSINESS
        this.preferenceBusiness = PreferenceBusiness.getInstance(preferenceDAO);

        // APPLICATION
        this.preferenceApplication = PreferenceApplication.getInstance(preferenceBusiness);

        // MODEL
        this.preferenceModel = PreferenceModel.getInstance(preferenceApplication);

        // CONTROLLER
        this.preferenceController = PreferenceController.getInstance(preferenceModel);
        this.exitController = ExitController.getInstance();

        // --- I18N INITIALIZATION ---
        Locale loadedLocale = this.preferenceApplication.loadLanguagePreference();
        I18nManager i18n = I18nManager.getInstance();
        i18n.setLocale(loadedLocale);

        // VIEW
        this.preferenceView = PreferenceView.getInstance(preferenceController, i18n);
        this.helpView = HelpView.getInstance();
        this.creditsView = CreditsView.getInstance();
        this.exitView = ExitView.getInstance(exitController);



        System.out.println("Application started with language: " + loadedLocale.getLanguage());

        this.applicationTitle = i18n.getString("app.title");

        // FILE MENU
        MenuItem newMenuItem = new MenuItem(i18n.getString("menu.file.new"));
        newMenuItem.setId("newMenuItem");

        MenuItem openMenuItem = new MenuItem(i18n.getString("menu.file.open"));
        openMenuItem.setId("openMenuItem");

        MenuItem saveMenuItem = new MenuItem(i18n.getString("menu.file.save"));
        saveMenuItem.setId("saveMenuItem");

        MenuItem saveAsMenuItem = new MenuItem(i18n.getString("menu.file.save_as"));
        saveAsMenuItem.setId("saveAsMenuItem");

        // EXIT MENU
        MenuItem exitMenuItem = new MenuItem(i18n.getString("menu.file.exit"));
        exitMenuItem.setId("exitMenuItem");
        exitMenuItem.setOnAction(event -> exitView.showView());


        this.fileMenu = new Menu(i18n.getString("menu.file"));
        this.fileMenu.setId("fileMenu");
        this.fileMenu.getItems().add(newMenuItem);
        this.fileMenu.getItems().add(new SeparatorMenuItem());
        this.fileMenu.getItems().add(openMenuItem);
        this.fileMenu.getItems().add(saveMenuItem);
        this.fileMenu.getItems().add(saveAsMenuItem);
        this.fileMenu.getItems().add(new SeparatorMenuItem());
        this.fileMenu.getItems().add(exitMenuItem);

        // EDIT MENU
        MenuItem preferencesMenuItem = new MenuItem(i18n.getString("menu.edit.preferences"));
        preferencesMenuItem.setId("preferencesMenuItem");
        preferencesMenuItem.setOnAction(event -> preferenceView.showView());

        this.editMenu = new Menu(i18n.getString("menu.edit"));
        this.editMenu.setId("editMenu");
        this.editMenu.getItems().add(preferencesMenuItem);

        // HELP MENU
        MenuItem helpMenuItem = new MenuItem(i18n.getString("menu.help.help"));
        helpMenuItem.setId("helpMenuItem");
        helpMenuItem.setOnAction(event -> helpView.showView());

        // CREDITS MENU
        MenuItem aboutMenuItem = new MenuItem(i18n.getString("menu.help.about"));
        aboutMenuItem.setId("aboutMenuItem");
        aboutMenuItem.setOnAction(event -> creditsView.showView());

        this.helpMenu = new Menu(i18n.getString("menu.help"));
        this.helpMenu.setId("helpMenu");
        this.helpMenu.getItems().add(helpMenuItem);
        this.helpMenu.getItems().add(aboutMenuItem);

        // MENU BAR
        this.menuBar = new MenuBar();
        this.menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);

        // COMMAND LINE
        this.enter = new Button(i18n.getString("commandLine.enter"));
        this.enter.setId("enter");

        this.commandLineLabel = new Label(i18n.getString("commandLine.command"));
        this.commandLine = new TextField();
        this.commandLine.setFont(this.preferenceController.getCommandLineFont());
        this.commandLine.setPrefColumnCount(this.preferenceController.getColumn());
        System.out.println(commandLine.getPrefColumnCount());

        // OUTPUT VIEW (to be encapsulated properly)
        this.outputView = new TextArea();
        this.outputView.setId("outputView");
        this.outputView.appendText("1This is an example output text...\n");
        this.outputView.appendText("2This is an example output text...\n");
        this.outputView.appendText("3This is an example output text...\n");
        this.outputView.appendText("4This is an example output text...\n");
        this.outputView.setPrefRowCount(this.preferenceController.getOutputAreaRow());
        System.out.println(outputView.getPrefRowCount());
        outputView.setFont(this.preferenceController.getOutputAreaFont());

        // LOG VIEW (to be encapsulated properly)
        this.logView = new TextArea();
        this.logView.setId("logView");
        this.logView.appendText("1This is an example log text...\n");
        this.logView.appendText("2This is an example log text...\n");
        this.logView.appendText("3This is an example log text...\n");
        this.logView.appendText("4This is an example log text...\n");
        logView.setFont(this.preferenceController.getLogAreaFont());
        logView.setPrefRowCount(this.preferenceController.getLogAreaRow());
        System.out.println(logView.getPrefRowCount());
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

        commandLinePane.getChildren().add(this.commandLineLabel);
        commandLinePane.getChildren().add(spacer1);
        commandLinePane.getChildren().add(this.commandLine);
        commandLinePane.getChildren().add(spacer2);
        commandLinePane.getChildren().add(this.enter);

        // vertical pane to hold the menu bar and the command line
        VBox top = new VBox(
                this.menuBar,
                commandLinePane
        );

        // output view
        //this.outputView.setPrefRowCount(PREF_OUTPUT_VIEW_ROW_COUNT);
        this.outputView.setEditable(false);

        // scroll pane to hold the output view
        ScrollPane centerPane = new ScrollPane();
        centerPane.setFitToHeight(true);
        centerPane.setFitToWidth(true);
        centerPane.setPadding(new Insets(PREF_INSETS_SIZE));
        centerPane.setContent(this.outputView);

        // log view
        //this.logView.setPrefRowCount(PREF_LOG_VIEW_ROW_COUNT);
        this.logView.setEditable(false);

        // scroll pane to hold log view
        ScrollPane bottomPane = new ScrollPane();
        bottomPane.setFitToHeight(true);
        bottomPane.setFitToWidth(true);
        bottomPane.setPadding(new Insets(PREF_INSETS_SIZE));
        bottomPane.setContent(this.logView);

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
