package ch.supsi.fscli.backend.business.filesystem;

import ch.supsi.fscli.backend.business.command.business.CommandExecutor;

public interface IFileSystem {
    String executeCommand(String command);
    boolean isDataToSave();
    void setDataToSave(boolean dataToSave);
    void create();

    void setCommandExecutor(CommandExecutor executor);

    boolean isCreated();
}
