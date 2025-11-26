package ch.supsi.fscli.frontend.model.filesystem;

public interface IFileSystemModel {
    void createFileSystem();
    String sendCommand(String userInput);
}
