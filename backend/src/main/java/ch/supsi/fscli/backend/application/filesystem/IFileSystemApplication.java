package ch.supsi.fscli.backend.application.filesystem;

import java.util.List;

public interface IFileSystemApplication {
    void createFileSystem();
    String sendCommand(String userInput);
    boolean isDataToSave();
    void setDataToSave(boolean dataToSave);
    boolean isFileSystemCreated();
    List<String> getCommandsHelp();
}
