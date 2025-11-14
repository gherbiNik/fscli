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
    private Map<String, String> options;

    public ParsedCommand() {
        this.arguments = new ArrayList<>();
        this.options = new HashMap<>();
    }

    public String getCommandName() { return commandName; }
    public void setCommandName(String name) { this.commandName = name; }

    public List<String> getArguments() { return arguments; }
    public void setArguments(List<String> args) { this.arguments = args; }

    public Map<String, String> getOptions() { return options; }
    public void setOptions(Map<String, String> opts) { this.options = opts; }
}
