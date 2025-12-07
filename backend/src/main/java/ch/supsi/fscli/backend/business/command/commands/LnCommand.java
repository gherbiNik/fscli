package ch.supsi.fscli.backend.business.command.commands;

import ch.supsi.fscli.backend.business.filesystem.DirectoryNode;
import ch.supsi.fscli.backend.business.filesystem.Inode;
import ch.supsi.fscli.backend.business.filesystem.SoftLink;
import ch.supsi.fscli.backend.business.service.IFileSystemService;
import ch.supsi.fscli.backend.business.service.PathParts;

import java.util.List;

public class LnCommand extends AbstractCommand {

    public LnCommand(IFileSystemService fileSystemService, String name, String synopsis, String description) {
        super(fileSystemService, name, synopsis, description);
    }

    @Override
    public CommandResult execute(CommandContext context) {
        // 0. GUARD CLAUSE: Internal Errors
        if (context.getArguments() == null || context.getOptions() == null) {
            return CommandResult.error("internal error: arguments or options null");
        }

        List<String> args = context.getArguments();
        List<String> opts = context.getOptions();

        // 1. GESTIONE OPZIONI
        boolean isSoftLink = false;
        for (String opt : opts) {
            if (opt.equals("-s")) {
                isSoftLink = true;
            } else {
                return CommandResult.error("ln: illegal option -- " + opt.replace("-", ""));
            }
        }

        // 2. CONTROLLO NUMERO ARGOMENTI
        // ln richiede: [OPZIONI] SOURCE DESTINATION (o DIRECTORY)
        if (args.size() != 2) {
            // Nota: su Unix standard se manca l'argomento è "missing file operand"
            return CommandResult.error("usage: " + getSynopsis());
        }

        String sourcePath = args.get(0);
        String destinationPath = args.get(1);

        // 3. VALIDAZIONE SORGENTE (SOURCE)
        Inode sourceInode = fileSystemService.getInode(sourcePath);

        if (!isSoftLink) {
            // su hard link non controllo il source path perchè può essere anche sbagliato

            if (sourceInode == null) {
                return CommandResult.error("ln: cannot access '" + sourcePath + "': No such file or directory");
            }

            if (sourceInode.isDirectory()) {
                return CommandResult.error("ln: '" + sourcePath + "': hard link not allowed for directory");
            }
        }

        // 4. RISOLUZIONE DESTINAZIONE (TARGET)
        // Dobbiamo capire dove mettere il link e come chiamarlo.

        DirectoryNode targetDir = null;
        String linkName = null;

        Inode destinationInode = fileSystemService.getInode(destinationPath);

        if (destinationInode != null) {
            // CASO A: La destinazione esiste già
            if (destinationInode.isDirectory()) {
                // CASO A1: Destinazione è una cartella.
                // Comportamento: creiamo il link DENTRO quella cartella con lo stesso nome del source.
                targetDir = (DirectoryNode) destinationInode;

                // Estraiamo il nome del file sorgente dal path (es. "a/b/file.txt" -> "file.txt")
                linkName = getFileNameFromPath(sourcePath);
            } else {
                // CASO A2: Destinazione è un file esistente.
                // Errore: non possiamo sovrascrivere
                return CommandResult.error("ln: failed to create link '" + destinationPath + "': File exists");
            }
        } else {
            // CASO B: La destinazione NON esiste (è il nome del nuovo link)
            // Dobbiamo trovare la cartella padre e il nome del nuovo file

            // Logica manuale di split del path (simile a quella nel Service, ma qui serve lato Command)
            PathParts parts = resolveParentAndName(destinationPath);

            if (parts.parentDir() == null) {
                return CommandResult.error("ln: cannot create link '" + destinationPath + "': No such file or directory");
            }

            targetDir = parts.parentDir();
            linkName = parts.name();
        }

        // 5. CONTROLLO FINALE PRIMA DELLA CREAZIONE
        // Verifichiamo se dentro targetDir esiste già un file con nome linkName
        // (Questo serve per il Caso A1 e per sicurezza nel Caso B)
        if (targetDir.getChild(linkName) != null) {
            return CommandResult.error("ln: failed to create link '" + linkName + "': File exists");
        }

        // 6. ESECUZIONE (CREAZIONE LINK)
        if (isSoftLink) {
            // Logica Soft Link
            SoftLink softLink = new SoftLink(sourcePath);
            targetDir.addChild(linkName, softLink);
        } else {
            // Logica Hard Link
            targetDir.addChild(linkName, sourceInode);
        }

        return CommandResult.success("");
    }

    // Estrae il nome del file da un path completo (es. /home/user/file.txt -> file.txt)
    private String getFileNameFromPath(String path) {
        if (path == null || path.isEmpty()) return "";
        // Rimuove slash finale se presente (es. dir/ -> dir)
        String cleanPath = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
        int lastSlashIndex = cleanPath.lastIndexOf('/');
        if (lastSlashIndex == -1) {
            return cleanPath; // È già solo il nome
        }
        return cleanPath.substring(lastSlashIndex + 1);
    }

    private PathParts resolveParentAndName(String path) {
        String parentPath;
        String name;

        if (path.contains("/")) {
            // Path complesso (es. dir/link)
            int lastSlash = path.lastIndexOf('/');
            parentPath = path.substring(0, lastSlash);
            name = path.substring(lastSlash + 1);

            // Gestione root "/"
            if (parentPath.isEmpty()) parentPath = "/";
        } else {
            // Path semplice (es. link) -> parent è la current dir
            return new PathParts(fileSystemService.getCurrentDirectory(), path);
        }

        // Risolviamo il parent path usando il service
        Inode parentNode = fileSystemService.getInode(parentPath);

        if (parentNode instanceof DirectoryNode) {
            return new PathParts((DirectoryNode) parentNode, name);
        }

        // Parent non trovato o non è una directory
        return new PathParts(null, name);
    }
}
