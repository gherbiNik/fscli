package ch.supsi.fscli.backend.business.filesystem;

public interface IFileSystem {
    String executeCommand(String command);
    boolean isDataToSave();
    void setDataToSave(boolean dataToSave);
}
