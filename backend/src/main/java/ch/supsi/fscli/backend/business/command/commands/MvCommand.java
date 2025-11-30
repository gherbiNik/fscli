package ch.supsi.fscli.backend.business.command.commands;

/*
mv SOURCE DESTINATION
move a file/directory to a new file/directory
(therefore also acts as rename)
 */

import ch.supsi.fscli.backend.business.service.FileSystemService;

public class MvCommand extends AbstractCommand{
    public MvCommand(FileSystemService fileSystemService, String name, String synopsis, String description) {
        super(fileSystemService, name, synopsis, description);
    }

    @Override
    public CommandResult execute(CommandContext context) {
        if (context.getArguments() == null || context.getArguments().isEmpty()) {
            return CommandResult.error("mv: missing arguments");
        }

        if(context.getArguments().size() != 2){
            return CommandResult.error("You need 2 arguments: [SOURCE] [DESTINATION]");
        }

        String source = context.getArguments().get(0);
        String destination = context.getArguments().get(1);

        try {
            fileSystemService.move(source, destination);
            return CommandResult.success("Moved '" + source + "' to '" + destination + "'");
        } catch (IllegalArgumentException e){
            return CommandResult.error("mv: " + e.getMessage());
        }

        // TODO ADD SOFT LINK CONTROLL

    }
}
