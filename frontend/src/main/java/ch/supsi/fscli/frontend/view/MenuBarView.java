package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.controller.filesystem.IFileSystemController;
import ch.supsi.fscli.frontend.controller.mapper.FsStateMapperController;
import ch.supsi.fscli.frontend.controller.mapper.IFsStateMapperController;
import ch.supsi.fscli.frontend.event.FileSystemCreationEvent;
import ch.supsi.fscli.frontend.event.FileSystemSaved;
import ch.supsi.fscli.frontend.event.FileSystemToSaved;
import ch.supsi.fscli.frontend.util.I18nManager;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


public class MenuBarView implements ViewComponent, PropertyChangeListener {
    private final MenuBar menuBar;
    private final I18nManager i18n;
    private final IFileSystemController fileSystemController;

    // Dipendenze verso le finestre modali che il menu deve aprire
    private final ShowView exitView;
    private final ShowView creditsView;
    private final ShowView helpView;
    private final ShowView preferenceView;
    private final ShowView openView;
    private final ShowView saveAsView;

    private final IFsStateMapperController fsStateMapperController;
    // Componenti UI da aggiornare
    private Menu fileMenu, editMenu, helpMenu;
    private MenuItem newMenuItem, openMenuItem, saveMenuItem, saveAsMenuItem, exitMenuItem;
    private MenuItem preferencesMenuItem, helpMenuItem, aboutMenuItem;

    public MenuBarView(I18nManager i18n, ExitView exitView, CreditsView creditsView, OpenView openView, SaveAsView saveAsView,
                       HelpView helpView, PreferenceView preferenceView, IFileSystemController fileSystemController, IFsStateMapperController fsStateMapperController) {

        this.i18n = i18n;
        this.fileSystemController = fileSystemController;
        this.exitView = exitView;
        this.creditsView = creditsView;
        this.helpView = helpView;
        this.preferenceView = preferenceView;
        this.openView = openView;
        this.saveAsView = saveAsView;
        this.menuBar = new MenuBar();
        this.fsStateMapperController = fsStateMapperController;

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

        this.openMenuItem = new MenuItem();
        this.openMenuItem.setId("openMenuItem");
        this.openMenuItem.setOnAction(event -> openView.show());

        saveMenuItem = new MenuItem();
        saveMenuItem.setId("saveMenuItem");
        saveMenuItem.setOnAction(event -> fsStateMapperController.save());
        saveMenuItem.setDisable(true);

        this.saveAsMenuItem = new MenuItem();
        this.saveAsMenuItem.setId("saveAsMenuItem");
        this.saveAsMenuItem.setOnAction(event -> saveAsView.show());
        this.saveAsMenuItem.setDisable(true);

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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt instanceof FileSystemCreationEvent) {
            this.saveMenuItem.setDisable(false);
            this.saveAsMenuItem.setDisable(false);
            this.newMenuItem.setDisable(true);
        }
        if (evt instanceof FileSystemSaved) {
            this.saveMenuItem.setDisable(true);
            this.saveAsMenuItem.setDisable(true);
        }
        if (evt instanceof FileSystemToSaved) {
            this.saveMenuItem.setDisable(false);
            this.saveAsMenuItem.setDisable(false);
        }


    }
}
