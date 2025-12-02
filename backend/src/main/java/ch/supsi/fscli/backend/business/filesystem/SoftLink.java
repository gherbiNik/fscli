package ch.supsi.fscli.backend.business.filesystem;

public class SoftLink extends Inode implements ISoftLink{
    private String targetPath;

    public SoftLink(DirectoryNode parent, String targetPath) {
        super(parent, InodeType.SOFTLINK);
        this.targetPath = targetPath;
    }

    @Override
    public String toString() {
        return super.toString()+", path = " + targetPath;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
