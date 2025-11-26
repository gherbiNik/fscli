package ch.supsi.fscli.backend.business.command.commands;

import ch.supsi.fscli.backend.business.command.business.CommandDetails;
import ch.supsi.fscli.backend.business.command.business.CommandHelpContainer;
import ch.supsi.fscli.backend.business.service.FileSystemService;

import java.util.Map;

public class HelpCommand extends AbstractCommand{
    private CommandHelpContainer container;
    public HelpCommand(FileSystemService fileSystemService, String name, String synopsis, String description, CommandHelpContainer commandHelpContainer) {
        super(fileSystemService, name, synopsis, description);
        this.container = commandHelpContainer;
    }

    @Override
    public CommandResult execute(CommandContext context) {
        if (context.getArguments() != null && !context.getArguments().isEmpty()) {
            return CommandResult.error("help: no args needed");
        }
        if (context.getOptions() != null && !context.getOptions().isEmpty()) {
            return CommandResult.error("help: no options needed");
        }

        StringBuilder sb = new StringBuilder("Available Commands List:\n");
        Map<String, CommandDetails> infos = container.getCommandDetailsMap();

        if(infos == null)
            return CommandResult.error("help: error occurred while reading commands");
        if(infos.isEmpty())
            return CommandResult.error("help: no commands available");

        for(Map.Entry<String, CommandDetails> commandInfos : infos.entrySet()){
            sb.append(commandInfos.getKey())
                    .append(" : ")
                    .append(commandInfos.getValue().synopsis())
                    .append("\n");
        }
        return CommandResult.success(sb.toString());

    }
}
