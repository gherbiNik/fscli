package ch.supsi.fscli.backend.business.filesystem;

public class SoftLink extends Inode implements ISoftLink{
    private String path;

    public SoftLink(DirectoryNode parent, String path) {
        super(parent, InodeType.SOFTLINK);
        this.path = path;
    }
}
