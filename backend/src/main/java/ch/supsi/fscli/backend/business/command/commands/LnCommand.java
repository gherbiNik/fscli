package ch.supsi.fscli.backend.business.command.commands;

import ch.supsi.fscli.backend.business.command.commands.validators.CommandValidator;
import ch.supsi.fscli.backend.business.command.commands.validators.NoArgsOrOptNullValidator;
import ch.supsi.fscli.backend.business.filesystem.DirectoryNode;
import ch.supsi.fscli.backend.business.filesystem.Inode;
import ch.supsi.fscli.backend.business.filesystem.SoftLink;
import ch.supsi.fscli.backend.business.service.FileSystemService;

import java.util.List;

public class LnCommand extends AbstractValidatedCommand {

    public LnCommand(FileSystemService fileSystemService, String name, String synopsis, String description) {
        super(fileSystemService, name, synopsis, description);
    }

    @Override
    protected CommandValidator getValidator() {
        return new NoArgsOrOptNullValidator(getName());
    }

    @Override
    public CommandResult executeCommand(CommandContext context) {
        List<String> args = context.getArguments();
        List<String> opts = context.getOptions();

        // 1. GESTIONE OPZIONI
        boolean isSoftLink = false;
        for (String opt : opts) {
            if (opt.equals("-s")) {
                isSoftLink = true;
            } else {
                //return CommandResult.error("ln: illegal option -- " + opt.replace("-", ""));
                return CommandResult.error(getName() + ": " + translate("illegal_option") + " " + opt.replace("-", ""));
            }
        }

        // 2. CONTROLLO NUMERO ARGOMENTI
        // ln richiede: [OPZIONI] SOURCE DESTINATION (o DIRECTORY)
        if (args.size() != 2) {
            // Nota: su Unix standard se manca l'argomento è "missing file operand"
            //return CommandResult.error("usage: " + getSynopsis());
            return CommandResult.error(translate("usage") + " " + translate(getSynopsis()));
        }

        String sourcePath = args.get(0);
        String destinationPath = args.get(1);

        // 3. VALIDAZIONE SORGENTE (SOURCE)
        Inode sourceInode = fileSystemService.getInode(sourcePath);

        if (!isSoftLink) {
            // su hard link non controllo il source path perchè può essere anche sbagliato

            if (sourceInode == null) {
                //return CommandResult.error("ln: cannot access '" + sourcePath + "': No such file or directory");
                return CommandResult.error(getName() + ": " + translate("cannot_access_prefix") + sourcePath + translate("no_such_file_suffix"));
            }

            if (sourceInode.isDirectory()) {
                //return CommandResult.error("ln: '" + sourcePath + "': hard link not allowed for directory");
                return CommandResult.error(getName() + ": '" + sourcePath + translate("hard_link_suffix"));
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
                //return CommandResult.error("ln: failed to create link '" + destinationPath + "': File exists");
                return CommandResult.error(getName() + ": " + translate("failed_create_link_prefix") + destinationPath + translate("file_exists_suffix"));
            }
        } else {
            // CASO B: La destinazione NON esiste (è il nome del nuovo link)
            // Dobbiamo trovare la cartella padre e il nome del nuovo file

            // Logica manuale di split del path (simile a quella nel Service, ma qui serve lato Command)
            FileSystemService.PathParts parts = resolveParentAndName(destinationPath);

            if (parts.parentDir() == null) {
                //return CommandResult.error("ln: cannot create link '" + destinationPath + "': No such file or directory");
                return CommandResult.error(getName() + ": " + translate("cannot_create_link_prefix") + destinationPath + translate("no_such_file_suffix"));
            }

            targetDir = parts.parentDir();
            linkName = parts.name();
        }

        // 5. CONTROLLO FINALE PRIMA DELLA CREAZIONE
        // Verifichiamo se dentro targetDir esiste già un file con nome linkName
        // (Questo serve per il Caso A1 e per sicurezza nel Caso B)
        if (targetDir.getChild(linkName) != null) {
            //return CommandResult.error("ln: failed to create link '" + linkName + "': File exists");
            return CommandResult.error(getName() + ": " + translate("failed_create_link_prefix") + linkName + translate("file_exists_suffix"));
        }

        // 6. ESECUZIONE (CREAZIONE LINK)

        // if the execution reaches this point everything went good and we
        // alter the state of the filesystem
        fileSystemService.setDataToSave(true);


        if (isSoftLink) {
            SoftLink softLink = new SoftLink(targetDir, sourcePath);
            targetDir.addChild(linkName, softLink);
        } else {
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

    private FileSystemService.PathParts resolveParentAndName(String path) {
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
            return new FileSystemService.PathParts(fileSystemService.getCurrentDirectory(), path);
        }

        // Risolviamo il parent path usando il service
        Inode parentNode = fileSystemService.getInode(parentPath);

        if (parentNode instanceof DirectoryNode) {
            return new FileSystemService.PathParts((DirectoryNode) parentNode, name);
        }

        // Parent non trovato o non è una directory
        return new FileSystemService.PathParts(null, name);
    }
}
