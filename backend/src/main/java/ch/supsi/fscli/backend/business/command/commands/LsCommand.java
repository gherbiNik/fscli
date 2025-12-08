package ch.supsi.fscli.backend.business.command.commands;

import ch.supsi.fscli.backend.business.command.commands.validators.CommandValidator;
import ch.supsi.fscli.backend.business.command.commands.validators.NoArgsOrOptNullValidator;
import ch.supsi.fscli.backend.business.filesystem.DirectoryNode;
import ch.supsi.fscli.backend.business.filesystem.Inode;
import ch.supsi.fscli.backend.business.service.FileSystemService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class LsCommand extends AbstractValidatedCommand {

    public LsCommand(FileSystemService fileSystemService, String name, String synopsis, String description) {
        super(fileSystemService, name, synopsis, description);
    }

    @Override
    protected CommandValidator getValidator() {
        return new NoArgsOrOptNullValidator(getName());
    }

    @Override
    public CommandResult executeCommand(CommandContext context) {
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
                error.append(translate("usage")).append(" ").append(getSynopsis());
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
                //error.append("ls: cannot access '").append(targetPath).append("': No such file or directory\n");
                error.append(getName())
                        .append(": ")
                        .append(translate("cannot_access_prefix"))
                        .append(targetPath)
                        .append(translate("no_such_file_suffix"))
                        .append("\n");
                continue;
            }

            // C. LOGICA DI VISUALIZZAZIONE
            if (targetInode.isDirectory()) {

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

            } else if (targetInode.isSoftLink()){
                Inode resolved = fileSystemService.followLink(targetInode);

                if (targets.size() > 1 && !isCurrentDir) {
                    output.append(targetPath).append(":\n");
                }

                // Se resolved è una directory, ls si comporta come se avessi chiesto di listare quella directory
                if (resolved != null && resolved.isDirectory()) {
                    // Usa 'resolved' per ottenere la tabella dei figli
                    Map<String, Inode> children = ((DirectoryNode) resolved).getChildren();
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
                } else {
                    // E' un file o un link a un file (o un link rotto): stampa solo la riga del link
                    printStrategy.accept(targetPath, targetInode);
                }
            } else {
                // È UN FILE
                // Stampiamo direttamente il file usando la strategia
                printStrategy.accept(targetPath, targetInode);
                output.append("\n");
            }
        }

        if (!error.isEmpty()) {
            return CommandResult.partialError(output.toString().trim(), error.toString().trim());
        }
        return CommandResult.success(output.toString().trim());
    }
}