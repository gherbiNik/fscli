package ch.supsi.fscli.backend.business.command.commands;

import ch.supsi.fscli.backend.business.service.IFileSystemService;

public class TouchCommand extends AbstractCommand{

    public TouchCommand(IFileSystemService fileSystemService, String name, String synopsis, String description) {
        super(fileSystemService, name, synopsis, description);
    }

    @Override
    public CommandResult execute(CommandContext context) {
        if (context.getArguments() == null || context.getArguments().isEmpty()) {
            return CommandResult.error("touch: missing arguments");
        }

        StringBuilder output = new StringBuilder();
        StringBuilder errors = new StringBuilder();
        boolean hasErrors = false;

        for (String fileName : context.getArguments()) {
            if (fileName == null || fileName.trim().isEmpty()) {
                errors.append("touch: invalid file name");
                hasErrors = true;
                continue;
            }

            try {
                fileSystemService.createFile(fileName);
                output.append("File '").append(fileName).append("' created successfully");
            } catch (Exception e) {
                hasErrors = true;
                errors.append("touch: cannot create file '").append(fileName).append("': ").append(e.getMessage());
            }

        }
        if(hasErrors){
            return CommandResult.error(errors.toString().trim());
        }
        return CommandResult.success(output.toString().trim());
    }

}
