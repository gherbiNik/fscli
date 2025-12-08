package ch.supsi.fscli.backend.business.command.business;

import ch.supsi.fscli.backend.business.command.commands.CommandContext;
import ch.supsi.fscli.backend.business.command.commands.CommandResult;
import ch.supsi.fscli.backend.business.command.commands.ICommand;
import ch.supsi.fscli.backend.business.filesystem.DirectoryNode;
import ch.supsi.fscli.backend.business.service.IFileSystemService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandExecutor {
    private Map<String, ICommand> commandList;
    private CommandParser commandParser;
    private static CommandExecutor instance;
    private IFileSystemService fileSystemService;

    private CommandExecutor() {}

    public static CommandExecutor getInstance(IFileSystemService fileSystemService, CommandParser commandParser, List<ICommand> commandList){
        if(instance == null){
            instance = new CommandExecutor();
            instance.initialize(fileSystemService, commandParser, commandList);
        }
        return instance;
    }

    private void initialize(IFileSystemService fileSystemService, CommandParser commandParser, List<ICommand> commands){
        this.fileSystemService = fileSystemService;
        this.commandParser = commandParser;

        this.commandList = new HashMap<>();

        if (commands != null) {
            //FIXME DEBUGGING
            System.out.println("DEBUGGING: sto mappando la mappa");
            for (ICommand c : commands) {
                if (c != null && c.getName() != null) {
                    this.commandList.put(c.getName(), c);
                }
            }
        }
    }

    private List<String> expandArguments(DirectoryNode cwd, List<String> rawArguments) {
        List<String> expandedArgs = new ArrayList<>();

        for (String arg : rawArguments) {
            if (arg.equals("*")) {
                expandedArgs.addAll(cwd.getChildNames().stream()
                        .filter(name -> !name.startsWith(".")) // Rimuove ., .. and evrey .file
                        .toList());
            } else {
                expandedArgs.add(arg);
            }
        }
        return expandedArgs;
    }

    public CommandResult execute(String input){
        try {
            ParsedCommand parsed = commandParser.parse(input);
            ICommand command = commandList.get(parsed.getCommandName());
            if (command == null) {
                return CommandResult.error("Command not found: " + parsed.getCommandName());
            }

            DirectoryNode cwd = fileSystemService.getCurrentDirectory();

            List<String> expandedArguments = expandArguments(cwd, parsed.getArguments());

            CommandContext commandContext = new CommandContext(
                    cwd,
                    expandedArguments,
                    parsed.getOptions()
            );
            System.out.println(commandContext);

            return command.execute(commandContext);
        } catch (InvalidCommandException e){
            return CommandResult.error(e.getMessage());
        }
    }

}
