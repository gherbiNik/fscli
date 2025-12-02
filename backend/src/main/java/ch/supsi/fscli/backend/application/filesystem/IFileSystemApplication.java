package ch.supsi.fscli.backend.application.filesystem;

public interface IFileSystemApplication {
    void createFileSystem();
    String sendCommand(String userInput);
    boolean isDataToSave();
    void setDataToSave(boolean dataToSave);

    boolean isFileSystemCreated();
}
