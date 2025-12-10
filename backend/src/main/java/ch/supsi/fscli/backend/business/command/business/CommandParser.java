package ch.supsi.fscli.backend.business.command.business;

import com.google.inject.Singleton;

import java.util.*;

@Singleton
public class CommandParser {

    public CommandParser(){}

    public ParsedCommand parse(String input) throws InvalidCommandException {
        if (input == null || input.trim().isEmpty()) {
            throw new InvalidCommandException("Empty command");
        }

        StringTokenizer tokenizer = new StringTokenizer(input.trim());

        if (!tokenizer.hasMoreTokens()) {
            throw new InvalidCommandException("Empty command");
        }

        String commandName = tokenizer.nextToken();
        ParsedCommand parsed = new ParsedCommand();
        parsed.setCommandName(commandName);

        List<String> args = new ArrayList<>();
        List<String> options = new ArrayList<>();

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();

            if (token.startsWith("-")) {
                options.add(token);
            } else {
                args.add(token);
            }
        }

        parsed.setArguments(args);
        parsed.setOptions(options);

        return parsed;
    }
}