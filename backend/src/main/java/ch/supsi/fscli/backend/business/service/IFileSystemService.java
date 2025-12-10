package ch.supsi.fscli.backend.business.service;

import ch.supsi.fscli.backend.business.filesystem.DirectoryNode;
import ch.supsi.fscli.backend.business.filesystem.Inode;
import ch.supsi.fscli.backend.util.BackendTranslator;

import java.util.Map;

public interface IFileSystemService {
    DirectoryNode getCurrentDirectory();

    Inode getCurrentDirInode();

    Inode getInode(String targetPath);

    Map<String, Inode> getINodeTableCurrentDir();

    Map<String, Inode> getChildInodeTable(String targetPath);

    Inode followLink(Inode targetInode);

    void changeDirectory(String newDirPath);

    void createDirectory(String directoryName);

    void move(String source, String destinationPath);

    String getCurrentDirectoryAbsolutePath();

    void removeFile(String fileName);

    boolean removeDirectory(String directoryName);

    void createFile(String fileName);

    void setDataToSave(boolean b);

    void setTranslator(BackendTranslator translator);
}
