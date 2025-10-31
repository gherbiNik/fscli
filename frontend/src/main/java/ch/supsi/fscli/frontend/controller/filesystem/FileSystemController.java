package ch.supsi.fscli.frontend.controller.filesystem;

import ch.supsi.fscli.frontend.model.filesystem.IFileSystemModel;

public class FileSystemController implements IFileSystemController {
    private static FileSystemController instance;
    private final IFileSystemModel fileSystemModel;

    public static FileSystemController getInstance(IFileSystemModel fileSystemModel) {
        if (instance == null) {
            instance = new FileSystemController(fileSystemModel);
        }
        return instance;
    }

    private FileSystemController(IFileSystemModel fileSystemModel) {
        this.fileSystemModel = fileSystemModel;
    }

    @Override
    public void createFileSystem() {
        fileSystemModel.createFileSystem();
    }

}
