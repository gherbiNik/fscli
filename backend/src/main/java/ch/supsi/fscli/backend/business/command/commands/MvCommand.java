package ch.supsi.fscli.backend.business.command.commands;

import ch.supsi.fscli.backend.business.command.commands.validators.CommandValidator;
import ch.supsi.fscli.backend.business.command.commands.validators.NoOptionsValidator;
import ch.supsi.fscli.backend.business.command.commands.validators.RequiresArgumentsValidator;
import ch.supsi.fscli.backend.business.command.commands.validators.RequiresAtLeastArgumentsValidator;
import ch.supsi.fscli.backend.business.service.IFileSystemService;

import java.util.List;

/*
mv SOURCE DESTINATION
move a file/directory to a new file/directory
(therefore also acts as rename)
 */
public class MvCommand extends AbstractValidatedCommand{
    public MvCommand(IFileSystemService fileSystemService, String name, String synopsis, String description) {
        super(fileSystemService, name, synopsis, description);
    }

    @Override
    protected CommandValidator getValidator() {
        return new RequiresArgumentsValidator(getName())
                .and(new NoOptionsValidator(getName()))
                .and(new RequiresAtLeastArgumentsValidator(getName(), 2));
    }

    @Override
    protected CommandResult executeCommand(CommandContext context) {
        List<String> args = context.getArguments();
        String destination = args.get(args.size() - 1);

        try {
            if (args.size() == 2) {
                String source = args.get(0);
                fileSystemService.move(source, destination);
                fileSystemService.setDataToSave(true);
                return CommandResult.success(translate("mv_success_prefix")
                        + source
                        + translate("mv_success_middle")
                        + destination
                        + translate("mv_success_suffix"));
            }

            List<String> sources = args.subList(0, args.size() - 1);

            StringBuilder successMsg = new StringBuilder();
            boolean firstMove = true;

            for (String source : sources) {
                fileSystemService.move(source, destination);

                if (!firstMove) {
                    successMsg.append(", ");
                }
                successMsg.append(source);
                firstMove = false;
            }

            fileSystemService.setDataToSave(true);
            return CommandResult.success(translate("mv_success_prefix")
                    + successMsg
                    + translate("mv_success_middle")
                    + destination
                    + translate("mv_success_suffix"));

        } catch (IllegalArgumentException e) {
            return CommandResult.error(getName() + ": " + e.getMessage());
        }
    }
}