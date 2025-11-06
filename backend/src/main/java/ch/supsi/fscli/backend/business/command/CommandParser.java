package ch.supsi.fscli.backend.business.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandParser {

    private static CommandParser instance;

    private CommandParser(){}

    public static CommandParser getInstance(){
        if(instance == null){
            instance = new CommandParser();
            instance.initialize();
        }
        return instance;
    }

    private void initialize(){}

    // Takes in input a String and tries to convert it into a command
    //FIXME does not supporte all commands. this is a basic initial version
    public ParsedCommand parse(String input) throws InvalidCommandException {
        if (input == null || input.trim().isEmpty()) {
            throw new InvalidCommandException("Empty command");
        }

        String[] tokens = input.trim().split("\\s+");
        String commandName = tokens[0];

        ParsedCommand parsed = new ParsedCommand();
        parsed.setCommandName(commandName);

        List<String> args = new ArrayList<>();
        Map<String, String> options = new HashMap<>();

        for (int i = 1; i < tokens.length; i++) {
            if (tokens[i].startsWith("-")) {
                options.put(tokens[i], "");
            } else {
                args.add(tokens[i]);
            }
        }

        parsed.setArguments(args);
        parsed.setOptions(options);

        return parsed;
    }
}