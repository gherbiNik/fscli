package ch.supsi.fscli.frontend.model.filesystem;

import ch.supsi.fscli.backend.application.filesystem.IFileSystemApplication;

public class FileSystemModel implements IFileSystemModel {

    private static FileSystemModel instance;
    private final IFileSystemApplication application;

    public static FileSystemModel getInstance(IFileSystemApplication application) {
        if (instance == null) {
            instance = new FileSystemModel(application);
        }
        return instance;
    }

    private FileSystemModel(IFileSystemApplication application) {
        this.application = application;
    }


    @Override
    public void createFileSystem() {
        application.createFileSystem();
    }

    @Override
    public String sendCommand(String userInput) {
        return application.sendCommand(userInput);
    }
}
