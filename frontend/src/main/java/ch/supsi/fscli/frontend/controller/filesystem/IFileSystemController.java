package ch.supsi.fscli.frontend.controller.filesystem;

public interface IFileSystemController {
    void createFileSystem();
    void sendCommand(String userInput);
    boolean hasDataToSave();
}
