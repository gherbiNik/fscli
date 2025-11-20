package ch.supsi.fscli.backend.business.command.commands;

import ch.supsi.fscli.backend.business.filesystem.Inode;
import ch.supsi.fscli.backend.business.service.FileSystemService;

import java.util.List;
import java.util.Map;

public class LsCommand extends AbstractCommand{


    public LsCommand(FileSystemService fileSystemService, String name, String synopsis, String description) {
        super(fileSystemService, name, synopsis, description);
    }

    @Override
    public CommandResult execute(CommandContext context) {
        if (context.getArguments() == null || context.getOptions() == null){
            // Non dovrebbe mai entrare qui dentro
            return CommandResult.error("argument and options null");
        }
        List<String> arg = context.getArguments();
        List<String> opt = context.getOptions();
        Map<String, Inode> table = fileSystemService.getINodeTableCurrentDir();
        StringBuilder output = new StringBuilder();
        StringBuilder error = new StringBuilder();

        // ls
        if (arg.isEmpty() && opt.isEmpty()) {
            table.forEach((key, inode) -> output.append(key).append(" "));
            return CommandResult.success(output.toString());
        }

        // Verifica: nessun argomento (file) E l'unica opzione presente è "-i"
        if (arg.isEmpty() && context.getOptions().size() == 1 && context.getOptions().contains("-i")) {


            table.forEach((key, inode) ->
                    output.append(inode.getUid()).append(" ").append(key).append(" ")
            );

            return CommandResult.success(output.toString());
        }

        if (!arg.isEmpty()){
            for (String file: arg){

                Map<String, Inode> t = fileSystemService.getChildInodeTable(file);

                if (t == null){
                    error.append("ls: cannot access ").append(file).append(": No such file or directory");
                }
                else {
                    output.append(file).append(":\n");
                    if (t.isEmpty()){
                        output.append("\n");
                    } else {
                        t.forEach((key, inode) ->
                                output.append(key).append(" ").append(" ")
                        );
                    }
                }


            }
            return CommandResult.partialError(output.toString(), error.toString());
        }
        return CommandResult.error("error");





    }
}
