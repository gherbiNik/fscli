package ch.supsi.fscli.backend.business.command;

import ch.supsi.fscli.backend.business.filesystem.Inode;

import java.util.List;
import java.util.Map;

public class CommandContext {
    private Inode currentWorkingDirectory;
    private List<String> arguments;
    private Map<String, String> options; // for example: "-s" / "-i"

    public CommandContext(Inode currentWorkingDirectory, List<String> arguments, Map<String, String> options) {
        this.currentWorkingDirectory = currentWorkingDirectory;
        this.arguments = arguments;
        this.options = options;
    }
}
