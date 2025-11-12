package ch.supsi.fscli.backend.business.service;

import ch.supsi.fscli.backend.business.filesystem.DirectoryNode;
import ch.supsi.fscli.backend.business.filesystem.FileNode;
import ch.supsi.fscli.backend.business.filesystem.FileSystem;
import ch.supsi.fscli.backend.business.filesystem.IDirectoryNode;

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

    public void createFile(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be empty");
        }

        DirectoryNode currentDir = fileSystem.getCurrentDirectory();

        // File already exists
        if (currentDir.getChild(fileName) != null) {
            throw new IllegalArgumentException("File already exists");
        }

        // Creates new File
        FileNode newFile = new FileNode(currentDir);
        currentDir.addChild(fileName, newFile);
    }

    public void removeFile(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be empty");
        }

        DirectoryNode currentDir = fileSystem.getCurrentDirectory();

        // File does not exists
        if (currentDir.getChild(fileName) == null) {
            throw new IllegalArgumentException("File does not exists");
        }

        // Remove file
        FileNode nodeToRemove = (FileNode) currentDir.getChild(fileName);
        currentDir.removeChild(fileName, nodeToRemove);
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

    public boolean removeDirectory(String directoryName) {
        DirectoryNode currentDir = fileSystem.getCurrentDirectory();

        if(currentDir.getChild(directoryName) == null) {
            throw new IllegalArgumentException("Directory does not exists");
        }

        DirectoryNode childDir = (DirectoryNode) currentDir.getChild(directoryName);
        if(childDir.getNumChild() > 0) {
            return false;  // Directory not empty
        }
        currentDir.removeChild(directoryName,childDir);
        return true;
    }
}
