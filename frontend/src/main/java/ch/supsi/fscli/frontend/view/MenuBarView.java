package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.controller.filesystem.IFileSystemController;
import ch.supsi.fscli.frontend.controller.mapper.FsStateMapperController;
import ch.supsi.fscli.frontend.controller.mapper.IFsStateMapperController;
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
    private IFsStateMapperController fsStateMapperController;

    public static MenuBarView getInstance(I18nManager i18n, ExitView exitView, CreditsView creditsView,
    HelpView helpView, PreferenceView preferenceView, IFileSystemController fileSystemController, OpenView openView,
                                          SaveAsView saveAsView, IFsStateMapperController fsStateMapperController) {
        if(instance == null) {
            instance = new MenuBarView();
            instance.initialize(i18n, exitView, creditsView, helpView, preferenceView, fileSystemController, openView, saveAsView, fsStateMapperController);
        }
        return instance;
    }

     public void initialize(I18nManager i18n, ExitView exitView, CreditsView creditsView,
            HelpView helpView, PreferenceView preferenceView, IFileSystemController fileSystemController,
                            OpenView openView, SaveAsView saveAsView,IFsStateMapperController fsStateMapperController) {

        this.i18n = i18n;
        this.fileSystemController = fileSystemController;
        this.menuBar = new MenuBar();
        this.fsStateMapperController = fsStateMapperController;

        initFileMenu(exitView, openView, saveAsView);
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

    private void initFileMenu(ExitView exitView, OpenView openView, SaveAsView saveAsView) {
        newMenuItem = new MenuItem();
        newMenuItem.setId("newMenuItem");
        newMenuItem.setOnAction(event -> fileSystemController.createFileSystem());

        this.openMenuItem = new MenuItem();
        this.openMenuItem.setId("openMenuItem");
        this.openMenuItem.setOnAction(event -> openView.showView());

        saveMenuItem = new MenuItem();
        saveMenuItem.setId("saveMenuItem");
        saveMenuItem.setOnAction(event -> fsStateMapperController.save());
        saveMenuItem.setDisable(true);

        this.saveAsMenuItem = new MenuItem();
        this.saveAsMenuItem.setId("saveAsMenuItem");
        this.saveAsMenuItem.setOnAction(event -> saveAsView.showView());
        this.saveAsMenuItem.setDisable(true);

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

        if (this.fileSystemController.hasDataToSave()) {
            this.saveMenuItem.setDisable(false);
            this.saveAsMenuItem.setDisable(false);
            this.newMenuItem.setDisable(true);
        } else {
            this.saveMenuItem.setDisable(true);
            this.saveAsMenuItem.setDisable(true);
        }
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
