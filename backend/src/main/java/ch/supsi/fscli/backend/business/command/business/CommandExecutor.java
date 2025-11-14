package ch.supsi.fscli.backend.business.command.business;

import ch.supsi.fscli.backend.business.command.commands.CommandContext;
import ch.supsi.fscli.backend.business.command.commands.CommandResult;
import ch.supsi.fscli.backend.business.command.commands.ICommand;
import ch.supsi.fscli.backend.business.service.FileSystemService;

import java.util.List;
import java.util.Map;

public class CommandExecutor {
    private Map<String, ICommand> commandList;
    private CommandParser commandParser;
    private static CommandExecutor instance;
    private FileSystemService fileSystemService; // FIXME: maybe to remove from here

    private CommandExecutor() {}

    public static CommandExecutor getInstance(FileSystemService fileSystemService,
                          CommandParser commandParser, List<ICommand> commandList){
        if(instance == null){
            instance = new CommandExecutor();
            instance.initialize(fileSystemService, commandParser, commandList);
        }
        return instance;
    }

    private void initialize(FileSystemService fileSystemService, CommandParser commandParser,
                            List<ICommand> commands){
        this.fileSystemService = fileSystemService;
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
                    fileSystemService.getCurrentDirectory(),
                    parsed.getArguments(),
                parsed.getOptions()
            );
            return command.execute(commandContext);

        } catch (InvalidCommandException e){
            return CommandResult.error(e.getMessage());
        }
    }

}
