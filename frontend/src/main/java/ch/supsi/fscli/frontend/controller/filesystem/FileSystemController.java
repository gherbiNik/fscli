package ch.supsi.fscli.frontend.controller.filesystem;

import ch.supsi.fscli.frontend.model.filesystem.IFileSystemModel;
import ch.supsi.fscli.frontend.util.I18nManager;
import ch.supsi.fscli.frontend.view.CommandLineView;
import ch.supsi.fscli.frontend.view.LogView;
import ch.supsi.fscli.frontend.view.OutputView;
import ch.supsi.fscli.frontend.view.ViewComponent;

public class FileSystemController implements IFileSystemController {
    private static FileSystemController instance;
    private final IFileSystemModel fileSystemModel;
    private final I18nManager i18n;

    private final OutputView outputView;
    private final LogView logView;
    private CommandLineView commandLineView;

    public static FileSystemController getInstance(IFileSystemModel fileSystemModel, OutputView outputView, LogView logView, I18nManager i18n) {
        if (instance == null) {
            instance = new FileSystemController(fileSystemModel, outputView, logView, i18n);
        }
        return instance;
    }

    private FileSystemController(IFileSystemModel fileSystemModel, OutputView outputView, LogView logView, I18nManager i18n) {
        this.fileSystemModel = fileSystemModel;
        this.outputView = outputView;
        this.logView = logView;
        this.i18n = i18n;
    }

    public void setCommandLineView(CommandLineView commandLineView) {
        this.commandLineView = commandLineView;
    }

    @Override
    public void createFileSystem() {
        fileSystemModel.createFileSystem();
        // TODO spostare da qua alle view e sistemare tutto
        commandLineView.setDisable(false);
        outputView.clear();
        logView.log(i18n.getString("log.fsCreated"));
    }

    @Override
    public void sendCommand(String userInput) {
        fileSystemModel.sendCommand(userInput);

    }


}
