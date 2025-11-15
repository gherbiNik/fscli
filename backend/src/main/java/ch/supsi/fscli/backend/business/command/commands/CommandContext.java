package ch.supsi.fscli.backend.business.command.commands;

import ch.supsi.fscli.backend.business.filesystem.Inode;

import java.util.List;
import java.util.Map;

public class CommandContext {
    private Inode currentWorkingDirectory;
    private List<String> arguments;
    private List<String> options; // for example: "-s" / "-i"

    public CommandContext(Inode currentWorkingDirectory, List<String> arguments, List<String> options) {
        this.currentWorkingDirectory = currentWorkingDirectory;
        this.arguments = arguments;
        this.options = options;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public List<String> getOptions() {
        return options;
    }
}
