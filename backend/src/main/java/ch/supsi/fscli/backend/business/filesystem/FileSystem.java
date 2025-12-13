package ch.supsi.fscli.backend.business.filesystem;

import ch.supsi.fscli.backend.business.command.business.CommandExecutor;
import ch.supsi.fscli.backend.business.command.commands.CommandResult;
import ch.supsi.fscli.backend.business.command.commands.ICommand;
import ch.supsi.fscli.backend.util.BackendTranslator;
import com.google.inject.Singleton;

import java.util.List;
import java.util.Map;

@Singleton
public class FileSystem implements FileSystemComponent, IFileSystem
{
    private DirectoryNode root;
    private DirectoryNode currentDirectory;
    private CommandExecutor commandExecutor;
    private List<ICommand> commandList; // FIXME: perché non è utilizzata?
    private boolean dataToSave;
    private static BackendTranslator i18n;


    public FileSystem(){
        root = new DirectoryNode(null);
        dataToSave = true;
        currentDirectory = root;
    }

    public void setCommandExecutor(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
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
                if (currentNode instanceof SoftLink) {
                    Inode resolved = followLink(currentNode);
                    if (resolved instanceof DirectoryNode) {
                        currentNode = resolved; // Sostituisci il link con la directory vera e prosegui
                    } else {
                        return null; // Link rotto o punta a un file
                    }
                } else {
                    return null; // E' un file normale, non posso entrarci
                }
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

    public Inode followLink(Inode inode) {
        int maxDepth = 10; // Prevenzione loop infiniti (link A -> link B -> link A)
        int depth = 0;

        Inode current = inode;

        // Continua a seguire finché è un SoftLink
        while (current instanceof SoftLink) {
            if (depth > maxDepth) {
                //throw new IllegalArgumentException("Too many levels of symbolic links");
                throw new IllegalArgumentException(i18n.getString("too_many_symlinks"));
            }

            // Recupera il path salvato nel SoftLink
            String targetPath = ((SoftLink) current).getTargetPath();

            // Risolve il path target

            Inode target = resolveNode(targetPath);

            if (target == null) {
                //il link punta a qualcosa che non esiste
                return null;
            }

            current = target;
            depth++;
        }

        return current;
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
        // FIXME: Pensa al FileSystem come se fosse il disco rigido fisico del tuo computer.
        //  Secondo te, il disco rigido dovrebbe sapere come interpretare i comandi testuali scritti da un utente (come "ls" o "mkdir"),
        //  oppure il suo compito dovrebbe essere solo quello di leggere e scrivere dati quando qualcuno glielo chiede?
        checkDataToSave(command);
        if(result.isSuccess()) {

            return result.getOutput();
        }
        else
            return "ERROR-"+result.getError();
    }

    private void checkDataToSave(String command) {
        System.out.println("DEBUG: "+command);
        if (command.contains("ls") || command.contains("pwd") || command.contains("clear"))
            dataToSave = false;
        else
            dataToSave = true;

    }

    @Override
    public boolean isDataToSave() {
        return dataToSave;
    }

    @Override
    public void setDataToSave(boolean dataToSave) {
        this.dataToSave = dataToSave;
    }

    @Override
    public void create() {
        // 1. Crea la nuova radice: passiamo 'null' come padre.
        DirectoryNode newRoot = new DirectoryNode(null);

        // 2. Resetta i campi di stato
        this.root = newRoot;
        this.currentDirectory = newRoot;
        this.dataToSave = false;

        System.out.println("File system state reset to initial structure.");
    }

    public static void setTranslator(BackendTranslator translator) {
        FileSystem.i18n = translator;
    }

    public void setCurrentDirectory(DirectoryNode currentDirectory) {
        this.currentDirectory = currentDirectory;
    }
}
