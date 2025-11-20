package ch.supsi.fscli.backend.business.filesystem;

import java.util.Date;

public abstract class Inode implements FileSystemComponent{
    private static int idCounter = 0;
    private final int uid;
    private final Date creationDate;
    private final DirectoryNode parent;
    // FIXME da capire
    private int linkCount = 1;
    private InodeType type;

    public Inode(DirectoryNode parent, InodeType type) {
        this.uid = idCounter++;
        this.creationDate = new Date();
        this.parent = parent;
        this.type = type;
        System.out.println(creationDate);
    }

    public DirectoryNode getParent() {
        return parent;
    }

    public int getUid() {
        return uid;
    }

    @Override
    public String toString() {
        return "Inode{" +
                "uid=" + uid +
                ", creationDate=" + creationDate +
                ", linkCount=" + linkCount +
                ", type=" + type +
                '}';
    }
}
