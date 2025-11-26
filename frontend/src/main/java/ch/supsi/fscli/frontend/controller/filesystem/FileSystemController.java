package ch.supsi.fscli.frontend.controller.filesystem;

import ch.supsi.fscli.frontend.model.filesystem.IFileSystemModel;
import ch.supsi.fscli.frontend.util.I18nManager;
import ch.supsi.fscli.frontend.view.DataView;
import ch.supsi.fscli.frontend.view.OutputView;

public class FileSystemController implements IFileSystemController {
    private static FileSystemController instance;
    private final IFileSystemModel fileSystemModel;
    private final I18nManager i18n;

    private final DataView outputView;
    private final DataView logView;

    public static FileSystemController getInstance(IFileSystemModel fileSystemModel, DataView outputView, DataView logView, I18nManager i18n) {
        if (instance == null) {
            instance = new FileSystemController(fileSystemModel, outputView, logView, i18n);
        }
        return instance;
    }

    private FileSystemController(IFileSystemModel fileSystemModel, DataView outputView, DataView logView, I18nManager i18n) {
        this.fileSystemModel = fileSystemModel;
        this.outputView = outputView;
        this.logView = logView;
        this.i18n = i18n;
    }

    @Override
    public void createFileSystem() {
        fileSystemModel.createFileSystem();
        // FIXME: is casting to (OutputView) good?
        ((OutputView)outputView).clear();
        logView.update(i18n.getString("log.fsCreated"));
    }

    @Override
    public void sendCommand(String userInput) {
        String result = fileSystemModel.sendCommand(userInput);
        outputView.update(result + "\n");
    }


}
