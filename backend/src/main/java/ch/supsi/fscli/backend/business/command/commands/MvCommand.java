package ch.supsi.fscli.backend.business.command.commands;

import ch.supsi.fscli.backend.business.filesystem.Inode;
import ch.supsi.fscli.backend.business.service.IFileSystemService;

import java.util.List;

public class MvCommand extends AbstractCommand {
    public MvCommand(IFileSystemService fileSystemService, String name, String synopsis, String description) {
        super(fileSystemService, name, synopsis, description);
    }

    @Override
    public CommandResult execute(CommandContext context) {
        if (context.getArguments() == null || context.getArguments().isEmpty()) {
            return CommandResult.error("mv: missing file operand");
        }

        List<String> args = context.getArguments();

        if (args.size() < 2) {
            return CommandResult.error("mv: missing destination file operand after '" + args.get(0) + "'");
        }

        String destinationPath = args.get(args.size() - 1); // L'ultimo argomento è la destinazione
        List<String> sources = args.subList(0, args.size() - 1); // Tutti gli altri sono sorgenti

        // --- VALIDAZIONE PER SPOSTAMENTO MULTIPLO ---
        // Se stiamo spostando più di un file, la destinazione DEVE essere una directory esistente.
        if (sources.size() > 1) {
            Inode destNode = fileSystemService.getInode(destinationPath);

            // Gestione Soft Link: se è un link, vediamo se punta a una directory
            if (destNode != null && destNode.isSoftLink()) {
                destNode = fileSystemService.followLink(destNode);
            }

            if (destNode == null || !destNode.isDirectory()) {
                return CommandResult.error("mv: target '" + destinationPath + "' is not a directory");
            }
        }

        // --- ESECUZIONE ---
        StringBuilder output = new StringBuilder();
        StringBuilder errors = new StringBuilder();
        boolean hasError = false;

        for (String source : sources) {
            try {
                // Chiamiamo il service per ogni file.
                // Il service (nella versione aggiornata) gestisce già lo spostamento "dentro" se la destinazione è una directory.
                fileSystemService.move(source, destinationPath);
                // output.append("Moved '").append(source).append("'\n"); // Mv è solitamente silenzioso
            } catch (Exception e) {
                hasError = true;
                errors.append(e.getMessage());
            }
        }

        if (hasError) {
            return CommandResult.error(errors.toString().trim());
        }

        // Se tutto va bene e abbiamo spostato più file, possiamo restituire un messaggio cumulativo o stringa vuota
        if (sources.size() > 1) {
            return CommandResult.success("Moved " + sources.size() + " files to " + destinationPath);
        } else {
            // Caso singolo (compatibilità output precedente)
            return CommandResult.success("Moved '" + sources.get(0) + "' to '" + destinationPath + "'");
        }
    }
}