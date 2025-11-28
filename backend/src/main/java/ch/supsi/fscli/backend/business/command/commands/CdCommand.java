package ch.supsi.fscli.backend.business.command.commands;

import ch.supsi.fscli.backend.business.service.FileSystemService;

public class CdCommand extends AbstractCommand{

    public CdCommand(FileSystemService fileSystemService, String name, String synopsis, String description) {
        super(fileSystemService, name, synopsis, description);
    }
// cd [DIR]
    @Override
    public CommandResult execute(CommandContext context) {
        if(context.getArguments() == null || context.getArguments().isEmpty()){
            return CommandResult.error("cd: missing arguments");
        }

        if(context.getArguments().size() != 1) {
            return CommandResult.error("cd: too many arguments");
        }

        String newDirPath = context.getArguments().get(0);
        try {
            fileSystemService.changeDirectory(newDirPath);
        } catch (IllegalArgumentException e){
            return CommandResult.error("Error: " + e.getMessage());
        }

        // TODO ADD SOFT LINK CONTROLL

        return CommandResult.success(""); // Nothing should be notified
    }
}
