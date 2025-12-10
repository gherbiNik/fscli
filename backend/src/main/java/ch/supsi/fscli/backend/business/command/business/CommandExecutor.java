package ch.supsi.fscli.backend.business.command.business;

import ch.supsi.fscli.backend.business.command.commands.CommandContext;
import ch.supsi.fscli.backend.business.command.commands.CommandResult;
import ch.supsi.fscli.backend.business.command.commands.ICommand;
import ch.supsi.fscli.backend.business.filesystem.DirectoryNode;
import ch.supsi.fscli.backend.business.service.IFileSystemService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class CommandExecutor {
    private final Map<String, ICommand> commandList;
    private final CommandParser commandParser;
    private final  IFileSystemService fileSystemService;

    @Inject
    public CommandExecutor( IFileSystemService fileSystemService, CommandParser commandParser, List<ICommand> commandList) {
        this.fileSystemService = fileSystemService;
        this.commandParser = commandParser;
        this.commandList = new HashMap<>();

        if (commandList != null) {
            System.out.println("DEBUGGING: sto mappando la mappa");
            for (ICommand c : commandList) {
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
