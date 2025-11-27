package ch.supsi.fscli.backend.business.command.commands;

import ch.supsi.fscli.backend.business.filesystem.Inode;
import ch.supsi.fscli.backend.business.service.FileSystemService;

import java.util.List;

public class LnCommand extends AbstractCommand {

    public LnCommand(FileSystemService fileSystemService, String name, String synopsis, String description) {
        super(fileSystemService, name, synopsis, description);
    }

    @Override
    public CommandResult execute(CommandContext context) {
        if (context.getArguments() == null || context.getOptions() == null) {
            return CommandResult.error("internal error: arguments or options null");
        }
        List<String> arg = context.getArguments();
        List<String> opt = context.getOptions();

        StringBuilder output = new StringBuilder();
        StringBuilder error = new StringBuilder();

        boolean isSoftLink = false;

        for (String o : opt) {
            if (o.equals("-s")) {
                isSoftLink = true;
            } else {
                error.append("usage: ").append(getSynopsis());
                return CommandResult.error(error.toString());
            }
        }

        boolean finalIsSoftLink = isSoftLink;
        if (finalIsSoftLink) {
            // TODO
        }

        if (arg.size() != 2)
            return CommandResult.error("usage: "+getSynopsis());

        Inode source = fileSystemService.getInode(arg.get(0));
        if (source == null) {
            return CommandResult.error("ln: cannot access "+arg.get(0)+": No such file or directory.");
        }

        if (source.isDirectory())
            return CommandResult.error("ln: "+arg.get(0)+": hard link not allowed for directory.");

        Inode destination = fileSystemService.getInode(arg.get(1));

        // TODO devo controllare se con indirizzo dir/file -> dir esiste o no
        if (destination == null) {
            return CommandResult.error("ln: cannot create link "+arg.get(1)+": No such file or directory.");
        }

        if (destination != null) {
            return CommandResult.error("ln: failed to create hard link "+arg.get(1)+": File exists");
        }

        if (destination.isDirectory()){
            // TODO: controllo se dentro la dir c'è file con lo stesso nome della source
        }


        return null;

    }
}
