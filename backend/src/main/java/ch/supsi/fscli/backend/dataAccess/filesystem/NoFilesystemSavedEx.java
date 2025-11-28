package ch.supsi.fscli.backend.dataAccess.filesystem;

public class NoFilesystemSavedEx extends RuntimeException {
    public NoFilesystemSavedEx(String message) {
        super(message);
    }
}
