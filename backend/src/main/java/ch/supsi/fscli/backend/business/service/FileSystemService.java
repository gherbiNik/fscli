package ch.supsi.fscli.backend.business.service;

import ch.supsi.fscli.backend.business.filesystem.*;

import java.util.Map;

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

    public Map<String, Inode> getINodeTableCurrentDir() {

        return fileSystem.getCurrentDirectoryTable();
    }

    public Map<String, Inode> getChildInodeTable(String path) {

        return fileSystem.getChildInodeTable(path);
    }

    public Inode getCurrentDirInode() {
        return fileSystem.getCurrentDirectory();
    }

    public Inode getInode(String targetPath) {
        return fileSystem.resolveNode(targetPath);
    }


    private record PathParts(DirectoryNode parentDir, String name) {}

    private PathParts resolveParentDirectoryAndName(String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Path cannot be empty");
        }

        String name;
        DirectoryNode parentDir;

        if (path.contains("/")) {
            // È un percorso complesso (es. "docs/file.txt" o "/file.txt")
            String parentPath = path.substring(0, path.lastIndexOf('/'));
            name = path.substring(path.lastIndexOf('/') + 1);

            if (parentPath.isEmpty()) {
                parentPath = "/";
            }

            Inode parentNode = fileSystem.resolveNode(parentPath);

            if (parentNode == null) {
                throw new IllegalArgumentException("Directory not found: " + parentPath);
            }
            if (!(parentNode instanceof DirectoryNode)) {
                throw new IllegalArgumentException("Path is not a directory: " + parentPath);
            }
            parentDir = (DirectoryNode) parentNode;

        } else {
            // È un percorso semplice (es. "file.txt")
            name = path;
            parentDir = fileSystem.getCurrentDirectory();
        }

        // Il nome non può essere vuoto o un operatore di navigazione
        if (name.isEmpty() || name.equals(".") || name.equals("..")) {
            throw new IllegalArgumentException("Invalid name: " + name);
        }

        return new PathParts(parentDir, name);
    }

    public void createFile(String path) {
        // 1. Risolvi percorso genitore e nome
        PathParts parts = resolveParentDirectoryAndName(path);
        DirectoryNode targetDir = parts.parentDir();
        String fileName = parts.name();

        // 2. Esegui la logica (ora sulla directory corretta)
        if (targetDir.getChild(fileName) != null) {
            throw new IllegalArgumentException("File already exists: " + fileName);
        }

        FileNode newFile = new FileNode(targetDir);
        targetDir.addChild(fileName, newFile);
    }

    public void  removeFile(String path) {
        // 1. Risolvi percorso genitore e nome
        PathParts parts = resolveParentDirectoryAndName(path);
        DirectoryNode targetDir = parts.parentDir();
        String fileName = parts.name();

        // 2. Esegui la logica (ora sulla directory corretta)
        Inode nodeToRemove = targetDir.getChild(fileName);

        if (nodeToRemove == null) {
            throw new IllegalArgumentException("File does not exist: " + fileName);
        }

        if (nodeToRemove instanceof DirectoryNode) {
            throw new IllegalArgumentException("Specified item is a directory");
        }

        targetDir.removeChild(fileName, nodeToRemove);
    }

    public void createDirectory(String path) {
        // 1. Risolvi percorso genitore e nome
        PathParts parts = resolveParentDirectoryAndName(path);
        DirectoryNode targetDir = parts.parentDir();
        String directoryName = parts.name();

        // 2. Esegui la logica (ora sulla directory corretta)
        if (targetDir.getChild(directoryName) != null) {
            throw new IllegalArgumentException("Directory already exists: " + directoryName);
        }

        DirectoryNode newDirectory = new DirectoryNode(targetDir);
        targetDir.addChild(directoryName, newDirectory);
    }

    public boolean removeDirectory(String path) {
        // 1. Risolvi percorso genitore e nome
        PathParts parts = resolveParentDirectoryAndName(path);
        DirectoryNode targetDir = parts.parentDir();
        String directoryName = parts.name();

        // 2. Esegui la logica (ora sulla directory corretta)
        Inode nodeToRemove = targetDir.getChild(directoryName);

        if(nodeToRemove == null) {
            throw new IllegalArgumentException("Directory does not exist: " + directoryName);
        }

        if (!(nodeToRemove instanceof DirectoryNode)) {
            throw new IllegalArgumentException("Specified item is a file: " + directoryName);
        }

        DirectoryNode childDir = (DirectoryNode) nodeToRemove;
        if(childDir.getNumChild() > 2) {
            return false;  // Directory not empty
        }
        targetDir.removeChild(directoryName, childDir);
        return true;
    }

    public String getCurrentDirectoryAbsolutePath(){
        return fileSystem.getCurrentDirectoryAbsolutePath();
    }

}
