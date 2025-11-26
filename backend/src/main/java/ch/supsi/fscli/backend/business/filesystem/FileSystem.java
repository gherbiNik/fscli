package ch.supsi.fscli.backend.business.filesystem;

import ch.supsi.fscli.backend.business.command.business.CommandExecutor;
import ch.supsi.fscli.backend.business.command.business.CommandParser;
import ch.supsi.fscli.backend.business.command.commands.CommandResult;
import ch.supsi.fscli.backend.business.command.commands.ICommand;
import ch.supsi.fscli.backend.business.service.FileSystemService;

import java.util.List;

public class FileSystem implements FileSystemComponent, IFileSystem
{
    private static FileSystem instance;
    private final DirectoryNode root;
    private DirectoryNode currentDirectory;
    private CommandExecutor commandExecutor;
    // TODO: creare la lista dei comandi
    private List<ICommand> commandList;


    public static FileSystem getInstance() {
        if (instance == null) {
            instance = new FileSystem();
        }
        return instance;
    }

    private FileSystem(){
        root = new DirectoryNode(null);
        currentDirectory = root;
        this.commandExecutor = CommandExecutor.getInstance(FileSystemService.getInstance(this), CommandParser.getInstance(), commandList);
    }

    public DirectoryNode getRoot() {
        return root;
    }

    public DirectoryNode getCurrentDirectory() {
        return currentDirectory;
    }

    public String getCurrentDirectoryAbsolutePath(){
        StringBuilder result = new StringBuilder();
        DirectoryNode currentDirectoryForString = getCurrentDirectory();
        DirectoryNode parent = currentDirectoryForString.getParent();

        if (parent == null) {
            return "/";
        }

        while(parent != null){
            result.insert(0, "/"+parent.getINodeName(currentDirectoryForString));
            currentDirectoryForString = parent;
            parent = currentDirectoryForString.getParent();
        }
        return result.toString();
    }

    public void changeDirectory(String path) {
        if(findDirectoryByPath(path) == null)
            throw new IllegalArgumentException();

        this.currentDirectory = findDirectoryByPath(path);
    }

    public Inode resolveNode(String path) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }

        DirectoryNode startNode;
        String effectivePath;

        if (path.startsWith("/")) {
            startNode = this.root;
            effectivePath = path.length() > 1 ? path.substring(1) : "";
        } else {
            startNode = this.currentDirectory;
            effectivePath = path;
        }

        if (effectivePath.isEmpty()) {
            return startNode;
        }

        String[] parts = effectivePath.split("/");
        Inode currentNode = startNode;

        for (String part : parts) {
            if (part.isEmpty() || part.equals(".")) {
                continue;
            }

            if (!(currentNode instanceof DirectoryNode)) {
                return null; // Percorso non valido (es. /file.txt/qualcosa)
            }

            DirectoryNode currentDir = (DirectoryNode) currentNode;

            if (part.equals("..")) {
                DirectoryNode parent = currentDir.getParent();
                if (parent == null) {
                    // Sei alla root, il genitore della root è la root stessa
                    currentNode = this.root;
                } else {
                    currentNode = parent;
                }
            } else {
                currentNode = currentDir.getChild(part);

                if (currentNode == null) {
                    return null;
                }
            }
        }

        return currentNode;
    }

    private DirectoryNode findDirectoryByPath(String path) {
        Inode node = resolveNode(path);

        if (node instanceof DirectoryNode) {
            return (DirectoryNode) node;
        }

        return null;
    }

    @Override
    public String toString() {
        return "FileSystem{" +
                "root=" + root +
                '}';
    }

    public Map<String, Inode> getCurrentDirectoryTable() {
        return currentDirectory.getChildren();
    }

    public  Map<String, Inode> getChildInodeTable(String path) {
        DirectoryNode dir = findDirectoryByPath(path);
        return  dir == null ? null : dir.getChildren();
    }

    @Override
    public String executeCommand(String command) {
        CommandResult result = commandExecutor.execute(command);

        if(result.isSuccess())
            return result.getOutput();
        else
            return result.getError();
    }
}
