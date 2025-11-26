package ch.supsi.fscli.backend.application.filesystem;

import ch.supsi.fscli.backend.business.filesystem.FileSystem;
import ch.supsi.fscli.backend.business.filesystem.IFileSystem;

public class FileSystemApplication implements IFileSystemApplication {

    private static FileSystemApplication instance;
    private IFileSystem fileSystem;

    private FileSystemApplication() {}


    public static FileSystemApplication getInstance() {
        if (instance == null) {
            instance = new FileSystemApplication();
        }
        return instance;
    }

    @Override
    public void createFileSystem() {
        fileSystem = FileSystem.getInstance();
        System.out.println(fileSystem);
    }

    @Override
    public String sendCommand(String userInput) {
        return fileSystem.executeCommand(userInput);
    }
}
