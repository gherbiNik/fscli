package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.controller.filesystem.IFileSystemController;
import ch.supsi.fscli.frontend.util.I18nManager;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;


public class MenuBarView implements ViewComponent {
    private final MenuBar menuBar;
    private final I18nManager i18n;
    private final IFileSystemController fileSystemController;

    // Dipendenze verso le finestre modali che il menu deve aprire
    private final ShowView exitView;
    private final ShowView creditsView;
    private final ShowView helpView;
    private final ShowView preferenceView;

    // Componenti UI da aggiornare
    private Menu fileMenu, editMenu, helpMenu;
    private MenuItem newMenuItem, openMenuItem, saveMenuItem, saveAsMenuItem, exitMenuItem;
    private MenuItem preferencesMenuItem, helpMenuItem, aboutMenuItem;

    public MenuBarView(I18nManager i18n, ExitView exitView, CreditsView creditsView,
                       HelpView helpView, PreferenceView preferenceView, IFileSystemController fileSystemController) {

        this.i18n = i18n;
        this.fileSystemController = fileSystemController;
        this.exitView = exitView;
        this.creditsView = creditsView;
        this.helpView = helpView;
        this.preferenceView = preferenceView;
        this.menuBar = new MenuBar();

        createStructure();     // Crea gli oggetti Menu/MenuItem
        setupEventHandlers();  // Collega le azioni ai bottoni
    }

    private void setupEventHandlers() {
        exitMenuItem.setOnAction(e -> exitView.show());
        preferencesMenuItem.setOnAction(e -> preferenceView.show());
        aboutMenuItem.setOnAction(e -> creditsView.show());
        helpMenuItem.setOnAction(e -> helpView.show());
        newMenuItem.setOnAction(e -> fileSystemController.createFileSystem());
    }

    private void createStructure() {
        initFileMenu();
        initEditMenu();
        initHelpMenu();
        setLocalizedText();
        menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);
    }


    private void initHelpMenu() {
        helpMenu = new Menu();
        helpMenuItem = new MenuItem();
        aboutMenuItem = new MenuItem();
        helpMenu.getItems().add(helpMenuItem);
        helpMenu.getItems().add(aboutMenuItem);

        helpMenuItem.setOnAction(event -> helpView.show());
        aboutMenuItem.setOnAction(event -> creditsView.show());

    }

    private void initEditMenu() {
        editMenu = new Menu();
        preferencesMenuItem = new MenuItem();
        editMenu.getItems().add(preferencesMenuItem);
        preferencesMenuItem.setOnAction(actionEvent -> preferenceView.show());
    }

    private void initFileMenu() {
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
        exitMenuItem.setOnAction(event -> exitView.show());



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
