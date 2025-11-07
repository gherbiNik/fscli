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

    // METODI ad esempio
    // createDirectory
    // remove directory
    // interazione con il filesystem


}
