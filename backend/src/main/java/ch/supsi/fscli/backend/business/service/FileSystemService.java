package ch.supsi.fscli.backend.business.service;

import ch.supsi.fscli.backend.business.filesystem.DirectoryNode;
import ch.supsi.fscli.backend.business.filesystem.FileSystem;

public class FileSystemService {

    private static FileSystemService instance;
    private FileSystem fileSystem;

    private FileSystemService() {
    }

    public static FileSystemService getInstance(FileSystem fileSystem) {
        if (instance == null) {
            instance = new FileSystemService();
            instance.initialize(fileSystem);
        }
        return instance;
    }

    private void initialize(FileSystem fileSystem){
        this.fileSystem = fileSystem;
    }

    public DirectoryNode getCurrentDirectory() {
        return fileSystem.getCurrentDirectory();
    }



    public void createDirectory(String directoryName) {
        if (directoryName == null || directoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Directory name cannot be empty");
        }

        DirectoryNode currentDir = fileSystem.getCurrentDirectory();

        // Directory already exists
        if (currentDir.getChild(directoryName) != null) {
            throw new IllegalArgumentException("Directory already exists");
        }

        // Creates new directory
        DirectoryNode newDirectory = new DirectoryNode(currentDir);
        currentDir.addChild(directoryName, newDirectory);
    }

}
