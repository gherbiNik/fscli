package ch.supsi.fscli.backend.business.filesystem;

public class SoftLink extends Inode implements ISoftLink{
    private String targetPath;

    public SoftLink(String targetPath) {
        super(InodeType.SOFTLINK);
        this.targetPath = targetPath;
    }

    @Override
    public String toString() {
        return super.toString()+", path = " + targetPath;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setPath(String path) {
        this.targetPath = path;
    }
}
