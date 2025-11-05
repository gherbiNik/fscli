package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.controller.filesystem.IFileSystemController;
import ch.supsi.fscli.frontend.util.I18nManager;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;


public class MenuBarView implements ControlledFxView {
    private static MenuBarView instance;

    private MenuBar menuBar;
    private Menu fileMenu;
    private Menu editMenu;
    private Menu helpMenu;
    private MenuItem newMenuItem;
    private MenuItem openMenuItem;
    private MenuItem saveMenuItem;
    private MenuItem saveAsMenuItem;
    private MenuItem exitMenuItem;

    private MenuItem preferencesMenuItem;
    private MenuItem helpMenuItem;
    private MenuItem aboutMenuItem;

    private I18nManager i18n;
    private IFileSystemController fileSystemController;

    public static MenuBarView getInstance(I18nManager i18n, ExitView exitView, CreditsView creditsView,
    HelpView helpView, PreferenceView preferenceView, IFileSystemController fileSystemController) {
        if(instance == null) {
            instance = new MenuBarView();
            instance.initialize(i18n, exitView, creditsView, helpView, preferenceView, fileSystemController);
        }
        return instance;
    }

     public void initialize(I18nManager i18n, ExitView exitView, CreditsView creditsView,
            HelpView helpView, PreferenceView preferenceView, IFileSystemController fileSystemController) {

        this.i18n = i18n;
        this.fileSystemController = fileSystemController;
        this.menuBar = new MenuBar();

        initFileMenu(exitView);
        initEditMenu(preferenceView);
        initHelpMenu(helpView, creditsView);

        setLocalizedText();
        menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);
     }

    private void initHelpMenu(HelpView helpView, CreditsView creditsView) {
        helpMenu = new Menu();
        helpMenuItem = new MenuItem();
        aboutMenuItem = new MenuItem();
        helpMenu.getItems().add(helpMenuItem);
        helpMenu.getItems().add(aboutMenuItem);

        helpMenuItem.setOnAction(event -> helpView.showView());
        aboutMenuItem.setOnAction(event -> creditsView.showView());

    }

    private void initEditMenu(PreferenceView preferenceView) {
        editMenu = new Menu();
        preferencesMenuItem = new MenuItem();
        editMenu.getItems().add(preferencesMenuItem);
        preferencesMenuItem.setOnAction(actionEvent -> preferenceView.showView());
    }

    private void initFileMenu(ExitView exitView) {
        newMenuItem = new MenuItem();
        newMenuItem.setId("newMenuItem");
        newMenuItem.setOnAction(event -> fileSystemController.createFileSystem());

        openMenuItem = new MenuItem();
        openMenuItem.setId("openMenuItem");

        saveMenuItem = new MenuItem();
        saveMenuItem.setId("saveMenuItem");
        saveMenuItem.setDisable(true); // when app starts there is nothing to save

        saveAsMenuItem = new MenuItem();
        saveAsMenuItem.setId("saveAsMenuItem");
        saveAsMenuItem.setDisable(true);

        exitMenuItem = new MenuItem();
        exitMenuItem.setId("exitMenuItem");
        exitMenuItem.setOnAction(event -> exitView.showView());



        this.fileMenu = new Menu();
        this.fileMenu.setId("fileMenu");
        this.fileMenu.getItems().add(newMenuItem);
        this.fileMenu.getItems().add(new SeparatorMenuItem());
        this.fileMenu.getItems().add(openMenuItem);
        this.fileMenu.getItems().add(saveMenuItem);
        this.fileMenu.getItems().add(saveAsMenuItem);
        this.fileMenu.getItems().add(new SeparatorMenuItem());
        this.fileMenu.getItems().add(exitMenuItem);
    }

    @Override
    public Node getNode() {
        return this.menuBar;
    }

    @Override
    public void update(String message) {
        /* FIXME
            if (this.gameService.isStarted()) {
                newMenuItem.disableProperty().setValue(false);
                if (!this.gameService.isGameOver()) {
                    saveMenuItem.disableProperty().setValue(false);
                    saveAsMenuItem.disableProperty().setValue(false);
                } else {
                    saveMenuItem.disableProperty().setValue(true);
                    saveAsMenuItem.disableProperty().setValue(true);
                }
            }
        */
    }

    @Override
    public void setLocalizedText() {
        // FILE MENU
        newMenuItem.setText(i18n.getString("menu.file.new"));
        openMenuItem.setText(i18n.getString("menu.file.open"));
        saveMenuItem.setText(i18n.getString("menu.file.save"));
        saveAsMenuItem.setText(i18n.getString("menu.file.save_as"));
        exitMenuItem.setText(i18n.getString("menu.file.exit"));
        fileMenu.setText(i18n.getString("menu.file"));

        // EDIT MENU
        preferencesMenuItem.setText(i18n.getString("menu.edit.preferences"));
        editMenu.setText(i18n.getString("menu.edit"));


        // HELP MENU
        helpMenu.setText(i18n.getString("menu.help"));
        helpMenuItem.setText(i18n.getString("menu.help.help"));
        aboutMenuItem.setText(i18n.getString("menu.help.about"));

    }
}
