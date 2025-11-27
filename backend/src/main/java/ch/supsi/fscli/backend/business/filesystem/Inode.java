package ch.supsi.fscli.backend.business.filesystem;

import java.util.Date;

public abstract class Inode implements FileSystemComponent{
    private static int idCounter = 0;
    private final int uid;
    // FIXME da capire
    private int linkCount = 1;
    private InodeType type;

    public Inode(DirectoryNode parent, InodeType type) {
        this.uid = idCounter++;
        this.type = type;
    }



    public int getUid() {
        return uid;
    }

    @Override
    public String toString() {
        return "Inode{" +
                "uid=" + uid +
                ", linkCount=" + linkCount +
                ", type=" + type +
                '}';
    }

    public boolean isDirectory() {
        return type == InodeType.DIRECTORY;
    }
}
