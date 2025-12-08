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

    public void changeDirectory(String newDirPath) {
        // 1. Risoluzione Inode dal path stringa
        Inode target = getInode(newDirPath);

        if (target == null) {
            throw new IllegalArgumentException("No such file or directory: " + newDirPath);
        }

        // 2. Seguiamo i link (se presenti)
        Inode resolved = followLink(target);
        if (resolved == null) {
            throw new IllegalArgumentException("No such file or directory (broken link)");
        }

        // 3. Verifica fondamentale: DEVE essere una Directory
        if (!resolved.isDirectory()) {
            throw new IllegalArgumentException("Not a directory: " + newDirPath);
        }

        // 4. Casting sicuro a DirectoryNode
        // Ora che sappiamo che è una directory, possiamo usarla come tale per risalire i padri
        DirectoryNode dirNode = (DirectoryNode) resolved;

        // 5. Calcoliamo il path assoluto reale (senza link)
        String realPath = calculateAbsolutePath(dirNode);

        // 6. Eseguiamo il cambio directory sul FileSystem
        fileSystem.changeDirectory(realPath);
    }

    /**
     * Ricostruisce il percorso assoluto partendo da un DirectoryNode e risalendo tramite getParent().
     * Accetta SOLO DirectoryNode perché Inode non ha getParent().
     */
    private String calculateAbsolutePath(DirectoryNode directory) {
        // Caso base: siamo già alla root
        if (directory == fileSystem.getRoot()) {
            return "/";
        }

        StringBuilder path = new StringBuilder();
        DirectoryNode current = directory;

        // Risaliamo finché il padre non è null (la root ha parent null o gestito internamente)
        // Nota: current deve essere DirectoryNode per chiamare getParent()
        while (current.getParent() != null) {
            DirectoryNode parent = current.getParent();

            // Troviamo il nome della cartella corrente guardando dentro al padre
            String name = parent.getINodeName(current);

            if (name != null) {
                path.insert(0, "/" + name);
            }

            // Saliamo di un livello
            current = parent;
        }

        return path.toString();
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

    public void move(String sourcePath, String destinationPath) {
        // resolves source node
        Inode nodeToMove = fileSystem.resolveNode(sourcePath);
        if(nodeToMove == null) {
            throw new IllegalArgumentException("Source not found: " + sourcePath);
        }
        PathParts sourceParts = resolveParentDirectoryAndName(sourcePath);
        DirectoryNode sourceParent = sourceParts.parentDir();
        String sourceName = sourceParts.name();

        // resolves destination
        Inode destination = fileSystem.resolveNode(destinationPath);
        DirectoryNode targetDir;
        String newName;
        
        if(destination != null && destination.isDirectory()){
            // Case 1: the destination directory exists
            targetDir = (DirectoryNode) destination;
            newName = sourceName;
        } else if (destination == null) {
            // Case 2: Destination doesn't exist.
            // Could be RENAME or MOVE with new name
            PathParts destinationParts = resolveParentDirectoryAndName(destinationPath);
            targetDir = destinationParts.parentDir();
            newName = destinationParts.name();

            if(targetDir.getChild(newName) != null){
                // target already exists
                throw new IllegalArgumentException("Destination already exists: " + destinationPath);
            }
        } else {
            // Case 3: destination exists but is a file
            throw new IllegalArgumentException("Cannot overwrite non-directory: " + destinationPath);
        }

        // Prevent moving a directory into itself or its subdirectory
        if (nodeToMove.isDirectory() && isSubdirectory((DirectoryNode) nodeToMove, targetDir)) {
            throw new IllegalArgumentException("Cannot move directory into itself or its subdirectory");
        }

        // Perform the move/rename
        sourceParent.removeChild(sourceName, nodeToMove);
        targetDir.addChild(newName, nodeToMove);

        // Update parent reference (if it's a directory)
        if (nodeToMove instanceof DirectoryNode) {
            //((DirectoryNode) nodeToMove).setParent(targetDir);
        }

    }

    private boolean isSubdirectory(DirectoryNode parent, DirectoryNode potentialChild) {
        DirectoryNode current = potentialChild;
        while (current != null) {
            if (current == parent) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }

    public record PathParts(DirectoryNode parentDir, String name) {}

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

        if(nodeToRemove.isSoftLink()){
            throw new IllegalArgumentException("Specified item is a link: " + directoryName);
        }

        if (!nodeToRemove.isDirectory()) {
            throw new IllegalArgumentException("Specified item is a directory: " + directoryName);
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

    public Inode followLink(Inode inode) {
        return fileSystem.followLink(inode);
    }

    public void setDataToSave(boolean dataToSave) {
        this.fileSystem.setDataToSave(dataToSave);
    }


}
