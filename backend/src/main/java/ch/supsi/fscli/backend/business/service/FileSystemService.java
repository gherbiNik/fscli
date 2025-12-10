package ch.supsi.fscli.backend.business.service;

import ch.supsi.fscli.backend.business.filesystem.*;
import ch.supsi.fscli.backend.util.BackendTranslator;

import java.util.Map;

public class FileSystemService implements IFileSystemService{

    private static FileSystemService instance;
    private FileSystem fileSystem;
    private static BackendTranslator i18n;

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

    public static void setTranslator(BackendTranslator translator) {
        FileSystemService.i18n = translator;
    }

    public void changeDirectory(String newDirPath) {
        // 1. Risoluzione Inode dal path stringa
        Inode target = getInode(newDirPath);

        if (target == null) {
            // "No such file or directory: path"
            throw new IllegalArgumentException(i18n.getString("no_such_file_or_directory") + ": " + newDirPath);
        }

        // 2. Seguiamo i link (se presenti)
        Inode resolved = followLink(target);
        if (resolved == null) {
            // "No such file or directory (broken link)"
            throw new IllegalArgumentException(i18n.getString("no_such_file_or_directory") + " (" + i18n.getString("broken_link") + ")");
        }

        // 3. Verifica fondamentale: DEVE essere una Directory
        if (!resolved.isDirectory()) {
            // "Not a directory: path"
            throw new IllegalArgumentException(i18n.getString("not_a_directory") + ": " + newDirPath);
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
        // 1. Risoluzione Nodo Sorgente
        // Nota: resolveNode restituisce il Link stesso se la sorgente è un link (corretto per mv)
        Inode nodeToMove = fileSystem.resolveNode(sourcePath);

        if(nodeToMove == null) {
            //throw new IllegalArgumentException("cannot stat '" + sourcePath + "': No such file or directory");
            throw new IllegalArgumentException(i18n.getString("cannot_stat_prefix") + sourcePath + i18n.getString("no_such_file_suffix"));
        }

        // Otteniamo il genitore e il nome attuale per poterlo rimuovere dopo
        PathParts sourceParts = resolveParentDirectoryAndName(sourcePath);
        DirectoryNode sourceParent = sourceParts.parentDir();
        String sourceName = sourceParts.name();

        // 2. Risoluzione Nodo Destinazione
        Inode rawDestNode = fileSystem.resolveNode(destinationPath);

        DirectoryNode targetDir = null;
        String newName = "";
        boolean moveIntoDirectory = false;

        // Analizziamo la destinazione per capire se è una cartella (o link a cartella)
        if (rawDestNode != null) {
            Inode effectiveDest = rawDestNode;

            // Se è un link, vediamo cosa c'è dietro SOLO per capire se è una directory
            if (rawDestNode instanceof SoftLink) {
                Inode resolved = followLink(rawDestNode);
                if (resolved != null) {
                    effectiveDest = resolved;
                }
                // Se resolved è null (broken link), trattiamo rawDestNode come un file da sovrascrivere
            }
            else if (effectiveDest.isDirectory()) {
                // CASO A: Spostamento DENTRO una directory esistente
                moveIntoDirectory = true;
                targetDir = (DirectoryNode) effectiveDest;
                newName = sourceName; // Mantiene il nome originale
            }
            else { // is a file
                PathParts destParts = resolveParentDirectoryAndName(destinationPath);
                String destname = destParts.name();
                throw new IllegalArgumentException(i18n.getString("item_is_file_prefix") + destname);
            }
        }

        if (!moveIntoDirectory) {
            // CASO B: Rename o Overwrite (Destinazione è un file, un link a file, o non esiste)
            PathParts destParts = resolveParentDirectoryAndName(destinationPath);
            targetDir = destParts.parentDir();
            newName = destParts.name();
        }

        // 3. Controllo Cicli (Non spostare una directory dentro se stessa)
        if (nodeToMove instanceof DirectoryNode && isSubdirectory((DirectoryNode) nodeToMove, targetDir)) {
            //throw new IllegalArgumentException("cannot move '" + sourcePath + "' to a subdirectory of itself");
            throw new IllegalArgumentException(i18n.getString("cannot_move_prefix") + sourcePath + i18n.getString("subdirectory_of_itself_suffix"));
        }

        // 4. Gestione Sovrascrittura (Overwrite)
        Inode existingNode = targetDir.getChild(newName);
        if (existingNode != null) {
            if (existingNode == nodeToMove) {
                return; // Stesso file, non fare nulla
            }
            if (existingNode.isDirectory()) {
                // Non si può sovrascrivere una directory con un file (o altra dir)
                //throw new IllegalArgumentException("cannot overwrite directory '" + newName + "' with non-directory");
                throw new IllegalArgumentException(i18n.getString("cannot_overwrite_directory_prefix") + newName + i18n.getString("with_non_directory_suffix"));
            }

            // Rimuovi il file/link esistente per far spazio al nuovo
            targetDir.removeChild(newName, existingNode);
        }

        // 5. Esecuzione Spostamento Atomic-like
        sourceParent.removeChild(sourceName, nodeToMove);
        targetDir.addChild(newName, nodeToMove);

        // Opzionale: Aggiornamento parent se il nodo spostato è una directory
        if (nodeToMove instanceof DirectoryNode) {
            ((DirectoryNode) nodeToMove).setParent(targetDir);
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



    private PathParts resolveParentDirectoryAndName(String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException(i18n.getString("path_cannot_be_empty"));
            //throw new IllegalArgumentException("Path cannot be empty");
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
                //throw new IllegalArgumentException("Directory not found: " + parentPath);
                throw new IllegalArgumentException(i18n.getString("directory_not_found_prefix") + parentPath);
            }
            if (!(parentNode instanceof DirectoryNode)) {
                //throw new IllegalArgumentException("Path is not a directory: " + parentPath);
                throw new IllegalArgumentException(i18n.getString("path_not_directory_prefix") + parentPath);
            }
            parentDir = (DirectoryNode) parentNode;

        } else {
            // È un percorso semplice (es. "file.txt")
            name = path;
            parentDir = fileSystem.getCurrentDirectory();
        }

        // Il nome non può essere vuoto o un operatore di navigazione
        if (name.isEmpty() || name.equals(".") || name.equals("..")) {
            //throw new IllegalArgumentException("Invalid name: " + name);
            throw new IllegalArgumentException(i18n.getString("invalid_name_prefix") + name);
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
            //throw new IllegalArgumentException("File already exists: " + fileName);
            throw new IllegalArgumentException(i18n.getString("file_already_exists_prefix") + fileName);
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
            throw new IllegalArgumentException(i18n.getString("file_does_not_exist_prefix") + fileName);
            //throw new IllegalArgumentException("File does not exist: " + fileName);
        }

        if (nodeToRemove instanceof DirectoryNode) {
            throw new IllegalArgumentException(i18n.getString("item_is_directory"));
            //throw new IllegalArgumentException("Specified item is a directory");
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
            throw new IllegalArgumentException(i18n.getString("directory_already_exists_prefix") + directoryName);
            //throw new IllegalArgumentException("Directory already exists: " + directoryName);
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
            throw new IllegalArgumentException(i18n.getString("directory_does_not_exist_prefix") + directoryName);
            //throw new IllegalArgumentException("Directory does not exist: " + directoryName);
        }

        if(nodeToRemove.isSoftLink()){
            throw new IllegalArgumentException(i18n.getString("item_is_link_prefix") + directoryName);
            //throw new IllegalArgumentException("Specified item is a link: " + directoryName);
        }

        if (!nodeToRemove.isDirectory()) {
            throw new IllegalArgumentException(i18n.getString("item_is_directory_prefix") + directoryName);
            //throw new IllegalArgumentException("Specified item is a directory: " + directoryName);
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
