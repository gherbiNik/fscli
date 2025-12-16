package ch.supsi.fscli.backend.business.filesystem;

public class FileNode extends Inode implements IFileNode {

    public FileNode() {
        super(InodeType.FILE);
    }
}
