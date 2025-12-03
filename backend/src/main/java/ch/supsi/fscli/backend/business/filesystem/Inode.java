package ch.supsi.fscli.backend.business.filesystem;

public abstract class Inode implements FileSystemComponent{
    private static int idCounter = 0;
    private int uid;
    // FIXME da capire
    private int linkCount = 1;
    private InodeType type;

    public Inode(InodeType type) {
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
                ", type=" + type;
    }

    public static int getIdCounter() {
        return idCounter;
    }

    // only for deserialization
    protected void setUid(int uid) {
        this.uid = uid;
    }

    public void setType(InodeType type) {
        this.type = type;
    }

    public InodeType getType() {
        return type;
    }

    public boolean isDirectory() {
        return type == InodeType.DIRECTORY;
    }

    public boolean isSoftLink() {
        return type == InodeType.SOFTLINK;
    }
}
