package ch.supsi.fscli.backend.business.command.commands;

import ch.supsi.fscli.backend.business.service.IFileSystemService;

public class PwdCommand extends AbstractCommand{


    public PwdCommand(IFileSystemService fileSystemService, String name, String synopsis, String description) {
        super(fileSystemService, name, synopsis, description);
    }

    @Override
    public CommandResult execute(CommandContext context) {
        if (context.getArguments() != null && (!context.getArguments().isEmpty() || !context.getOptions().isEmpty())) {
            return CommandResult.error("pwd: doesn't need options or arguments");
        }

        return CommandResult.success("root: "+fileSystemService.getCurrentDirectoryAbsolutePath());
    }
}
