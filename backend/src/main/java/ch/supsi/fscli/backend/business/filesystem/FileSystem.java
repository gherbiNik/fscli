package ch.supsi.fscli.backend.business.filesystem;

public class FileSystem implements FileSystemComponent, IFileSystem
{
    private static FileSystem instance;
    private final DirectoryNode root;
    private DirectoryNode currentDirectory;


    public static FileSystem getInstance() {
        if (instance == null) {
            instance = new FileSystem();
        }
        return instance;
    }

    private FileSystem(){
        root = new DirectoryNode(null);
        currentDirectory = root;
    }

    public DirectoryNode getRoot() {
        return root;
    }

    public DirectoryNode getCurrentDirectory() {
        return currentDirectory;
    }

    public void changeDirectory(String path) {
        this.currentDirectory = findDirectoryByPath(path);
    }

    private DirectoryNode findDirectoryByPath(String path) {
        // TODO
        return null;
    }

    @Override
    public String toString() {
        return "FileSystem{" +
                "root=" + root +
                '}';
    }
}
