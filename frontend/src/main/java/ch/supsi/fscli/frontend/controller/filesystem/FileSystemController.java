package ch.supsi.fscli.frontend.controller.filesystem;

import ch.supsi.fscli.frontend.model.filesystem.IFileSystemModel;
import ch.supsi.fscli.frontend.util.I18nManager;
import ch.supsi.fscli.frontend.view.CommandLineView;
import ch.supsi.fscli.frontend.view.LogView;
import ch.supsi.fscli.frontend.view.OutputView;
import ch.supsi.fscli.frontend.view.ViewComponent;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class FileSystemController implements IFileSystemController {
    private final IFileSystemModel fileSystemModel;
    private final I18nManager i18n;

    private final OutputView outputView;
    private final LogView logView;
    private CommandLineView commandLineView;

    @Inject
    public FileSystemController(IFileSystemModel fileSystemModel, OutputView outputView, LogView logView, I18nManager i18n) {
        this.fileSystemModel = fileSystemModel;
        this.outputView = outputView;
        this.logView = logView;
        this.i18n = i18n;
    }

    // Setter Injection per rompere la dipendenza circolare
    // Guice chiamerà questo metodo automaticamente dopo aver costruito l'oggetto!
    @Inject
    public void setCommandLineView(CommandLineView commandLineView) {
        this.commandLineView = commandLineView;
    }

    @Override
    public void createFileSystem() {
        fileSystemModel.createFileSystem();
    }

    @Override
    public void sendCommand(String userInput) {
        fileSystemModel.sendCommand(userInput);

    }

    public boolean hasDataToSave(){
        return fileSystemModel.isDataToSave();
    }

}
