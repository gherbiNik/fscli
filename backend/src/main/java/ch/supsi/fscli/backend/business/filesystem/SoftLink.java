package ch.supsi.fscli.backend.business.filesystem;

public class SoftLink extends Inode implements ISoftLink{
    private Inode pointer;

    public SoftLink(DirectoryNode parent) {
        super(parent, InodeType.SOFTLINK);
    }
}
