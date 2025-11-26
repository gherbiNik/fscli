package ch.supsi.fscli.backend.business.command.commands;

import ch.supsi.fscli.backend.business.service.FileSystemService;

public class ClearCommand extends AbstractCommand{
    public ClearCommand(FileSystemService fileSystemService, String name, String synopsis, String description) {
        super(fileSystemService, name, synopsis, description);
    }

    // clear
    @Override
    public CommandResult execute(CommandContext context) {
        if (context.getArguments() != null && !context.getArguments().isEmpty()) {
            return CommandResult.error("clear: no args needed");
        }
        if (context.getOptions() != null && !context.getOptions().isEmpty()) {
            return CommandResult.error("clear: no options needed");
        }

        // in the frontend when this message arrives, the output will be cleared
        return CommandResult.success("Perform Clear");

    }
}
