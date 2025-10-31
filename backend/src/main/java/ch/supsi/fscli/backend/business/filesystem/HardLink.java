package ch.supsi.fscli.backend.business.filesystem;

public class HardLink extends Inode implements IHardLink {
    private FileNode pointer;

    public HardLink(DirectoryNode parent) {
        super(parent, InodeType.HARDLINK);
    }
}
