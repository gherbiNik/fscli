package ch.supsi.fscli.backend.business.command.business;

// Represents a command meaningful for the filesystem
// so it can be executed

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParsedCommand {
    private String commandName;
    private List<String> arguments;
    private List<String> options;

    public ParsedCommand() {
        this.arguments = new ArrayList<>();
        this.options = new ArrayList<>();
    }

    public String getCommandName() { return commandName; }
    public void setCommandName(String name) { this.commandName = name; }

    public List<String> getArguments() { return arguments; }
    public void setArguments(List<String> args) { this.arguments = args; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> opts) { this.options = opts; }
}
