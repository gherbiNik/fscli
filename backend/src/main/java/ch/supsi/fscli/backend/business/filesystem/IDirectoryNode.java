package ch.supsi.fscli.backend.business.filesystem;

public interface IDirectoryNode {
    Inode getChild(String directoryName);

    void addChild(String directoryName, Inode newNode);
}
