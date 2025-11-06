package ch.supsi.fscli.backend.business.command;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public class CommandExecutor {
    private Map<String, ICommand> commandList;
    private CommandParser commandParser;
    private static CommandExecutor instance;

    private CommandExecutor() {}

    public static CommandExecutor getInstance(CommandParser commandParser, List<ICommand> commandList){
        if(instance == null){
            instance = new CommandExecutor();
            instance.initialize(commandParser, commandList);
        }
        return instance;
    }

    private void initialize(CommandParser commandParser, List<ICommand> commands){
        this.commandParser = commandParser;
        if (commands != null) {
            for (ICommand c : commands) {
                if (c != null && c.getName() != null) {
                    this.commandList.put(c.getName(), c);
                }
            }
        }
    }

    public CommandResult execute(String input){
        try {
            ParsedCommand parsed = commandParser.parse(input);
            ICommand command = commandList.get(parsed.getCommandName());
            if (command == null) {
                return CommandResult.error("Command not found: " + parsed.getCommandName());
            }

            CommandContext commandContext = new CommandContext(
                null, // FIXME : get the curr working directory
                    parsed.getArguments(),
                parsed.getOptions()
            );
            return command.execute(commandContext);

        } catch (InvalidCommandException e){
            return CommandResult.error(e.getMessage());
        }
    }

}
