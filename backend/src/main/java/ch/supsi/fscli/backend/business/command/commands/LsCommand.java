package ch.supsi.fscli.backend.business.command.commands;

import ch.supsi.fscli.backend.business.filesystem.Inode;
import ch.supsi.fscli.backend.business.service.FileSystemService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class LsCommand extends AbstractCommand {

    public LsCommand(FileSystemService fileSystemService, String name, String synopsis, String description) {
        super(fileSystemService, name, synopsis, description);
    }

    @Override
    public CommandResult execute(CommandContext context) {
        if (context.getArguments() == null || context.getOptions() == null) {
            return CommandResult.error("internal error: arguments or options null");
        }

        StringBuilder output = new StringBuilder();
        StringBuilder error = new StringBuilder();

        // 1. VALIDAZIONE OPZIONI & STRATEGIA DI STAMPA
        boolean showInode = false;

        // Cicliamo su tutte le opzioni per verificare che siano valide
        for (String opt : context.getOptions()) {
            if (opt.equals("-i")) {
                showInode = true;
            } else {
                // Se troviamo qualsiasi cosa diversa da "-i", blocchiamo tutto
                // Rimuoviamo il trattino "-" solo per estetica nel messaggio d'errore
                error.append("usage: ").append(getSynopsis());
                return CommandResult.error(error.toString());
            }
        }

        // Qui definisci la strategia (nota: showInode deve essere 'effectively final' per la lambda)
        boolean finalShowInode = showInode;
        BiConsumer<String, Inode> printStrategy = (name, inode) -> {
            if (finalShowInode) {
                output.append(inode.getUid()).append(" ");
            }
            output.append(name).append(" ");
        };

        // 2. NORMALIZZAZIONE INPUT
        // Se non ci sono argomenti, usiamo null per indicare la "directory corrente"
        List<String> targets = context.getArguments().isEmpty()
                ? Collections.singletonList((String) null)
                : context.getArguments();

        for (String targetPath : targets) {
            boolean isCurrentDir = (targetPath == null);

            // A. RECUPERO INODE TARGET
            // Dobbiamo capire se il target esiste e cos'è (File o Dir)
            Inode targetInode;
            if (isCurrentDir) {
                // Se siamo nella current dir, dobbiamo recuperare l'inode della directory corrente
                targetInode = fileSystemService.getCurrentDirInode();
            } else {
                targetInode = fileSystemService.getInode(targetPath);
            }

            // B. GESTIONE ERRORE (Non esiste)
            if (targetInode == null) {
                error.append("ls: cannot access '").append(targetPath).append("': No such file or directory\n");
                continue;
            }

            // C. LOGICA DI VISUALIZZAZIONE
            if (!targetInode.isDirectory()) {
                // --- CASO 1: È UN FILE ---
                // Stampiamo direttamente il file usando la strategia
                printStrategy.accept(targetPath, targetInode);
                output.append("\n");

            } else {
                // --- CASO 2: È UNA DIRECTORY ---

                // Se stiamo listando più cartelle, stampiamo il nome header (es. "folder:")
                if (targets.size() > 1 && !isCurrentDir) {
                    output.append(targetPath).append(":\n");
                }

                // Recuperiamo i figli
                Map<String, Inode> children = isCurrentDir
                        ? fileSystemService.getINodeTableCurrentDir()
                        : fileSystemService.getChildInodeTable(targetPath);

                // Stampiamo i figli applicando il FILTRO
                if (children != null && !children.isEmpty()) {
                    children.forEach((name, inode) -> {
                        // Ignoriamo . e ..
                        if (!name.startsWith(".")) {
                            printStrategy.accept(name, inode);
                        }
                    });
                }

                output.append("\n");
                if (targets.size() > 1) output.append("\n"); // Spaziatura extra tra cartelle
            }
        }

        if (!error.isEmpty()) {
            return CommandResult.partialError(output.toString().trim(), error.toString().trim());
        }
        return CommandResult.success(output.toString().trim());
    }
}