package ch.supsi.fscli.frontend.controller.filesystem;

import ch.supsi.fscli.frontend.model.filesystem.IFileSystemModel;
import ch.supsi.fscli.frontend.util.I18nManager;
import ch.supsi.fscli.frontend.view.CommandLineView;
import ch.supsi.fscli.frontend.view.ControlledFxView;
import ch.supsi.fscli.frontend.view.DataView;
import ch.supsi.fscli.frontend.view.OutputView;

public class FileSystemController implements IFileSystemController {
    private static FileSystemController instance;
    private final IFileSystemModel fileSystemModel;
    private final I18nManager i18n;

    private ControlledFxView commandLineView;
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

    public void setCommandLineView(CommandLineView commandLineView) {
        this.commandLineView = commandLineView;
    }

    @Override
    public void createFileSystem() {
        fileSystemModel.createFileSystem();
        // FIXME: è giusto castare?
        ((CommandLineView) commandLineView).setDisable(false);
        ((OutputView)outputView).clear();
        logView.update(i18n.getString("log.fsCreated"));
    }

    @Override
    public void sendCommand(String userInput) {
        String result = fileSystemModel.sendCommand(userInput);
        if(result.equals("Perform Clear")){
            ((OutputView)outputView).clear();
            return;
        }
        outputView.update("<user> " + userInput + "\n" + result + "\n\n");
    }


}
